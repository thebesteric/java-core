package org.example.juc;

import sun.misc.Unsafe;

public class UnsafeOperateCAS {

    private volatile int num = 0;

    static Unsafe unsafe = MyUnsafe.getUnsafe();

    public static void main(String[] args) {

        UnsafeOperateCAS obj = new UnsafeOperateCAS();

        new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                obj.increment(i);
            }
        }, "t1").start();

        new Thread(() -> {
            for (int i = 6; i <= 10; i++) {
                obj.increment(i);
            }
        }, "t2").start();
    }

    private void increment(int x) {
        try {
            long offset;
            do {
                offset = unsafe.objectFieldOffset(UnsafeOperateCAS.class.getDeclaredField("num"));
            } while (!unsafe.compareAndSwapInt(this, offset, x - 1, x));
            System.out.println(Thread.currentThread().getName() + ": num = " + this.num);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
