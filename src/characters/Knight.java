package characters;

import core.GameWorld;
// no direct dependency on RetroMultithreadingAdventure required here

public class Knight extends GameCharacter {
    public Knight() { this("Knight", null); }

    public Knight(String name) { this(name, null); }

    public Knight(String name, String advice) {
        super(name);
        GameWorld.log("The brave " + getCharacterName() + " sets out on a quest to slay the dragon!");
        if (advice != null) {
            GameWorld.log(getCharacterName() + " hears a piece of advice: \"" + advice + "\"");
        }
    }

    @Override
    protected void actOnce() throws InterruptedException {
        Thread.sleep(rand.nextInt(400, 900));
        boolean won = rand.nextDouble() < 0.6;
        if (won) {
            recordWin();
            GameWorld.log(getCharacterName() + " wins a skirmish!");
            String loot = GameWorld.takeLoot(getCharacterName());
            if (loot != null) {
                inventory.add(loot);
                GameWorld.log(getCharacterName() + " loots: " + loot);
            }
        } else {
            GameWorld.log(getCharacterName() + " struggles but survives the encounter.");
        }
    }
}
