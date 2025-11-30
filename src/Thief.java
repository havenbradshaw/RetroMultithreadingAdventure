import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Thief extends GameCharacter {
    public Thief() {
        super("Thief");
    }

    @Override
    public void run() {
        GameWorld.log("The desperate Thief sets out on a quest to slay the dragon!");
        String advice = RetroMultithreadingAdventure.latestAdvice;
        if (advice != null) {
            GameWorld.log("Thief quietly notes the advice: \"" + advice + "\"");
        }

        try {
            for (int i = 0; i < 5; i++) {
                actOnce();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            GameWorld.log("Thief's adventure was interrupted!");
        }

        if (advice != null && advice.toLowerCase().contains("quiet")) {
            GameWorld.log("The Thief returns victorious and remarkably silent about it.");
        } else {
            GameWorld.log("The Thief returns victorious!");
        }
    }

    @Override
    protected void actOnce() throws InterruptedException {
        // Thief tries to pilfer loot; uses tryLock to simulate stealthy attempts
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
            // Failed to acquire lock — stealthy fallback
            GameWorld.log("Thief couldn't get close enough to the loot this round.");
        }

        if (stole && rand.nextDouble() < 0.4) {
            recordWin();
            GameWorld.log("Thief successfully escapes with the prize.");
        }
    }
}