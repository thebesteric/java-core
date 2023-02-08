package org.example.thread_design_pattern.single_threaded_execution;

public class MySemaphoreTest {
    public static void main(String[] args) {
        MySemaphoreTest mySemaphoreTest = new MySemaphoreTest();
        // Semaphore semaphore = new Semaphore(5);
       MySemaphore semaphore = new MySemaphore(5);
        for (int i = 1; i <= 15; i++) {
            int finalI = i;
            new Thread(() -> {
                try {
                    semaphore.acquire();
                    mySemaphoreTest.eat(finalI);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    semaphore.release();
                }
            }).start();
        }
    }

    public void eat(int i) {
        try {
            System.out.println(">>>> " + i + "号客人，开始用餐...");
            Thread.sleep(100);
            System.out.println("<<<< " + i + "号客人，用餐完毕");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
