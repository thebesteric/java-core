package org.example.thread.thread_design_pattern.two_phase_terminate.game;

import java.util.Date;

public class NoticeSystem extends GameSystem {

    public NoticeSystem(String name, int refreshInterval) {
        super(name, refreshInterval);
    }

    @Override
    public void start() {
        System.out.println(Thread.currentThread().getName() + "> " + new Date() + " <" + getName() + "> 正在运行中...");
    }

    @Override
    public void finish() {
        System.out.println("<" + getName() + "> 已关闭");
    }
}
