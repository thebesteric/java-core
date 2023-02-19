package org.example.thread.sync.juc.test;


import org.example.thread.sync.juc.MyLock;

public class MySyncTest implements Runnable {

    public int count = 0;

    private final MyLock myLock = new MyLock();


    public static void main(String[] args) {
        MySyncTest task = new MySyncTest();
        new Thread(task, "t1").start();
        new Thread(task, "t2").start();
        new Thread(task, "t3").start();
    }

    @Override
    public void run() {
        myLock.lock();
        for (int i = 1; i <= 5; i++) {
            System.out.println(Thread.currentThread().getName() + " calc COUNT " + (++count));
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                myLock.unlock();
                throw new RuntimeException(e);
            }
        }
        myLock.unlock();

    }
}
