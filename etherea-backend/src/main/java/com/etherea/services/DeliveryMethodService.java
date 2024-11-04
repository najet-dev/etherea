package com.etherea.services;

import com.etherea.dtos.DeliveryAddressDTO;
import com.etherea.dtos.DeliveryMethodDTO;
import com.etherea.enums.DeliveryOption;
import com.etherea.exception.DeliveryAddressNotFoundException;
import com.etherea.exception.InvalidCoordinatesException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DeliveryMethodService {

    @Autowired private DeliveryAddressService deliveryAddressService;
    @Autowired private GeocodingService geocodingService;
    @Autowired private OverpassService overpassService;

    private final Map<String, CachedCoordinates> coordinatesCache = new ConcurrentHashMap<>();
    private static final double FREE_DELIVERY_THRESHOLD = 50.0;
    private static final Map<DeliveryOption, Double> DELIVERY_COSTS = Map.of(
            DeliveryOption.PICKUP_POINT, 3.0,
            DeliveryOption.HOME_STANDARD, 5.0,
            DeliveryOption.HOME_EXPRESS, 8.0
    );

    public List<DeliveryMethodDTO> getAvailableDeliveryMethods(Long userId, double radius, Double orderAmount) {
        DeliveryAddressDTO deliveryAddress = getDefaultDeliveryAddress(userId);
        Map<String, Double> coordinates = getCoordinatesWithCache(deliveryAddress.getFormattedAddress());

        List<DeliveryMethodDTO> deliveryOptions = new ArrayList<>();
        deliveryOptions.add(createHomeDelivery(DeliveryOption.HOME_STANDARD, 3, orderAmount));
        deliveryOptions.add(createHomeDelivery(DeliveryOption.HOME_EXPRESS, 1, orderAmount));

        if (coordinates != null) {
            List<Map<String, Object>> pickupPoints = getNearbyPickupPoints(coordinates, radius);
            deliveryOptions.addAll(createPickupOptions(pickupPoints, orderAmount));
        }

        return deliveryOptions;
    }

    private DeliveryAddressDTO getDefaultDeliveryAddress(Long userId) {
        return deliveryAddressService.getAllDeliveryAddresses(userId).stream()
                .filter(DeliveryAddressDTO::isDefault)
                .findFirst()
                .orElseThrow(() -> new DeliveryAddressNotFoundException("No default address found for user ID: " + userId));
    }

    private Map<String, Double> getCoordinatesWithCache(String fullAddress) {
        CachedCoordinates cached = coordinatesCache.get(fullAddress);
        if (cached != null && cached.isValid()) return cached.coordinates;

        Map<String, Double> newCoordinates = geocodingService.getCoordinates(fullAddress);
        coordinatesCache.put(fullAddress, new CachedCoordinates(newCoordinates));
        return newCoordinates;
    }

    private List<Map<String, Object>> getNearbyPickupPoints(Map<String, Double> coordinates, double radius) {
        return overpassService.getNearbyPickupPoints(coordinates.get("lat"), coordinates.get("lon"), radius);
    }

    private DeliveryMethodDTO createHomeDelivery(DeliveryOption option, int days, Double orderAmount) {
        double cost = orderAmount >= FREE_DELIVERY_THRESHOLD ? 0.0 : DELIVERY_COSTS.get(option);
        return new DeliveryMethodDTO(null, option, LocalDate.now().plusDays(days), cost, null, null, null, null);
    }

    private List<DeliveryMethodDTO> createPickupOptions(List<Map<String, Object>> pickupPoints, Double orderAmount) {
        List<DeliveryMethodDTO> pickupOptions = new ArrayList<>();
        double cost = orderAmount >= FREE_DELIVERY_THRESHOLD ? 0.0 : DELIVERY_COSTS.get(DeliveryOption.PICKUP_POINT);

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

    private static class CachedCoordinates {
        private final Map<String, Double> coordinates;
        private final LocalDateTime timestamp;

        public CachedCoordinates(Map<String, Double> coordinates) {
            this.coordinates = coordinates;
            this.timestamp = LocalDateTime.now();
        }

        public boolean isValid() {
            return Duration.between(timestamp, LocalDateTime.now()).toMinutes() < 10;
        }
    }
}
