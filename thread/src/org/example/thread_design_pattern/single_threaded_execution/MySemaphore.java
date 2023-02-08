package org.example.thread_design_pattern.single_threaded_execution;

public class MySemaphore {
    private int count;

    public MySemaphore(int count) {
        this.count = count;
    }

    public void acquire() throws InterruptedException {
        while (true) {
            synchronized (this) {
                if (this.count > 0) {
                    this.count--;
                    break;
                }
                this.wait();
            }
        }
    }

    public void release() {
        synchronized (this) {
            this.count++;
            this.notifyAll();
        }
    }
}
