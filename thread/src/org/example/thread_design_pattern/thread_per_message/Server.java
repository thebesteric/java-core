package org.example.thread_design_pattern.thread_per_message;

public class Server {
    private final Handler handler = new Handler();

    public void request(int num) {
        System.out.println("[" + num + "] " + Thread.currentThread().getName() + ": start request");
        new Thread(()->{
            handler.handle(num);
        }).start();
        System.out.println("[" + num + "] " + Thread.currentThread().getName() + ": end request");
    }
}
