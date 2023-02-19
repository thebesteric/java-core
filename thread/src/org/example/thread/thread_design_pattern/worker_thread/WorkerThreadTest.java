package org.example.thread.thread_design_pattern.worker_thread;

public class WorkerThreadTest {
    public static void main(String[] args) {
        // 任务队列
        ContractQueue contractQueue = new ContractQueue(100);

        // 执行者
        Contractor[] contractors = new Contractor[5];
        for (int i = 0; i < 5; i++) {
            contractors[i] = new Contractor("worker-" + i, contractQueue);
            contractors[i].start();
        }

        // 发布者
        for (int i = 0; i < 5; i++) {
            new Publisher("publisher-1", contractQueue).start();
        }


    }
}
