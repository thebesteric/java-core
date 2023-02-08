package org.example.thread_design_pattern.read_write_lock;

public class MyReadWriteLock {
    /** 正在读读线程数 */
    private int readingThreads = 0;
    /** 正在写读线程数 */
    private int writingThreads = 0;

    /** 读锁-加锁 */
    public synchronized void readLock() throws InterruptedException {
        while (writingThreads > 0) {
            this.wait();
        }
        readingThreads++;
    }

    /** 读锁-解锁 */
    public synchronized void readUnlock() {
        readingThreads--;
        notifyAll();
    }

    /** 写锁-加锁 */
    public synchronized void writeLock() throws InterruptedException {
        while (writingThreads > 0 || readingThreads > 0) {
            this.wait();
        }
        writingThreads++;
    }

    /** 写锁-解锁 */
    public synchronized void writeUnlock() {
        writingThreads--;
        notifyAll();
    }
}
