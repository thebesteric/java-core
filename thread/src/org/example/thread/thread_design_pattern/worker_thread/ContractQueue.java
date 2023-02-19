package org.example.thread.thread_design_pattern.worker_thread;

/**
 * 任务队列
 */
public class ContractQueue {
    private final Contract[] buffer;
    private int head;
    private int tail;
    private volatile int size;

    public ContractQueue(int capacity) {
        this.buffer = new Contract[capacity];
        this.head = 0;
        this.tail = 0;
        this.size = 0;
    }

    /**
     * 生产者调用
     */
    public synchronized void put(Contract contract) throws InterruptedException {
        while (size >= buffer.length) {
            this.wait();
        }
        buffer[tail] = contract;
        // tail++;
        // if(tail >= buffer.length) {
        //     tail = 0;
        // }
        tail = (tail + 1) % buffer.length;
        size++;
//        System.out.println(Thread.currentThread().getName() + " put " + contract);
        this.notifyAll();
    }

    /**
     * 消费者调用
     */
    public synchronized Contract take() throws InterruptedException {
        while (size <= 0) {
            this.wait();
        }
        Contract contract = buffer[head];
        // head++;
        // if (head >= buffer.length) {
        //     head = 0;
        // }
        head = (head + 1) % buffer.length;
        size--;
        // System.out.println(Thread.currentThread().getName() + " take " + contract);
        notifyAll();
        return contract;
    }
}
