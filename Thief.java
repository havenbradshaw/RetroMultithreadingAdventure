import java.util.Random;

public class Thief extends Thread {
    @Override
    public void run() {
        System.out.println("The desperate Thief sets out on a quest to slay the dragon!");
        Random rand = new Random();

        try {
            for(int i = 1; i <= 5; i++) {
                Thread.sleep(rand.nextInt(1000) + 500);
            }
        } catch (InterruptedException e) {
            System.err.println("Thiefs adventure was interrupted!");
        }
        System.out.println("The Thief returns victorious!");
    }
    }