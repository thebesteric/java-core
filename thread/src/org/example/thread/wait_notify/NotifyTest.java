package org.example.thread.wait_notify;

import java.util.concurrent.TimeUnit;

/**
 * 案例问题：为什么 thread-C 的执行，永远在 thread-A 之后执行？
 * 当 thread-A 执行后，立即去启动 thread-B，然后执行 wait 后，thread-A 会进入 waitSet 队列，并阻塞，同时释放锁
 *
 * 此时 thread-B 就会获取到锁，然后去启动 thread-C，由于 thread-C 获取不到锁，先自旋抢锁，最终进入 cxq 队列，
 * 然后 thread-B 执行 notify，而 notify 是将 waitSet 中的头部线程移动到 entryList 或 cxq 队列到头部（此时会进入 entryList，因为 entryList 是空）
 * 然后 thread-B 释放锁
 *
 * thread-B 释放锁到同时，会根据 QMode 抉择谁先被唤醒，
 * QMode = 2，直接从 cxq 头部节点唤醒
 * QMode = 3，如果 cxq 非空，通过尾插法。把 cxq 队列的线程，加入 entryList 尾部，顺序和 cxq 一致
 * QMode = 4，如果 cxq 非空，通过头插法，把 cxq 队列的线程，加入 entryList 头部，顺序和 cxq 相反
 * 默认：QMode = 0，什么都不做，直接判断 entryList 是否为空，不为空，则取出，唤醒；否则将 cxq 通过尾插法加入到 entryList，获取第一个，再唤醒
 *
 * 此时 JVM 会从 cxq 中获取头部线程，让其有执行权，所以 thread-A 又获取到锁，并执行完成，然后释放锁
 * 此时 thread-C 才会继续执行
 */
public class NotifyTest {

    private final static Object LOCK = new Object();

    public static void main(String[] args) {
        NotifyTest notifyTest = new NotifyTest();
        notifyTest.startThreadA();
    }

    public void startThreadA() {
        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " start");
            synchronized (LOCK) {
                System.out.println(Thread.currentThread().getName() + " get lock");
                System.out.println(Thread.currentThread().getName() + " go to start Thread-B");
                startThreadB();
                try {
                    System.out.println(Thread.currentThread().getName() + " begin wait");
                    LOCK.wait();
                    System.out.println(Thread.currentThread().getName() + " after wait");
                    System.out.println(Thread.currentThread().getName() + " release lock");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "thread-A").start();
    }

    private void startThreadB() {
        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " start");
            synchronized (LOCK) {
                System.out.println(Thread.currentThread().getName() + " get lock");
                System.out.println(Thread.currentThread().getName() + " go to start Thread-C");
                startThreadC();
                try {
                    TimeUnit.MILLISECONDS.sleep(200);
                    System.out.println(Thread.currentThread().getName() + " begin notify other thread");
                    LOCK.notify(); // 从 waitSet 头部获取线程，移动到 cxq 头部
                    System.out.println(Thread.currentThread().getName() + " after notify");
                    System.out.println(Thread.currentThread().getName() + " release lock");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "thread-B").start();
    }

    private void startThreadC() {
        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " start");
            synchronized (LOCK) {
                System.out.println(Thread.currentThread().getName() + " get lock");
                System.out.println(Thread.currentThread().getName() + " release lock");
            }
        }, "thread-C").start();
    }
}
