package org.example.thread.thread_design_pattern.future;

public class FutureTest {
    public static void main(String[] args) {
        MyServer myServer = new MyServer();

        System.out.println(">>> 发送请求");
        FutureProduct f1 = myServer.request(5, 'A');
        FutureProduct f2 = myServer.request(6, 'B');
        FutureProduct f3 = myServer.request(7, 'C');
        FutureProduct f4 = myServer.request(8, 'D');
        System.out.println("<<< 请求发送完成");

        System.out.println("=== 开始做其他事情 ===");

        new Thread(() -> System.out.println(Thread.currentThread().getName() + ": RealProduct = " + f1.getContent()), "Client-A").start();
        new Thread(() -> System.out.println(Thread.currentThread().getName() + ": RealProduct = " + f2.getContent()), "Client-B").start();
        new Thread(() -> System.out.println(Thread.currentThread().getName() + ": RealProduct = " + f3.getContent()), "Client-C").start();
        new Thread(() -> System.out.println(Thread.currentThread().getName() + ": RealProduct = " + f4.getContent()), "Client-D").start();

    }
}
