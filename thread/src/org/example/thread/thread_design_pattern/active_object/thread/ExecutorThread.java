package org.example.thread.thread_design_pattern.active_object.thread;

import org.example.thread.thread_design_pattern.active_object.request.PrintOrCopyRequest;
import org.example.thread.thread_design_pattern.active_object.RequestQueue;

public class ExecutorThread extends Thread {

    public final RequestQueue requestQueue;

    public ExecutorThread(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }

    @Override
    public void run() {
        try {
            // 不断从队列中获取请求
            while (true) {
                PrintOrCopyRequest printOrCopyRequest = requestQueue.take();
                printOrCopyRequest.execute();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
