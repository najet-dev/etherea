package com.etherea.services;

import com.etherea.dtos.DeliveryAddressDTO;
import com.etherea.dtos.DeliveryMethodDTO;
import com.etherea.enums.DeliveryOption;
import com.etherea.exception.DeliveryAddressNotFoundException;
import com.etherea.exception.InvalidCoordinatesException;
import com.etherea.models.DeliveryMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class DeliveryMethodService {

    @Autowired private DeliveryAddressService deliveryAddressService;
    @Autowired private GeocodingService geocodingService;
    @Autowired private OverpassService overpassService;

    private final Map<String, CachedCoordinates> coordinatesCache = new ConcurrentHashMap<>();
    private static final int MAX_RETRIES = 1;
    public static final long RETRY_DELAY = 5000;

    private static final double FREE_DELIVERY_THRESHOLD = 50.0;
    private static final double PICKUP_POINT_COST = 3.0;
    private static final double HOME_STANDARD_COST = 5.0;
    private static final double HOME_EXPRESS_COST = 8.0;
    private static final Duration CACHE_EXPIRATION = Duration.ofMinutes(10); // Durée d'expiration pour le cache

    public List<DeliveryMethodDTO> getAvailableDeliveryMethods(Long userId, double radius, Double orderAmount) {
        DeliveryAddressDTO deliveryAddress = deliveryAddressService.getAllDeliveryAddresses(userId).stream()
                .filter(DeliveryAddressDTO::isDefault)
                .findFirst()
                .orElseThrow(() -> new DeliveryAddressNotFoundException("No default address found for user ID: " + userId));

        String fullAddress = String.format("%s, %s, %s, %s", deliveryAddress.getAddress(), deliveryAddress.getCity(), deliveryAddress.getZipCode(), deliveryAddress.getCountry());

        // Vérifier si les coordonnées sont dans le cache et valides
        Map<String, Double> coordinates = getCoordinatesFromCache(fullAddress);
        Double latitude = coordinates.get("lat"), longitude = coordinates.get("lon");

        if (latitude == null || longitude == null) throw new InvalidCoordinatesException("Invalid coordinates for address: " + fullAddress);

        // Obtenez les points relais à proximité si nécessaire
        List<Map<String, Object>> pickupPoints = getNearbyPickupPointsWithRetry(latitude, longitude, radius);

        // Générer les méthodes de livraison disponibles
        List<DeliveryMethodDTO> deliveryOptions = new ArrayList<>();
        deliveryOptions.add(createHomeStandardDelivery(orderAmount));
        deliveryOptions.add(createHomeExpressDelivery(orderAmount));

        if (pickupPoints != null) {
            deliveryOptions.addAll(createPickupPointDeliveryOptions(pickupPoints, orderAmount));
        }

        return deliveryOptions;
    }

    private Map<String, Double> getCoordinatesFromCache(String fullAddress) {
        CachedCoordinates cached = coordinatesCache.get(fullAddress);
        LocalDateTime now = LocalDateTime.now();

        // Vérifie si les coordonnées sont en cache et non expirées
        if (cached != null && Duration.between(cached.timestamp, now).compareTo(CACHE_EXPIRATION) < 0) {
            System.out.println("Using cached coordinates for: " + fullAddress);
            return cached.coordinates;
        }

        // Sinon, géocodez l'adresse et mettez à jour le cache
        System.out.println("Fetching new coordinates for: " + fullAddress);
        Map<String, Double> newCoordinates = geocodingService.getCoordinates(fullAddress);
        coordinatesCache.put(fullAddress, new CachedCoordinates(newCoordinates, now)); // Met à jour le cache
        return newCoordinates;
    }

    private List<Map<String, Object>> getNearbyPickupPointsWithRetry(double latitude, double longitude, double radius) {
        List<Map<String, Object>> pickupPoints = null;
        for (int attempts = 0; attempts < MAX_RETRIES; attempts++) {
            try {
                pickupPoints = overpassService.getNearbyPickupPoints(latitude, longitude, radius);
                break; // Sors de la boucle si la requête réussit
            } catch (Exception e) {
                System.err.println("Attempt " + (attempts + 1) + " failed with exception: " + e.getMessage());
                if (attempts >= MAX_RETRIES - 1) {
                    throw new RuntimeException("Max retries reached. Please try later.", e);
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(RETRY_DELAY);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        return pickupPoints;
    }


    private DeliveryMethodDTO createHomeStandardDelivery(Double orderAmount) {
        Double cost = orderAmount >= FREE_DELIVERY_THRESHOLD ? 0.0 : HOME_STANDARD_COST;
        return new DeliveryMethodDTO(null, DeliveryOption.HOME_STANDARD, calculateExpectedDeliveryDate(3), cost, null, null, null, null);
    }

    private DeliveryMethodDTO createHomeExpressDelivery(Double orderAmount) {
        Double cost = orderAmount >= FREE_DELIVERY_THRESHOLD ? 0.0 : HOME_EXPRESS_COST;
        return new DeliveryMethodDTO(null, DeliveryOption.HOME_EXPRESS, calculateExpectedDeliveryDate(1), cost, null, null, null, null);
    }

    private List<DeliveryMethodDTO> createPickupPointDeliveryOptions(List<Map<String, Object>> pickupPoints, Double orderAmount) {
        List<DeliveryMethodDTO> pickupOptions = new ArrayList<>();
        Double cost = orderAmount >= FREE_DELIVERY_THRESHOLD ? 0.0 : PICKUP_POINT_COST;

        for (Map<String, Object> point : pickupPoints) {
            pickupOptions.add(new DeliveryMethodDTO(
                    ((Number) point.get("id")).longValue(),
                    DeliveryOption.PICKUP_POINT,
                    null, cost,
                    (String) point.get("name"),
                    (String) point.get("address"),
                    (Double) point.get("lat"),
                    (Double) point.get("lon")
            ));
        }
        return pickupOptions;
    }

    private LocalDate calculateExpectedDeliveryDate(int days) {
        return LocalDate.now().plusDays(days);
    }


    private static class CachedCoordinates {
        private final Map<String, Double> coordinates;
        private final LocalDateTime timestamp;

        public CachedCoordinates(Map<String, Double> coordinates, LocalDateTime timestamp) {
            this.coordinates = coordinates;
            this.timestamp = timestamp;
        }
    }
}
