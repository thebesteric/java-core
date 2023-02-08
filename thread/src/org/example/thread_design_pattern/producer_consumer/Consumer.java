package org.example.thread_design_pattern.producer_consumer;

public class Consumer extends Thread {

    private MyQueue queue;

    public Consumer(MyQueue queue, String name) {
        super(name);
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(700);
                queue.take();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
