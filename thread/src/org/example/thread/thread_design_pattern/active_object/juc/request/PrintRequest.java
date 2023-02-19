package org.example.thread.thread_design_pattern.active_object.juc.request;

import java.util.concurrent.Callable;

public class PrintRequest implements Callable<String> {

    private final String content;

    public PrintRequest(String content) {
        this.content = content;
    }

    @Override
    public String call() throws Exception {
        System.out.println("打印: " + content);
        return content;
    }
}
