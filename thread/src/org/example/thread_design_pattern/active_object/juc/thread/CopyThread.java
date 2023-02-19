package org.example.thread_design_pattern.active_object.juc.thread;

import org.example.thread_design_pattern.active_object.juc.ActiveObject;

public class CopyThread extends Thread {

    private final String content;
    private final int count;
    private final ActiveObject proxy;

    private static int threadNumber;

    public CopyThread(String content, int count, ActiveObject proxy) {
        super("复印线程-" + getThreadNumber());
        this.content = content;
        this.count = count;
        this.proxy = proxy;
    }

    private static int getThreadNumber() {
        return threadNumber++;
    }

    @Override
    public void run() {
        try {
            for (int i = 1; i <= count; i++) {
                proxy.copyContent(content);
                System.out.println(getName() + ": 第 " + i + " 次复印 => " + content);
                Thread.sleep(200);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
