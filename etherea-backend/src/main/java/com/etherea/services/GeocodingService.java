package com.etherea.services;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class GeocodingService {
    private final RestTemplate restTemplate;

    public GeocodingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, Double> getCoordinates(String address) {
        String url = "https://us1.locationiq.com/v1/search.php?key=pk.f7749adbb572df4d741f1032664f787f&q="
                + URLEncoder.encode(address, StandardCharsets.UTF_8)
                + "&format=json";

        HttpHeaders headers = new HttpHeaders();
        headers.add("User-Agent", "Mozilla/5.0 (compatible; YourAppName/1.0; +https://yourapp.com/contact)");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // Respectez la limite de 1 requête par seconde
            Thread.sleep(1000);

            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            String response = responseEntity.getBody();

            System.out.println("Réponse brute : " + response);

            if (response == null || response.isEmpty()) {
                throw new RuntimeException("Réponse vide de LocationIQ pour l'adresse : " + address);
            }

            JSONArray results = new JSONArray(response);
            if (results.length() > 0) {
                JSONObject firstResult = results.getJSONObject(0);
                Map<String, Double> coordinates = new HashMap<>();
                coordinates.put("lat", Double.parseDouble(firstResult.getString("lat")));
                coordinates.put("lon", Double.parseDouble(firstResult.getString("lon")));
                return coordinates;
            }

            throw new RuntimeException("Adresse non trouvée dans LocationIQ : " + address);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Erreur lors de l'attente entre les requêtes", e);
        } catch (HttpClientErrorException e) {
            System.err.println("Erreur HTTP : " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            throw new RuntimeException("Erreur HTTP lors de l'appel à LocationIQ : " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des coordonnées : " + e.getMessage());
        }
    }
}
