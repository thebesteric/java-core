package org.example.juc.atomic;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

public class LongAdderTest {
    public static void main(String[] args) throws InterruptedException {
        int count = 1000;
        int times = 100000;
        testAtomicLong(count, times);
        testLongAdder(count, times);
    }

    public static void testAtomicLong(int count, int times) throws InterruptedException {
        AtomicLong atomicLong = new AtomicLong(0);
        CountDownLatch latch = new CountDownLatch(count);
        long start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            new Thread(()->{
                for (int j = 0; j < times; j++) {
                    atomicLong.incrementAndGet();
                }
                latch.countDown();
            }).start();
        }
        latch.await();
        System.out.println("AtomicLong value: " + atomicLong.get() + ", AtomicLong spend: " + (System.currentTimeMillis() - start) + " ms");
    }

    public static void testLongAdder(int count, int times) throws InterruptedException {
        LongAdder longAdder = new LongAdder();
        CountDownLatch latch = new CountDownLatch(count);
        long start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            new Thread(()->{
                for (int j = 0; j < times; j++) {
                    longAdder.increment();
                }
                latch.countDown();
            }).start();
        }
        latch.await();
        System.out.println("LongAdder value: " + longAdder.longValue() + ", AtomicLong spend: " + (System.currentTimeMillis() - start) + " ms");
    }
}
