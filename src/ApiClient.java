import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Small API client used by the terminal game to fetch a short piece of advice/fortune.
 *
 * This class uses only the Java standard library (HttpURLConnection) so it works on
 * older JDKs where the newer java.net.http client may not be available.
 */
public class ApiClient {

    /**
     * Fetches a short advice string from a public advice API.
     *
     * Implementation details:
     * - Performs an HTTP GET to https://api.adviceslip.com/advice
     * - On success returns the advice text (e.g. "Be mindful of...")
     * - On failure returns null (calling code should handle a null value)
     *
     * This method does not throw checked exceptions to keep the sample code simple.
     *
     * @return a short advice string, or null if the request failed or the response could not be parsed
     */
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
            // The response is like: {"slip":{"id":123,"advice":"Always..."}}
            // We do a tiny, dependency-free parse to extract the advice value.
            String key = "\"advice\":";
            int idx = json.indexOf(key);
            if (idx >= 0) {
                int start = idx + key.length();
                // first quote after the colon
                int firstQuote = json.indexOf('"', start);
                if (firstQuote >= 0) {
                    int secondQuote = json.indexOf('"', firstQuote + 1);
                    if (secondQuote > firstQuote) {
                        return json.substring(firstQuote + 1, secondQuote);
                    }
                }
            }
            return null;
        } catch (Exception e) {
            // Keep failure silent for this small demo — return null to indicate no advice
            return null;
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (Exception ignored) {}
            if (conn != null) conn.disconnect();
        }
    }
}
