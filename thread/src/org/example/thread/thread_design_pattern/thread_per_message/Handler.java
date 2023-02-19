package org.example.thread.thread_design_pattern.thread_per_message;

public class Handler {
    public void handle(int num) {
        System.out.println(">>> [" + num + "] " + Thread.currentThread().getName() + ": start handler");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(">>> [" + num + "] " + Thread.currentThread().getName() + ": end handler");
    }
}
