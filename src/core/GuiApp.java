package core;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;
import javafx.geometry.Insets;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import java.io.File;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Simple JavaFX UI wrapper for the RetroMultithreadingAdventure.
 */
public class GuiApp extends Application {
    private volatile boolean gameStarted = false;
    private final BlockingQueue<String> uiQueue = new LinkedBlockingQueue<>();

    @Override
    public void start(Stage stage) {
        Label centerLabel = new Label("Click or press Enter to start the adventure");
        centerLabel.setId("centerLabel");

        Label dotLabel = new Label("");
        dotLabel.setId("dotBuffer");

        VBox contentBox = new VBox(8, centerLabel, dotLabel);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(12));

        centerLabel.setWrapText(true);
        centerLabel.setMaxWidth(700);
        centerLabel.setAlignment(Pos.CENTER);
        centerLabel.setTextAlignment(TextAlignment.CENTER);

        // Hardcoded design choices (font families, sizes, text color)
        String pixelFamilyFallback = "'Press Start 2P', 'Minecraftia', 'Courier New', monospace";
        // Try to load bundled pixel font; if present use it, otherwise fall back to family + sizes
        URL fontRes = getClass().getResource("/fonts/PressStart2P-Regular.ttf");
        if (fontRes != null) {
            Font loaded = Font.loadFont(fontRes.toExternalForm(), 18);
            if (loaded != null) centerLabel.setFont(loaded);
        }
        
        // Center label uses chunky retro text at 18px
        centerLabel.setStyle(String.format("-fx-font-family: %s; -fx-font-size: 18px; -fx-text-fill: #1b1001; -fx-font-weight: normal;", pixelFamilyFallback));
        // Dot buffer is larger (24px) and centered
        dotLabel.setStyle(String.format("-fx-font-family: %s; -fx-font-size: 24px; -fx-text-fill: #1b1001; -fx-font-weight: bold;", pixelFamilyFallback));
        dotLabel.setMaxWidth(440);
        dotLabel.setAlignment(Pos.CENTER);

        contentBox.setMaxWidth(720);
        // Hardcode the content box background and rounded corners with slight opacity
        contentBox.setStyle("-fx-background-color: rgba(250,245,230,0.8); background-color: rgba(250,245,230,0.8); -fx-background-radius: 8; -fx-padding: 12;");

        ScrollPane scrollPane = new ScrollPane(contentBox);
        // Hardcode scroll box visual styles (size + opacity + compatibility fallbacks)
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        scrollPane.setMaxWidth(760);
        scrollPane.setMaxHeight(170);
        scrollPane.setPrefWidth(720);
        scrollPane.setPrefHeight(150);
        // Inline style: include both standard and -fx properties for compatibility
        scrollPane.setStyle("background-color: rgba(250,245,230,0.8); -fx-background-color: rgba(250,245,230,0.8); max-height: 170px; -fx-max-height: 170px; opacity: 0.98;");

        BorderPane root = new BorderPane();
        root.getStyleClass().add("pixel-bg");
        URL bgUrl = getClass().getResource("/pixel-bg.webp");
        String img = null;
        if (bgUrl != null) img = bgUrl.toExternalForm();
        else {
            File f = new File("src/pixel-bg.webp");
            if (f.exists()) img = f.toURI().toString();
        }

        if (img != null) {
            // Prefer the pixelated background image when available
            root.setStyle(
                "-fx-background-image: url('" + img + "');"
                + " -fx-background-repeat: no-repeat;"
                + " -fx-background-position: center center;"
                + " -fx-background-size: cover;"
            );
        } else {
            // Fallback gradient background (standard and -fx versions)
            root.setStyle
            ("background-color: linear-gradient(to bottom, #2f8a2f 0%, #1b5a1b 50%, #143e14 100%); -fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #2f8a2f 0%, #1b5a1b 50%, #143e14 100%);");
        }

        // contentBox and scrollPane are styled inline above; no external stylesheet required
        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 800, 600);
        // Intentionally not loading external `ui.css`; all design choices are hardcoded here.

        scene.setOnMouseClicked(e -> { if (!gameStarted) startGame(centerLabel, dotLabel); });
        scene.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER && !gameStarted) startGame(centerLabel, dotLabel); });

        stage.setTitle("Retro Multithreading Adventure - GUI");
        stage.setScene(scene);
        stage.show();
    }

    private void startGame(Label centerLabel, Label dotLabel) {
        if (gameStarted) return;
        gameStarted = true;

        GameWorld.uiLogger = m -> {
            try { uiQueue.put(m == null ? "" : m); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
        };

        new Thread(() -> {
            while (true) {
                try {
                    String queuedMsg = uiQueue.take();
                    if ("__EXIT__".equals(queuedMsg)) break;

                    if ("__NEXT_SIGNAL__".equals(queuedMsg)) {
                        for (int i = 1; i <= 3; i++) {
                            final String dots = new String(new char[i]).replace('\0', '.');
                            Platform.runLater(() -> dotLabel.setText(dots));
                            Thread.sleep(1000);
                        }
                        Platform.runLater(() -> dotLabel.setText(""));
                        RetroMultithreadingAdventure.continueToNextPart();
                        continue;
                    }

                    final String display = queuedMsg;
                    Platform.runLater(() -> { centerLabel.setText(display); dotLabel.setText(""); });

                    for (int i = 1; i <= 3; i++) {
                        final String dots = new String(new char[i]).replace('\0', '.');
                        Platform.runLater(() -> dotLabel.setText(dots));
                        Thread.sleep(1000);
                    }
                    Platform.runLater(() -> dotLabel.setText(""));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "UiQueueConsumer").start();

        new Thread(() -> {
            RetroMultithreadingAdventure.runGame();
            Platform.runLater(() -> centerLabel.setText("Adventure complete. Restart the app to play again."));
        }, "GameRunner").start();
    }

    public static void main(String[] args) { launch(args); }
}
