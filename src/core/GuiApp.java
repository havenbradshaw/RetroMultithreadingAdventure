package core;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import java.util.Optional;
import javafx.geometry.Insets;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.input.KeyCode;
 
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.CountDownLatch;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

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
        // Centralized font loading via ResourceUtil (classpath or src/resources)
        Font loadedFont = ResourceUtil.loadFont(18.0, "PressStart2P-Regular.ttf", "fonts/PressStart2P-Regular.ttf", "PressStart2P-Regular.ttf");

        String effectiveFontFamily = "Press Start 2P"; // preferred family name
        if (loadedFont != null) {
            effectiveFontFamily = loadedFont.getFamily();
            // Apply loaded font with explicit sizes to the labels
            centerLabel.setFont(Font.font(effectiveFontFamily, 18));
            dotLabel.setFont(Font.font(effectiveFontFamily, 24));
        }

        // Center label uses chunky retro text at 18px
        centerLabel.setStyle(String.format("-fx-font-family: '%s'; -fx-font-size: 18px; -fx-text-fill: #1b1001; -fx-font-weight: normal;", effectiveFontFamily));
        // Dot buffer is larger (24px) and centered
        dotLabel.setStyle(String.format("-fx-font-family: '%s'; -fx-font-size: 24px; -fx-text-fill: #1b1001; -fx-font-weight: bold;", effectiveFontFamily));
        dotLabel.setMaxWidth(440);
        dotLabel.setAlignment(Pos.CENTER);

        contentBox.setMaxWidth(720);
        // Hardcode the content box background and rounded corners with slight opacity
        // Make the content box more opaque so text is easier to read and style as a parchment scroll
                contentBox.setStyle(
                        "-fx-background-color: linear-gradient(to bottom, rgba(255,250,230,0.95) 0%, rgba(245,230,200,0.95) 100%);"
                            + " -fx-background-radius: 12;"
                            + " -fx-background-insets: 0;"
                            + " -fx-padding: 14 18 14 18;"
                            + " -fx-border-color: rgba(139,111,59,0.95);"
                            + " -fx-border-width: 4;"
                            + " -fx-border-radius: 12;"
                            + " -fx-border-insets: 0;"
                            + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.45), 8, 0.0, 0, 3);"
                );

        ScrollPane scrollPane = new ScrollPane(contentBox);
        // Hardcode scroll box visual styles (size + opacity + compatibility fallbacks)
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // Make the scroll box shorter to look like a small parchment scroll
        scrollPane.setMaxWidth(700);
        scrollPane.setMaxHeight(140);
        scrollPane.setPrefWidth(660);
        scrollPane.setPrefHeight(120);
        // Inline style: include both standard and -fx properties for compatibility
        // Leave the scroll pane background transparent but ensure the control isn't dimmed
        scrollPane.setStyle("background-color: transparent; -fx-background-color: transparent; max-height: 140px; -fx-max-height: 140px; opacity: 1.0; -fx-background-insets: 0;");

        BorderPane root = new BorderPane();
        root.getStyleClass().add("pixel-bg");
        // Prefer a developer-provided `New_art` file in resources (check common extensions),
        // falling back to the legacy pixel background or a green gradient.
        Image bgImg = ResourceUtil.loadImage(
                "New_art.jpg","new_art.jpg","New_art.png","new_art.png",
                "New_art.webp","new_art.webp","New_art.avif","new_art.avif",
                "pixel-bg.webp","pixel-bg.png");

        String gradientFallback = "background-color: linear-gradient(to bottom, #2f8a2f 0%, #1b5a1b 50%, #143e14 100%); -fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #2f8a2f 0%, #1b5a1b 50%, #143e14 100%);";
        if (bgImg != null) {
            try {
                BackgroundSize bgSize = new BackgroundSize(100, 100, true, true, false, true); // cover
                BackgroundImage bimg = new BackgroundImage(bgImg, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, bgSize);
                root.setBackground(new Background(bimg));
            } catch (Exception ex) {
                root.setStyle(gradientFallback);
            }
        } else {
            root.setStyle(gradientFallback);
        }

        // contentBox and scrollPane are styled inline above; no external stylesheet required
        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 800, 600);
        // Hardcode the default font for the scene/root so all controls inherit the pixel font
        try {
            String fontFamilySpec;
            if (loadedFont != null) {
                // use the loaded font's family name
                fontFamilySpec = String.format("-fx-font-family: '%s'; -fx-font-size: 18px;", loadedFont.getName());
            } else {
                fontFamilySpec = String.format("-fx-font-family: %s; -fx-font-size: 18px;", pixelFamilyFallback);
            }
            // Append the font spec to the root's style so it applies globally
            root.setStyle(root.getStyle() + " " + fontFamilySpec);
        } catch (Exception ignored) {
        }

        // Intentionally not loading external `ui.css`; all design choices are hardcoded here.

        scene.setOnMouseClicked(e -> { if (!gameStarted) startGame(centerLabel, dotLabel); });
        scene.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER && !gameStarted) startGame(centerLabel, dotLabel); });

        stage.setTitle("Retro Multithreading Adventure - GUI");
        stage.setScene(scene);
        stage.show();

        // Ensure the ScrollPane viewport itself is transparent
        Platform.runLater(() -> {
            try {
                Node viewport = scene.lookup(".scroll-pane .viewport");
                if (viewport == null) viewport = scene.lookup(".viewport");
                if (viewport != null) {
                    viewport.setStyle("background-color: transparent; -fx-background-color: transparent; -fx-background-insets: 0;");
                }
            } catch (Exception ignored) {
            }
        });
    }

    private void startGame(Label centerLabel, Label dotLabel) {
        if (gameStarted) return;
        gameStarted = true;

        // Prompt the player for optional personal names for each character.
        // If the user provides a short name (e.g., "Arthur"), the character
        // will be named like "Knight Arthur" as requested.
        try {
            TextInputDialog knightDlg = new TextInputDialog("");
            knightDlg.setTitle("Name the Knight");
            knightDlg.setHeaderText(null);
            knightDlg.setContentText("Enter a personal name for the Knight (leave blank to use 'Knight'):");
            Optional<String> k = knightDlg.showAndWait();
            String kname = k.isPresent() && !k.get().trim().isEmpty() ? "Knight " + k.get().trim() : "Knight";

            TextInputDialog wizDlg = new TextInputDialog("");
            wizDlg.setTitle("Name the Wizard");
            wizDlg.setHeaderText(null);
            wizDlg.setContentText("Enter a personal name for the Wizard (leave blank to use 'Wizard'):");
            Optional<String> w = wizDlg.showAndWait();
            String wname = w.isPresent() && !w.get().trim().isEmpty() ? "Wizard " + w.get().trim() : "Wizard";

            TextInputDialog thiefDlg = new TextInputDialog("");
            thiefDlg.setTitle("Name the Thief");
            thiefDlg.setHeaderText(null);
            thiefDlg.setContentText("Enter a personal name for the Thief (leave blank to use 'Thief'):");
            Optional<String> t = thiefDlg.showAndWait();
            String tname = t.isPresent() && !t.get().trim().isEmpty() ? "Thief " + t.get().trim() : "Thief";

            // fetch advice early and build a GameConfig to pass into the game runner
            String advice = ApiClient.fetchAdvice();
            core.GameConfig cfg = new core.GameConfig(3, 2, kname, wname, tname, advice);
            final core.GameConfig finalCfg = cfg;

            GameWorld.uiLogger = m -> {
                try { uiQueue.put(m == null ? "" : m); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
            };

            new Thread(() -> {
                while (true) {
                    try {
                        String queuedMsg = uiQueue.take();
                        if ("__EXIT__".equals(queuedMsg)) break;

                        if ("__NEXT_SIGNAL__".equals(queuedMsg)) {
                            playDotAnimationAndWait(dotLabel, 3, 1000);
                            RetroMultithreadingAdventure.continueToNextPart();
                            continue;
                        }

                        final String display = queuedMsg;
                        Platform.runLater(() -> { centerLabel.setText(display); dotLabel.setText(""); });

                        playDotAnimationAndWait(dotLabel, 3, 1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }, "UiQueueConsumer").start();

            new Thread(() -> {
                RetroMultithreadingAdventure.runGame(finalCfg);
                Platform.runLater(() -> centerLabel.setText("Adventure complete. Restart the app to play again."));
            }, "GameRunner").start();

            return; // we've launched the game runner, return early from startGame
        } catch (Exception ignored) {}

        GameWorld.uiLogger = m -> {
            try { uiQueue.put(m == null ? "" : m); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
        };

        new Thread(() -> {
            while (true) {
                try {
                    String queuedMsg = uiQueue.take();
                    if ("__EXIT__".equals(queuedMsg)) break;

                    if ("__NEXT_SIGNAL__".equals(queuedMsg)) {
                        playDotAnimationAndWait(dotLabel, 3, 1000);
                        RetroMultithreadingAdventure.continueToNextPart();
                        continue;
                    }

                    final String display = queuedMsg;
                    Platform.runLater(() -> { centerLabel.setText(display); dotLabel.setText(""); });

                    playDotAnimationAndWait(dotLabel, 3, 1000);
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

    private void playDotAnimationAndWait(Label dotLabel, int steps, long intervalMillis) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                Timeline t = new Timeline();
                for (int i = 1; i <= steps; i++) {
                    final String dots = new String(new char[i]).replace('\0', '.');
                    KeyFrame kf = new KeyFrame(Duration.millis(intervalMillis * (i - 1)), e -> dotLabel.setText(dots));
                    t.getKeyFrames().add(kf);
                }
                // clear after the last frame
                KeyFrame clear = new KeyFrame(Duration.millis(intervalMillis * steps), e -> dotLabel.setText(""));
                t.getKeyFrames().add(clear);
                t.setOnFinished(e -> latch.countDown());
                t.play();
            } catch (Exception ex) {
                latch.countDown();
            }
        });

        // Wait for the timeline to finish (or interruption)
        try {
            latch.await();
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw ie;
        }
    }

    public static void main(String[] args) { launch(args); }
}
