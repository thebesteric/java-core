package org.example.vola;

public class ShareDemo {

    // public static int i = 0;
    public static volatile int i = 0;

    public static Object obj = new Object();

    public static void main(String[] args) {
        new Thread(() -> {
            System.out.println("消费者线程启动...");
            while (true) {
                // 1、加了打印
                // System.out.println("加了打印语句，就可以拿到");

                // 2、加了一个空的锁，就可以拿到
                // synchronized (obj) {}

                // 3、加了一个空的休眠，就可以拿到
                // try {
                //     Thread.sleep(0);
                // } catch (InterruptedException e) {
                //     throw new RuntimeException(e);
                // }

                // 4、使用 StringBuffer 类的 append 方法
                // StringBuffer sb = new StringBuffer();
                // sb.append("str");

                // 总结：上述方法都利用了 synchronized 都可见性（刷新内存）

                if (i == 1) {
                    System.out.println("消费了：" + i);
                    break;
                }
            }
        }).start();

        new Thread(() -> {
            System.out.println("生产者线程启动...");
            sleep(2000);
            i = 1;
            System.out.println("生产了：" + i);
        }).start();
    }

    public static void sleep(long millis) {
        long nano = millis * 1000 * 1000;
        long start = System.nanoTime();
        long end;
        do {
            end = System.nanoTime();
        } while (end < start + nano);
    }
}
