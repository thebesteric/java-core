package org.example.thread.thread_pool.simulate;

public class MyAbortPolicy implements MyRejectedExecutionHandler{
    @Override
    public void rejectedExecution(Runnable r, MyThreadPoolExecutor executor) {
        throw new RuntimeException("任务被拒绝: " + r);
    }
}
