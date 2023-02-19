package org.example.thread.thread_design_pattern.balking;

import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class DoctorThread extends Thread {
    private final Scanner scanner = new Scanner(System.in);
    private Screen screen;
    private String docName;

    public DoctorThread(Screen screen) {
        this.screen = screen;
    }

    @Override
    public void run() {
        try {
            int times = 0;
            while (true) {
                String name = scanner.next();
                int room = new Random().nextInt(10) + 1;
                screen.add(name, room);
                times++;
                if (times == 5) {
                    screen.save(false);
                    times = 0;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
