package org.example.vola;

import java.util.ArrayList;
import java.util.List;

public class ShareTest {

    public static int i = 0;

    // 设置 100，200，300...1000，查看结果
    // 为什么在某个数值之前不打印，在某个数值之后就开始打印，更大就开始随机打印
    // 和 x86 CPU 架构和 tso 内存模型有关
    public static final int COUNT = 10453;

    public static void main(String[] args) {
        new Thread(() -> {
            System.out.println("消费者线程启动...");
            List<Integer> list = new ArrayList<>();
            for (int j = 1; j <= COUNT; j++) {
                list.add(j);
            }
            while (true) {
                if (list.contains(i)) {
                    System.out.println("消费了：" + i);
                    break;
                }
            }
        }).start();

        new Thread(() -> {
            sleep(1000);
            System.out.println("生产者线程启动...");
            for (int j = 1; j <= COUNT; j++) {
                i = j;
            }
            System.out.println("生产者线程结束...");
        }).start();
    }

    public static void sleep(long millis) {
        long nano = millis * 1000 * 1000;
        long start = System.nanoTime();
        long end;
        do {
            end = System.nanoTime();
        } while (end < start + nano);
    }
}
