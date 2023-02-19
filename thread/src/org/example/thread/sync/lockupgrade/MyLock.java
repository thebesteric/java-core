package org.example.thread.sync.lockupgrade;

import lombok.Data;

/**
 * 自定义锁对象
 */
@Data
public class MyLock {
    private MarkWord markWord;

    private BiasedLocking biasedLocking;

    public MyLock() {
        this.markWord = new MarkWord();
        this.biasedLocking = new BiasedLocking();
    }
}
