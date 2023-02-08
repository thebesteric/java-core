package org.example.sync;

public class SyncCount implements Runnable {

    private int index = 0;
    private static int COUNT = 500;

    public static void main(String[] args) {
        SyncCount syncCount = new SyncCount();
        new Thread(syncCount, "t1").start();
        new Thread(syncCount, "t2").start();
        new Thread(syncCount, "t2").start();
    }
    @Override
    public void run() {
        while (true) {
            synchronized (this) {
                if (index > COUNT) {
                    break;
                }
                System.out.println(Thread.currentThread().getName() + ", index: " + (index++));
            }
        }
    }
}
