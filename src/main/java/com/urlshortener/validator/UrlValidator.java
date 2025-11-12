package com.urlshortener.validator;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

@Component
public class UrlValidator {

    private static final Pattern URL_PATTERN = Pattern.compile(
            "^(https?://)?(www\\.)?[a-zA-Z0-9-]+(\\.[a-zA-Z]{2,})+(/.*)?$"
    );

    public boolean isValidUrlFormat(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        return URL_PATTERN.matcher(url).matches();
    }

    public boolean urlExists(String urlString) {
        try {
            // Adiciona https:// se não tiver protocolo
            if (!urlString.startsWith("http://") && !urlString.startsWith("https://")) {
                urlString = "https://" + urlString;
            }

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setInstanceFollowRedirects(true);

            // User-Agent para evitar bloqueios
            connection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");

            int responseCode = connection.getResponseCode();
            connection.disconnect();

            // Considera sucesso códigos 2xx e 3xx
            return responseCode >= 200 && responseCode < 400;

        } catch (IOException e) {
            return false;
        }
    }

    public String normalizeUrl(String url) {
        if (url == null) {
            return null;
        }

        url = url.trim();

        // Adiciona https:// se não tiver protocolo
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }

        return url;
    }
}