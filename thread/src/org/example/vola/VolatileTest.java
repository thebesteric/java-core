package org.example.vola;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * storestore：写屏障，x86 的写屏障默认是空操作，因为 x86 规定，所有写操作都必须进入 store buffer 中
 * loadload：读屏障，x86 的写屏障默认是空操作，因为 x86 不存在 invalidate queue
 * loadstore：读写屏障，x86 的写屏障默认是空操作，因为 x86 所有写操作都必须进入 store buffer 中，所以读写不会重排序
 * storeload：写读屏障，x86 实现了写读屏障
 *
 * 下面这个案例就是写读屏障
 */
public class VolatileTest {

    private static int x = 0, y = 0;
    private static int a = 0, b = 0;

    public static Unsafe getUnsafe() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            return (Unsafe) theUnsafe.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws InterruptedException {

        int i = 0;
        for (; ; ) {
            i++;
            x = 0;
            y = 0;
            a = 0;
            b = 0;
            Thread t1 = new Thread(() -> {
                a = 1;
                // getUnsafe().storeFence();
                // VolatileTest.class.getDeclaredField("a"); // 原因是反射里面含有 cas 操作
                x = b; // x=b, 相当于 先读 b，在写 x，先读 b 就与上面的 a=1，引发了写读，引起重排序
            });
            Thread t2 = new Thread(() -> {
                b = 1;
                // getUnsafe().storeFence();
                // VolatileTest.class.getDeclaredField("a"); // 原因是反射里面含有 cas 操作
                y = a;
            });

            t1.start();
            t2.start();
            t1.join();
            t2.join();

            // x，y 可能是什么值？
            // 情况1、t1 先跑完，t2 后跑完
            //      x = 0, y = 1
            // 情况2、t2 先跑完，t1 后跑完
            //      x = 1, y = 0
            // 情况3、t1 跑一半，t2 跑一半，
            //      x = 1, y = 1
            // 清空4、发生指令重排：
            //      x = 0, y = 0
            System.out.printf("第%s次，x=%s, y=%s\n", i, x, y);
            if (x == 0 && y == 0) {
                break;
            }

        }

    }

}
