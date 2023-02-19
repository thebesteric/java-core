package org.example.thread.thread_pool.simulate;

public interface MyRejectedExecutionHandler {
    void rejectedExecution(Runnable r, MyThreadPoolExecutor executor);
}
