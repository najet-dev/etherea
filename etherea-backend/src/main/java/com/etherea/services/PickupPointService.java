package com.etherea.services;

import com.etherea.dtos.AddDeliveryMethodRequestDTO;
import com.etherea.enums.DeliveryOption;
import com.etherea.models.DeliveryAddress;
import com.etherea.repositories.DeliveryAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public List<AddDeliveryMethodRequestDTO> findPickupPoints(Long userId) {
        // Récupérer l'adresse de l'utilisateur
        DeliveryAddress userAddress = deliveryAddressRepository.findTopByUserIdOrderByIdDesc(userId)
                .orElseThrow(() -> new IllegalArgumentException("No address found for user with ID: " + userId));

        // Obtenir les coordonnées géographiques (latitude et longitude)
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

        // Construire la requête pour Overpass API
        String query = String.format(Locale.ROOT,
                "[out:json];node(around:5000,%.6f,%.6f)[\"amenity\"~\"parcel_pickup|post_office\"];out;",
                latitude, longitude);
        String url = OVERPASS_API_URL + "?data=" + query;

        // Récupérer les points de retrait à partir de Overpass API
        List<AddDeliveryMethodRequestDTO> pickupPoints = new ArrayList<>();
        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode nodes = new ObjectMapper().readTree(response).path("elements");

            // Traitement de chaque point de retrait trouvé
            for (JsonNode node : nodes) {
                AddDeliveryMethodRequestDTO dto = new AddDeliveryMethodRequestDTO();
                dto.setUserId(userId);
                dto.setDeliveryOption(DeliveryOption.PICKUP_POINT);
                dto.setPickupPointName(node.path("tags").path("name").asText("Unknown Pickup Point"));
                dto.setPickupPointLatitude(node.path("lat").asDouble());
                dto.setPickupPointLongitude(node.path("lon").asDouble());

                // Enrichir l'adresse complète via reverse geocoding Nominatim
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
            // Formater les coordonnées en précisant les décimales
            String reverseGeocodeUrl = String.format(Locale.ROOT, "%s?lat=%.6f&lon=%.6f&format=json", NOMINATIM_API_URL, latitude, longitude);

            // Appeler Nominatim avec les coordonnées formatées
            JsonNode reverseGeocodeResponse = restTemplate.getForObject(reverseGeocodeUrl, JsonNode.class);

            // Log de la réponse pour vérifier ce qui est renvoyé par Nominatim
            System.out.println("Réponse de Nominatim pour (" + latitude + ", " + longitude + "): " + reverseGeocodeResponse);

            // Extraire l'adresse complète si disponible
            return reverseGeocodeResponse != null && reverseGeocodeResponse.has("display_name")
                    ? reverseGeocodeResponse.path("display_name").asText()
                    : "Adresse non disponible";
        } catch (Exception e) {
            System.err.println("Erreur lors du reverse geocoding pour (" + latitude + ", " + longitude + "): " + e.getMessage());
            return "Erreur d'obtention de l'adresse";
        }
    }
}
