package org.example.thread.thread_design_pattern.worker_thread;

import lombok.Data;

import java.util.Random;

@Data
public class Contract {
    private final Integer id;
    private final String name;
    private static final Random RANDOM = new Random();

    public Contract(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public void execute() {
        System.out.println(Thread.currentThread().getName() + " 负责 " + this);
        try {
            Thread.sleep(RANDOM.nextInt(1000));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
