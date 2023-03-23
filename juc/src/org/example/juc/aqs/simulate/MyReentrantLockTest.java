package org.example.juc.aqs.simulate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MyReentrantLockTest {
    static MyReentrantLock lock = new MyReentrantLock(true);
    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void one() {
        System.out.println("[" + dateFormat.format(new Date()) + "] " + Thread.currentThread().getName() + ": 准备进入 one 方法");
        lock.lock();
        try {
            System.out.println("[" + dateFormat.format(new Date()) + "] " + Thread.currentThread().getName() + ": 进入 one 方法");
            two();
            TimeUnit.SECONDS.sleep(3);
            System.out.println("[" + dateFormat.format(new Date()) + "] " + Thread.currentThread().getName() + ": 退出 one 方法");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public static void two() {
        System.out.println("[" + dateFormat.format(new Date()) + "] " + Thread.currentThread().getName() + ": 准备进入 two 方法");
        lock.lock();
        try {
            System.out.println("[" + dateFormat.format(new Date()) + "] " + Thread.currentThread().getName() + ": 进入 two 方法");
            TimeUnit.SECONDS.sleep(5);
            System.out.println("[" + dateFormat.format(new Date()) + "] " + Thread.currentThread().getName() + ": 退出 two 方法");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        new Thread(() -> one(), "t2").start();
        new Thread(() -> two(), "t3").start();
    }
}
