package com.etherea.services;

import com.etherea.dtos.AddDeliveryMethodRequestDTO;
import com.etherea.enums.DeliveryType;
import com.etherea.models.DeliveryAddress;
import com.etherea.repositories.DeliveryAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class PickupPointService {
    private static final String OVERPASS_API_URL = "https://overpass-api.de/api/interpreter";
    private static final String NOMINATIM_API_URL = "https://nominatim.openstreetmap.org/reverse";
    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Cacheable(value = "pickupPointsCache", key = "#userId", unless = "#result == null || #result.isEmpty()")
    public List<AddDeliveryMethodRequestDTO> findPickupPoints(Long userId) {
        // Retrieve user address
        DeliveryAddress userAddress = deliveryAddressRepository.findTopByUserIdOrderByIdDesc(userId)
                .orElseThrow(() -> new IllegalArgumentException("No address found for user with ID: " + userId));

        // Get geographic coordinates (latitude and longitude)
        double latitude;
        double longitude;
        try {
            String geocodeUrl = String.format("%s?q=%s&format=json&limit=1",
                    NOMINATIM_API_URL.replace("/reverse", "/search"), userAddress.getFullAddress().replace(" ", "+"));
            JsonNode geocodeResponse = restTemplate.getForObject(geocodeUrl, JsonNode.class);

            if (geocodeResponse != null && !geocodeResponse.isEmpty()) {
                latitude = geocodeResponse.get(0).path("lat").asDouble();
                longitude = geocodeResponse.get(0).path("lon").asDouble();
            } else {
                throw new RuntimeException("Failed to geocode the address.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while geocoding the user's address", e);
        }

        // Building the request for Overpass API
        String query = String.format(Locale.ROOT,
                "[out:json];node(around:5000,%.6f,%.6f)[\"amenity\"~\"parcel_pickup|post_office\"];out;",
                latitude, longitude);
        String url = OVERPASS_API_URL + "?data=" + query;

        // Retrieve pick-up points from Overpass API
        List<AddDeliveryMethodRequestDTO> pickupPoints = new ArrayList<>();
        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode nodes = new ObjectMapper().readTree(response).path("elements");

            // Treatment of each withdrawal point found
            for (JsonNode node : nodes) {
                AddDeliveryMethodRequestDTO dto = new AddDeliveryMethodRequestDTO();
                dto.setUserId(userId);
                dto.setDeliveryType(DeliveryType.PICKUP_POINT);
                dto.setPickupPointName(node.path("tags").path("name").asText("Unknown Pickup Point"));
                dto.setPickupPointLatitude(node.path("lat").asDouble());
                dto.setPickupPointLongitude(node.path("lon").asDouble());

                // Enrich complete address via reverse geocoding Nominatim
                String fullAddress = getFullAddressFromCoordinates(dto.getPickupPointLatitude(), dto.getPickupPointLongitude());
                dto.setPickupPointAddress(fullAddress != null ? fullAddress : "Address not available");

                pickupPoints.add(dto);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch pickup points from Overpass API", e);
        }
        return pickupPoints;
    }
    private String getFullAddressFromCoordinates(double latitude, double longitude) {
        try {
            // Format coordinates with decimals
            String reverseGeocodeUrl = String.format(Locale.ROOT, "%s?lat=%.6f&lon=%.6f&format=json", NOMINATIM_API_URL, latitude, longitude);

            // Call Nominatim with formatted contact details
            JsonNode reverseGeocodeResponse = restTemplate.getForObject(reverseGeocodeUrl, JsonNode.class);

            // Log the response to check what is returned by Nominatim
            System.out.println("Reply from Nominatim for (" + latitude + ", " + longitude + "): " + reverseGeocodeResponse);

            // Extract full address if available
            return reverseGeocodeResponse != null && reverseGeocodeResponse.has("display_name")
                    ? reverseGeocodeResponse.path("display_name").asText()
                    : "Address not available";
        } catch (Exception e) {
            System.err.println("Reverse geocoding error for (" + latitude + ", " + longitude + "): " + e.getMessage());
            return "Error obtaining address";
        }
    }
    @CacheEvict(value = "pickupPointsCache", allEntries = true)
    @Scheduled(fixedRate = 86400000) // Every 24 hours (in milliseconds)
    public void clearCacheAutomatically() {
        System.out.println("Cache vidé automatiquement !");
    }
}