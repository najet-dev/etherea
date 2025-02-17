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
import java.util.Optional;

@Service
public class PickupPointService {

    private static final String OVERPASS_API_URL = "https://overpass-api.de/api/interpreter";
    private static final String NOMINATIM_API_URL = "https://nominatim.openstreetmap.org/reverse";
    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;

    @Autowired
    private DeliveryMethodService deliveryMethodService;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Récupère les points relais disponibles pour un utilisateur donné en fonction de son adresse.
     */
    @Cacheable(value = "pickupPointsCache", key = "#userId", unless = "#result == null || #result.isEmpty()")
    public List<AddDeliveryMethodRequestDTO> findPickupPoints(Long userId) {
        // Récupérer la dernière adresse de l'utilisateur
        DeliveryAddress userAddress = deliveryAddressRepository.findTopByUserIdOrderByIdDesc(userId)
                .orElseThrow(() -> new IllegalArgumentException("Aucune adresse trouvée pour l'utilisateur ID: " + userId));

        // Récupérer les coordonnées GPS de l'adresse utilisateur
        double latitude, longitude;
        try {
            String geocodeUrl = String.format("%s?q=%s&format=json&limit=1",
                    NOMINATIM_API_URL.replace("/reverse", "/search"), userAddress.getFullAddress().replace(" ", "+"));
            JsonNode geocodeResponse = restTemplate.getForObject(geocodeUrl, JsonNode.class);

            if (geocodeResponse != null && !geocodeResponse.isEmpty()) {
                latitude = geocodeResponse.get(0).path("lat").asDouble();
                longitude = geocodeResponse.get(0).path("lon").asDouble();
            } else {
                throw new RuntimeException("Échec du géocodage de l'adresse.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du géocodage de l'adresse utilisateur", e);
        }

        // Construire la requête pour l'API Overpass
        String query = String.format(Locale.ROOT,
                "[out:json];node(around:5000,%.6f,%.6f)[\"amenity\"~\"parcel_pickup|post_office\"];out;",
                latitude, longitude);
        String url = OVERPASS_API_URL + "?data=" + query;

        // Récupérer les points relais via l'API
        List<AddDeliveryMethodRequestDTO> pickupPoints = new ArrayList<>();
        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode nodes = new ObjectMapper().readTree(response).path("elements");

            // Vérifier si l'option de livraison PICKUP_POINT existe dans la base de données
            Optional<DeliveryType> pickupOption = deliveryMethodService.getOptionByType(DeliveryType.PICKUP_POINT);

            if (pickupOption.isEmpty()) {
                throw new RuntimeException("L'option de livraison PICKUP_POINT n'est pas configurée.");
            }

            // Traiter chaque point relais trouvé
            for (JsonNode node : nodes) {
                AddDeliveryMethodRequestDTO dto = new AddDeliveryMethodRequestDTO();
                dto.setUserId(userId);
                dto.setDeliveryType(DeliveryType.PICKUP_POINT);
                dto.setPickupPointName(node.path("tags").path("name").asText("Point relais inconnu"));
                dto.setPickupPointLatitude(node.path("lat").asDouble());
                dto.setPickupPointLongitude(node.path("lon").asDouble());

                // Récupérer l'adresse complète via géocodage inversé
                String fullAddress = getFullAddressFromCoordinates(dto.getPickupPointLatitude(), dto.getPickupPointLongitude());
                dto.setPickupPointAddress(fullAddress != null ? fullAddress : "Adresse non disponible");

                pickupPoints.add(dto);
            }
        } catch (Exception e) {
            throw new RuntimeException("Échec de la récupération des points relais depuis l'API Overpass", e);
        }
        return pickupPoints;
    }

    /**
     * Récupère l'adresse complète à partir des coordonnées GPS via l'API Nominatim.
     */
    private String getFullAddressFromCoordinates(double latitude, double longitude) {
        try {
            String reverseGeocodeUrl = String.format(Locale.ROOT, "%s?lat=%.6f&lon=%.6f&format=json", NOMINATIM_API_URL, latitude, longitude);
            JsonNode reverseGeocodeResponse = restTemplate.getForObject(reverseGeocodeUrl, JsonNode.class);

            return reverseGeocodeResponse != null && reverseGeocodeResponse.has("display_name")
                    ? reverseGeocodeResponse.path("display_name").asText()
                    : "Adresse non disponible";
        } catch (Exception e) {
            System.err.println("Erreur de géocodage inversé pour (" + latitude + ", " + longitude + "): " + e.getMessage());
            return "Erreur lors de la récupération de l'adresse";
        }
    }

    /**
     * Vide le cache des points relais toutes les 24 heures.
     */
    @CacheEvict(value = "pickupPointsCache", allEntries = true)
    @Scheduled(fixedRate = 86400000) // 24 heures en millisecondes
    public void clearCacheAutomatically() {
        System.out.println("Cache des points relais vidé automatiquement !");
    }
}
