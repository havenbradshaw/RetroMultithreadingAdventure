import java.util.Random;

public class Knight extends GameCharacter {
    public Knight() {
        super("Knight");
        GameWorld.log("The brave Knight sets out on a quest to slay the dragon!");
        String advice = RetroMultithreadingAdventure.latestAdvice;
        if (advice != null) {
            GameWorld.log("Knight hears a piece of advice: \"" + advice + "\"");
        }
    }
    @Override
    protected void actOnce() throws InterruptedException {
        // Simulate a battle attempt
        Thread.sleep(rand.nextInt(400, 900));
        boolean won = rand.nextDouble() < 0.6; // Knight is strong
        if (won) {
            recordWin();
            GameWorld.log("Knight wins a skirmish!");
            String loot = GameWorld.takeLoot("Knight");
            if (loot != null) {
                inventory.add(loot);
                GameWorld.log("Knight loots: " + loot);
            }
        } else {
            GameWorld.log("Knight struggles but survives the encounter.");
        }
    }
}