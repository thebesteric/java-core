package org.example.juc.atomic;

import java.util.concurrent.atomic.AtomicBoolean;

public class AtomicBooleanTest {
    public static void main(String[] args) {
        AtomicBooleanTask task = new AtomicBooleanTask();
        for (int i = 0; i < 3; i++) {
            new Thread(task).start();
        }
    }

    static class AtomicBooleanTask implements Runnable {

        private static final AtomicBoolean flag = new AtomicBoolean(false);

        @Override
        public void run() {
            while (true) {
                if (flag.compareAndSet(false, true)) {
                    for (int i = 1; i <= 3; i++) {
                        System.out.println(Thread.currentThread().getName() + " 执行第 " + i + " 个步骤");
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    flag.set(false);
                    break;
                } else {
                    System.out.println(Thread.currentThread().getName() + " 等待执行...");
                }
            }
        }
    }
}
