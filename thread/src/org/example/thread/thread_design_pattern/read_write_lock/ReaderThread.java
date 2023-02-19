package org.example.thread.thread_design_pattern.read_write_lock;

import java.util.Arrays;
import java.util.Random;

public class ReaderThread extends Thread {
    private final TextData textData;

    private static final Random random = new Random();

    public ReaderThread(TextData textData, String name) {
        super(name);
        this.textData = textData;
    }

    @Override
    public void run() {
        try {
            while (true) {
                char[] chars = textData.read();
                System.out.println(getName() + " read: " + Arrays.toString(chars));
                try {
                    Thread.sleep(random.nextInt(500));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
