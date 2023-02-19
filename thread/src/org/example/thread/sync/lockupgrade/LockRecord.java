package org.example.thread.sync.lockupgrade;

import lombok.Data;

@Data
public class LockRecord {
    /** MarkWord 的拷贝 */
    private MarkWord head;
    /** 当前线程的栈帧 */
    private MarkWord owner;

    public LockRecord(MarkWord head, MarkWord owner) {
        this.head = head;
        this.owner = owner;
    }
}
