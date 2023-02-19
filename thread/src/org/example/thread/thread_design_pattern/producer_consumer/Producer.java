package org.example.thread.thread_design_pattern.producer_consumer;

public class Producer extends Thread {

    private static volatile int ID;
    private final MyQueue queue;

    public Producer(MyQueue queue, String name) {
        super(name);
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(500);
                queue.put("product-" + nextId());
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized int nextId() {
        return ID++;
    }
}
