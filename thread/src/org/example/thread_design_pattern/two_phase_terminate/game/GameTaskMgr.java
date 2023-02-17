package org.example.thread_design_pattern.two_phase_terminate.game;

public class GameTaskMgr implements Runnable {

    private final GameSystem gameSystem;

    public GameTaskMgr(GameSystem gameSystem) {
        this.gameSystem = gameSystem;
    }

    @Override
    public void run() {
        try {
            while (!GameSysState.getInstance().isShutdown()) {
                gameSystem.start();
                Thread.sleep(gameSystem.getRefreshInterval());
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            gameSystem.finish();
        }
    }
}
