package org.example.thread.wait_notify;

/**
 * 证明线程退出时会：加锁、notifyAll、释放锁
 * <p/>
 * t1 线程等待 2 秒后，执行完毕，此时 t2 线程会继续等待，
 * 那么理论上 t1 线程应该已经死亡，但是由于 t2 线程使用的是 t1 做为锁，
 * 所以，t1 线程在调用退出逻辑 ensure_join 的时候，会先加锁，那么此时就会等待 t2 释放锁，才可以加锁成功，
 * 所以在 t2 线程的代码块中 t1 线程依然存活，证明了线程在退出的时候，会先加锁，最后释放锁
 */
public class JoinTest03 {
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
                    Thread.sleep(5000);
                    System.out.println("t1 线程是否依然存活：" + t1.isAlive());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println(Thread.currentThread().getName() + " end");
        }, "t2");
        t2.start();
    }
}
