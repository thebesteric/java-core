package org.example.thread_design_pattern.worker_thread;

/**
 * 任务执行者
 */
public class Contractor extends Thread {

    private final ContractQueue contractQueue;

    public Contractor(String name, ContractQueue contractQueue) {
        super(name);
        this.contractQueue = contractQueue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Contract contract = contractQueue.take();
                contract.execute();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
