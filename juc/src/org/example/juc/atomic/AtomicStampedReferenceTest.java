package org.example.juc.atomic;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.atomic.AtomicStampedReference;

public class AtomicStampedReferenceTest {

    public static void main(String[] args) throws InterruptedException {

        noABA();
        testObjectNoABA();

    }

    public static void noABA() throws InterruptedException {
        AtomicStampedReference<Integer> i1 = new AtomicStampedReference<>(0, 0);

        Thread t3 = new Thread(() -> {
            int curVersion = i1.getStamp();
            System.out.println(Thread.currentThread().getName() + " 初始 ret = " + i1.getReference() + " version = " + i1.getStamp());

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            boolean success = i1.compareAndSet(0, 1, curVersion, curVersion + 1);
            System.out.println(Thread.currentThread().getName() + " 第一次 ret = " + i1.getReference() + ", version = " + i1.getStamp() + ", 修改" + (success ? "成功" : "失败"));

            curVersion = i1.getStamp();
            success = i1.compareAndSet(1, 0, curVersion, curVersion + 1);
            System.out.println(Thread.currentThread().getName() + " 第二次 ret = " + i1.getReference() + ", version = " + i1.getStamp() + ", 修改" + (success ? "成功" : "失败"));
        }, "t3");

        Thread t4 = new Thread(() -> {
            int curVersion = i1.getStamp();
            System.out.println(Thread.currentThread().getName() + " 初始 ret = " + i1.getReference() + ", version = " + i1.getStamp());

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            boolean success = i1.compareAndSet(0, 2, curVersion, curVersion + 1);
            System.out.println(Thread.currentThread().getName() + " 第一次 ret = " + i1.getReference() + ", version = " + i1.getStamp() + ", 修改" + (success ? "成功" : "失败"));
            if (!success) {
                System.out.println(Thread.currentThread().getName() + " 期望 version = " + curVersion + ", 实际 version = " + i1.getStamp());
            }
        }, "t4");

        t3.start();
        t4.start();

        t3.join();
        t4.join();
    }

    private static void testObjectNoABA() throws InterruptedException {
        User user1 = new User("1", "eric");
        User user2 = new User("2", "lucy");
        User user3 = new User("3", "lili");

        AtomicStampedReference<User> atomic = new AtomicStampedReference<>(user1, 0);

        Thread t3 = new Thread(() -> {
            int curVersion = atomic.getStamp();
            System.out.println(Thread.currentThread().getName() + " 初始 ret = " + atomic.getReference() + " version = " + atomic.getStamp());

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            boolean success = atomic.compareAndSet(user1, user2, curVersion, curVersion + 1);
            System.out.println(Thread.currentThread().getName() + " 第一次 ret = " + atomic.getReference() + ", version = " + atomic.getStamp() + ", 修改" + (success ? "成功" : "失败"));

            curVersion = atomic.getStamp();
            success = atomic.compareAndSet(user2, user1, curVersion, curVersion + 1);
            System.out.println(Thread.currentThread().getName() + " 第二次 ret = " + atomic.getReference() + ", version = " + atomic.getStamp() + ", 修改" + (success ? "成功" : "失败"));
        }, "t3");

        Thread t4 = new Thread(() -> {
            int curVersion = atomic.getStamp();
            System.out.println(Thread.currentThread().getName() + " 初始 ret = " + atomic.getReference() + ", version = " + atomic.getStamp());

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            boolean success = atomic.compareAndSet(user1, user3, curVersion, curVersion + 1);
            System.out.println(Thread.currentThread().getName() + " 第一次 ret = " + atomic.getReference() + ", version = " + atomic.getStamp() + ", 修改" + (success ? "成功" : "失败"));
            if (!success) {
                System.out.println(Thread.currentThread().getName() + " 期望 version = " + curVersion + ", 实际 version = " + atomic.getStamp());
            }
        }, "t4");

        t3.start();
        t4.start();

        t3.join();
        t4.join();
    }

    @AllArgsConstructor
    @Data
    static class User {
        private String id;
        private String name;
    }

}
