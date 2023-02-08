package org.example.thread;

public class DestroyJavaVM {
    public static void main(String[] args) {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
                Thread[] threads = new Thread[threadGroup.activeCount()];
                int enumerate = threadGroup.enumerate(threads);
                for (int i = 0; i < enumerate; i++) {
                    System.out.println(threads[i].getName());
                    if ("DestroyJavaVM".equals(threads[i].getName())) {
                        System.out.println("DestroyJavaVM is Daemon? " + threads[i].isDaemon());
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
