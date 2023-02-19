package org.example.thread.thread;

public class ThreadException {

    public static void main(String[] args) {

        // 优先级 2
        // 由于会先调用线程组的异常处理，所以线程组的优先级大于全局的异常处理
        ThreadGroup group = new ThreadGroup("group-1") {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                System.out.println("group: " + thread.getName() + " exception is " + throwable.getMessage());
            }
        };

        Thread t = new Thread(group, () -> {
            // 这种情况：
            // 如果在线程内部使用 try-catch 的话，那么代码就会显得特别臃肿
            // 如果在外部使用 try-catch 处理的话，也无法在外部处理线程异常
            // 那么就需要是用 Thread 提供的 setUncaughtExceptionHandler 来处理了
            int ret = 1 / 0;
        }) {
            // 重写了 getUncaughtExceptionHandler() 方法
            @Override
            public UncaughtExceptionHandler getUncaughtExceptionHandler() {
                return (thread, throwable) -> {
                    System.out.println("inner: " + thread.getName() + " exception is " + throwable.getMessage());
                };
            }
        };

        // 优先级 1
        // 异常处理：为某个线程单独处理
        // t.setUncaughtExceptionHandler((thread, throwable) -> {
        //     System.out.println("thread: " + thread.getName() + " exception is " + throwable.getMessage());
        // });

        // 优先级 3
        // 异常处理，为所有线程处理异常
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            System.out.println("global: " + thread.getName() + " exception is11 " + throwable.getMessage());
        });

        t.start();
    }
}
