package characters;

import core.GameWorld;
// no direct dependency on RetroMultithreadingAdventure required here

public class Wizard extends GameCharacter {
    public Wizard() { this("Wizard", null); }

    public Wizard(String name) { this(name, null); }

    public Wizard(String name, String advice) {
        super(name);
        GameWorld.log("The powerful " + getCharacterName() + " sets out on a quest to slay the dragon!");
        if (advice != null) {
            GameWorld.log(getCharacterName() + " ponders the advice: \"" + advice + "\"");
        }
    }

    @Override
    protected void actOnce() throws InterruptedException {
        Thread.sleep(rand.nextInt(300, 800));
        boolean success = rand.nextDouble() < 0.5;
        if (success) {
            recordWin();
            GameWorld.log(getCharacterName() + " conjures a potent spell and succeeds!");
            String loot = GameWorld.takeLoot(getCharacterName());
            if (loot != null) {
                inventory.add(loot);
                GameWorld.log(getCharacterName() + " collects: " + loot);
            }
        } else {
            GameWorld.log(getCharacterName() + "'s spell fizzles but knowledge is gained.");
        }
    }
}
