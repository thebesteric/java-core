package org.example.juc.atomic;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.LongAccumulator;

public class LongAccumulatorTest {

    public static void calc(LongAccumulator accumulator) {
        accumulator.accumulate(2);
        System.out.println(Thread.currentThread().getName() + " 当前 count 值为：" + accumulator.get());
    }
    public static void main(String[] args) throws InterruptedException {
        LongAccumulator accumulator = new LongAccumulator(((x, y) -> x * y), 1);

        CountDownLatch latch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            new Thread(()->{
                calc(accumulator);
                latch.countDown();
            }, "t" + i).start();
        }

        latch.await();
        System.out.println("最终 count 值为：" + accumulator.get());
    }
}
