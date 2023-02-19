package org.example.thread.thread_design_pattern.two_phase_terminate.game;

import java.util.List;

public class GameServerTest {
    public static void main(String[] args) throws InterruptedException {
        GameServer gameServer = new GameServer();
        gameServer.addSystem(new ActivitySystem("公告子系统", 1000));
        gameServer.addSystem(new NoticeSystem("活动子系统", 500));
        List<Thread> threads = gameServer.start();

        Thread.sleep(5000);

        System.out.println("游戏服务器准备关闭...");
        gameServer.shutdown(threads);

    }
}
