import java.util.Random;

public class Wizard extends Thread {
    @Override
    public void run() {
        System.out.println("The powerful Wizard sets out on a quest to slay the dragon!");
        String advice = RetroMultithreadingAdventure.latestAdvice;
        if (advice != null) {
            System.out.println("Wizard ponders the advice: \"" + advice + "\"");
        }
        Random rand = new Random();

        try {
            for(int i = 1; i <= 5; i++) {
                Thread.sleep(rand.nextInt(500, 1500));
                // If the advice sounds encouraging, the Wizard feels emboldened (no timing change in this demo)
                if (advice != null && (advice.toLowerCase().contains("go") || advice.toLowerCase().contains("brave"))) {
                    // simple behavioral note; could change spell power or timing in an extended demo
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Wizards adventure was interrupted!");
        }
        System.out.println("The Wizard returns victorious!");
    }
    }