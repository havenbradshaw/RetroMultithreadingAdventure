public class RetroMultithreadingAdventure extends Thread {
    public static void main(String[] args) {
        Knight knight = new Knight();
        Wizard wizard = new Wizard();
        Thief thief = new Thief();

        knight.start();
        wizard.start();
        thief.start();

        try {
            knight.join();
            wizard.join();
            thief.join();
        } 
        catch (InterruptedException e){
            System.err.println("Their adventure was inturrupted!");
        }
    }
}