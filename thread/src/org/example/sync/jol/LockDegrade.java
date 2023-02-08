package org.example.sync.jol;

import org.openjdk.jol.info.ClassLayout;

import java.util.concurrent.TimeUnit;

public class LockDegrade {

    public static MyObject myObject = new MyObject();

    public static void main(String[] args) throws InterruptedException {

        TimeUnit.SECONDS.sleep(5);

        System.out.println("===== 加锁前（偏向锁） =====");
        System.out.println(ClassLayout.parseInstance(myObject).toPrintable());

        Thread t1 = new Thread(() -> {
            synchronized (myObject) {
                System.out.println("===== t1 加锁中（偏向锁） =====");
                System.out.println(ClassLayout.parseInstance(myObject).toPrintable());
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "t1");

        Thread t2 = new Thread(() -> {
            synchronized (myObject) {
                System.out.println("===== t2 加锁中（偏向失败，出现竞争，自旋获取失败，升级为重量级锁） =====");
                System.out.println(ClassLayout.parseInstance(myObject).toPrintable());
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "t2");

        t1.start();
        TimeUnit.SECONDS.sleep(1);
        t2.start();

        t1.join();
        t2.join();

        TimeUnit.SECONDS.sleep(2);
        System.out.println("===== 加锁后 =====");
        System.out.println(ClassLayout.parseInstance(myObject).toPrintable());

        Thread t3 = new Thread(() -> {
            synchronized (myObject) {
                System.out.println("===== t3 加锁中（从无锁升级到轻量级锁） =====");
                System.out.println(ClassLayout.parseInstance(myObject).toPrintable());
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "t3");
        t3.start();
    }

}
