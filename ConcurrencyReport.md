# Concurrency Report
**RetroMultithreadingAdventure**  

---

## Thread Interaction

The RetroMultithreadingAdventure game uses **four concurrent threads** that work together to create an interactive experience:

1. **Main Game Thread** - Controls the overall game flow, waiting for character threads to complete each part before advancing
2. **Knight Thread** - Performs combat actions concurrently with other characters
3. **Wizard Thread** - Executes magical actions in parallel
4. **Thief Thread** - Carries out stealth actions simultaneously
5. **UI Consumer Thread** - Handles displaying messages to the player

These threads interact by sharing several resources:
- **Loot Pool**: A shared collection of items (Golden Chalice, Emerald Dagger, etc.) that characters compete to collect
- **Event Log**: A shared list where all threads record game events
- **Message Queue**: Transfers log messages from game threads to the UI thread for display

The threads follow a synchronized pattern for each game part:
1. All three character threads execute their actions simultaneously
2. When finished, each character signals they've arrived at a checkpoint
3. The main thread waits for all characters to arrive
4. The UI displays the results and waits for the player to click "continue"
5. All threads synchronize and proceed together to the next part

This ensures characters act concurrently within each part, but parts execute sequentially to maintain game narrative flow.

---

**Race Condition: Concurrent Loot Access**

When multiple character threads try to take items from the shared loot pool at the same time, a race condition can occur. Without proper synchronization, two threads might:
- Attempt to grab the same item simultaneously
- One thread removes an item while another is checking if it exists
- Cause the item to be lost or counted incorrectly

**Solution: ReentrantLock**

The `GameWorld.takeLoot()` method uses a `ReentrantLock` to ensure only one thread can access the loot pool at a time:

This creates a **critical section** where only one character can take loot at a time. The `try-finally` block ensures the lock is always released, even if an error occurs. This prevents race conditions and ensures each item goes to exactly one character.

---

## Thread Coordination Mechanism: Phaser

The game uses a **Phaser** to coordinate all character threads across multiple game parts. Think of it like a checkpoint system in a race where all runners must arrive before anyone can continue.

**How it works:**

1. **Registration**: When each character thread starts, it registers with the phaser and after completing actions for a part, each character arrives at the barrier and waits. The phaser blocks each thread until all registered threads arrive. Once everyone arrives, all threads are released simultaneously to continue to the next part.

- **Reusable**: Unlike a CountDownLatch (which works once), a Phaser can be reused for all three game parts
- **Dynamic**: Threads can register and deregister during execution
- **Coordinated**: Ensures all characters stay synchronized—no character gets ahead or falls behind

This mechanism ensures the game maintains its narrative structure while allowing concurrent action within each part, creating both performance benefits and proper story pacing.
