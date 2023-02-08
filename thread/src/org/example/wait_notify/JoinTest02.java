package org.example.wait_notify;

public class JoinTest02 {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " start");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(Thread.currentThread().getName() + " end");
        }, "t1");
        t1.start();

        Thread t2 = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " start");
            // 模拟 join 方法的实现
            synchronized (t1) {
                try {
                    while(t1.isAlive()) {
                        t1.wait(); // 等待被唤醒
                        System.out.println(Thread.currentThread().getName() + " wakeup");
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println(Thread.currentThread().getName() + " end");
        }, "t2");
        t2.start();
    }
}
