package com.etherea.services;

import com.etherea.dtos.DeliveryAddressDTO;
import com.etherea.dtos.PickupPointDeliveryDTO;
import com.etherea.exception.DeliveryAddressNotFoundException;
import com.etherea.exception.InvalidCoordinatesException;
import com.etherea.interfaces.IPickupPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PickupPointService implements IPickupPoint {
    @Autowired
    private DeliveryAddressService deliveryAddressService;
    @Autowired
    private GeocodingService geocodingService;
    @Autowired
    private OverpassService overpassService;
    public List<PickupPointDeliveryDTO> findNearestPickupPoints(Long userId, double radius) {

        DeliveryAddressDTO deliveryAddress = deliveryAddressService.getAllDeliveryAddresses(userId).stream()
                .filter(DeliveryAddressDTO::isDefault)
                .findFirst()
                .orElseThrow(() -> new DeliveryAddressNotFoundException(
                        "Aucune adresse par défaut trouvée pour l'utilisateur ID : " + userId
                ));

        String fullAddress = String.format("%s, %s, %s, %s",
                deliveryAddress.getAddress(),
                deliveryAddress.getCity(),
                deliveryAddress.getZipCode(),
                deliveryAddress.getCountry());

        System.out.println("Adresse à géocoder : " + fullAddress);

        Map<String, Double> coordinates = geocodingService.getCoordinates(fullAddress);

        Double latitude = coordinates.get("lat");
        Double longitude = coordinates.get("lon");

        if (latitude == null || longitude == null) {
            throw new InvalidCoordinatesException("Coordonnées non valides pour l'adresse : " + fullAddress);
        }

        System.out.println("Coordonnées obtenues - Latitude: " + latitude + ", Longitude: " + longitude);

        List<Map<String, Object>> pickupPoints;
        try {
            pickupPoints = overpassService.getNearbyPickupPoints(latitude, longitude, radius);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des points relais : " + e.getMessage(), e);
        }

        System.out.println("Nombre de points relais trouvés : " + pickupPoints.size());

        return pickupPoints.stream()
                .map(point -> {
                    try {
                        Long id = ((Number) point.get("id")).longValue();
                        String name = (String) point.get("name");
                        String address = (String) point.get("address");

                        Double lat = (Double) point.get("lat");
                        Double lon = (Double) point.get("lon");

                        return new PickupPointDeliveryDTO(id, name, address, lat, lon);
                    } catch (Exception e) {
                        System.err.println("Erreur lors de la conversion du point relais : " + e.getMessage());
                        return null;
                    }
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }
}
