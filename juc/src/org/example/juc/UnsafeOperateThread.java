package org.example.juc;

import sun.misc.Unsafe;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class UnsafeOperateThread {
    public static void main(String[] args) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Unsafe unsafe = MyUnsafe.getUnsafe();

        Thread mainThread = Thread.currentThread();

        System.out.println("[" +LocalDateTime.now().format(formatter) + "] " + mainThread.getName() + ": running...");

        System.out.println("[" +LocalDateTime.now().format(formatter) + "] " + mainThread.getName() + " ready to park 5 seconds");
        // isAbsolute = true，那么 time = 当前时间 + duration（milliseconds）
        unsafe.park(true, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5));

        System.out.println("[" + LocalDateTime.now().format(formatter) + "] " + mainThread.getName() + ": continue running...");

        System.out.println("[" +LocalDateTime.now().format(formatter) + "] " + mainThread.getName() + " ready to park 3 seconds");
        // isAbsolute = false，那么就是 time 就是 nanoseconds，不用加当前时间
        unsafe.park(false, TimeUnit.SECONDS.toNanos(3));

        System.out.println("[" + LocalDateTime.now().format(formatter) + "] " + mainThread.getName() + ": continue running...");

        new Thread(()->{
            System.out.println("[" +LocalDateTime.now().format(formatter) + "] " + Thread.currentThread().getName() + " ready to park 5 seconds");
            unsafe.park(false, TimeUnit.SECONDS.toNanos(5));
            System.out.println("[" +LocalDateTime.now().format(formatter) + "] " + Thread.currentThread().getName() + " unpark " + mainThread.getName());
            unsafe.unpark(mainThread);
        }, "t1").start();

        // 当 isAbsolute = false 且 time = 0 时，表示一直 park 直到有线程调用 unpark
        unsafe.park(false, 0);
    }
}
