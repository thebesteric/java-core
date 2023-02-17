package org.example.thread_design_pattern.two_phase_terminate;

public class TwoPhaseTerminateTest {
    public static void main(String[] args) throws InterruptedException {

        Worker worker = new Worker("worker");
        worker.start();

        new Wife("妻子", worker).start();

        worker.join();
    }
}
