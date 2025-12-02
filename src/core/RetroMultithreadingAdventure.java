package core;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.Phaser;
import java.util.concurrent.Semaphore;
import characters.Knight;
import characters.Thief;
import characters.Wizard;
import characters.GameCharacter;

public class RetroMultithreadingAdventure extends Thread {
    public static Phaser partPhaser = null;
    private static final Semaphore continueSemaphore = new Semaphore(0);
    // Monitor used to wait for characters to arrive at the phaser without busy-waiting
    private static final Object arrivedMonitor = new Object();
    public static int totalParts = 0;
    public static int roundsPerPart = 0;

    public static void main(String[] args) {
        runGame();
    }

    public static void runGame() {
        // legacy default behavior: create a simple config with defaults
        GameConfig cfg = new GameConfig(3, 2, "Knight", "Wizard", "Thief", ApiClient.fetchAdvice());
        runGame(cfg);
    }

    public static void runGame(GameConfig cfg) {
        GameWorld.initDefaultLoot();

        String latestAdvice = cfg.latestAdvice != null ? cfg.latestAdvice : ApiClient.fetchAdvice();
        GameWorld.log("[World] Advice of the day: " + (latestAdvice == null ? "(none)" : '"' + latestAdvice + '"'));

        partPhaser = new Phaser(1);
        RetroMultithreadingAdventure.totalParts = cfg.totalParts;
        RetroMultithreadingAdventure.roundsPerPart = cfg.roundsPerPart;

        List<GameCharacter> characters = new ArrayList<>();
        characters.add(new Knight(cfg.knightName, latestAdvice));
        characters.add(new Wizard(cfg.wizardName, latestAdvice));
        characters.add(new Thief(cfg.thiefName, latestAdvice));

        characters.forEach(Thread::start);

        for (int part = 1; part <= cfg.totalParts; part++) {
            int expectedArrivals = Math.max(0, partPhaser.getRegisteredParties() - 1);
            waitForArrivals(expectedArrivals);

            GameWorld.signalNextToUi();

            try {
                continueSemaphore.acquire();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            partPhaser.arriveAndAwaitAdvance();
        }

        for (GameCharacter c : characters) {
            try { c.join(); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
        }

        String summary = characters.stream()
                .map(c -> c.getCharacterName() + ": wins=" + c.getBattlesWon() + ", items=" + c.getInventory().size())
                .collect(Collectors.joining(" | "));

        GameWorld.log("--- Adventure Summary ---");
        GameWorld.log(summary);
    }

    public static void continueToNextPart() {
        continueSemaphore.release();
    }

    /**
     * Called by characters when they arrive at the phaser so the main thread
     * waiting in `waitForArrivals` can wake up without busy-waiting.
     */
    public static void notifyCharacterArrived() {
        synchronized (arrivedMonitor) {
            arrivedMonitor.notifyAll();
        }
    }

    /**
     * Wait (without busy-waiting) until the specified number of arrivals
     * have been observed on the shared Phaser.
     */
    public static void waitForArrivals(int expectedArrivals) {
        synchronized (arrivedMonitor) {
            while (partPhaser.getArrivedParties() < expectedArrivals) {
                try {
                    arrivedMonitor.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}
