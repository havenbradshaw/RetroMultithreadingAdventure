import java.util.Random;

public class Thief extends Thread {
    @Override
    public void run() {
        System.out.println("The desperate Thief sets out on a quest to slay the dragon!");
        String advice = RetroMultithreadingAdventure.latestAdvice;
        if (advice != null) {
            System.out.println("Thief quietly notes the advice: \"" + advice + "\"");
        }
        Random rand = new Random();

        try {
            for(int i = 1; i <= 5; i++) {
                Thread.sleep(rand.nextInt(500, 1500));
            }
        } catch (InterruptedException e) {
            System.err.println("Thiefs adventure was interrupted!");
        }
        if (advice != null && advice.toLowerCase().contains("quiet")) {
            System.out.println("The Thief returns victorious and remarkably silent about it.");
        } else {
            System.out.println("The Thief returns victorious!");
        }
    }
    }