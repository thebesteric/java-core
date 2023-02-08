package org.example.vola;

/**
 * 如果多个线程修改不同的变量，但是不同的变量却在同一个 cache line 中，这样就会引发伪共享问题
 * 使用 JOL 查看内存布局
 */
public class PseudoCacheShare {
    public static void main(String[] args) throws InterruptedException {
        // Count count = new Count();
        // CountB count = new CountB();
        CountAnnoPadding count = new CountAnnoPadding();
        long start = System.currentTimeMillis();
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 100000000; i++) {
                count.a++;
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 100000000; i++) {
                count.b++;
            }
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        long end = System.currentTimeMillis();
        System.out.println("a = " + count.a);
        System.out.println("b = " + count.b);
        System.out.println("time is = " + (end - start) + " ms");
    }

    /** 解决方案一：使用变量进行 padding（不推荐） */
    static class Count {
        volatile long a;
        // 填充 cache line，保证 a 独占一个 cache line
        // 因为一个 cache line 占 64 byte，所以补满，这样可以保证一定独占
        private long p1, p2, p3, p4, p5, p6, p7;
        volatile long b;
    }

    /** 解决方案二：使用 class 层级进行 padding */
    static class CountA {
        volatile long a;
    }

    static class CountPadding extends CountA {
        private long p1, p2, p3, p4, p5, p6, p7;
    }

    static class CountB extends CountPadding {
        volatile long b;
    }

    /** 解决方案三：JDK 8 及以上，可以使用注解进行填充（推荐） */
    static class CountAnnoPadding {
        volatile long a;
        // 虚拟机需要添加：-XX:-RestrictContended 参数
        // 会在变量 b 之前，之后分别加入 padding 128 byte 补齐
        // @jdk.internal.vm.annotation.Contended
        volatile long b;
    }
}
