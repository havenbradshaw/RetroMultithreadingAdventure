public class RetroMultithreadingAdventure extends Thread {
    /**
     * The last piece of advice (fetched from an external API) shared with all characters.
     * Made volatile because it's written by the main thread and read by multiple character threads.
     */
    public static volatile String latestAdvice = null;

    public static void main(String[] args) {
        // Fetch an external "advice" before starting the characters. The ApiClient does an HTTP GET
        // and returns a short string. If the call fails, a fallback message is used.
        latestAdvice = ApiClient.fetchAdvice();
        System.out.println("[World] Advice of the day: " + (latestAdvice == null ? "(none)" : '"' + latestAdvice + '"'));

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
            Thread.currentThread().interrupt();
        }
    }
}