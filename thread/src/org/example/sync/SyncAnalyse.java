package org.example.sync;

/**
 * -server
 * -XX:+DoEscapeAnalysis
 * -XX:+EliminateLocks
 * -XX:+EliminateAllocations
 * <p>
 * 虚拟机默认会开启：
 * 逃逸分析：看局部变量是否会在方法之外，如：返回出去
 * 标量替换：把对象打散之后分配在栈上或者寄存器上，如：数组
 */
public class SyncAnalyse {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 2000000; i++) {
            // test(); // 测试标量替换
            calc(); // 测试锁消除
        }
        System.out.println("spend time = " + (System.currentTimeMillis() - start) + "ms");
    }

    /**
     * -Xmx20m
     * -Xms20m
     * -Xlog:gc
     * -XX:+EliminateAllocations 花费了 5ms，没有发生 GC
     * -XX:-EliminateAllocations 花费了 43ms，发生了 GC
     */
    public static void test() {
        byte[] bytes = new byte[2];
        bytes[0] = 1;
    }

    /**
     * 如果没有逃逸，锁会自动消除
     * -XX:+EliminateLocks 花费了 26ms
     * -XX:-EliminateLocks 花费了 51ms
     */
    public synchronized static void calc() {
        int x = 0;
        for (int i = 0; i < 10; i++) {
            x++;
        }
    }
}
