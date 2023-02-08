package org.example.sync.lockupgrade;

import org.example.UnsafeUtils;

import java.lang.reflect.Field;

/**
 * 偏向锁
 */
public class BiasedLocking {

    public boolean revokeAndRebias(MyLock myLock) {
        MarkWord markWord = myLock.getMarkWord();
        // 获取偏向锁标记
        int biasedFlag = markWord.getBiasedFlag();
        // 获取锁标记
        MarkWord.LockFlag lockFlag = markWord.getLockFlag();
        try {
            Field threadIdField = markWord.getClass().getDeclaredField("threadId");
            long offset = UnsafeUtils.unsafe.objectFieldOffset(threadIdField);
            long threadIdInMarkWork = UnsafeUtils.unsafe.getLongVolatile(markWord, offset);
            long currThreadId = Thread.currentThread().getId();
            // 可以偏向，但是还没有偏向任何线程
            if (threadIdInMarkWork == -1L && biasedFlag == 0 && MarkWord.LockFlag.NEUTRAL == lockFlag) {
                // 执行 CAS 操作，将当前线程 ID 写入 markWork
                boolean isSucceed = UnsafeUtils.unsafe.compareAndSwapLong(markWord, offset, threadIdInMarkWork, currThreadId);
                if (isSucceed) {
                    markWord.setLockBias(currThreadId);
                    System.err.println(Thread.currentThread().getName() + " 获取到偏向锁");
                    return true;
                }
            }
            // 可偏向，并且已经偏向某个线程
            else if (threadIdInMarkWork != -1L && biasedFlag == 1 && MarkWord.LockFlag.BIASED == lockFlag) {
                System.err.println(Thread.currentThread().getName() + " 111");
                // 判断偏向的线程是否是当前线程
                if (threadIdInMarkWork == currThreadId) {
                    System.err.println(Thread.currentThread().getName() + " 获取到偏向锁（重入）");
                    return true;
                }
                // 不一致，撤销偏向锁
                else {
                    // 撤销偏向锁
                    revokeBiased(myLock);
                    System.err.println(Thread.currentThread().getName() + " 获取到偏向锁失败（可偏向）");
                    return false;
                }
            }
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        System.err.println(Thread.currentThread().getName() + " 获取到偏向锁失败");
        return false;
    }

    /**
     * 撤销偏向锁
     */
    private boolean revokeBiased(MyLock myLock) {
        // 其他线程也要来抢锁，此时撤销的动作是由其他线程来操作的，所以撤销操作不是由拥有偏向锁的线程（thread-01）来操作的
        // 所以需要判断拥有偏向锁的线程是否已经离开同步代码块（需要在安全点撤销，Java 无法实现）
        // 如果 thread-01 已经退出了，则将 markWord 撤销到无锁的状态
        // 如果 thread-01 没有退出，则将 markWord 升级到轻量级锁状态
        MarkWord markWord = myLock.getMarkWord();
        boolean isAlive = isAlive(myLock);
        // 线程存活：这里有问题，因为无发判断线程是否已经执行
        if (isAlive) {
            // 设置为无锁状态
            markWord.setLockNone();
            return true;
        }
        // 准备升级到轻量级锁
        return false;
    }

    /**
     * 判断拥有偏向锁的线程是否还存活
     */
    public boolean isAlive(MyLock myLock) {
        boolean isAlive = false;
        long threadIdInMarkWord = myLock.getMarkWord().getThreadId();
        // 获取当前线程的线程组
        ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
        int activeCount = threadGroup.activeCount();
        Thread[] activeThreads = new Thread[activeCount];
        threadGroup.enumerate(activeThreads);
        for (Thread activeThread : activeThreads) {
            if (activeThread != null && threadIdInMarkWord == activeThread.getId()) {
                // 表示当前拥有锁的线程还存活
                isAlive = true;
                break;
            }
        }
        return isAlive;
    }

}
