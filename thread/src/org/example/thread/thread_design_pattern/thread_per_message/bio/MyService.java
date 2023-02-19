package org.example.thread.thread_design_pattern.thread_per_message.bio;

import java.io.DataOutputStream;
import java.net.Socket;

public class MyService {
    public void service(Socket socket) throws Exception {
        System.out.println(Thread.currentThread().getName() + ": service start...");
        try (socket; DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {
            dataOutputStream.writeBytes("HTTP/1.1 200 OK\r\n");
            dataOutputStream.writeBytes("Content-Type: text/html\r\n");
            dataOutputStream.writeBytes("Cache-Control: no-cache\r\n");
            dataOutputStream.writeBytes("Pragma: no-cache\r\n");
            dataOutputStream.writeBytes("Expires: -1\r\n");
            dataOutputStream.writeBytes("\r\n");
            dataOutputStream.writeBytes("<html><head><body>");
            dataOutputStream.writeBytes("start</br>");
            for (int i = 0; i < 5; i++) {
                dataOutputStream.writeBytes("hello world " + i + "</br>");
                dataOutputStream.flush();
                Thread.sleep(1000);
            }
            dataOutputStream.writeBytes("end</br>");
            dataOutputStream.writeBytes("</head></body></html>");
            dataOutputStream.flush();
        }
        System.out.println(Thread.currentThread().getName() + ": service end");

    }
}
