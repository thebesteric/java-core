package org.example.thread.thread_design_pattern.active_object.thread;

import org.example.thread.thread_design_pattern.active_object.ActiveObject;
import org.example.thread.thread_design_pattern.active_object.result.PrintOrCopyResult;

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
                    PrintOrCopyResult futurePrintOrCopyResult = proxy.printContent(content.substring(0, j + 1));
                    String result = futurePrintOrCopyResult.getResult(); // 阻塞直到获取到结果
                    System.out.println(getName() + ": 打印 => " + result);
                    Thread.sleep(100);
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
