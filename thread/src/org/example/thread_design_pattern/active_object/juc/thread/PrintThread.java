package org.example.thread_design_pattern.active_object.juc.thread;

import org.example.thread_design_pattern.active_object.juc.ActiveObject;

import java.util.concurrent.Future;

public class PrintThread extends Thread {

    private final ActiveObject proxy;
    private final String content;
    private final int count;

    private static int threadNum;

    public PrintThread(String content, int count, ActiveObject proxy) {
        super("打印线程-" + getThreadNum());
        this.content = content;
        this.count = count;
        this.proxy = proxy;
    }

    private static int getThreadNum() {
        return threadNum++;
    }

    @Override
    public void run() {
        try {
            for (int i = 1; i <= count; i++) {
                System.out.println(getName() + ": 第 " + i + " 次打印");
                for (int j = 0; j < content.length(); j++) {
                    Future<String> future = proxy.printContent(content.substring(0, j + 1));
                    String result = future.get(); // 阻塞直到获取到结果
                    System.out.println(getName() + ": 打印 => " + result);
                    Thread.sleep(100);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
