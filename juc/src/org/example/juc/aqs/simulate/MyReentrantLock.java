package org.example.juc.aqs.simulate;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class MyReentrantLock implements Lock {

    private final MySync mySync;

    public MyReentrantLock() {
        mySync = new MyNoFairSync();
    }

    public MyReentrantLock(boolean fair) {
        if (fair) {
            mySync = new MyFairSync();
        } else {
            mySync = new MyNoFairSync();
        }
    }

    /**
     * MyFairSync   ---> MySync ---> MyAbstractQueuedSynchronized
     * MyNoFairSync ---> MySync ---> MyAbstractQueuedSynchronized
     */
    abstract static class MySync extends MyAbstractQueuedSynchronized {
        abstract void lock();

        /**
         * 释放锁：公平和非公平统一使用这个方法
         */
        @Override
        protected boolean tryRelease(int arg) {
            int state = getState() - arg;
            if (Thread.currentThread() != getOwner()) {
                throw new IllegalMonitorStateException("非法进入");
            }
            boolean free = false;
            if (state == 0) {
                free = true;
                setOwner(null);
            }
            setState(state); // 可能是 0，也可能是重入 state - 1
            return free;
        }
    }

    /**
     * 公平锁（可以重入）
     */
    static class MyFairSync extends MySync {
        /**
         * 加锁入口
         */
        @Override
        void lock() {
            // state = 1，有两个功能
            // 0->1 表示首次加锁
            // 1->N 表示重入
            super.acquire(1);
        }

        @Override
        protected boolean tryAcquire(int arg) {
            Thread currentThread = Thread.currentThread();
            int state = getState();
            // 当前还没有线程获取到锁，所有线程一起抢锁
            if (state == 0) {
                // 检查队列，是有有线程在排队
                if (!hasQueuePred() && compareAndSetState(0, arg)) {
                    setOwner(currentThread);
                    return true;
                }
                // 队列中有线程在排队，或者 cas 失败
                else {
                    addNode(Node.EXCLUSIVE);
                    return false;
                }
            }
            // 判断当前线程是否已经拥有锁，是否是重入
            else if (currentThread == getOwner()) {
                int newState = state + arg;
                if (newState < 0) {
                    throw new IllegalArgumentException("Invalid state");
                }
                setState(newState);
                return true;
            }

            return false;
        }
    }

    /**
     * 非公平锁（可以重入）
     */
    static class MyNoFairSync extends MySync {
        @Override
        void lock() {
            // 非公平的体现，一开始就尝试获取锁
            if (compareAndSetState(0, 1)) {
                setOwner(Thread.currentThread());
            } else {
                super.acquire(1);
            }
        }

        @Override
        protected boolean tryAcquire(int arg) {
            Thread currentThread = Thread.currentThread();
            int state = getState();
            // 当前还没有线程获取到锁，所有线程一起抢锁
            if (state == 0) {
                // 尝试修改
                if (compareAndSetState(0, arg)) {
                    setOwner(currentThread);
                    return true;
                }
                // 队列中有线程在排队，或者 cas 失败
                else {
                    addNode(Node.EXCLUSIVE);
                    return false;
                }
            }
            // 判断当前线程是否已经拥有锁，是否是重入
            else if (currentThread == getOwner()) {
                int newState = state + arg;
                if (newState < 0) {
                    throw new IllegalArgumentException("Invalid state");
                }
                setState(newState);
                return true;
            }

            return false;
        }
    }

    /**
     * 加锁
     */
    @Override
    public void lock() {
        // 公平锁/非公平锁
        mySync.lock();
    }

    /**
     * 释放锁
     */
    @Override
    public void unlock() {
        // 释放锁流程
        // 1、state N->0；owner -> null
        // 2、唤醒 head 节点的后继节点
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
