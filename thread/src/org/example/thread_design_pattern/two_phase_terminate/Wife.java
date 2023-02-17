package org.example.thread_design_pattern.two_phase_terminate;

import java.util.Random;

public class Wife extends Thread {

    private Worker worker;

    public Wife(String name, Worker worker) {
        super(name);
        this.worker = worker;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(new Random().nextInt(10000));
            if (!worker.isStop()) {
                System.out.println(getName() + ": 打电话个给 " + worker.getName() + " 说家里有事...");
                worker.setStop(true);
                System.out.println(getName() + ": 挂了电话");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
