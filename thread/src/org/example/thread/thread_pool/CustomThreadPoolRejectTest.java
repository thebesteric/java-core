package org.example.thread.thread_pool;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 自定义拒绝策略
 * 实现：RejectedExecutionHandler
 */
public class CustomThreadPoolRejectTest {

    static ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 2, 1, TimeUnit.MINUTES,
            new LinkedBlockingDeque<>(2));

    public static void main(String[] args) {
        // 自定义拒绝策略
        executor.setRejectedExecutionHandler(new MyRejectPolicy());

        for (int i = 1; i <= 5; i++) {
            executor.execute(new MyTask(i));
        }

        executor.shutdown();
    }

    static class MyRejectPolicy implements RejectedExecutionHandler {

        private static int threadInitNumber;

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if (!executor.isShutdown()) {
                new Thread(r, "custom-thread-" + nextThreadNum()).start();
            }
        }

        private static synchronized int nextThreadNum() {
            return threadInitNumber++;
        }
    }

    static class MyTask implements Runnable {
        @Getter
        @Setter
        private int no;

        public MyTask(int no) {
            this.no = no;
        }

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + " start: " + no + " 号任务");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(Thread.currentThread().getName() + " end");
        }

        @Override
        public String toString() {
            return "【MyTask: " + no + "】";
        }
    }
}
