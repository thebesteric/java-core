package org.example.thread.thread_design_pattern.two_phase_terminate.game;

import lombok.Getter;

public abstract class GameSystem {

    @Getter
    private String name;

    @Getter
    private Integer refreshInterval;

    public GameSystem(String name, int refreshInterval) {
        this.name = name;
        this.refreshInterval = refreshInterval;
    }

    public abstract void start();

    public abstract void finish();
}
