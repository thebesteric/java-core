package org.example.thread.thread_design_pattern.producer_consumer;

public class ProducerConsumerTest {
    public static void main(String[] args) {
        MyQueue queue = new MyQueue(10);
        for (int i = 0; i < 5; i++) {
            new Producer(queue, "producer-" + i).start();
            new Consumer(queue, "consumer-" + i).start();
        }
    }
}
