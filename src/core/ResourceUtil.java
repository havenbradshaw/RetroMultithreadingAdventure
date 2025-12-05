package core;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javafx.scene.text.Font;
import javafx.scene.image.Image;

/**
 * Small helper for loading resources from classpath or developer `src/resources`.
 */
public final class ResourceUtil {
    private ResourceUtil() {}

    public static URL findResource(String... names) {
        for (String name : names) {
            if (name == null) continue;
            // Try classpath first
            URL res = ResourceUtil.class.getResource("/" + name);
            if (res != null) return res;
            // Then developer-friendly src/resources
            File f = new File("src/resources/" + name);
            if (f.exists()) {
                try { return f.toURI().toURL(); } 
                catch (MalformedURLException ignored) {}
            }
            // Also allow top-level src/ fallback
            f = new File("src/" + name);
            if (f.exists()) {
                try { return f.toURI().toURL(); } 
                catch (MalformedURLException ignored) {}
            }
        }
        return null;
    }

    public static Font loadFont(double size, String...candidatePaths) {
        try {
            URL res = findResource(candidatePaths);
            if (res != null) return Font.loadFont(res.toExternalForm(), size);
        } catch (Exception ignored) {}
        return null;
    }

    public static Image loadImage(String...candidatePaths) {
        try {
            URL res = findResource(candidatePaths);
            if (res != null) return new Image(res.toExternalForm(), 0, 0, true, true);
        } catch (Exception ignored) {}
        return null;
    }
}
