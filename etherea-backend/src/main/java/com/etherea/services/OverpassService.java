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
    private final RestTemplate restTemplate;

    @Autowired
    public OverpassService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Map<String, Object>> getNearbyPickupPoints(double latitude, double longitude, double radius) {
        validateCoordinates(latitude, longitude);

        int radiusInt = (int) Math.round(radius);
        String url = buildOverpassUrl(latitude, longitude, radiusInt);

        String response = restTemplate.getForObject(url, String.class);
        if (response == null) {
            throw new PickupPointNotFoundException("No response from Overpass API for the provided coordinates.");
        }

        return parsePickupPoints(response, latitude, longitude);
    }

    private void validateCoordinates(double latitude, double longitude) {
        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Invalid latitude or longitude values: " +
                    "latitude = " + latitude + ", longitude = " + longitude);
        }
    }

    private String buildOverpassUrl(double latitude, double longitude, int radius) {
        return String.format(
                Locale.ROOT,
                "https://overpass-api.de/api/interpreter?data=[out:json];" +
                        "(node[\"amenity\"=\"post_office\"](around:%d,%.6f,%.6f);" +
                        "node[\"amenity\"=\"parcel_locker\"](around:%d,%.6f,%.6f);" +
                        "node[\"shop\"](around:%d,%.6f,%.6f);" +
                        ");out;",
                radius, latitude, longitude,
                radius, latitude, longitude,
                radius, latitude, longitude
        );
    }

    private List<Map<String, Object>> parsePickupPoints(String response, double latitude, double longitude) {
        JSONObject jsonResponse = new JSONObject(response);
        JSONArray elements = jsonResponse.optJSONArray("elements");

        if (elements == null || elements.length() == 0) {
            throw new PickupPointNotFoundException("No pickup points found in the Overpass API response.");
        }

        List<Map<String, Object>> pickupPoints = new ArrayList<>();
        for (int i = 0; i < elements.length(); i++) {
            JSONObject element = elements.getJSONObject(i);
            if (element.has("lat") && element.has("lon")) {
                double lat = element.getDouble("lat");
                double lon = element.getDouble("lon");

                Map<String, Object> point = new HashMap<>();
                point.put("id", element.getLong("id"));
                point.put("lat", lat);
                point.put("lon", lon);
                point.put("name", element.optJSONObject("tags").optString("name", "No name available"));
                point.put("address", getCompleteAddress(lat, lon));

                pickupPoints.add(point);
            }
        }
        return pickupPoints;
    }

    public String getCompleteAddress(double latitude, double longitude) {
        try {
            String url = "https://us1.locationiq.com/v1/reverse.php?key=pk.f7749adbb572df4d741f1032664f787f&lat="
                    + latitude + "&lon=" + longitude + "&format=json";
            String response = restTemplate.getForObject(url, String.class);
            if (response == null) {
                throw new GeocodingApiException("Empty response from geocoding API.");
            }
            JSONObject jsonResponse = new JSONObject(response);
            return jsonResponse.optString("display_name", "Address not found");
        } catch (Exception e) {
            throw new GeocodingApiException("Error retrieving address: " + e.getMessage(), e);
        }
    }
}