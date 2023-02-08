package org.example.wait_notify;

public class JoinTest01 {
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " start");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(Thread.currentThread().getName() + " end");
        }, "t1");
        t1.start();

        Thread t2 = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " start");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(Thread.currentThread().getName() + " end");
        }, "t2");
        t2.start();

        t1.join(); // 告诉当前执行的线程，先等等 t1，等 t1 结束了，再往下进行
        t2.join(); // 告诉当前执行的线程，先等等 t2，等 t2 结束了，再往下进行

        System.out.println(Thread.currentThread().getName() + " running");
    }


}
