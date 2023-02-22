package org.example.thread.thread;

import java.util.concurrent.TimeUnit;

public class ThreadGroupDemo {
    public static void main(String[] args) throws Exception {
        // threadGroup();
        // enumerateThread(false);
        // enumerateThreadGroup(true);
        interrupt();
    }

    /**
     * 线程组基础演示
     */
    public static void threadGroup() throws InterruptedException {
        // main
        System.out.println("main 线程所在的线程组名称：" + Thread.currentThread().getThreadGroup().getName());
        // system：这个线程组是 c 调用的，可以通过无参的 new Thread() 构造函数查看
        System.out.println("main 线程所在的线程组的线程组名称：" + Thread.currentThread().getThreadGroup().getParent().getName());

        // 创建一个线程组
        ThreadGroup threadGroup = new ThreadGroup("thread-group");

        Thread thread1 = new Thread(threadGroup, () -> {
            int count = 0;
            while (count < 5) {
                System.out.println(Thread.currentThread().getName() + " run...");
                count++;
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        }, "thread-1");

        Thread thread2 = new Thread(threadGroup, () -> {
            int count = 0;
            while (count < 10) {
                System.out.println(Thread.currentThread().getName() + " run...");
                count++;
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        }, "thread-2");

        // 必须启动后，才会加入线程组，线程组的活动线程数量才会 +1
        thread1.start();
        thread2.start();

        System.out.println("thread1 所在线程组名称：" + thread1.getThreadGroup().getName());
        System.out.println("thread2 所在线程组名称：" + thread2.getThreadGroup().getName());

        Thread thread3 = new Thread(threadGroup, () -> {
            System.out.println(Thread.currentThread().getName() + " run...");
        }, "thread-3");
        thread3.start();

        // 创建一个子线程组，加入 thread-group 线程组
        ThreadGroup subThreadGroup = new ThreadGroup(threadGroup, "sub-thread-group");
        System.out.println("子线程组 sub-thread-group 的父线程组：" + subThreadGroup.getParent().getName());

        System.out.println("活动线程数量：" + threadGroup.activeCount());
        System.out.println("活动线程组数量：" + threadGroup.activeGroupCount());

        // 线程运行结束后，活动线程也会相应 -1
        // 注意：没有 start 的线程，也不会算做活动线程
        TimeUnit.SECONDS.sleep(2);
        System.out.println("活动线程数量：" + threadGroup.activeCount());

        TimeUnit.SECONDS.sleep(5);
        System.out.println("活动线程数量：" + threadGroup.activeCount());

        TimeUnit.SECONDS.sleep(5);
        System.out.println("活动线程数量：" + threadGroup.activeCount());
    }

    /**
     * 线程组：列出所属线程 演示
     *
     * @param recurse 是否递归查询
     */
    public static void enumerateThread(boolean recurse) {
        // 指定线程组
        ThreadGroup threadGroup1 = new ThreadGroup("thread-group-1");
        new Thread(threadGroup1, () -> {
            System.out.println(Thread.currentThread().getName() + " run...");
        }, "thread-1").start();

        ThreadGroup threadGroup2 = new ThreadGroup("thread-group-1");
        new Thread(threadGroup2, () -> {
            System.out.println(Thread.currentThread().getName() + " run...");
        }, "thread-2").start();

        new Thread(() -> {
            // 内部创建的线程，属于父线程的线程组
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + " run...");
            }, "thread-4").start();
            System.out.println(Thread.currentThread().getName() + " run...");
        }, "thread-3").start();

        new Thread(Thread.currentThread().getThreadGroup(), () -> {
            System.out.println(Thread.currentThread().getName() + " run...");
        }, "thread-5").start();

        // 该线程属于 system 线程组
        new Thread(Thread.currentThread().getThreadGroup().getParent(), () -> {
            System.out.println(Thread.currentThread().getName() + " run...");
        }, "thread-6").start();


        // 遍历当前所有活动线程
        ThreadGroup mainThreadGroup = Thread.currentThread().getThreadGroup();
        Thread[] threads = new Thread[mainThreadGroup.activeCount()];
        int enumerate = mainThreadGroup.enumerate(threads, recurse);
        System.out.println("mainThreadGroup enumerate = " + enumerate);

        int i = 1;
        for (Thread thread : threads) {
            if (thread != null)
                System.out.println("[" + (i++) + "] thread name = " + thread.getName());
        }
    }

    /**
     * 线程组：列出所属线程组 演示
     *
     * @param recurse 是否递归查询
     */
    public static void enumerateThreadGroup(boolean recurse) {
        ThreadGroup threadGroup1 = new ThreadGroup("thread-group-1");
        ThreadGroup threadGroup2 = new ThreadGroup(threadGroup1, "thread-group-2");
        ThreadGroup threadGroup3 = new ThreadGroup(Thread.currentThread().getThreadGroup(), "thread-group-3");
        ThreadGroup threadGroup4 = new ThreadGroup(Thread.currentThread().getThreadGroup().getParent(), "thread-group-4");

        ThreadGroup mainThreadGroup = Thread.currentThread().getThreadGroup();
        ThreadGroup[] threadGroups = new ThreadGroup[mainThreadGroup.activeGroupCount()];
        int enumerate = mainThreadGroup.enumerate(threadGroups, recurse);
        System.out.println("mainThreadGroup enumerate group = " + enumerate);

        int i = 1;
        for (ThreadGroup threadGroup : threadGroups) {
            if (threadGroup != null)
                System.out.println("[" + (i++) + "] thread group name = " + threadGroup.getName());
        }
    }

    /**
     * 线程组：中断组内所有线程
     */
    public static void interrupt() {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    while (true) {
                        System.out.println(Thread.currentThread().getName() + " run...");
                        sleep(1000);
                    }
                } catch (Exception ex) {
                    System.out.println(Thread.currentThread().getName() + " interrupted...");
                }
            }, "thread-" + i).start();
        }

        sleep(3000);
        ThreadGroup mainThreadGroup = Thread.currentThread().getThreadGroup();
        mainThreadGroup.interrupt();
    }

    public static void sleep(int milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
