package org.example.thread.thread_design_pattern.producer_consumer;

/**
 * 使用数组实现队列
 */
public class MyQueue {
    private final String[] buffer;
    private int head;
    private int tail;
    private volatile int size;

    public MyQueue(int capacity) {
        this.buffer = new String[capacity];
        this.head = 0;
        this.tail = 0;
        this.size = 0;
    }

    /**
     * 生产者调用
     */
    public synchronized void put(String str) throws InterruptedException {
        while (size >= buffer.length) {
            this.wait();
        }
        buffer[tail] = str;
        // tail++;
        // if(tail >= buffer.length) {
        //     tail = 0;
        // }
        tail = (tail + 1) % buffer.length;
        size++;
        System.out.println(Thread.currentThread().getName() + " put " + str);
        this.notifyAll();
    }

    /**
     * 消费者调用
     */
    public synchronized String take() throws InterruptedException {
        while (size <= 0) {
            this.wait();
        }
        String str = buffer[head];
        // head++;
        // if (head >= buffer.length) {
        //     head = 0;
        // }
        head = (head + 1) % buffer.length;
        size--;
        System.out.println(Thread.currentThread().getName() + " take " + str);
        notifyAll();
        return str;
    }
}
