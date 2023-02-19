package org.example.thread.thread_design_pattern.thread_per_message;

public class ThreadPerMessageTest {
    public static void main(String[] args) {
        Server server = new Server();
        for (int i = 0; i < 5; i++) {
            server.request(i);
        }
    }
}
