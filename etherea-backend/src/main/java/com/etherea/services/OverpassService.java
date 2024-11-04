package com.etherea.services;

import com.etherea.exception.GeocodingApiException;
import com.etherea.exception.PickupPointNotFoundException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
@Service
public class OverpassService {
    @Autowired private RestTemplate restTemplate;

    public List<Map<String, Object>> getNearbyPickupPoints(double latitude, double longitude, double radius) {
        String url = buildOverpassUrl(latitude, longitude, radius);
        String response = restTemplate.getForObject(url, String.class);

        if (response == null) throw new PickupPointNotFoundException("No response from Overpass API.");
        return parsePickupPoints(response);
    }

    private String buildOverpassUrl(double latitude, double longitude, double radius) {
        return String.format(
                Locale.ROOT,
                "https://overpass-api.de/api/interpreter?data=[out:json];node(around:%d,%.6f,%.6f);" +
                        "out;", (int) Math.round(radius), latitude, longitude
        );
    }

    private List<Map<String, Object>> parsePickupPoints(String response) {
        JSONArray elements = new JSONObject(response).optJSONArray("elements");
        List<Map<String, Object>> pickupPoints = new ArrayList<>();

        for (int i = 0; i < elements.length(); i++) {
            JSONObject element = elements.getJSONObject(i);
            pickupPoints.add(Map.of(
                    "id", element.getLong("id"),
                    "lat", element.getDouble("lat"),
                    "lon", element.getDouble("lon"),
                    "name", element.optJSONObject("tags").optString("name", "Unknown"),
                    "address", "Address not available"
            ));
        }
        return pickupPoints;
    }
}
