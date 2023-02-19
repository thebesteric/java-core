package org.example.thread.sync.lockupgrade;

import lombok.Data;

/**
 * 对象头
 * |----------------------------------------------------------------------------------|--------------------|
 * |                  Mark Word (64 bits)                                             |       State        |
 * |----------------------------------------------------------------------------------|--------------------|
 * |  unused:25 | identity_hashcode:25 | unused:1 | age:4 | biased_lock:1 | lock:2:01 |       Normal       |
 * |----------------------------------------------------------------------------------|--------------------|
 * |  thread:54 |       epoch:2        | unused:1 | age:4 | biased_lock:1 | lock:2:01 |       Biased       |
 * |----------------------------------------------------------------------------------|--------------------|
 * |                       ptr_to_lock_record:62                          | lock:2:00 | Lightweight Locked |
 * |----------------------------------------------------------------------------------|--------------------|
 * |                       ptr_to_heavyweight_monitor:62                  | lock:2:10 | Heavyweight Locked |
 * |----------------------------------------------------------------------------------|--------------------|
 * |                                                                      | lock:2:11 |    Marked for GC   |
 * |----------------------------------------------------------------------------------|--------------------|
 */
@Data
public class MarkWord implements Cloneable{
    /** 锁标记 */
    private LockFlag lockFlag;
    /** 偏向锁标记 */
    private int biasedFlag = 0;
    /** epoch */
    private String epoch;
    /** 分代年龄 */
    private int age;
    /** 线程 ID */
    private volatile long threadId = -1L;
    /** 指向轻量级锁*/
    private volatile LockRecord ptrLockRecord;
    /** 指向重量级锁 */
    private ObjectMonitor ptrMonitor;
    /** 锁的膨胀状态 */
    private volatile LockInflateStatus lockInflateStatus;

    public MarkWord() {
        this.lockFlag  = LockFlag.NEUTRAL;
        this.lockInflateStatus  = LockInflateStatus.NEUTRAL;
        this.ptrMonitor = new ObjectMonitor();
    }

    public void setLockBias(long threadId) {
        this.lockFlag = LockFlag.BIASED;
        this.biasedFlag = 1;
        this.threadId = threadId;
        this.ptrMonitor.setOwner(null);
        this.ptrLockRecord = null;
    }

    public void setLockNone() {
        this.lockFlag = LockFlag.NEUTRAL;
        this.lockInflateStatus = LockInflateStatus.NEUTRAL;
        this.ptrMonitor.setOwner(null);
        this.ptrLockRecord = null;
        this.biasedFlag = 0;
        this.threadId = -1L;
    }

    public void setLockLight(LockRecord lockRecord, MarkWord head, MarkWord owner) {
        this.lockFlag = LockFlag.LIGHT;
        this.biasedFlag = 0;
        this.threadId = -1L;
        try {
            lockRecord.setHead((MarkWord) head.clone());
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        lockRecord.setOwner(owner);
    }

    public void setLockHeavy(ObjectMonitor objectMonitor) {
        this.lockFlag = LockFlag.HEAVY;
        this.ptrMonitor = objectMonitor;
        this.ptrLockRecord = null;
        this.biasedFlag = 0;
        this.threadId = -1L;
    }

    public boolean isLockNone() {
        return this.lockFlag == LockFlag.NEUTRAL && this.lockInflateStatus == LockInflateStatus.NEUTRAL && this.biasedFlag == 0;
    }

    public boolean isLockBiased() {
        return this.lockFlag == LockFlag.BIASED && this.biasedFlag == 1 && threadId != -1L;
    }

    public boolean isLockLight() {
        return this.lockFlag == LockFlag.LIGHT && this.ptrLockRecord != null;
    }

    public boolean isLockHeavy() {
        return this.lockFlag == LockFlag.HEAVY && this.ptrMonitor != null;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * 锁标记
     */
    public enum LockFlag {
        NEUTRAL("01"), BIASED("01"), LIGHT("00"), HEAVY("10");

        private final String flag;

        LockFlag(String flag) {
            this.flag = flag;
        }

        public String getFlag() {
            return flag;
        }
    }

    /**
     * 锁的膨胀状态
     */
    public enum LockInflateStatus {
        NEUTRAL("无锁"),
        INFLATED("已膨胀"),
        INFLATING("膨胀中");

        private final String desc;

        LockInflateStatus(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }
    }
}
