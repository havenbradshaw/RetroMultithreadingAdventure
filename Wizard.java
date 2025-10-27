import java.util.Random;

public class Wizard extends Thread {
    @Override
    public void run() {
        System.out.println("The powerful Wizard sets out on a quest to slay the dragon!");
        Random rand = new Random();

        try {
            for(int i = 1; i <= 5; i++) {
                Thread.sleep(rand.nextInt(500, 1500));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Wizards adventure was interrupted!");
        }
        System.out.println("The Wizard returns victorious!");
    }
    }