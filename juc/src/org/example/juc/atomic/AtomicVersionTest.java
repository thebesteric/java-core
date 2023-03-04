package org.example.juc.atomic;

import lombok.AllArgsConstructor;
import lombok.Data;

public class AtomicVersionTest {

    public static void main(String[] args) throws InterruptedException {

        hasABA();
        // noABA();

        // testObject();
        // testObjectHasABA();
        // testObjectNoABA();

    }

    public static void hasABA() throws InterruptedException {
        AtomicVersion<Integer> i = new AtomicVersion<>(0, 0);

        Thread t1 = new Thread(() -> {
            // 触发 ABA 问题
            boolean success = i.compareAndSet(0, 1);
            System.out.println(Thread.currentThread().getName() + " ret = " + i.getValue() + ", 修改" + (success ? "成功" : "失败"));
            success = i.compareAndSet(1, 0);
            System.out.println(Thread.currentThread().getName() + " ret = " + i.getValue() + ", 修改" + (success ? "成功" : "失败"));
        }, "t1");

        Thread t2 = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            boolean success = i.compareAndSet(0, 2);
            System.out.println(Thread.currentThread().getName() + " ret = " + i.getValue() + ", 修改" + (success ? "成功" : "失败"));
        }, "t2");

        t1.start();
        t2.start();

        t1.join();
        t2.join();
    }

    public static void noABA() throws InterruptedException {
        AtomicVersion<Integer> i1 = new AtomicVersion<>(0, 0);

        Thread t3 = new Thread(() -> {
            int curVersion = i1.getVersion();
            System.out.println(Thread.currentThread().getName() + " 初始 ret = " + i1.getValue() + " version = " + i1.getVersion());

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            boolean success = i1.compareAndSet(0, curVersion, 1, curVersion + 1);
            System.out.println(Thread.currentThread().getName() + " 第一次 ret = " + i1.getValue() + ", version = " + i1.getVersion() + ", 修改" + (success ? "成功" : "失败"));

            curVersion = i1.getVersion();
            success = i1.compareAndSet(1, curVersion, 0, curVersion + 1);
            System.out.println(Thread.currentThread().getName() + " 第二次 ret = " + i1.getValue() + ", version = " + i1.getVersion() + ", 修改" + (success ? "成功" : "失败"));
        }, "t3");

        Thread t4 = new Thread(() -> {
            int curVersion = i1.getVersion();
            System.out.println(Thread.currentThread().getName() + " 初始 ret = " + i1.getValue() + ", version = " + i1.getVersion());

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            boolean success = i1.compareAndSet(0, curVersion, 2, curVersion + 1);
            System.out.println(Thread.currentThread().getName() + " 第一次 ret = " + i1.getValue() + ", version = " + i1.getVersion() + ", 修改" + (success ? "成功" : "失败"));
            if (!success) {
                System.out.println(Thread.currentThread().getName() + " 期望 version = " + curVersion + ", 实际 version = " + i1.getVersion());
            }
        }, "t4");

        t3.start();
        t4.start();

        t3.join();
        t4.join();
    }

    public static void testObject() {
        User user1 = new User("1", "eric");
        User user2 = new User("2", "lucy");
        User user3 = new User("3", "lili");

        AtomicVersion<User> atomic = new AtomicVersion<>(user1);

        boolean success = atomic.compareAndSet(user1, user2);
        System.out.println(Thread.currentThread().getName() + " ret = " + atomic.getValue() + ", version = " + atomic.getVersion() + ", 修改" + (success ? "成功" : "失败"));

        success = atomic.compareAndSet(user1, user3);
        System.out.println(Thread.currentThread().getName() + " ret = " + atomic.getValue() + ", version = " + atomic.getVersion() + ", 修改" + (success ? "成功" : "失败"));
    }

    private static void testObjectHasABA() throws InterruptedException {
        User user1 = new User("1", "eric");
        User user2 = new User("2", "lucy");
        User user3 = new User("3", "lili");

        AtomicVersion<User> atomic = new AtomicVersion<>(user1);

        Thread t1 = new Thread(() -> {
            // 触发 ABA 问题
            boolean success = atomic.compareAndSet(user1, user2);
            System.out.println(Thread.currentThread().getName() + " ret = " + atomic.getValue() + ", version = " + atomic.getVersion() + ", 修改" + (success ? "成功" : "失败"));
            success = atomic.compareAndSet(user2, user1);
            System.out.println(Thread.currentThread().getName() + " ret = " + atomic.getValue() + ", version = " + atomic.getVersion() + ", 修改" + (success ? "成功" : "失败"));
        }, "t1");

        Thread t2 = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            boolean success = atomic.compareAndSet(user1, user3);
            System.out.println(Thread.currentThread().getName() + " ret = " + atomic.getValue() + ", version = " + atomic.getVersion() + ", 修改" + (success ? "成功" : "失败"));
        }, "t2");

        t1.start();
        t2.start();

        t1.join();
        t2.join();
    }

    private static void testObjectNoABA() throws InterruptedException {
        User user1 = new User("1", "eric");
        User user2 = new User("2", "lucy");
        User user3 = new User("3", "lili");

        AtomicVersion<User> atomic = new AtomicVersion<>(user1, 0);

        Thread t3 = new Thread(() -> {
            int curVersion = atomic.getVersion();
            System.out.println(Thread.currentThread().getName() + " 初始 ret = " + atomic.getValue() + " version = " + atomic.getVersion());

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            boolean success = atomic.compareAndSet(user1, curVersion, user2, curVersion + 1);
            System.out.println(Thread.currentThread().getName() + " 第一次 ret = " + atomic.getValue() + ", version = " + atomic.getVersion() + ", 修改" + (success ? "成功" : "失败"));

            curVersion = atomic.getVersion();
            success = atomic.compareAndSet(user2, curVersion, user1, curVersion + 1);
            System.out.println(Thread.currentThread().getName() + " 第二次 ret = " + atomic.getValue() + ", version = " + atomic.getVersion() + ", 修改" + (success ? "成功" : "失败"));
        }, "t3");

        Thread t4 = new Thread(() -> {
            int curVersion = atomic.getVersion();
            System.out.println(Thread.currentThread().getName() + " 初始 ret = " + atomic.getValue() + ", version = " + atomic.getVersion());

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            boolean success = atomic.compareAndSet(user1, curVersion, user3, curVersion + 1);
            System.out.println(Thread.currentThread().getName() + " 第一次 ret = " + atomic.getValue() + ", version = " + atomic.getVersion() + ", 修改" + (success ? "成功" : "失败"));
            if (!success) {
                System.out.println(Thread.currentThread().getName() + " 期望 version = " + curVersion + ", 实际 version = " + atomic.getVersion());
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
