# RetroMultithreadingAdventure

A small Java text-adventure that demonstrates basic multithreading and concurrent behavior through simple character classes (Knight, Thief, Wizard) and a main game loop.

## Key features and intended use cases

- Simple multi-character text-adventure demonstrating threads interacting in a shared environment.
- Minimal codebase that highlights Java threading primitives and concurrency patterns (Thread, synchronized blocks / methods, shared state).
- Intended for: coursework demos, hands-on learning of Java concurrency, small experiments with thread scheduling and synchronization.

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
