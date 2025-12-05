# RetroMultithreadingAdventure

A small Java text-adventure that demonstrates basic multithreading and concurrent behavior through simple character classes (Knight, Thief, Wizard) and a main game loop.

## Key features and intended use cases


## Capstone: Requirements mapping

This project implements the capstone assignment requirements. Below is a short mapping from each requirement to the implementation in this repository.

- **Abstraction / Inheritance / Polymorphism**: Implemented via the abstract class `GameCharacter` (`src/GameCharacter.java`) which defines shared attributes and the abstract method `actOnce()`. `Knight`, `Wizard`, and `Thief` extend `GameCharacter` and provide unique behavior.

- **Multithreading**: Each character runs concurrently as a separate thread (classes extend `GameCharacter` which extends `Thread`). The `RetroMultithreadingAdventure` class acts as the `GameEngine` and starts/join()s threads (`src/RetroMultithreadingAdventure.java`).

- **Shared resources & concurrency control**: `GameWorld` (`src/GameWorld.java`) contains a shared `lootPool` (a `ConcurrentLinkedQueue`) and a `ReentrantLock` (`lootLock`) demonstrating safe concurrent access. Methods like `takeLoot()` use locking and synchronized logging.

- **Arrays / Collections**: Characters maintain inventories using `List<String>` (synchronized list). The main engine uses a `List<GameCharacter>` to store and start characters.

- **Lambdas / Streams**: Post-game summary and event printing use streams and lambda expressions in `RetroMultithreadingAdventure.java`.

- **Random events / Timing**: Characters use `Random` and `Thread.sleep()` inside their `actOnce()` implementations to create randomized events and timing.

- **Logging / Narrative**: `GameWorld.log()` centralizes event logging and prints narrative text for each character's events.

- **External API usage**: `ApiClient.fetchAdvice()` (see `src/ApiClient.java`) demonstrates calling an external API, parsing a small JSON response, and sharing the data via `RetroMultithreadingAdventure.latestAdvice`.

## Technologies used

- Language: Java
- No external libraries required — uses only the Java standard library and basic threading APIs
- Project layout:
	- `src/` — source files (e.g., `RetroMultithreadingAdventure.java`, `Knight.java`, `Wizard.java`, `Thief.java`)

## Setup — compile and run (Maven)

Prerequisites

- Install a Java Development Kit (JDK) and ensure `javac`/`java` are on your PATH.

Quick start (from the repository root) — compile and run:

```powershell
# Compile all Java sources into a `bin` directory
javac -d bin src/*.java

# Run the main class and the GUI(adjust the package name if sources are packaged)
mvn javafx:run
```

## Development and testing

- You can open the project in any Java-capable IDE (IntelliJ IDEA, Eclipse, VS Code with Java extensions). Import the `src` folder as a Java project and run the `RetroMultithreadingAdventure` main class.
- For experiments, try adding logging, increasing thread counts, or introducing synchronization primitives to observe different behaviors.
 
## API methods (method-level documentation)

This project includes a tiny, dependency-free API client used to fetch a short piece of advice from a public API. The advice is fetched once at program start and made available to every character thread.

- ApiClient.fetchAdvice()
	- Signature: `public static String fetchAdvice()`
	- Description: Performs an HTTP GET to `https://api.adviceslip.com/advice` and attempts to extract the short advice text from the JSON response. Returns the advice string on success, or `null` if the request fails or the response cannot be parsed.
	- Return value: `String` (advice) or `null` on error.

- How the app uses the API
	- The main class `RetroMultithreadingAdventure` calls `ApiClient.fetchAdvice()` once at startup and stores the result in the static field `RetroMultithreadingAdventure.latestAdvice`.
	- Each character thread (`Knight`, `Wizard`, `Thief`) reads `RetroMultithreadingAdventure.latestAdvice` at the start of its `run()` method and prints or lightly adapts behavior based on the advice content (for example, printing the advice and selecting slightly different output messages).

- AI Reflection:
    - I utilized AI generation and alteration to set up the API class and implementation, threading, and some design choices with JavaFX. I created the classes and foundation of this program, as well as the GameCharacter abstract class. I understand all of what the AI generated and altered it heavily. Copilot kept trying to implement the JavaFX formatting in a CSS file, and it was not working so I had to hardcode everything in. I found the API and provided the link. I also found the background image and the font, and decided on the inventory items.
 