package org.example.juc.atomic;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;

public class AtomicIntegerTest {
    public static void main(String[] args) throws InterruptedException {
        increment();
        // getAndUpdate();
        // testIntUnaryOperator();
        // testIntBinaryOperator();
    }

    public static void testIntBinaryOperator() {
        IntBinaryOperator intBinaryOperator = (left, right) -> left * right;

        int ret = intBinaryOperator.applyAsInt(3, 5);
        System.out.println("IntUnaryOperator.intBinaryOperator = " + ret);

        AtomicInteger atomicInteger01 = new AtomicInteger(10);
        ret = atomicInteger01.getAndAccumulate(5, intBinaryOperator);
        System.out.println("getAndAccumulate = " + ret + ", atomicInteger = " + atomicInteger01.get());

        AtomicInteger atomicInteger02 = new AtomicInteger(10);
        ret = atomicInteger02.accumulateAndGet(5, intBinaryOperator);
        System.out.println("getAndAccumulate = " + ret + ", atomicInteger = " + atomicInteger02.get());

    }

    public static void testIntUnaryOperator() {
        // identity 传入什么就传出什么
        // 相当于 IntUnaryOperator intUnaryOperator = x -> x;
        IntUnaryOperator intUnaryOperator = IntUnaryOperator.identity();
        int ret = intUnaryOperator.applyAsInt(1000);
        System.out.println("IntUnaryOperator.identity = " + ret);

        // applyAsInt 就是将值给 intUnaryOperator 表达式
        intUnaryOperator = x -> x * 3;
        ret = intUnaryOperator.applyAsInt(1000);
        System.out.println("IntUnaryOperator.applyAsInt = " + ret);

        // andThen 是 after 操作，就是先计算 intUnaryOperator 表达式，然后在计算 andThen 表达式
        // 多个 andThen 组合，按照从前往后执行
        intUnaryOperator = x -> x * 3;
        ret = intUnaryOperator.andThen(x -> x + 6).andThen(x -> x * 2).applyAsInt(2);
        System.out.println("IntUnaryOperator.andThen = " + ret);

        // compose 是 before 操作，就是先计算 compose 表达式，在计算 intUnaryOperator 表达式
        // 多个 compose 组合，按照从后往前执行
        intUnaryOperator = x -> x * 3;
        ret = intUnaryOperator.compose(x -> x + 6).compose(x -> x * 2).applyAsInt(2);
        System.out.println("IntUnaryOperator.compose = " + ret);

    }

    public static void getAndUpdate() {
        AtomicInteger atomicInteger = new AtomicInteger(10);

        IntUnaryOperator updateFunc = x -> x * x;
        int ret = atomicInteger.updateAndGet(updateFunc);
        System.out.println("updateAndGet ret = " + ret);
    }

    public static void increment() throws InterruptedException {
        AtomicInteger i = new AtomicInteger(0);

        Thread t1 = new Thread(() -> {
            for (int j = 0; j < 5000; j++) {
                // i++;
                i.incrementAndGet();
            }
        });

        Thread t2 = new Thread(() -> {
            for (int j = 0; j < 5000; j++) {
                // i++;
                i.incrementAndGet();
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("incrementAndGet ret = " + i);
    }
}
