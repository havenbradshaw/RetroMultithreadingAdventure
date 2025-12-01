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
        String endpoint = "https://api.adviceslip.com/advice";
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(endpoint);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

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
            String key = "\"advice\":";
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
            return null;
        } catch (IOException e) {
            return null;
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException ignored) {}
            if (conn != null) conn.disconnect();
        }
    }
}
