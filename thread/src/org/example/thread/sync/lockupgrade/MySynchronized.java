package org.example.thread.sync.lockupgrade;

import lombok.Data;
import org.example.thread.UnsafeUtils;

import java.lang.reflect.Field;

/**
 * interpreterRuntime.cpp#monitorenter
 */
@Data
public class MySynchronized {
    private MyLock myLock;

    private boolean usedBiasedLocking;

    public MySynchronized(MyLock myLock) {
        this.myLock = myLock;
        this.usedBiasedLocking = true;
    }

    public MySynchronized(MyLock myLock, boolean useBiasedLocking) {
        this(myLock);
        this.usedBiasedLocking = useBiasedLocking;
    }

    /**
     * 用 ThreadLocal 模拟栈帧中开辟的 LockRecord 空间
     */
    private ThreadLocal<LockRecord> threadLocal = ThreadLocal.withInitial(() -> new LockRecord(null, null));

    /**
     * 锁入口
     * 无锁->偏向锁->轻量级锁->重量级锁
     */
    public void monitorEnter() {
        // 是否开启偏向锁
        if (usedBiasedLocking) {
            fastEnter();
        }
        // 走轻量级锁
        else {
            slowEnter();
        }
    }

    /**
     * 锁出口
     */
    public void monitorExit() {
        if (myLock != null) {
            MarkWord markWord = myLock.getMarkWord();
            long threadId = markWord.getThreadId();
            long currThreadId = Thread.currentThread().getId();
            // 是否是偏向锁
            if (markWord.isLockBiased()) {
                if (threadId != currThreadId) {
                    throw new RuntimeException("非法释放锁");
                }
                // 释放偏向锁
                System.err.println(Thread.currentThread().getName() + " 释放了偏向锁");
                return;
            }
            // 轻量级锁和重量级锁的释放
            else {
                slowExit();
                // System.err.println(Thread.currentThread().getName() + " 解锁");
                // // 设置为无锁状态
                // markWord.setLockNone();
                // ObjectWait objectWait = markWord.getPtrMonitor().getCxq().poll();
                // if (objectWait != null) {
                //     System.err.println(Thread.currentThread().getName() + " 唤醒 " + objectWait.getThread().getName());
                //     UnsafeUtils.unsafe.unpark(objectWait.getThread());
                // }
            }

        } else {
            throw new RuntimeException("monitorExit must after monitorEnter");
        }
    }

    private void slowExit() {
        fastExit();
    }

    /**
     * 轻量级锁和重量级锁的释放
     */
    private void fastExit() {
        LockRecord lockRecord = threadLocal.get();
        MarkWord head = lockRecord.getHead();
        // 轻量级锁的释放：需要将 markWord 还原，并改为无锁状态
        if (head != null && lockRecord == myLock.getMarkWord().getPtrLockRecord()) {
            try {
                Field markWordField = myLock.getClass().getDeclaredField("markWord");
                long offset = UnsafeUtils.unsafe.objectFieldOffset(markWordField);
                Object expected = UnsafeUtils.unsafe.getObjectVolatile(myLock, offset);
                // 当前栈帧中的 markWord 还原到对象头中
                // 为什么撤销轻量级锁需要 CAS 来撤销，而且会撤销失败
                // 如：t1 获取来轻量级锁，markWord 指向 t1 所在的栈帧，此时 t2 也来请求锁，此时 t2 会不到锁
                // 那么就会膨胀成重量级锁，就把 markWord 更新为 ObjectMonitor 指针，
                // 此时 t1 退出的时候，准备将 markWord 还原，那么此时就会失败，t1 只能膨胀为重量级锁，再退出
                boolean isSucceed = UnsafeUtils.unsafe.compareAndSwapObject(myLock, offset, expected, head);
                if (isSucceed) {
                    lockRecord.setHead(null);
                    lockRecord.setOwner(null);
                    myLock.getMarkWord().setLockNone();
                    return;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        // 重量级锁的释放：CAS 修改失败，轻量级锁膨胀
        inflateExit();
    }

    /**
     * 偏向锁加锁
     */
    public void fastEnter() {
        BiasedLocking biasedLocking = myLock.getBiasedLocking();
        // 是否获取到偏向锁
        boolean isBiased = biasedLocking.revokeAndRebias(myLock);
        // 没有偏向成功
        if (!isBiased) {
            // 执行轻量级锁
            slowEnter();
        }
    }

    /**
     * 轻量级锁加锁
     */
    private void slowEnter() {
        // 如果是无锁
        MarkWord markWord = myLock.getMarkWord();
        // 如果是【无锁】或【偏向锁】的情况
        if ((markWord.isLockNone() || markWord.isLockBiased()) && !myLock.getBiasedLocking().isAlive(myLock)) {
            markWord.setThreadId(-1L);
            markWord.setBiasedFlag(0);
            // CAS 变更 LockRecord 指针
            try {
                Field ptrLockRecord = markWord.getClass().getDeclaredField("ptrLockRecord");
                long offset = UnsafeUtils.unsafe.objectFieldOffset(ptrLockRecord);
                Object currentLockRecord = UnsafeUtils.unsafe.getObjectVolatile(markWord, offset);
                // 获取当前线程的 lockRecord
                LockRecord currThreadLockRecord = threadLocal.get();
                boolean isSucceed = UnsafeUtils.unsafe.compareAndSwapObject(markWord, offset, currentLockRecord, currThreadLockRecord);
                if (isSucceed) {
                    // 设置为轻量级锁
                    markWord.setLockLight(currThreadLockRecord, markWord, markWord);
                    System.err.println(Thread.currentThread().getName() + " 获取到轻量级锁");
                    return;
                }
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
        // 已经是轻量级锁状态，检查是否是重入锁
        else if (markWord.isLockLight()) {
            markWord.setThreadId(-1L);
            markWord.setBiasedFlag(0);
            // 获取当前线程的 lockRecord
            LockRecord currThreadLockRecord = threadLocal.get();
            // 获取 markWork 中的 lockRecord
            LockRecord markWordLockRecord = markWord.getPtrLockRecord();
            // 表示是重入锁
            if (markWordLockRecord != null && currThreadLockRecord == markWordLockRecord) {
                return;
            }
        }
        // 锁膨胀
        inflateEnter();
    }

    /**
     * 加锁膨胀过程
     */
    private void inflateEnter() {
        // 具体的膨胀过程
        ObjectMonitor objectMonitor = inflate();
        objectMonitor.enter(myLock, threadLocal);
    }

    /**
     * 解锁膨胀过程
     */
    private void inflateExit() {
        // 具体的膨胀过程
        ObjectMonitor objectMonitor = inflate();
        objectMonitor.exit(myLock, threadLocal);
    }

    /**
     * 锁膨胀过程，ObjectMonitor 可以理解为重量级锁
     */
    private ObjectMonitor inflate() {
        for (; ; ) {
            MarkWord markWord = myLock.getMarkWord();
            // 1、是否是重量级锁
            if (markWord.isLockHeavy() || markWord.getPtrMonitor().getOwner() !=null) {
                return markWord.getPtrMonitor();
            }
            // 2、是否正在膨胀
            MarkWord.LockInflateStatus lockInflateStatus = markWord.getLockInflateStatus();
            if (MarkWord.LockInflateStatus.INFLATING == lockInflateStatus) {
                continue;
            }
            // 3、CAS 修改锁膨胀状态为正在膨胀：INFLATING
            if (markWord.isLockLight() || markWord.isLockBiased()) {
                try {
                    Field lockInflateStatusField = markWord.getClass().getDeclaredField("lockInflateStatus");
                    long offset = UnsafeUtils.unsafe.objectFieldOffset(lockInflateStatusField);
                    Object currLockInflateStatus = UnsafeUtils.unsafe.getObjectVolatile(markWord, offset);
                    boolean isSucceed = UnsafeUtils.unsafe.compareAndSwapObject(markWord, offset, currLockInflateStatus, MarkWord.LockInflateStatus.INFLATING);
                    // 修改失败
                    if (!isSucceed) {
                        continue;
                    }
                    // 修改成功
                    else {
                        ObjectMonitor objectMonitor = new ObjectMonitor();
                        markWord.setLockHeavy(objectMonitor);
                        return objectMonitor;
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }
}
