package characters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import core.GameWorld;
import core.RetroMultithreadingAdventure;

/**
 * Abstract base class for all characters in the game.
 */
public abstract class GameCharacter extends Thread {
    protected final String characterName;
    protected final String initialAdvice;
    protected final List<String> inventory = Collections.synchronizedList(new ArrayList<>());
    protected final Random rand = new Random();
    protected int battlesWon = 0;

    public GameCharacter(String name) {
        this(name, null);
    }

    public GameCharacter(String name, String advice) {
        this.characterName = name;
        this.initialAdvice = advice;
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

    protected abstract void actOnce() throws InterruptedException;

    @Override
    public void run() {
        if (RetroMultithreadingAdventure.partPhaser != null && RetroMultithreadingAdventure.totalParts > 0 && RetroMultithreadingAdventure.roundsPerPart > 0) {
            RetroMultithreadingAdventure.partPhaser.register();
            try {
                GameWorld.log(characterName + " enters the adventure.");
                for (int part = 1; part <= RetroMultithreadingAdventure.totalParts; part++) {
                    for (int r = 0; r < RetroMultithreadingAdventure.roundsPerPart; r++) {
                        actOnce();
                    }
                    GameWorld.log(characterName + " finished.");
                    // Notify the main thread that this character has arrived
                    RetroMultithreadingAdventure.notifyCharacterArrived();
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
            try {
                for (int i = 0; i < 5; i++) actOnce();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                GameWorld.log(characterName + "'s adventure was interrupted!");
            }
        }

        // Hook for subclasses to perform actions after the run finishes
        try {
            onReturn();
        } catch (Exception ignored) {}
    }

    /**
     * Called after the character has completed its run. Subclasses may override.
     */
    protected void onReturn() {
        // default no-op
    }
}
