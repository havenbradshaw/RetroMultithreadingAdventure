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
    public static volatile String latestAdvice = null;

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
        runGameWithParts(3, 2);
    }

    public static void runGameWithParts(int totalParts, int roundsPerPart) {
        GameWorld.initDefaultLoot();

        latestAdvice = ApiClient.fetchAdvice();
        GameWorld.log("[World] Advice of the day: " + (latestAdvice == null ? "(none)" : '"' + latestAdvice + '"'));

        RetroMultithreadingAdventure.totalParts = totalParts;
        RetroMultithreadingAdventure.roundsPerPart = roundsPerPart;

        partPhaser = new Phaser(1);

        List<GameCharacter> characters = new ArrayList<>();
        characters.add(new Knight());
        characters.add(new Wizard());
        characters.add(new Thief());

        characters.forEach(Thread::start);

        for (int part = 1; part <= totalParts; part++) {
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
