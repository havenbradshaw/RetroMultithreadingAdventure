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

Run the compile and run steps in the previous section to try the game and see the concurrent threads in action. If you'd like, I can also:

- Add a Gradle wrapper for simplified builds.
- Add unit tests for `GameWorld` concurrency behaviors.
- Improve `ApiClient` with retries and a JSON library for robust parsing.
## Technologies used

- Language: Java
- No external libraries required — uses only the Java standard library and basic threading APIs
- Project layout:
	- `src/` — source files (e.g., `RetroMultithreadingAdventure.java`, `Knight.java`, `Wizard.java`, `Thief.java`)

## Setup — compile and run (PowerShell / Windows)

Prerequisites

- Install a Java Development Kit (JDK) and ensure `javac`/`java` are on your PATH.

Quick start (from the repository root) — compile and run:

```powershell
# Compile all Java sources into a `bin` directory
javac -d bin src/*.java

# Run the main class (adjust the package name if sources are packaged)
java -cp bin RetroMultithreadingAdventure
```

## JavaFX GUI (optional)

This repository includes a minimal JavaFX UI wrapper `src/GuiApp.java` that displays real-time game logs and provides a `Start Adventure` button.

JavaFX is not part of the JDK on many platforms. To run the GUI from PowerShell you must install OpenJFX and provide the JavaFX SDK `lib` directory on the module path. Example steps (replace the path with your JavaFX SDK `lib` path):

```powershell
# Set this to the JavaFX SDK lib folder on your machine
$env:FX = 'C:\path\to\javafx-sdk-20\lib'

# Compile (include JavaFX modules)
javac --module-path $env:FX --add-modules javafx.controls -d bin src/*.java

# Run the JavaFX GUI
java --module-path $env:FX --add-modules javafx.controls -cp bin GuiApp
```

Notes:
- If you use an IDE (IntelliJ, Eclipse, VS Code) you can add the JavaFX SDK to the project's library settings and run `GuiApp` directly.
- If you prefer a one-command build/run, I can add a `build.gradle` and Gradle wrapper that downloads JavaFX automatically and simplifies these commands.
If you prefer a one-command build/run, I added a `build.gradle` that uses the `org.openjfx` Gradle plugin to obtain JavaFX.

### Run with Gradle

If you have Gradle installed, from the repository root run:

```powershell
# Build and run the JavaFX GUI (the plugin will fetch JavaFX automatically)
gradle run
```

If you'd like a project-local Gradle wrapper (recommended), generate it once locally with Gradle installed:

```powershell
# Generate the Gradle wrapper (run once)
gradle wrapper

# Then start the GUI using the wrapper (on Windows)
.\gradlew run
```

The Gradle `run` task is configured to launch `GuiApp` (the JavaFX UI). If you prefer the console version instead, use the `javac`/`java` commands shown earlier.

### Run with Maven

If you prefer Maven, a `pom.xml` has been added to the repository and is configured to run the JavaFX GUI using the `javafx-maven-plugin`.

Requirements: Maven installed and a JDK (11+ recommended). From the repository root run:

```powershell
# Build and run the JavaFX GUI using the OpenJFX Maven plugin
mvn javafx:run
```

If you want to run the console version (no JavaFX) via Maven:

```powershell
# Compile
mvn compile

# Run the console GameEngine
mvn exec:java -Dexec.mainClass="RetroMultithreadingAdventure"
```

Notes:
- The `javafx-maven-plugin` will download platform-appropriate JavaFX libraries automatically based on your JDK.
- If Maven reports missing JavaFX platform binaries, ensure your JDK version and the `javafx.version` (set to 20) are compatible, or adjust the `pom.xml` properties.

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
    - The only AI generation and alteration that I utilized in this project was to set up the API class and implementation. 
    - I found the API and provided the information, CoPilot took that and implemented it because of my unfamiliarity with API's and the syntax that surrounds them.
