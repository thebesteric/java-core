package org.example.thread.thread_design_pattern.read_write_lock.database;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MyDatabase<K, V> {
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
    private final Lock writeLock = readWriteLock.writeLock();
    private final Lock readLock = readWriteLock.readLock();

    private final Map<K, V> data = new HashMap<K, V>();

    public V get(K key) throws InterruptedException {
        readLock.lock();
        try {
            Thread.sleep(50);
            return data.get(key);
        } finally {
            readLock.unlock();
        }
    }

    public void set(K key, V value) throws InterruptedException {
        writeLock.lock();
        try {
            Thread.sleep(500);
            data.put(key, value);
        } finally {
            writeLock.unlock();
        }
    }
}
