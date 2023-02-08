package org.example.thread_design_pattern.guarded_suspension;

import java.util.LinkedList;
import java.util.Queue;

public class Cainiao {
    private final Queue<Package> queue = new LinkedList<>();

    public void add(Package pack) {
        synchronized (queue) {
            if (queue.offer(pack)) {
                queue.notifyAll();
            }
        }
    }

    public Package get() {
        while (queue.isEmpty()) {
            synchronized (queue) {  // 保护：queue
                try {
                    queue.wait(); // 暂时挂起：消费者线程
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return queue.remove();
    }
}
