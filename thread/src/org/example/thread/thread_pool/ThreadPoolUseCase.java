package org.example.thread.thread_pool;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.*;

public class ThreadPoolUseCase {
    public static void main(String[] args) {

        // 正确的使用方式
        ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 10, 1, TimeUnit.MINUTES,
                new LinkedBlockingDeque<>(5), new ThreadPoolExecutor.CallerRunsPolicy());

        for (int i = 1; i <= 5; i++) {
            MyTask myTask = new MyTask(i);
            executor.execute(myTask);
        }

        executor.shutdown();

        /*
         * Executors 框架提供的不允许的使用的线程池创建方式：
         * 1、Executors.newFixedThreadPool(int nThreads)：因为队列使用的是 LinkedBlockingQueue 没有设置容量，导致 OOM
         * 2、Executors.newSingleThreadExecutor()：因为队列使用的是 LinkedBlockingQueue 没有设置容量，导致 OOM
         * 3、Executors.newCachedThreadPool()：因为使用的队列是 SynchronousQueue，且最大线程数使用的是 Integer.MAX_VALUE，如果不停的 put，却没有找到匹配的 take，就会导致急剧消耗系统资源
         * 4、Executors.newScheduledThreadPool(int corePoolSize)：因为最大线程数使用的是 Integer.MAX_VALUE，且使用的队列是 DelayedWorkQueue，是无界队列，造成 OOM
         */
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(3);

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
