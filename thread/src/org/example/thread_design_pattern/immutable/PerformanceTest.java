package org.example.thread_design_pattern.immutable;

public class PerformanceTest {
    public static void main(String[] args) throws InterruptedException {
        // Immutable test = new Immutable("eric");
        Mutable test = new Mutable("eric");

        long start = System.currentTimeMillis();
        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            Thread thread = new Thread(()->{
                for (int j = 0; j < 100000; j++) {
                    test.sayHello();
                }
            });
            thread.start();
            threads[i] = thread;
        }

        for (int i = 0; i < 5; i++) {
            threads[i].join();
        }
        // Immutable spend time: 2807ms
        // Mutable spend time: 2827ms（因为没有任何变量发生逃逸，所以做了锁消除优化）
        System.out.println("spend time: " + (System.currentTimeMillis() - start) + "ms");
    }

    static final class Immutable {
        private final String name;

        Immutable(String name) {
            this.name = name;
        }

        public void sayHello() {
            System.out.println("Immutable name = " + name);
        }
    }

    static final class Mutable {
        private String name;

        Mutable(String name) {
            this.name = name;
        }

        public synchronized void sayHello() {
            System.out.println("Immutable name = " + name);
        }
    }
}


