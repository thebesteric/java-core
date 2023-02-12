package org.example.thread_design_pattern.thread_per_message.bio;

public class MyServerTest {
    public static void main(String[] args) throws Exception {
        MyServer server = new MyServer(8888);
        server.start();
    }
}
