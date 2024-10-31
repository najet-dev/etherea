package com.etherea.services;

import com.etherea.exception.GeocodingApiException;
import com.etherea.exception.PickupPointNotFoundException;
import com.etherea.interfaces.IOverpass;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class OverpassService implements IOverpass {
    private final RestTemplate restTemplate;

    @Autowired
    public OverpassService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Map<String, Object>> getNearbyPickupPoints(double latitude, double longitude, double radius) {
        // Vérification des valeurs pour s'assurer qu'elles sont dans les limites acceptées
        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Valeurs de latitude ou longitude incorrectes : " +
                    "latitude = " + latitude + ", longitude = " + longitude);
        }

        try {
            // Arrondir le rayon à l'entier le plus proche
            int radiusInt = (int) Math.round(radius);

            // Reformater la requête Overpass pour inclure les coordonnées correctement formatées
            String url = String.format(
                    Locale.ROOT,
                    "https://overpass-api.de/api/interpreter?data=[out:json];" +
                            "(node[\"amenity\"=\"post_office\"](around:%d,%.6f,%.6f);" +
                            "node[\"amenity\"=\"parcel_locker\"](around:%d,%.6f,%.6f);" +
                            "node[\"shop\"](around:%d,%.6f,%.6f);" +
                            ");out;",
                    radiusInt, latitude, longitude,
                    radiusInt, latitude, longitude,
                    radiusInt, latitude, longitude
            );

            System.out.println("URL de la requête Overpass : " + url);

            String response = restTemplate.getForObject(url, String.class);
            if (response == null) {
                throw new PickupPointNotFoundException("Aucune réponse de l'API Overpass pour les coordonnées fournies.");
            }

            JSONObject jsonResponse = new JSONObject(response);
            JSONArray elements = jsonResponse.optJSONArray("elements");

            List<Map<String, Object>> pickupPoints = new ArrayList<>();
            if (elements != null && elements.length() > 0) {
                for (int i = 0; i < elements.length(); i++) {
                    JSONObject element = elements.getJSONObject(i);

                    // Vérification et filtrage des points avec des coordonnées valides
                    if (element.has("lat") && element.has("lon")) {
                        double lat = element.getDouble("lat");
                        double lon = element.getDouble("lon");

                        if (lat >= -90 && lat <= 90 && lon >= -180 && lon <= 180) {
                            Map<String, Object> point = new HashMap<>();
                            point.put("id", element.getLong("id"));
                            point.put("lat", lat);
                            point.put("lon", lon);

                            // Gestion des tags pour obtenir le nom
                            if (element.has("tags")) {
                                JSONObject tags = element.getJSONObject("tags");
                                point.put("name", tags.optString("name", "Nom non disponible"));
                            } else {
                                point.put("name", "Nom non disponible");
                            }

                            // Récupérer l'adresse complète
                            String address = getCompleteAddress(lat, lon);
                            point.put("address", address);

                            pickupPoints.add(point);
                        } else {
                            System.err.println("Coordonnées invalides détectées pour l'élément " + element);
                        }
                    }
                }
            } else {
                throw new PickupPointNotFoundException("Aucun point relais trouvé dans la réponse de l'API Overpass.");
            }
            return pickupPoints;
        } catch (Exception e) {
            throw new PickupPointNotFoundException("Erreur lors de la récupération des points relais : " + e.getMessage(), e);
        }
    }

    public String getCompleteAddress(double latitude, double longitude) {
        try {
            String url = "https://us1.locationiq.com/v1/reverse.php?key=pk.f7749adbb572df4d741f1032664f787f&lat="
                    + latitude + "&lon=" + longitude + "&format=json";
            String response = restTemplate.getForObject(url, String.class);
            if (response == null) {
                throw new GeocodingApiException("Erreur de l'API de géocodage : réponse vide.");
            }
            JSONObject jsonResponse = new JSONObject(response);
            return jsonResponse.optString("display_name", "Adresse non trouvée");
        } catch (Exception e) {
            throw new GeocodingApiException("Erreur lors de la récupération de l'adresse : " + e.getMessage(), e);
        }
    }
}
