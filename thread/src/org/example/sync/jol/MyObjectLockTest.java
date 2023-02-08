package org.example.sync.jol;

import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.vm.VM;

import java.util.concurrent.TimeUnit;

/**
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

public class MyObjectLockTest {
    static MyObject myObject;

    public static void main(String[] args) throws Exception {
        myObject = new MyObject();

        // 打印虚拟机详细信息
        System.err.println(VM.current().details());

        // noneLock();
        // biasedLock();
        // thinLock();
        fatLock();

    }

    /**
     * 无锁的证明
     */
    public static void noneLock() {
        System.out.println("===== Hash 之前 =====");
        System.out.println(ClassLayout.parseInstance(myObject).toPrintable());

        System.out.println("===== 开始 Hash =====");
        System.out.println("Hash Code: " + Integer.toHexString(myObject.hashCode()));
        System.out.println();

        System.out.println("===== Hash 之后 =====");
        System.out.println(ClassLayout.parseInstance(myObject).toPrintable());
    }

    /**
     * 偏向锁的证明
     * JDK 8，需要休眠 4s 后在创建，因为偏向锁的启用，需要在虚拟机启动后 4 秒才开启偏向
     * -XX:+UseBiasedLocking，默认开启偏向锁
     * -XX:BiasedLockingStartupDelay=0，控制偏向锁延迟启动时间
     * 可以在 globals.hpp 中查看
     */
    public static void biasedLock() throws Exception {
        TimeUnit.SECONDS.sleep(4);
        System.out.println("===== 加锁之前 =====");
        System.out.println(ClassLayout.parseInstance(myObject).toPrintable());
        synchronized (myObject) {
            System.out.println("===== 加锁中 =====");
            System.out.println(ClassLayout.parseInstance(myObject).toPrintable());
        }
        System.out.println("===== 加锁之后 =====");
        System.out.println(ClassLayout.parseInstance(myObject).toPrintable());
    }

    /**
     * 轻量级锁的证明
     */
    public static void thinLock() throws Exception {
        System.out.println("===== 加锁之前 =====");
        System.out.println(ClassLayout.parseInstance(myObject).toPrintable());

        synchronized (myObject) {
            System.out.println("===== main 加锁中 =====");
            System.out.println(ClassLayout.parseInstance(myObject).toPrintable());
        }

        Thread thread = new Thread(() -> {
            synchronized (myObject) {
                System.out.println("===== t1 加锁中 =====");
                System.out.println(ClassLayout.parseInstance(myObject).toPrintable());
            }
        });
        thread.start();
        thread.join();

        // 稍等一会，等待撤销锁
        TimeUnit.SECONDS.sleep(2);
        System.out.println("===== 加锁之后 =====");
        System.out.println(ClassLayout.parseInstance(myObject).toPrintable());
    }

    /**
     * 重量级锁
     */
    public static void fatLock() throws Exception {
        System.out.println("===== 加锁之前 =====");
        System.out.println(ClassLayout.parseInstance(myObject).toPrintable());

        new Thread(()->{
            synchronized (myObject) {
                try {
                    TimeUnit.SECONDS.sleep(4);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "t1").start();

        System.out.println("===== t1 加锁中 =====");
        System.out.println(ClassLayout.parseInstance(myObject).toPrintable());

        // 发生锁资源争夺，由于 t1 执行同步代码块时间较长，所以这里会入队，然后 park，等待 unpark
        synchronized (myObject) {
            System.out.println("===== main 加锁中 =====");
            System.out.println(ClassLayout.parseInstance(myObject).toPrintable());
        }

        // 稍等一会，等待撤销锁
        TimeUnit.SECONDS.sleep(2);
        System.out.println("===== 加锁之后 =====");
        System.out.println(ClassLayout.parseInstance(myObject).toPrintable());
    }
}
