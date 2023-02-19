package org.example.thread.thread;

/**
 * 栈深度：可以存放多少方法
 * 栈容量：栈的大小
 * 影响参数：-Xss128k，和线程的 stackSize
 * 主线程，只能通过 -Xss 来设置，全局生效（对 new Thread 也生效）
 * 如果 new Thread 自己设置了 stackSize 参数，-Xss 将对其不再生效
 */
public class ThreadStack {

    public static int COUNT = 0;

    public static void main(String[] args) {
        // inMain();
        inThread();
    }

    public static void inMain() {
        try {
            ThreadStack threadStack = new ThreadStack();
            threadStack.count();
        } catch (Error e) {
            e.printStackTrace();
            System.out.println(">>> stack length = " + COUNT);
        }
    }

    public static void inThread() {
        // 自定义 stackSize 大小
        new Thread(null, () -> {
            try {
                ThreadStack threadStack = new ThreadStack();
                threadStack.count();
            } catch (Error e) {
                e.printStackTrace();
                System.out.println(">>> stack length = " + COUNT);
            }
        }, "thread-1", 1024 * 1024 * 2).start();
    }

    public void count() {
        COUNT++;
        count();
    }
}
