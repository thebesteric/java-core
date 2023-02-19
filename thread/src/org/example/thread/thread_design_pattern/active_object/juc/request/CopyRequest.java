package org.example.thread.thread_design_pattern.active_object.juc.request;

public class CopyRequest implements Runnable {

    private final String content;

    public CopyRequest(String content) {
        this.content = content;
    }

    @Override
    public void run() {
        System.out.println("复印: " + content);
    }
}
