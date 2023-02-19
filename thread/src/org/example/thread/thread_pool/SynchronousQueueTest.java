package org.example.thread.thread_pool;

import java.util.concurrent.SynchronousQueue;

/**
 * SynchronousQueue：同步移交队列，此队列中没有容器，
 * 一个生产者线程，当准备生产（准备 put）的时候，如果没有消费者来消费的时候，此时生产者就会阻塞，不会生产（执行 put），
 * 直到有消费者线程来调用（take），take 操作会唤醒生产者线程，生产者才会真正的生产（put），消费者线程此时就会消费产品，
 * 所以叫做一次配对
 */
public class SynchronousQueueTest {
    public static void main(String[] args) throws InterruptedException {
        SynchronousQueue<String> queue = new SynchronousQueue<>(true);

        // 生产者
        Thread producer = new Thread(()-> {
            try {
                System.out.println("producer start");
                queue.put("apple"); // 挂起
                System.out.println("producer end");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "producer");

        // 消费者
        Thread consumer = new Thread(()-> {
            try {
                System.out.println("consumer start");
                String object = queue.take(); // 唤醒
                System.out.println("object = " + object);
                System.out.println("consumer end");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "consumer");

        producer.start();
        Thread.sleep(2000);
        consumer.start();
    }
}
