import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.Phaser;
import java.util.concurrent.Semaphore;

public class RetroMultithreadingAdventure extends Thread {
    /**
     * The last piece of advice (fetched from an external API) shared with all characters.
     * Made volatile because it's written by the main thread and read by multiple character threads.
     */
    public static volatile String latestAdvice = null;

    // Synchronization primitives used for GUI-driven part progression
    public static Phaser partPhaser = null;
    private static final Semaphore continueSemaphore = new Semaphore(0);
    // configured by runGameWithParts
    public static int totalParts = 0;
    public static int roundsPerPart = 0;

    public static void main(String[] args) {
        // Run via runGame so GUI or command-line callers can reuse the logic.
        runGame();
    }

    /**
     * Programmatic entry point for running the game logic. Useful for launching from a GUI.
     * This method blocks until the adventure completes.
     */
    public static void runGame() {
        // Default: run 3 parts of the adventure with 2 rounds per part
        runGameWithParts(3, 2);
    }

    /**
     * Run the game broken into parts. After each part completes, the game will wait
     * until the GUI signals to continue to the next part.
     * @param totalParts number of parts
     * @param roundsPerPart how many actOnce() rounds each character performs per part
     */
    public static void runGameWithParts(int totalParts, int roundsPerPart) {
        GameWorld.initDefaultLoot();

        latestAdvice = ApiClient.fetchAdvice();
        GameWorld.log("[World] Advice of the day: " + (latestAdvice == null ? "(none)" : '"' + latestAdvice + '"'));

        // Configure global settings so character threads know to participate
        RetroMultithreadingAdventure.totalParts = totalParts;
        RetroMultithreadingAdventure.roundsPerPart = roundsPerPart;

        // Prepare phaser: main registered, characters will register themselves
        partPhaser = new Phaser(1);

        List<GameCharacter> characters = new ArrayList<>();
        characters.add(new Knight());
        characters.add(new Wizard());
        characters.add(new Thief());

        // Start characters (they should register with partPhaser in their constructors)
        characters.forEach(Thread::start);

        // Orchestrate parts
        for (int part = 1; part <= totalParts; part++) {
            // Wait for characters to finish their rounds and arrive at the phaser
            int expectedArrivals = Math.max(0, partPhaser.getRegisteredParties() - 1); // exclude main
            while (partPhaser.getArrivedParties() < expectedArrivals) {
                try { Thread.sleep(50); } catch (InterruptedException ignored) {}
            }

            // Signal the UI to advance to the next step. This does not print
            // a visible marker to the console so logs remain clean.
            GameWorld.signalNextToUi();

            // Wait for GUI to release continue
            try {
                continueSemaphore.acquire();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Now advance the phaser so characters can continue to next part
            partPhaser.arriveAndAwaitAdvance();
        }

        // All parts done — wait for characters to finish
        for (GameCharacter c : characters) {
            try { c.join(); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
        }

        // Summarize results
        String summary = characters.stream()
                .map(c -> c.getCharacterName() + ": wins=" + c.getBattlesWon() + ", items=" + c.getInventory().size())
                .collect(Collectors.joining(" | "));

        GameWorld.log("--- Adventure Summary ---");
        GameWorld.log(summary);
    }

    /** Called by the GUI to allow the next part to proceed. */
    public static void continueToNextPart() {
        continueSemaphore.release();
    }
}