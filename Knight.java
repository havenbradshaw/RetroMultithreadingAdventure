import java.util.Random;

public class Knight extends Thread {
    @Override
    public void run() {
        System.out.println("The brave Knight sets out on a quest to slay the dragon!");
        Random rand = new Random();

        try {
            for(int i = 1; i <= 5; i++) {
                Thread.sleep(rand.nextInt(500, 1500));
            }
        } catch (InterruptedException e) {
            System.err.println("Knights adventure was interrupted!");
        }
        System.out.println("The Knight returns victorious!");
    }
}