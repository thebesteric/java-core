package org.example.thread.thread_pool;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolRejectTest {

    static ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 10, 1, TimeUnit.MINUTES,
            new LinkedBlockingDeque<>(10));

    public static void main(String[] args) {
        // 中止策略：抛异常
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        // 丢弃策略：悄悄的
        // executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        // 丢弃最先老的（最先入队）策略：悄悄的
        // executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
        // 交给调用者策略：多余（无法入队且无法交给临时线程）的任务交给调用者
        // executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        // 全部使用核心线程数（3 个），队列为空
        // for (int i = 1; i <= 3; i++) {
        //     executor.execute(new MyTask(i));
        // }

        // 全部使用核心线程数（3 个），队列满了（10 个任务），核心线程从列队获取剩余任务
        // for (int i = 1; i <= 13; i++) {
        //     executor.execute(new MyTask(i));
        // }

        // 全部使用核心线程数（3 个），队列满了（10 个任务），开始启用临时线程(1 个），临时线程直接执行 14 号任务，工作线程从列队获取剩余任务
        // for (int i = 1; i <= 14; i++) {
        //     executor.execute(new MyTask(i));
        // }

        // 全部使用核心线程数（3 个），队列满了（10 个任务），开始启用临时线程（7 个），临时线程直接执行 14～20 号任务，工作线程从列队获取剩余任务
        // for (int i = 1; i <= 20; i++) {
        //     executor.execute(new MyTask(i));
        // }

        // 全部使用核心线程数（3 个），队列满了（10 个任务），开始启用临时线程（7 个），临时线程直接执行 14～20 号任务，工作线程从列队获取剩余任务
        // 如果是：AbortPolicy，则抛弃队列中的最后一个任务，也就是 21 号任务，并抛出异常
        // 如果是：DiscardPolicy，则抛弃队列中的最后一个任务，也就是 21 号任务，悄悄的抛弃，无感知
        // 如果是：DiscardOldestPolicy，则抛弃队列中的第一个任务，也就是 4 号任务，悄悄的抛弃，无感知
        // 如果是：CallerRunsPolicy，则将队列中的最后一个任务，也就是 21 号任务，交给调用者直接运行
        for (int i = 1; i <= 21; i++) {
            executor.execute(new MyTask(i));
        }

        executor.shutdown();
    }

    static class MyTask implements Runnable{
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

