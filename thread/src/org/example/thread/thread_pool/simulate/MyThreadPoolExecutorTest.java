package org.example.thread.thread_pool.simulate;

import java.util.concurrent.LinkedBlockingQueue;

public class MyThreadPoolExecutorTest {
    public static void main(String[] args) {
        MyThreadPoolExecutor executor = new MyThreadPoolExecutor(3, 10, new LinkedBlockingQueue<>());
        for (int i = 1; i <= 30; i++) {
            executor.execute(new MyThreadPoolExecutor.MyTask(i));
        }

        executor.shutdown();
    }
}
