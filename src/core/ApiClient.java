package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Small API client used by the terminal game to fetch a short piece of advice/fortune.
 */
public class ApiClient {

    @SuppressWarnings("deprecation")
    public static String fetchAdvice() {
        // Use API Ninjas quotes endpoint when an API key is provided.
        // Requires an API key provided via the environment variable `API_NINJAS_KEY`
        // or system property `api.ninjas.key`. If missing, fall back to local quotes.
        String endpoint = "https://api.api-ninjas.com/v2/quotes";
        String apiKey = System.getenv("API_NINJAS_KEY");
        if (apiKey == null || apiKey.isBlank()) apiKey = System.getProperty("api.ninjas.key");
        if (apiKey == null || apiKey.isBlank()) {
            // API key not available; return a local fallback immediately
            return getLocalFallback();
        }
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(endpoint);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("User-Agent", "RetroMultithreadingAdventure/1.0");
            conn.setRequestProperty("X-Api-Key", apiKey);

            int code = conn.getResponseCode();
            if (code != 200) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            String json = sb.toString();
            // API Ninjas returns an array of objects like: [{"quote":"...","author":"..."},...]
            // Try several common keys returned by quote APIs (including API Ninjas's "quote")
            String[] keys = new String[]{"\"quote\":", "\"content\":", "\"text\":", "\"q\":", "\"quoteText\":", "\"data\":"};
            for (String key : keys) {
                int idx = json.indexOf(key);
                if (idx >= 0) {
                    int start = idx + key.length();
                    int firstQuote = json.indexOf('"', start);
                    if (firstQuote >= 0) {
                        int secondQuote = json.indexOf('"', firstQuote + 1);
                        if (secondQuote > firstQuote) {
                            return json.substring(firstQuote + 1, secondQuote);
                        }
                    }
                }
            }
            // If parsing failed, fall back to a safe local quote
            return getLocalFallback();
        } catch (IOException e) {
            return getLocalFallback();
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException ignored) {}
            if (conn != null) conn.disconnect();
        }
    }

    private static String getLocalFallback() {
        String[] fallback = new String[] {
            "Fortune favors the bold.",
            "Keep your friends close and your code well-documented.",
            "A small step thoroughly tested is better than a giant leap unproven.",
            "Clarity is a superpower in a messy codebase.",
            "Patience and persistence unlock the hardest problems.",
            "Read the error — it often points the way forward.",
            "Good design trumps clever tricks.",
            "Measure twice, run once.",
            "Refactor early, refactor often.",
            "Keep things simple until complexity is needed."
        };
        int idx = (int) (Math.random() * fallback.length);
        return fallback[idx];
    }
}
