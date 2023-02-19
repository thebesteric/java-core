package org.example.thread.thread_design_pattern.read_write_lock;

import java.util.Random;

public class WriterThread extends Thread {
    private final TextData textData;
    private final String value;
    private int index;

    private static final Random random = new Random();

    public WriterThread(TextData textData, String name, String value) {
        super(name);
        this.textData = textData;
        this.value = value;
    }

    @Override
    public void run() {
        try {
            while (true) {
                char c = nextChar();
                textData.write(c);
                System.out.println(getName() + " write: " + c);
                try {
                    Thread.sleep(random.nextInt(1000));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private char nextChar() {
        char c = this.value.charAt(index++);
        if (index >= this.value.length()) {
            index = 0;
        }
        return c;
    }
}
