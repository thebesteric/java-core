package org.example.sync.lockupgrade;

import lombok.Data;

/**
 * ObjectWaiter 表示一个等待获取 ObjectMonitor 锁的线程
 */
@Data
public class ObjectWait {
    private Thread thread;

    public ObjectWait(Thread thread) {
        this.thread = thread;
    }
}
