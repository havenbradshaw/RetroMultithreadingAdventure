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
    private volatile boolean canContinue = false;

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

        // Input handlers: click or Enter to start; Space/Click to continue when prompted
        scene.setOnMouseClicked(e -> {
            if (!gameStarted) startGame(centerLabel);
            else if (canContinue) {
                RetroMultithreadingAdventure.continueToNextPart();
                canContinue = false;
            }
        });
        scene.setOnKeyPressed(e -> {
            KeyCode code = e.getCode();
            if (code == KeyCode.ENTER) {
                if (!gameStarted) startGame(centerLabel);
            } else if (code == KeyCode.SPACE) {
                if (canContinue) {
                    RetroMultithreadingAdventure.continueToNextPart();
                    canContinue = false;
                }
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
            if (msg != null) centerLabel.setText(msg);
            if (msg != null && msg.contains("PART_COMPLETE")) {
                canContinue = true;
                centerLabel.setText(msg + "\n(Click or press Space to continue)");
            }
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
