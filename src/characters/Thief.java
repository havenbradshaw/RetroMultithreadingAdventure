package characters;

import core.GameWorld;
import core.RetroMultithreadingAdventure;

public class Thief extends GameCharacter {
    public Thief() {
        super("Thief");
    }

    @Override
    public void run() {
        String advice = RetroMultithreadingAdventure.latestAdvice;
        GameWorld.log("The desperate Thief sets out on a quest to slay the dragon!");
        if (advice != null) {
            GameWorld.log("Thief quietly notes the advice: \"" + advice + "\"");
        }

        if (RetroMultithreadingAdventure.partPhaser != null && RetroMultithreadingAdventure.totalParts > 0 && RetroMultithreadingAdventure.roundsPerPart > 0) {
            RetroMultithreadingAdventure.partPhaser.register();
            try {
                for (int part = 1; part <= RetroMultithreadingAdventure.totalParts; part++) {
                    for (int r = 0; r < RetroMultithreadingAdventure.roundsPerPart; r++) {
                        actOnce();
                    }
                    GameWorld.log(getCharacterName() + " finished.");
                    RetroMultithreadingAdventure.partPhaser.arriveAndAwaitAdvance();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                GameWorld.log("Thief's adventure was interrupted!");
            } finally {
                try { RetroMultithreadingAdventure.partPhaser.arriveAndDeregister(); } catch (Exception ignored) {}
            }
        } else {
            try {
                for (int i = 0; i < 5; i++) actOnce();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                GameWorld.log("Thief's adventure was interrupted!");
            }
        }

        if (advice != null && advice.toLowerCase().contains("quiet")) {
            GameWorld.log("The Thief returns victorious and remarkably silent about it.");
        } else {
            GameWorld.log("The Thief returns victorious!");
        }
    }

    @Override
    protected void actOnce() throws InterruptedException {
        Thread.sleep(rand.nextInt(200, 700));
        boolean stole = false;
        if (GameWorld.lootLock.tryLock()) {
            try {
                String item = GameWorld.lootPool.poll();
                if (item != null) {
                    inventory.add(item);
                    GameWorld.log("Thief quietly steals: " + item);
                    stole = true;
                }
            } finally {
                GameWorld.lootLock.unlock();
            }
        } else {
            GameWorld.log("Thief couldn't get close enough to the loot this round.");
        }

        if (stole && rand.nextDouble() < 0.4) {
            recordWin();
            GameWorld.log("Thief successfully escapes with the prize.");
        }
    }
}
