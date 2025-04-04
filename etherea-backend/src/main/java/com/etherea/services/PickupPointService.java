package com.etherea.services;

import com.etherea.dtos.AddDeliveryMethodRequestDTO;
import com.etherea.exception.DeliveryAddressNotFoundException;
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

    /**
     * Retrieves pickup points for a given user based on their current address.
     *
     * @param userId the ID of the user for whom pickup points are being retrieved
     * @return a list of DTOs containing pickup point details
     */
    @Cacheable(value = "pickupPointsCache", key = "#userId", unless = "#result == null || #result.isEmpty()")
    public List<AddDeliveryMethodRequestDTO> findPickupPoints(Long userId) {
        // Récupérer l'adresse de l'utilisateur
        DeliveryAddress userAddress = deliveryAddressRepository.findTopByUserIdOrderByIdDesc(userId)
                .orElseThrow(() -> new DeliveryAddressNotFoundException("No address found for user ID: " + userId));

        System.out.println("User address: " + userAddress.getFullAddress());

        // Géocodage
        double latitude;
        double longitude;
        try {
            String geocodeUrl = String.format("%s?q=%s&format=json&limit=1",
                    NOMINATIM_API_URL.replace("/reverse", "/search"), userAddress.getFullAddress().replace(" ", "+"));
            System.out.println("Nominatim URL: " + geocodeUrl);
            JsonNode geocodeResponse = restTemplate.getForObject(geocodeUrl, JsonNode.class);

            if (geocodeResponse != null && !geocodeResponse.isEmpty()) {
                latitude = geocodeResponse.get(0).path("lat").asDouble();
                longitude = geocodeResponse.get(0).path("lon").asDouble();
                System.out.println("Geocoded Latitude: " + latitude + ", Longitude: " + longitude);
            } else {
                throw new RuntimeException("Failed to geocode the address.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error during user address geocoding", e);
        }

        // Requête Overpass API
        String query = String.format(Locale.ROOT,
                "[out:json];node(around:5000,%.6f,%.6f)[\"amenity\"~\"parcel_pickup|post_office\"];out;",
                latitude, longitude);
        String url = OVERPASS_API_URL + "?data=" + query;

        System.out.println("Overpass Query URL: " + url);

        List<AddDeliveryMethodRequestDTO> pickupPoints = new ArrayList<>();
        try {
            String response = restTemplate.getForObject(url, String.class);
            System.out.println("Overpass API response: " + response);
            JsonNode nodes = new ObjectMapper().readTree(response).path("elements");

            if (nodes.isEmpty()) {
                System.out.println("No pickup points found for user ID: " + userId);
            }

            for (JsonNode node : nodes) {
                AddDeliveryMethodRequestDTO dto = new AddDeliveryMethodRequestDTO();
                dto.setUserId(userId);
                dto.setPickupPointName(node.path("tags").path("name").asText("Unknown pickup point"));
                dto.setPickupPointLatitude(node.path("lat").asDouble());
                dto.setPickupPointLongitude(node.path("lon").asDouble());

                // Géocodage inverse pour récupérer l’adresse complète
                String fullAddress = getFullAddressFromCoordinates(dto.getPickupPointLatitude(), dto.getPickupPointLongitude());
                dto.setPickupPointAddress(fullAddress != null ? fullAddress : "Address not available");

                pickupPoints.add(dto);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve pickup points from the Overpass API", e);
        }

        return pickupPoints;
    }

    /**
     * Retrieves the full address using GPS coordinates.
     *
     * @param latitude  the latitude of the point
     * @param longitude the longitude of the point
     * @return the full address or an error message
     */
    private String getFullAddressFromCoordinates(double latitude, double longitude) {
        try {
            // Format the coordinates for Nominatim
            String reverseGeocodeUrl = String.format(Locale.ROOT, "%s?lat=%.6f&lon=%.6f&format=json", NOMINATIM_API_URL, latitude, longitude);

            // Call the Nominatim API to retrieve the address
            JsonNode reverseGeocodeResponse = restTemplate.getForObject(reverseGeocodeUrl, JsonNode.class);

            // Return the full address if available
            return reverseGeocodeResponse != null && reverseGeocodeResponse.has("display_name")
                    ? reverseGeocodeResponse.path("display_name").asText()
                    : "Address not available";
        } catch (Exception e) {
            System.err.println("Reverse geocoding error for coordinates (" + latitude + ", " + longitude + "): " + e.getMessage());
            return "Error retrieving address";
        }
    }

    /**
     * Clears the pickup points cache every 24 hours.
     */
    @CacheEvict(value = "pickupPointsCache", allEntries = true)
    @Scheduled(fixedRate = 86400000) // Every 24 hours (in milliseconds)
    public void clearCacheAutomatically() {
        System.out.println("Cache automatically cleared!");
    }
}
