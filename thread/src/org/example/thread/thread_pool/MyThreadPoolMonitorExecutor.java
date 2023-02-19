package org.example.thread.thread_pool;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MyThreadPoolMonitorExecutor {

    private final ThreadPoolExecutor pool;
    private final SimpleDateFormat dateFormat =new SimpleDateFormat("hh:mm:ss");

    public static void main(String[] args) throws InterruptedException {
        MyThreadPoolMonitorExecutor poolMonitorExecutor = new MyThreadPoolMonitorExecutor(2, 5, 10, 30);
        for (int i = 0; i < 35; i++) {
            poolMonitorExecutor.execute(() -> {
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        // 动态修改任务
        TimeUnit.SECONDS.sleep(1);
        System.out.println("===执行调整===");
        poolMonitorExecutor.adjust(30, 30, 10, 100);


        // 关闭线程池
        poolMonitorExecutor.shutdown();
    }

    public MyThreadPoolMonitorExecutor(int corePoolSize, int maximumPoolSize, int keepAliveTime, int queueCapacity) {
        pool = createThreadPool(corePoolSize, maximumPoolSize, keepAliveTime, queueCapacity);
    }

    private ThreadPoolExecutor createThreadPool(int corePoolSize, int maximumPoolSize, int keepAliveTime, int queueCapacity) {
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS,
                new MyLinkedBlockingQueue<>(queueCapacity)) {
            @Override
            protected void beforeExecute(Thread t, Runnable r) {
                monitor(">>>任务执行前");
            }

            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                // monitor("<<<任务执行后");
            }

            @Override
            protected void terminated() {
                monitor("线程池已经关闭");
            }
        };
    }

    public void adjust(int corePoolSize, int maximumPoolSize, int keepAliveTime, int queueCapacity)  {
        MyLinkedBlockingQueue<Runnable> queue = (MyLinkedBlockingQueue<Runnable>) pool.getQueue();
        queue.setCapacity(queueCapacity);
        pool.setMaximumPoolSize(maximumPoolSize);
        pool.setCorePoolSize(corePoolSize);
        pool.setKeepAliveTime(keepAliveTime, TimeUnit.SECONDS);

    }

    public void execute(Runnable runnable) {
        pool.execute(runnable);
    }

    public void shutdown() {
        pool.shutdown();
    }

    public void monitor(String name) {
        String message = "%s [%s] monitor %s: 核心线程数: %d, 活动线程数: %d, 最大线程数: %d, 线程池活跃度: %s, 任务完成数: %d, 队列大小: %d, 队列使用率: %s";
        String result = String.format(message,
                dateFormat.format(new Date()),
                name,
                Thread.currentThread().getName(),
                pool.getCorePoolSize(),
                pool.getActiveCount(),
                pool.getMaximumPoolSize(),
                division(pool.getActiveCount(), pool.getMaximumPoolSize()),
                pool.getCompletedTaskCount(),
                pool.getQueue().size() + pool.getQueue().remainingCapacity(),
                division(pool.getQueue().size(), pool.getQueue().size() + pool.getQueue().remainingCapacity()));
        System.out.println(result);
    }

    private String division(int num1, int num2) {
        return String.format("%1.2f%%", Double.parseDouble(num1 + "") / Double.parseDouble(num2 + "") * 100);
    }

}
