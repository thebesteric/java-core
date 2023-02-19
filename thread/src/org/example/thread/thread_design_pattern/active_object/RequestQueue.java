package org.example.thread.thread_design_pattern.active_object;

import org.example.thread.thread_design_pattern.active_object.request.PrintOrCopyRequest;

/**
 * 使用数组实现队列
 */
public class RequestQueue {
    private final PrintOrCopyRequest[] buffer;
    private int head;
    private int tail;
    private volatile int size;

    public RequestQueue(int capacity) {
        this.buffer = new PrintOrCopyRequest[capacity];
        this.head = 0;
        this.tail = 0;
        this.size = 0;
    }

    /**
     * 生产者调用
     */
    public synchronized void put(PrintOrCopyRequest request) throws InterruptedException {
        while (size >= buffer.length) {
            this.wait();
        }
        buffer[tail] = request;
        // tail++;
        // if(tail >= buffer.length) {
        //     tail = 0;
        // }
        tail = (tail + 1) % buffer.length;
        size++;
        // System.out.println(Thread.currentThread().getName() + " put " + request);
        this.notifyAll();
    }

    /**
     * 消费者调用
     */
    public synchronized PrintOrCopyRequest take() throws InterruptedException {
        while (size <= 0) {
            this.wait();
        }
        PrintOrCopyRequest request = buffer[head];
        // head++;
        // if (head >= buffer.length) {
        //     head = 0;
        // }
        head = (head + 1) % buffer.length;
        size--;
        // System.out.println(Thread.currentThread().getName() + " take " + request);
        notifyAll();
        return request;
    }
}
