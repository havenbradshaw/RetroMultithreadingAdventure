import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import java.net.URL;

/**
 * Simple JavaFX UI wrapper for the RetroMultithreadingAdventure.
 * Shows a centered story label and uses clicks/keys to control flow.
 */
public class GuiApp extends Application {
    private volatile boolean gameStarted = false;

    @Override
    public void start(Stage stage) {
        // Center area: tiled pixel-art background with centered story text
        Label centerLabel = new Label("Click or press Enter to start the adventure");
        centerLabel.setId("centerLabel");
        centerLabel.getStyleClass().add("center-text");
        StackPane centerStack = new StackPane(centerLabel);
        centerStack.getStyleClass().add("pixel-bg");
        StackPane.setAlignment(centerLabel, Pos.CENTER);

        BorderPane root = new BorderPane();
        root.setCenter(centerStack);

        Scene scene = new Scene(root, 800, 600);
        // Attach CSS (resource placed at /ui.css)
        URL css = getClass().getResource("/ui.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }

        // Input handlers: click or Enter to start
        scene.setOnMouseClicked(e -> {
            if (!gameStarted) startGame(centerLabel);
        });
        scene.setOnKeyPressed(e -> {
            KeyCode code = e.getCode();
            if (code == KeyCode.ENTER) {
                if (!gameStarted) startGame(centerLabel);
            }
        });

        stage.setTitle("Retro Multithreading Adventure - GUI");
        stage.setScene(scene);
        stage.show();
    }

    private void startGame(Label centerLabel) {
        if (gameStarted) return;
        gameStarted = true;
        GameWorld.uiLogger = msg -> Platform.runLater(() -> {
            if (msg == null) return;

            // Internal signal used to trigger the 3-second loading animation
            // without printing any visible marker to the console.
            if ("__NEXT_SIGNAL__".equals(msg)) {
                // Use the current label text as the message to display while
                // animating (it should already contain the last logged message).
                final String displayText = (centerLabel.getText() == null || centerLabel.getText().isEmpty()) ? "Completed." : centerLabel.getText();

                // Animate dots for 3 seconds (one dot added each second), then continue.
                new Thread(() -> {
                    try {
                        for (int i = 1; i <= 3; i++) {
                            final String dots = new String(new char[i]).replace('\0', '.');
                            Platform.runLater(() -> centerLabel.setText(displayText + "\n" + dots));
                            Thread.sleep(1000);
                        }
                        RetroMultithreadingAdventure.continueToNextPart();
                    } catch (InterruptedException ignored) {
                        Thread.currentThread().interrupt();
                    }
                }, "UiPartAnimator").start();
                return;
            }

            // Normal log messages are displayed as-is.
            centerLabel.setText(msg);
        });

        new Thread(() -> {
            RetroMultithreadingAdventure.runGame();
            // After completion, show summary text
            Platform.runLater(() -> centerLabel.setText("Adventure complete. Restart the app to play again."));
        }, "GameRunner").start();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
