package org.example.thread_design_pattern.two_phase_terminate.game;

import lombok.Getter;

public final class GameSysState {

    public volatile static GameSysState instance;

    @Getter
    private volatile boolean shutdown = false;

    private GameSysState() {
        super();
    }

    public static GameSysState getInstance() {
        if (instance == null) {
            synchronized (GameSysState.class) {
                if (instance == null) {
                    instance = new GameSysState();
                }
            }
        }
        return instance;
    }

    public void shutdown() {
        this.shutdown = true;
    }
}
