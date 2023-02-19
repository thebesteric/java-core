package org.example.thread.thread_design_pattern.worker_thread;

import java.util.Random;

/**
 * 任务发布者
 */
public class Publisher extends Thread {
    private final ContractQueue contractQueue;

    private static final Random RANDOM = new Random();

    public Publisher(String name, ContractQueue contractQueue) {
        super(name);
        this.contractQueue = contractQueue;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 100; i++) {
                Contract contract = new Contract(i, getName());
                contractQueue.put(contract);
                Thread.sleep(RANDOM.nextInt(1000));
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
