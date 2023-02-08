package org.example.thread_pool.simulate;

public interface MyRejectedExecutionHandler {
    void rejectedExecution(Runnable r, MyThreadPoolExecutor executor);
}
