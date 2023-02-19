package org.example.thread.wait_notify;

import java.util.concurrent.TimeUnit;

public class N01_ProducerConsumer {

    /**
     * 模拟：一个生产者消，一个消费者，同时只能一个产品，消费一个产品
     */
    public static class ProducerConsumer {

        private static final Object LOCK = new Object();

        private static Object product;

        public static void main(String[] args) {
            Thread producer = new Thread(() -> {
                while(true) {
                    synchronized (LOCK) {
                        try {
                            if (product == null) {
                                product = producer();
                                LOCK.notify(); // 通知的是别的正在 wait 的线程
                            } else {
                                LOCK.wait();
                            }
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }, "t-producer");
            producer.start();

            Thread consumer = new Thread(() -> {
                while (true) {
                    synchronized (LOCK) {
                        try {
                            if (product != null) {
                                consumer();
                                LOCK.notify(); // 通知的是别的正在 wait 的线程
                            } else {
                                LOCK.wait();
                            }
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }, "t-consumer");
            consumer.start();
        }

        /**
         * 生产
         */
        public static Object producer() throws InterruptedException {
            TimeUnit.SECONDS.sleep(1);
            Object product = new Object();
            System.out.println(Thread.currentThread().getName() + ": 生产了 " + product);
            return product;
        }

        /**
         * 生产
         */
        public static void consumer() throws InterruptedException {
            TimeUnit.SECONDS.sleep(1);
            System.out.println(Thread.currentThread().getName() + ": 消费了 " + product);
            product = null;
        }

    }

}
