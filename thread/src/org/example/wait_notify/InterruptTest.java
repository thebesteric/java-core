package org.example.wait_notify;

import org.example.UnsafeUtils;

/**
 * 无法中断的 main 线程
 */
public class InterruptTest {

    public static final Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread mainThread = Thread.currentThread();

        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("开始中断 main 方法");
            mainThread.interrupt();
            System.out.println("完成中断 main 方法");
        }, "t1").start();

        notInterrupted();
        // sleepCanInterrupt();
        // unsafeParkCanInterrupt();
        // syncParkCanInterrupt();
    }

    /**
     * 唤醒 Unsafe 的 park 的方法
     */
    private static void syncParkCanInterrupt() {
        synchronized (lock) {
            System.out.println("synchronized：main ready to park");
            try {
                lock.wait();
            } catch (InterruptedException e) {
                System.out.println("synchronized：main un-park");
            }
        }
    }

    /**
     * 唤醒 Unsafe 的 park 的方法
     */
    private static void sleepCanInterrupt() {
        System.out.println("sleep：main ready to park");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            System.out.println("sleep：main un-park");
        }
    }

    /**
     * 唤醒 Unsafe 的 park 的方法
     */
    private static void unsafeParkCanInterrupt() {
        System.out.println("Unsafe-park：main ready to park");
        UnsafeUtils.unsafe.park(false, 0L);
        System.out.println("Unsafe-park：main un-park");
    }

    /**
     * 无法被中断
     */
    public static void notInterrupted() {
        while (true) {
            // if (Thread.currentThread().isInterrupted()) {
            //     break;
            // }
            try {
                System.out.println("main running");
                UnsafeUtils.sleep(1000);
            } catch (Exception e) { // 就算这里抓了异常，也不会捕捉到中断异常
                e.printStackTrace();
            }
        }
    }
}
