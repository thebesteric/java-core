package org.example.juc.atomic;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class AtomicIntegerArrayTest {
    public static void main(String[] args) throws InterruptedException {
        api();
        multi();
    }

    public static void multi() throws InterruptedException {

        CountDownLatch countDownLatch1 = new CountDownLatch(10);

        // 线程安全的数组
        AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(10);

        long start = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            new Thread(()->{
                for (int j = 0; j < 10000; j++) {
                    atomicIntegerArray.getAndIncrement(j % atomicIntegerArray.length());
                }
                countDownLatch1.countDown();
            }).start();
        }
        countDownLatch1.await();

        System.out.println("atomicIntegerArray spend: " + (System.currentTimeMillis() - start) + " ms");
        printArr(atomicIntegerArray);

        CountDownLatch countDownLatch2 = new CountDownLatch(10);
        int[] arr = new int[10];
        start = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            new Thread(()->{
                for (int j = 0; j < 10000; j++) {
                    synchronized (AtomicIntegerArrayTest.class) {
                        arr[j % arr.length] += 1;
                    }
                }
                countDownLatch2.countDown();
            }).start();
        }
        countDownLatch2.await();

        System.out.println("sync arr spend: " + (System.currentTimeMillis() - start) + " ms");
        printArr(arr);

    }

    public static void api() {
        AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(10);
        printArr(atomicIntegerArray);

        System.out.println("========== set(2, 2) ============");
        atomicIntegerArray.set(2, 2);
        printArr(atomicIntegerArray);

        System.out.println("========== addAndGet(2, 5) ============");
        int ret = atomicIntegerArray.addAndGet(2, 5);
        System.out.println("ret = " + ret);
        printArr(atomicIntegerArray);

        System.out.println("========== compareAndSet(2, ret, 10) ============");
        atomicIntegerArray.compareAndSet(2, ret, 10);
        printArr(atomicIntegerArray);

        System.out.println("========== decrementAndGet(2) ============");
        atomicIntegerArray.decrementAndGet(2);
        printArr(atomicIntegerArray);
    }

    private static void printArr(AtomicIntegerArray atomicIntegerArray) {
        for (int i = 0; i < atomicIntegerArray.length(); i++) {
            System.out.print(i + " = " + atomicIntegerArray.get(i));
            if (i != atomicIntegerArray.length() - 1) {
                System.out.print(", ");
            } else {
                System.out.println();
            }
        }
    }

    private static void printArr(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            System.out.print(i + " = " + arr[i]);
            if (i != arr.length - 1) {
                System.out.print(", ");
            } else {
                System.out.println();
            }
        }
    }
}
