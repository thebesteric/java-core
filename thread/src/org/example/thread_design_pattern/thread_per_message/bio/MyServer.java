package org.example.thread_design_pattern.thread_per_message.bio;

import java.net.ServerSocket;
import java.net.Socket;

public class MyServer {
    // 启动后会接受客户端请求，并发送相应
    private final int port;
    private final MyService myService;

    public MyServer(int port) {
        this.port = port;
        this.myService = new MyService();
    }

    public void start() throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Starting server on port " + port);
            while (true) {
                System.out.println(Thread.currentThread().getName() + ": Accepting request...");
                Socket socket = serverSocket.accept();
                System.out.println(Thread.currentThread().getName() + ": Connect to " + socket);
                new Thread(() -> {
                    try {
                        myService.service(socket);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }
        }

    }
}
