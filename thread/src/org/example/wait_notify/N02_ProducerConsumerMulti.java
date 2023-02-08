package org.example.wait_notify;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class N02_ProducerConsumerMulti {

    /**
     * 模拟：100 个生产者消，100 个消费者，同时只能 10 个产品，消费 10 个产品
     */
    public static class ProducerConsumer {

        private static final Object LOCK = new Object();

        private static Map<Integer, Object> products = new LinkedHashMap<>();
        private static int index = 0; // 做多 10 个

        public static void main(String[] args) {
            for (int i = 0; i < 100; i++) {
                new Thread(() -> {
                    while(true) { // 防止虚假唤醒，让被唤醒当线程继续执行一次
                        synchronized (LOCK) {
                            try {
                                if (index < 10) {
                                    producer();
                                    LOCK.notifyAll();
                                    // break; // 保证每个线程只执行一次后退出
                                } else {
                                    LOCK.wait(); // 如果线程在这里 wait，当被唤醒当时候，会继续往下执行，不会在继续上面逻辑，就是虚假唤醒
                                }
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                }, "producer-" + i).start();

                new Thread(() -> {
                    while (true) { // 防止虚假唤醒，让被唤醒当线程继续执行一次
                        try {
                            TimeUnit.MILLISECONDS.sleep(100);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        synchronized (LOCK) {
                            try {
                                if (index > 0) {
                                    consumer();
                                    LOCK.notifyAll();
                                    // break; // 保证每个线程只执行一次后退出
                                } else {
                                    LOCK.wait(); // 如果线程在这里 wait，当被唤醒当时候，会继续往下执行，不会在继续上面逻辑，就是虚假唤醒
                                }
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }, "consumer-" + i).start();
            }
        }

        public static void producer() throws InterruptedException {
            index++;
            Object product = new Object();
            products.put(index, product);
            System.out.println(Thread.currentThread().getName() + ": 生产了 " + index + ":" + product);
        }

        public static void consumer() throws InterruptedException {
            Object product = products.get(index);
            System.out.println(Thread.currentThread().getName() + ": 消费了 " + index + ":" + product);
            index--;
        }
    }

}
