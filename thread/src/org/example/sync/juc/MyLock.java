package org.example.sync.juc;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class MyLock implements Lock {

    private MySync mySync;

    public MyLock() {
        mySync = new MySync();
    }

    /**
     * AQS = 一个队列 + state
     */
    private static class MySync extends AbstractQueuedSynchronizer {
        /**
         * 加锁逻辑
         */
        public void lock() {
            // 尝试修改 state 从 0 改为 1
            if (compareAndSetState(0, 1)) {
                System.err.println(Thread.currentThread().getName() + " 加锁成功");
                setExclusiveOwnerThread(Thread.currentThread());
            } else {
                // 尝试获取锁
                // arg = 1：表示同时只允许有一个线程进入同步代码块
                // acquire 会先 tryAcquire，如果尝试获取锁失败，就会将线程入队
                System.err.println(Thread.currentThread().getName() + " 尝试获取锁");
                acquire(1);
            }
        }

        /**
         * 尝试获取锁
         * 获取锁失败的话，会入队
         */
        @Override
        protected boolean tryAcquire(int arg) {
            Thread thread = Thread.currentThread();
            int state = getState();
            // 无锁的情况
            if (state == 0) {
                if (compareAndSetState(0, 1)) {
                    System.err.println(Thread.currentThread().getName() + " 获取锁成功");
                    setExclusiveOwnerThread(Thread.currentThread());
                    return true;
                }
            }
            // 重入锁的情况
            else if (thread == getExclusiveOwnerThread()) {
                int next = state + arg;
                if (next < 0) {
                    throw new IllegalStateException("Invalid arg: " + arg);
                }
                System.err.println(Thread.currentThread().getName() + " 重入锁，加锁后的 state = " + state);
                setState(next);
                return true;
            }
            System.err.println(Thread.currentThread().getName() + " 获取锁失败");
            return false;
        }

        @Override
        protected boolean tryRelease(int arg) {
            System.err.println(Thread.currentThread().getName() + " 尝试解锁");
            if (Thread.currentThread() != getExclusiveOwnerThread()) {
                throw new IllegalMonitorStateException("非法释放锁");
            }
            int state = getState();
            state = state - arg;
            // 释放锁成功
            if (state == 0) {
                System.err.println(Thread.currentThread().getName() + " 解锁成功");
                setExclusiveOwnerThread(null);
                setState(0);
                return true;
            }
            // 重入锁的情况，将 state 减少后，重新设置回 state
            System.err.println(Thread.currentThread().getName() + " 重入锁，解锁后的 state = " + state);
            setState(state);
            return false;
        }
    }

    /**
     * 加锁入口
     */
    @Override
    public void lock() {
        mySync.lock();
    }

    /**
     * 解锁入口
     */
    @Override
    public void unlock() {
        mySync.release(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }


    @Override
    public Condition newCondition() {
        return null;
    }
}
