package org.example.thread.thread_design_pattern.two_phase_terminate.game;

import java.util.ArrayList;
import java.util.List;

public class GameServer {

    public List<GameSystem> gameSystems = new ArrayList<>();

    public void addSystem(GameSystem gameSystem) {
        gameSystems.add(gameSystem);
    }

    public List<Thread> start() {
        List<Thread> threads = new ArrayList<>();
        for (GameSystem gameSystem : gameSystems) {
            Thread thread = new Thread(new GameTaskMgr(gameSystem));
            thread.start();
            threads.add(thread);
        }
        return threads;
    }

    public void shutdown(List<Thread> threads) {
        GameSysState.getInstance().shutdown();
        for (Thread thread : threads) {
            thread.interrupt();
        }
    }

}
