package org.example.thread.thread_local;

import java.io.Serializable;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * -Xms20m -Xmx20m
 * 测试线程池引起的 ThreadLocal 内存泄漏问题
 * 因为线程池会回收线程，所以线程无法释放 threadLocalMap 中的 entry，最终导致 OOM
 * 解决方法：threadLocal.remove();
 */
public class ThreadLocalForOOM {

    private static final ThreadPoolExecutor pool = new ThreadPoolExecutor(5,5,1, TimeUnit.MINUTES, new LinkedBlockingDeque<>());
    private static final ThreadLocal<OomObject> threadLocal = new ThreadLocal<OomObject>();

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 500; i++) {
            int index = i;
            pool.execute(()->{
                threadLocal.set(new OomObject());
                OomObject oomObject = threadLocal.get();
                // threadLocal.remove();
                System.out.println(index + ": oomObject = " + oomObject);
            });
            Thread.sleep(50);
        }
        System.out.println("END");
    }

    static class OomObject implements Serializable {
        private Byte[] a = new Byte[1024 * 1024]; // 1M 大小空间
    }
}
