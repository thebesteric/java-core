package org.example.thread_design_pattern.read_write_lock;

public class MyReadWriteLock {
    /** 正在读读线程数 */
    private int readingThreads = 0;
    /** 正在写读线程数 */
    private int writingThreads = 0;
    /** 优先写（防止读线程太多，写线程抢不到资源） */
    private boolean preferWrite = true;
    /** 准备挂起的写线程（配合优先写，防止没有写线程，只有读线程） */
    private int readyToWaitingWriters = 0;

    /** 读锁-加锁 */
    public synchronized void readLock() throws InterruptedException {
        // 只要有写线程，就不能读
        while (writingThreads > 0 || (preferWrite && readyToWaitingWriters > 0)) {
            this.wait();
        }
        readingThreads++;
    }

    /** 读锁-解锁 */
    public synchronized void readUnlock() {
        readingThreads--;
        preferWrite = true;
        notifyAll();
    }

    /** 写锁-加锁 */
    public synchronized void writeLock() throws InterruptedException {
        readyToWaitingWriters++;
        try {
            // 只要有写线程或者读线程，都不能写
            while (writingThreads > 0 || readingThreads > 0) {
                this.wait();
            }
        } finally {
            readyToWaitingWriters--;
        }
        writingThreads++;
    }

    /** 写锁-解锁 */
    public synchronized void writeUnlock() {
        writingThreads--;
        preferWrite = false;
        notifyAll();
    }
}
