package core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * Shared game world resources used by multiple threads.
 */
public class GameWorld {
    public static final ConcurrentLinkedQueue<String> lootPool = new ConcurrentLinkedQueue<>();
    public static final ReentrantLock lootLock = new ReentrantLock();
    public static final List<String> eventLog = new ArrayList<>();
    public static volatile Consumer<String> uiLogger = null;

    public static void initDefaultLoot() {
        lootPool.add("Golden Chalice");
        lootPool.add("Emerald Dagger");
        lootPool.add("Mana Crystal");
        lootPool.add("Bag of Silver");
        lootPool.add("Ancient Rune");
    }

    public static String takeLoot(String taker) {
        lootLock.lock();
        try {
            String item = lootPool.poll();
            if (item != null) {
                synchronized (eventLog) {
                    eventLog.add(taker + " took " + item);
                }
            }
            return item;
        } finally {
            lootLock.unlock();
        }
    }

    public static void log(String msg) {
        synchronized (eventLog) {
            eventLog.add(msg);
        }
        System.out.println(msg);
        try {
            Consumer<String> c = uiLogger;
            if (c != null) {
                c.accept(msg);
            }
        } catch (Exception ignored) {
        }
    }

    public static void signalNextToUi() {
        try {
            Consumer<String> c = uiLogger;
            if (c != null) {
                c.accept("__NEXT_SIGNAL__");
            }
        } catch (Exception ignored) {
        }
    }

    public static List<String> getEventSnapshot() {
        synchronized (eventLog) {
            return new ArrayList<>(eventLog);
        }
    }
}
