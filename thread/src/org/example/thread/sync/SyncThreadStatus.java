package org.example.thread.sync;

public class SyncThreadStatus implements Runnable{
    public static void main(String[] args) {
        SyncThreadStatus runnable = new SyncThreadStatus();
        new Thread(runnable, "t1").start();
        new Thread(runnable, "t2").start();
        new Thread(runnable, "t3").start();
    }

    @Override
    public void run() {
        synchronized (this) {
            try {
                Thread.sleep(100 * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
