package org.example.thread.thread;

/**
 * 优先级只能让 CPU 尽可能的多调用优先级高的线程
 * 并不能依靠优先级完全控制线程的执行
 * 默认，线程会继承父线程的优先级
 */
public class ThreadPriority {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 200; i++) {
                System.out.println(Thread.currentThread().getName() + " ===== run..." + i);
            }
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + " priority = " + Thread.currentThread().getPriority());
            }, "t1-1").start();
        }, "t1");

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 200; i++) {
                System.out.println(Thread.currentThread().getName() + " >>>>> run..." + i);
            }
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + " priority = " + Thread.currentThread().getPriority());
            }, "t2-1").start();
        }, "t2");

        t1.setPriority(5);
        t2.setPriority(10);

        t1.start();
        t2.start();
    }
}
