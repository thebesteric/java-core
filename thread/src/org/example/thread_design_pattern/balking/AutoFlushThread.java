package org.example.thread_design_pattern.balking;

public class AutoFlushThread extends Thread {

    private Screen screen;
    public AutoFlushThread(Screen screen) {
        this.screen = screen;
    }

    @Override
    public void run() {
        while (true) {
            try {
                screen.save(true);
                Thread.sleep(2000);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }
}
