package org.example.thread.thread;

public class HookThread {
    public static void main(String[] args) {
        Thread daemon = new Thread(() -> {
            while (true) {
                System.out.println("i am daemon thread");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        daemon.setDaemon(true);
        daemon.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("hook thread " + Thread.currentThread().getName());
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    public static class HookDemo {
        public static void main(String[] args) {

            // 钩子一：释放异常
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("释放资源");
            }, "relase-thread"));
            // 钩子二：上报异常，通知相关人员
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("上报异常");
            }, "report-thread"));

            int i = 0;
            while (true) {
                try {
                    // 模拟发生异常
                    i++;
                    System.out.println("task is running..." + i);
                    Thread.sleep(1000);
                    if (i == 5) {
                        throw new RuntimeException("发生了异常");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }

            // 发生异常，会执行钩子函数
        }
    }
}
