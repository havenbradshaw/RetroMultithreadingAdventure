package core;

import javafx.application.Application;

/**
 * IDE-friendly bootstrap class.
 *
 * Running this class avoids the JVM's direct JavaFX Application launch path
 * that can trigger "JavaFX runtime components are missing" in some setups.
 */
public final class Launcher {
    private Launcher() {
        // Utility class
    }

    public static void main(String[] args) {
        Application.launch(GuiApp.class, args);
    }
}