import java.util.Random;

public class Knight extends Thread {
    @Override
    public void run() {
        System.out.println("The brave Knight sets out on a quest to slay the dragon!");
        // Read shared advice fetched by the main thread (may be null)
        String advice = RetroMultithreadingAdventure.latestAdvice;
        if (advice != null) {
            System.out.println("Knight hears a piece of advice: \"" + advice + "\"");
        }
        Random rand = new Random();

        try {
            for(int i = 1; i <= 5; i++) {
                Thread.sleep(rand.nextInt(500, 1500));
                // If the advice sounded cautionary, the Knight hesitates a bit longer.
                if (advice != null && (advice.toLowerCase().contains("don't") || advice.toLowerCase().contains("not"))) {
                    Thread.sleep(200);
                }
            }
        } catch (InterruptedException e) {
            System.err.println("Knights adventure was interrupted!");
        }
        if (advice != null && advice.toLowerCase().contains("brave")) {
            System.out.println("The Knight returns more confident and victorious!");
        } else {
            System.out.println("The Knight returns victorious!");
        }
    }
}