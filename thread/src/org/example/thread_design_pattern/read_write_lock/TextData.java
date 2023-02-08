package org.example.thread_design_pattern.read_write_lock;

import java.util.Arrays;

public class TextData {
    private final char[] buffer;

    private final MyReadWriteLock lock;

    public TextData(int size) {
        this.buffer = new char[size];
        this.lock = new MyReadWriteLock();
        Arrays.fill(this.buffer, '*');
    }

    public char[] read() throws InterruptedException {
        // 加读锁
        lock.readLock();
        try {
            return doRead();
        } finally {
            // 解读锁
            lock.readUnlock();
        }
    }

    public synchronized void write(char c) throws InterruptedException {
        // 加写锁
        lock.writeLock();
        try {
            doWrite(c);
        } finally {
            // 解写锁
            lock.writeUnlock();
        }
    }

    private char[] doRead() {
        char[] newChars = new char[buffer.length];
        System.arraycopy(buffer, 0, newChars, 0, buffer.length);
        return newChars;

    }

    private void doWrite(char c) {
        Arrays.fill(buffer, c);
    }
}
