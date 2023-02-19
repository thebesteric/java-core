package org.example.thread.sync.lockupgrade.demo;

import org.example.thread.sync.lockupgrade.MyLock;
import org.example.thread.sync.lockupgrade.MySynchronized;

public class MySynchronizedTest implements Runnable {

    public int count = 0;

    private final MySynchronized mySynchronized = new MySynchronized(new MyLock(), true);


    public static void main(String[] args) {
        MySynchronizedTest task = new MySynchronizedTest();
        new Thread(task, "t1").start();
        new Thread(task, "t2").start();
        new Thread(task, "t3").start();
    }

    @Override
    public void run() {
        mySynchronized.monitorEnter();
        for (int i = 1; i <= 5; i++) {
            System.out.println(Thread.currentThread().getName() + " calc COUNT " + (++count));
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                mySynchronized.monitorExit();
                throw new RuntimeException(e);
            }
        }
        mySynchronized.monitorExit();

    }
}
