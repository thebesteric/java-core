package org.example.sync.lockupgrade;

import lombok.Data;
import org.example.UnsafeUtils;

import java.lang.reflect.Field;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * ObjectMonitor 可以理解为一个同步工具或一种同步机制，通常被描述为一个对象。
 * 每一个 Java 对象就有一把看不见的锁，称为内部锁或者 Monitor 锁。
 * ObjectMonitor 不仅是重量级锁的实现，还是 Object 的 wait/notify/notifyAll 方法的底层核心实现
 */
@Data
public class ObjectMonitor {
    /**
     * 重入次数
     */
    private int recursion = 0;
    /**
     * 当前拥有锁的线程
     */
    private volatile Thread owner;
    /**
     * 多线程竞争锁时，没有抢到锁，进入的单向链表
     */
    private LinkedBlockingQueue<ObjectWait> cxq = new LinkedBlockingQueue<>();
    /**
     * 多线程执行 wait 方法时，进入的双向链表
     */
    private LinkedBlockingDeque<ObjectWait> waitSet = new LinkedBlockingDeque<>();
    ;
    /**
     * 线程被唤醒时，进入到单向链表
     */
    private LinkedBlockingQueue<ObjectWait> entryList = new LinkedBlockingQueue<>();

    /**
     * 重量级锁入口
     */
    public void enter(MyLock myLock, ThreadLocal<LockRecord> threadLocal) {
        // 1、CAS 修改 owner 为当前线程
        if (MarkWord.LockInflateStatus.INFLATING != myLock.getMarkWord().getLockInflateStatus()) {
            Thread ownerThread = compareAndSwapOwner(myLock);
            System.err.println(Thread.currentThread().getName() + " owner = " + ownerThread);
            if (ownerThread == null) {
                return;
            }
            // 2、如果之前到 owner 就是当前线程，表示是重入
            if (ownerThread == Thread.currentThread()) {
                recursion++;
                return;
            }
        }
        // 3、从轻量级锁膨胀而来
        LockRecord lockRecord = threadLocal.get();
        MarkWord head = lockRecord.getHead();
        if (head != null) {
            if (!myLock.getBiasedLocking().isAlive(myLock)) {
                recursion = 1;
                owner = Thread.currentThread();
                return;
            }
        }

        // 4、预备入队挂起
        enterI(myLock);
    }

    /**
     * 准别挂起
     */
    public void enterI(MyLock myLock) {
        // 尝试加锁一次
        if (tryLock(myLock) > 0) {
            return;
        }
        try {
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // 自旋：尝试 10 次
        if (trySpin(myLock) > 0) {
            return;
        }
        // 还没有抢到锁，准备入队挂起
        ObjectWait objectWait = new ObjectWait(Thread.currentThread());
        for (; ; ) {
            if (myLock.getMarkWord().getPtrMonitor().getCxq().offer(objectWait)) {
                System.err.println(Thread.currentThread().getName() + " 入队");
                break;
            }
            if (tryLock(myLock) > 0) {
                return;
            }
        }
        // 挂起
        for (; ; ) {
            if (tryLock(myLock) > 0) {
                break;
            }
            System.err.println(Thread.currentThread().getName() + " park");
            // 膨胀为重量级锁
            if (myLock.getMarkWord().getLockFlag() != MarkWord.LockFlag.HEAVY) {
                myLock.getMarkWord().setLockInflateStatus(MarkWord.LockInflateStatus.INFLATED);
            }
            System.err.println(Thread.currentThread().getName() + " park 前的锁标记为 " + myLock.getMarkWord().getLockFlag());
            // 线程被挂起，线程会卡在这里（线程进入内核态）
            UnsafeUtils.unsafe.park(false, 0L);
            // 线程被唤醒，线程进入用户态
            System.err.println(Thread.currentThread().getName() + " park 后的锁标记为 " + myLock.getMarkWord().getLockFlag());
        }
    }

    public int trySpin(MyLock myLock) {
        int result = -1;
        for (int i = 0; i < 10; i++) {
            result = tryLock(myLock);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (result > 0) {
                return result;
            }
        }
        return result;
    }

    /**
     * 尝试加锁
     *
     * @return 0：退出，1：加锁成功，-1：加锁失败
     */
    private int tryLock(MyLock myLock) {
        // 如果有线程拥有重量级锁，直接退出
        if (this.owner != null) {
            return 0;
        }
        // 如果是无锁状态，则尝试 CAS 修改
        if (MarkWord.LockInflateStatus.NEUTRAL == myLock.getMarkWord().getLockInflateStatus()) {
            System.err.println(Thread.currentThread().getName() + " 尝试抢锁");
            Thread ownerThread = compareAndSwapOwner(myLock);
            if (ownerThread == null) {
                System.err.println(Thread.currentThread().getName() + " 抢锁成功");
                System.err.println(Thread.currentThread().getName() + " 的锁标记为 " + myLock.getMarkWord().getLockFlag());
                return 1;
            }
        }
        return -1;
    }

    /**
     * CAS 修改 ObjectMonitor 的 owner 字段
     *
     * @return 修改成功返回 null，否则返回 owner
     */
    private Thread compareAndSwapOwner(MyLock myLock) {
        MarkWord markWord = myLock.getMarkWord();
        ObjectMonitor objectMonitor = markWord.getPtrMonitor();
        boolean isSucceed;
        try {
            Field owner = objectMonitor.getClass().getDeclaredField("owner");
            long offset = UnsafeUtils.unsafe.objectFieldOffset(owner);
            isSucceed = UnsafeUtils.unsafe.compareAndSwapObject(objectMonitor, offset, null, Thread.currentThread());
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        if (isSucceed) {
            return null;
        }
        return objectMonitor.getOwner();
    }

    /**
     * 重量级锁到退出
     */
    public void exit(MyLock myLock, ThreadLocal<LockRecord> threadLocal) {
        Thread currThread = Thread.currentThread();
        for(;;) {
            if (owner != null && owner != currThread) {
                LockRecord lockRecord = threadLocal.get();
                MarkWord head = lockRecord.getHead();
                // 是不是从轻量级锁膨胀而来
                if (head != null) {
                    owner = currThread;
                    recursion = 0;
                } else {
                    throw new RuntimeException("不是锁拥有者，无权释放该锁");
                }
            }

            // 判断是不是重入锁
            if (recursion != 0) {
                recursion--;
                return;
            }

            // 设置屏障，让内存可见
            UnsafeUtils.unsafe.storeFence();

            // 从队列获取一个线程，准备唤醒
            ObjectWait objectWait = cxq.poll();
            if (objectWait != null) {
                exitEpiLog(myLock, objectWait);
                break;
            }

            if (myLock.getMarkWord().isLockNone() && myLock.getMarkWord().getPtrMonitor().getCxq().isEmpty()) {
                break;
            }
        }
        System.err.println(Thread.currentThread().getName() + " 运行结束");
    }

    /**
     * 唤醒线程
     */
    private void exitEpiLog(MyLock myLock, ObjectWait objectWait) {
        System.err.println(Thread.currentThread().getName() + " 解锁");
        // 丢弃锁
        myLock.getMarkWord().getPtrMonitor().setOwner(null);
        // 获取线程，并唤醒
        Thread waitThread = objectWait.getThread();
        System.err.println(Thread.currentThread().getName() + " 唤醒 " + waitThread.getName());
        myLock.getMarkWord().setLockNone();
        UnsafeUtils.unsafe.unpark(waitThread);
    }
}
