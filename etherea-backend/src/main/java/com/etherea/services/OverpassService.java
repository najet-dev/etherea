package com.etherea.services;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class OverpassService {
    private final RestTemplate restTemplate;

    @Autowired
    public OverpassService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Map<String, Object>> getNearbyPickupPoints(double latitude, double longitude, double radius) {
        try {
            // Reformater la requête Overpass pour inclure le bon format `around:<radius>,<lat>,<lon>`
            String url = String.format(
                    "https://overpass-api.de/api/interpreter?data=[out:json];(" +
                            "node[\"amenity\"=\"post_office\"](around:%.0f,%.6f,%.6f);" +
                            "node[\"amenity\"=\"parcel_locker\"](around:%.0f,%.6f,%.6f);" +
                            "node[\"shop\"](around:%.0f,%.6f,%.6f);" +
                            ");out;",
                    radius, latitude, longitude,
                    radius, latitude, longitude,
                    radius, latitude, longitude
            );

            System.out.println("URL de la requête Overpass : " + url);

            String response = restTemplate.getForObject(url, String.class);
            JSONObject jsonResponse = new JSONObject(Objects.requireNonNull(response));
            JSONArray elements = jsonResponse.optJSONArray("elements");

            List<Map<String, Object>> pickupPoints = new ArrayList<>();
            if (elements != null) {
                for (int i = 0; i < elements.length(); i++) {
                    JSONObject element = elements.getJSONObject(i);
                    Map<String, Object> point = new HashMap<>();

                    point.put("id", element.getLong("id"));
                    point.put("lat", element.getDouble("lat"));
                    point.put("lon", element.getDouble("lon"));

                    // Récupération du nom
                    if (element.has("tags")) {
                        JSONObject tags = element.getJSONObject("tags");
                        point.put("name", tags.optString("name", "Nom non disponible"));
                    } else {
                        point.put("name", "Nom non disponible");
                    }

                    // Récupération de l'adresse complète pour le point
                    String address = getCompleteAddress(
                            (Double) point.get("lat"),
                            (Double) point.get("lon")
                    );
                    point.put("address", address);

                    pickupPoints.add(point);
                }
            }
            return pickupPoints;
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des points relais : " + e.getMessage(), e);
        }
    }

    public String getCompleteAddress(double latitude, double longitude) {
        try {
            String url = "https://us1.locationiq.com/v1/reverse.php?key=pk.f7749adbb572df4d741f1032664f787f&lat="
                    + latitude + "&lon=" + longitude + "&format=json";
            String response = restTemplate.getForObject(url, String.class);
            JSONObject jsonResponse = new JSONObject(Objects.requireNonNull(response));
            return jsonResponse.optString("display_name", "Adresse non trouvée");
        } catch (Exception e) {
            return "Erreur lors de la récupération de l'adresse : " + e.getMessage();
        }
    }
}
