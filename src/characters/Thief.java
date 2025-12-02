package characters;

import core.GameWorld;
import core.RetroMultithreadingAdventure;

public class Thief extends GameCharacter {
    public Thief() { this("Thief"); }

    public Thief(String name) {
        super(name);
        String advice = RetroMultithreadingAdventure.latestAdvice;
        GameWorld.log("The desperate " + getCharacterName() + " sets out on a quest to slay the dragon!");
        if (advice != null) {
            GameWorld.log(getCharacterName() + " quietly notes the advice: \"" + advice + "\"");
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
                    GameWorld.log(getCharacterName() + " quietly steals: " + item);
                    stole = true;
                }
            } finally {
                GameWorld.lootLock.unlock();
            }
        } else {
            GameWorld.log(getCharacterName() + " couldn't get close enough to the loot this round.");
        }

        if (stole && rand.nextDouble() < 0.4) {
            recordWin();
            GameWorld.log(getCharacterName() + " successfully escapes with the prize.");
        }
    }
}


