import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Abstract base class for all characters in the game.
 * Demonstrates abstraction, inheritance, and shared behavior used by subclasses.
 */
public abstract class GameCharacter extends Thread {
    protected final String characterName;
    protected final List<String> inventory = Collections.synchronizedList(new ArrayList<>());
    protected final Random rand = new Random();
    protected int battlesWon = 0;

    public GameCharacter(String name) {
        this.characterName = name;
        setName(name);
    }

    public String getCharacterName() {
        return characterName;
    }

    public List<String> getInventory() {
        return inventory;
    }

    public int getBattlesWon() {
        return battlesWon;
    }

    protected synchronized void recordWin() {
        battlesWon++;
    }

    /**
     * Subclasses implement this to define a single action or a sequence of actions.
     */
    protected abstract void actOnce() throws InterruptedException;

    @Override
    public void run() {
        // If a phaser is configured, participate in the part-based progression.
        if (RetroMultithreadingAdventure.partPhaser != null && RetroMultithreadingAdventure.totalParts > 0 && RetroMultithreadingAdventure.roundsPerPart > 0) {
            RetroMultithreadingAdventure.partPhaser.register();
            try {
                GameWorld.log(characterName + " enters the adventure.");
                for (int part = 1; part <= RetroMultithreadingAdventure.totalParts; part++) {
                    for (int r = 0; r < RetroMultithreadingAdventure.roundsPerPart; r++) {
                        actOnce();
                    }
                    GameWorld.log(characterName + " finished.");
                    // Arrive at phaser and wait for main to signal continuation
                    RetroMultithreadingAdventure.partPhaser.arriveAndAwaitAdvance();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                GameWorld.log(characterName + "'s adventure was interrupted!");
            } finally {
                try {
                    RetroMultithreadingAdventure.partPhaser.arriveAndDeregister();
                } catch (Exception ignored) {}
            }
        } else {
            // Fallback simple threaded behavior (5 rounds)
            try {
                for (int i = 0; i < 5; i++) actOnce();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                GameWorld.log(characterName + "'s adventure was interrupted!");
            }
        }
    }
}
