package org.example.sync;

import java.util.concurrent.TimeUnit;

/**
 * 验证了 CXQ 队列，是将最后一个线程加入队列的头部，唤醒的时候也是从头部唤醒线程
 */
public class NonFairSync {

    private static final Object object = new Object();

    public static void main(String[] args) throws InterruptedException {

        NonFairSync nonFairSync = new NonFairSync();

        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(nonFairSync::run, "t" + i);
        }

        synchronized (object) {
            for (Thread thread : threads) {
                thread.start();
                System.out.println(thread.getName() + " await");
                TimeUnit.MILLISECONDS.sleep(200);
            }
        }


    }

    public void run() {
        synchronized (object) {
            System.out.println(Thread.currentThread().getName() + " running");
        }
    }
}
