package org.example.thread.thread_design_pattern.future;

import java.util.Random;

public class RealProduct implements Product {

    private final String content;

    private static final Random RANDOM = new Random();

    public RealProduct(int count, char c) {
        char[] chars = new char[count];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = c;
            try {
                Thread.sleep(RANDOM.nextInt(1000));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        this.content = new String(chars);
    }

    @Override
    public String getContent() {
        return content;
    }
}
