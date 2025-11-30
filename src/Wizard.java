public class Wizard extends GameCharacter {
    public Wizard() {
        super("Wizard");
        GameWorld.log("The powerful Wizard sets out on a quest to slay the dragon!");
        String advice = RetroMultithreadingAdventure.latestAdvice;
        if (advice != null) {
            GameWorld.log("Wizard ponders the advice: \"" + advice + "\"");
        }
    }
    @Override
    protected void actOnce() throws InterruptedException {
        // Wizard casts a spell and sometimes finds mana or a rune
        Thread.sleep(rand.nextInt(300, 800));
        boolean success = rand.nextDouble() < 0.5;
        if (success) {
            recordWin();
            GameWorld.log("Wizard conjures a potent spell and succeeds!");
            // Try to take a mana-related loot if available
            String loot = GameWorld.takeLoot("Wizard");
            if (loot != null) {
                inventory.add(loot);
                GameWorld.log("Wizard collects: " + loot);
            }
        } else {
            GameWorld.log("Wizard's spell fizzles but knowledge is gained.");
        }
    }
}