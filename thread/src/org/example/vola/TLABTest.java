package org.example.vola;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime) // 平均时间
@OutputTimeUnit(TimeUnit.NANOSECONDS) // 使用纳秒
public class TLABTest {
    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder().include(TLABTest.class.getSimpleName())
                .forks(1)
                .warmupIterations(5) // 预热几次
                .measurementIterations(5) // 执行几次测试
                .threads(4)
                .build();

        new Runner(options).run();
    }

    /**
     * 1、@State(Scope.Group)：share 组共享 MyCount
     * 2、@State(Scope.Thread)：每个线程创建一个 MyCount
     */
    // @State(Scope.Group)
    @State(Scope.Thread)
    public static class MyCount {
        MyObject[] myObjects = new MyObject[2];

        public MyCount() {
            myObjects[0] = new MyObject();
            myObjects[1] = new MyObject();
        }
    }

    @Benchmark
    @Group("share")
    public void testA(MyCount myCount) {
        myCount.myObjects[0].myValue++;
    }

    @Benchmark
    @Group("share")
    public void testB(MyCount myCount) {
        myCount.myObjects[1].myValue++;
    }

    public static class MyObject {
        volatile int myValue;
    }
}
