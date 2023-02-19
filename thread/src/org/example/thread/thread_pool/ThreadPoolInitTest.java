package org.example.thread.thread_pool;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolInitTest {
    public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 4, 1, TimeUnit.MINUTES,
                new LinkedBlockingDeque<>(2));

        System.out.println("==== 启动前 ====");
        System.out.println("活动线程数: " + executor.getActiveCount());

        System.out.println("==== 启动前，调用了 prestartCoreThread ====");
        boolean b = executor.prestartCoreThread();
        if (b) {
            System.out.println("创建了一个核心线程");
        }

        System.out.println("==== 启动前，调用了 prestartCoreThread ====");
        boolean b1 = executor.prestartCoreThread();
        if (b1) {
            System.out.println("创建了一个核心线程");
        }


        System.out.println("==== 启动前，调用了 prestartAllCoreThreads ====");
        int n = executor.prestartAllCoreThreads();
        System.out.println("已创建核心线程数: " + n);

        for (int i = 1; i <= 6; i++) {
            executor.execute(() -> {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(Thread.currentThread().getName() + ": executing task");
            });
            if (i == 6) {
                System.out.println("==== 任务执行中 ====");
                System.out.println("核心线程数: " + executor.getCorePoolSize());
                System.out.println("活动线程数: " + executor.getActiveCount());
            }
        }

        Thread.sleep(5000);

        System.out.println("==== 任务执行后 ====");
        System.out.println("活动线程数: " + executor.getActiveCount());

        System.out.println("==== 调整核心线程数前 ====");
        System.out.println("核心线程数: " + executor.getCorePoolSize());
        System.out.println("总线程数: " + executor.getMaximumPoolSize());

        System.out.println("==== 调整核心线程数后 ====");
        executor.setCorePoolSize(3);
        System.out.println("核心线程数: " + executor.getCorePoolSize());
        System.out.println("总线程数: " + executor.getMaximumPoolSize());

        System.out.println("==== 调整最大线程数前 ====");
        System.out.println("总线程数: " + executor.getMaximumPoolSize());

        System.out.println("==== 调整最大线程数后 ====");
        executor.setMaximumPoolSize(5);
        System.out.println("总线程数: " + executor.getMaximumPoolSize());

        executor.shutdown();
    }
}
