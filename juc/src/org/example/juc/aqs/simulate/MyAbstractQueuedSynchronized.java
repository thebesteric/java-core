package org.example.juc.aqs.simulate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.juc.MyUnsafe;
import sun.misc.Unsafe;

import java.util.NoSuchElementException;
import java.util.concurrent.locks.LockSupport;

@Getter
@Setter
public class MyAbstractQueuedSynchronized {
    private volatile int state;
    private volatile Thread owner;
    private volatile Node head;
    private volatile Node tail;
    private static long STATE_OFFSET;
    private static long HEAD_OFFSET;
    private static long TAIL_OFFSET;
    private static final long WAIT_STATE_OFFSET;
    private static final long NEXT_NODE_OFFSET;
    private static final long PREV_NODE_OFFSET;

    private static Unsafe UNSAFE = MyUnsafe.getUnsafe();

    static {
        try {
            STATE_OFFSET = UNSAFE.objectFieldOffset(MyAbstractQueuedSynchronized.class.getDeclaredField("state"));
            HEAD_OFFSET = UNSAFE.objectFieldOffset(MyAbstractQueuedSynchronized.class.getDeclaredField("head"));
            TAIL_OFFSET = UNSAFE.objectFieldOffset(MyAbstractQueuedSynchronized.class.getDeclaredField("tail"));
            WAIT_STATE_OFFSET = UNSAFE.objectFieldOffset(Node.class.getDeclaredField("waitState"));
            NEXT_NODE_OFFSET = UNSAFE.objectFieldOffset(Node.class.getDeclaredField("next"));
            PREV_NODE_OFFSET = UNSAFE.objectFieldOffset(Node.class.getDeclaredField("prev"));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * CAS 更新 state
     */
    public boolean compareAndSetState(int expected, int update) {
        return UNSAFE.compareAndSwapInt(this, STATE_OFFSET, expected, update);
    }

    public boolean compareAndSetHead(Node update) {
        return UNSAFE.compareAndSwapObject(this, HEAD_OFFSET, null, update);
    }

    public boolean compareAndSetTail(Node expected, Node update) {
        return UNSAFE.compareAndSwapObject(this, TAIL_OFFSET, expected, update);
    }

    private boolean compareAndSetWaitState(Node node, int expected, int update) {
        return UNSAFE.compareAndSwapInt(node, WAIT_STATE_OFFSET, expected, update);
    }

    private boolean compareAndSetNext(Node node, Node expected, Node update) {
        return UNSAFE.compareAndSwapObject(node, NEXT_NODE_OFFSET, expected, update);
    }

    private boolean compareAndSetPrev(Node node, Node expected, Node update) {
        return UNSAFE.compareAndSwapObject(node, PREV_NODE_OFFSET, expected, update);
    }

    /**
     * 加锁
     */
    public void acquire(int arg) {
        // 尝试获取锁，如果失败，就需要：
        // 1、先入队：addNode
        // 2、再自旋阻塞：acquireQueue
        if (!tryAcquire(arg) && acquireQueue(addNode(Node.EXCLUSIVE), arg)) {
            // 补上 parkAndCheckInterrupted 方法清除掉的中断标记位置
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 释放锁
     */
    public boolean release(int arg) {
        // 1、尝试释放锁
        if (tryRelease(arg)) {
            // 2、唤醒头节点的后继节点
            Node h = head;
            if (h != null && h.waitState != 0) {
                // 唤醒同步队列中被阻塞的线程（唤醒的是头节点的后继节点）
                unparkSuccessor(h);
            }
            return true;
        }
        return false;
    }

    /**
     * 让子类来实现：尝试加锁
     */
    protected boolean tryAcquire(int arg) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * 让子类来实现：尝试解锁
     */
    protected boolean tryRelease(int arg) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * 入队
     */
    public Node addNode(Node mode) {
        Node node = new Node(Thread.currentThread(), mode);
        Node pred = tail; // 尾节点赋值给一个 prev 节点
        // 快速尝试入队，这里时一个优化，此时判断队列里是有节点正在等待的
        if (pred != null) {
            node.prev = pred;
            if (compareAndSetTail(pred, node)) { // 尝试将自己设置为尾节点
                pred.next = node;
                return node;
            }
        }
        // 1、pred == null 表示队列还未初始化
        // 2、或者 cas 失败（有线程已经修改成功了），自旋尝试入队
        return casEnqueue(node);
    }

    /**
     * CAS 节点入队
     */
    private Node casEnqueue(Node node) {
        while (true) {
            Node tempTail = tail;
            // 队列还不存在
            if (tempTail == null) {
                if (compareAndSetHead(new Node())) {
                    tail = head;
                }
            }
            // 已经存在队列了
            else {
                node.prev = tempTail;
                if (compareAndSetTail(tempTail, node)) {
                    tempTail.next = node;
                    return node;
                }
            }
        }
    }


    /**
     * 自旋获取锁、阻塞
     */
    public boolean acquireQueue(Node node, int arg) {
        boolean failed = true;
        try {
            boolean interrupted = false;
            while (true) {
                // 1、检查前驱节点是否是头节点，如果是头节点则尝试获取锁
                Node prev = node.preNode();
                if (prev == head && tryAcquire(arg)) {
                    setHead(node); // 剔除原有头节点
                    prev.next = null; // 断开原有头节点的 next 指针
                    failed = false;
                    return interrupted;
                }
                // 2、阻塞
                if (shouldParkAfterAcquireFailed(prev, node) && parkAndCheckInterrupted()) {
                    // 发生了中断
                    interrupted = true;
                }
            }
        } finally {
            // 发生异常，将自己从队列中摘除
            if (failed) {
                cancelAcquire(node);
            }
        }
    }

    /**
     * 取消节点
     * 只是 node.prev 的 next 指针跨过了该节点，而 node.next 的 prev 指针并没有跨过该节点
     * 那么谁来修改 node.next 的 prev 的呢？
     * 两种情况：
     * 1、由下一个线程，执行 shouldParkAfterAcquireFailed 方式的时候，将 node.next 的 prev 指向 node.prev
     * 2、再次出现异常，由下一个线程，执行 cancelAcquire 时，循环判断 node.prev 的状态是否 > 0
     */
    private void cancelAcquire(Node node) {
        if (node == null) {
            return;
        }
        node.thread = null;
        Node pred = node.prev;
        while (pred.waitState > 0) {
            pred = pred.prev;
            node.prev = pred;
        }
        Node predNext = pred.next;
        // 设置当前节点状态为 CANCELLED
        node.waitState = Node.CANCELLED;

        // 如果是尾节点，则修改尾节点为 pred
        if (node == tail && compareAndSetTail(node, pred)) {
            // 将 pred 的 next 指针指向 null
            compareAndSetNext(pred, predNext, null);
        }
        // 中间节点或头节点
        else {
            int ws;
            // pred 不是头节点
            if (pred != head
                    && ((ws = pred.waitState) == Node.SIGNAL || (ws <= 0 && compareAndSetWaitState(pred, ws, Node.SIGNAL)))
                    && pred.thread != null) {
                Node nodeNext = node.next;
                // 跨过当前节点
                if (nodeNext != null && nodeNext.waitState <= 0) {
                    compareAndSetNext(pred, predNext, nodeNext);
                }
            }
            // pred 是头节点
            else {
                // 唤醒节点本应该是正常节点该做的事情
                // 因为当前节点发生了异常，如果不去唤醒后面的节点，那就后继节点就没有人去唤醒了
                unparkSuccessor(node);
            }
            node.next = node;
        }

    }

    /**
     * 唤醒后继节点（通常是头节点来唤醒）
     */
    private void unparkSuccessor(Node node) {
        int waitState = node.waitState;
        if (waitState < 0) {
            // 把该节点恢复为初始创建，因为该节点即将删除出队
            compareAndSetState(waitState, 0);
        }
        // 头节点的后继集点
        Node s = node.next;
        if (s == null || s.waitState > 0) {
            s = null;
            // 从 tail 往前找，找到最靠近头节点的正常节点
            for (Node t = tail; t != null && t != node; t = t.prev) {
                if (t.waitState <= 0) {
                    s = t;
                }
            }
        }
        if (s != null) {
            // 找到并唤醒后继节点
            LockSupport.unpark(s.thread);
        }
    }

    /**
     * 是否应该在获取锁失败后挂起
     */
    private boolean shouldParkAfterAcquireFailed(Node pred, Node node) {
        int waitState = pred.waitState;
        // 如果前驱节点的状态是 SIGNAL 则可以被阻塞
        if (waitState == Node.SIGNAL) {
            return true;
        }
        // CANCELLED 状态
        if (waitState > 0) {
            do {
                pred = pred.prev; // 找到 prev 的前驱节点
                node.prev = pred; // 将当前 node 的 prev 指针指向 prev 的前驱节点
            } while (pred.waitState > 0);
            pred.next = node; // 将prev 的前驱节点 指针指向当前 node 节点
        }
        // waitState 为 0（初始状态）
        else {
            compareAndSetWaitState(pred, waitState, Node.SIGNAL);
        }
        return false;
    }

    /**
     * 挂起
     * 有 2 个流程会唤醒 park
     * 1、正常流程：其他线程会执行 unpark 方法换新
     * 2、异常流程：当前线程执行了 interrupt 方法
     */
    private boolean parkAndCheckInterrupted() {
        LockSupport.park(this); // 被阻塞的线程会卡在这里，等待唤醒
        // 这里会清除中断标记位：如果已经中断，返回 true，否则 false，同时清理中断标记位；
        // 这里清除的中断标记为一定要还原，否则就是破坏了业务流程
        // 为什么不能使用 Thread.currentThread().isInterrupted() 呢，因为这里不会清除中断标记位，那么 park 就阻塞不了了
        return Thread.interrupted();
    }

    public void setHead(Node node) {
        this.head = node;
        node.thread = null;
        node.prev = null;
    }

    /**
     * 检查队列中有没有节点
     */
    public boolean hasQueuePred() {
        return false;
    }

    @NoArgsConstructor
    static class Node {
        public static Node EXCLUSIVE; // 独占模式
        // 共享或者独占
        private Node nextWaiter;
        private volatile Thread thread;
        private volatile Node prev;
        private volatile Node next;
        // 已经取消的状态
        public static final int CANCELLED = 1;
        // 前驱节点如果是 SIGNAL 状态，那么后继节点才可以阻塞
        public static final int SIGNAL = -1;
        // 节点等待状态
        private volatile int waitState = 0;

        Node(Thread thread, Node mode) {
            this.thread = thread;
            this.nextWaiter = mode;
        }

        public Node preNode() {
            Node prev = this.prev;
            if (prev == null) {
                throw new NoSuchElementException("No prev node");
            }
            return prev;
        }
    }


}
