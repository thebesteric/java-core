package org.example.thread.thread_design_pattern.active_object.juc;


import org.example.thread.thread_design_pattern.active_object.juc.request.CopyRequest;
import org.example.thread.thread_design_pattern.active_object.juc.request.PrintRequest;

import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PrintOrCopyProxy implements ActiveObject {

    private final ThreadPoolExecutor executor;

    public PrintOrCopyProxy() {
        this.executor = new ThreadPoolExecutor(10, 10,
                1, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(100));
    }

    @Override
    public Future<String> printContent(String content) {
        PrintRequest printRequest = new PrintRequest(content);
        Future<String> future = executor.submit(printRequest);
        return future;
    }

    @Override
    public void copyContent(String content) {
        executor.execute(new CopyRequest(content));
    }

    @Override
    public void shutdown() {
        executor.shutdown();
    }
}
