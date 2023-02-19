package org.example.thread.thread_pool;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 1. 在多个任务并发执行的时候，可以暂停线程池；
 * 2. 暂停之后可以恢复线程池；
 * 3. 在每个任务执行之后，需要获取执行结果；
 * 4. 当现场池关闭时，发送通知给管理员；
 */
public class MyExtendThreadPool extends ThreadPoolExecutor {

    private static final Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private boolean isPaused;

    public MyExtendThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        try {
            lock.lock();
            // 检查如果是暂停状态，此时挂起，等待唤醒
            while (isPaused) {
                condition.await();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        if (t == null && r instanceof Future<?>) {
            try {
                Future<?> future = (Future<?>) r;
                if (future.isDone()) {
                    Object result = future.get();
                    System.out.println(">>>> 任务执行结果为：" + result);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void terminated() {
        System.out.println(">>>> send email to administrator");
    }

    public void pause() {
        lock.lock();
        try {
            isPaused = true;
            System.out.println(">>>> 线程池已暂停");
        } finally {
            lock.unlock();
        }
    }

    public void resume() {
        lock.lock();
        try {
            isPaused = false;
            condition.signalAll(); // 唤醒线程
            System.out.println(">>>> 线程池已恢复启动");
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // 1. 在多个任务并发执行的时候，可以暂停线程池；
        // 2. 暂停之后可以恢复线程池；
        // 3. 在每个任务执行之后，需要获取执行结果；
        // 4. 当现场池关闭时，发送通知给管理员；
        MyExtendThreadPool myExtendThreadPool = new MyExtendThreadPool(3, 3,
                1, TimeUnit.MINUTES, new LinkedBlockingDeque<>(20));
        for (int i = 1; i <= 15; i++) {
            myExtendThreadPool.submit(new MyFutureTask(i));
        }

        Thread.sleep(1000);
        myExtendThreadPool.pause();
        Thread.sleep(3000);
        myExtendThreadPool.resume();

        myExtendThreadPool.shutdown();
    }

    static class MyFutureTask implements Callable<Integer> {
        @Getter
        @Setter
        private int no;

        public MyFutureTask(int no) {
            this.no = no;
        }

        @Override
        public Integer call() throws Exception {
            System.out.println(Thread.currentThread().getName() + " start: " + no + " 号任务");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(Thread.currentThread().getName() + " end");
            return no;
        }

        @Override
        public String toString() {
            return "【MyTask: " + no + "】";
        }
    }
}
