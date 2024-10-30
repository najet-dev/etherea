package com.etherea.services;

import com.etherea.dtos.DeliveryAddressDTO;
import com.etherea.dtos.PickupPointDTO;
import com.etherea.exception.DeliveryAddressNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PickupPointService {
    @Autowired
    private DeliveryAddressService deliveryAddressService;
    @Autowired
    private GeocodingService geocodingService;
    @Autowired
    private OverpassService overpassService;
    public List<PickupPointDTO> findNearestPickupPoints(Long userId, double radius) {
        // Récupérer l'adresse par défaut de l'utilisateur
        DeliveryAddressDTO deliveryAddress = deliveryAddressService.getAllDeliveryAddresses(userId).stream()
                .filter(DeliveryAddressDTO::isDefault)
                .findFirst()
                .orElseThrow(() -> new DeliveryAddressNotFoundException("Aucune adresse par défaut trouvée pour l'utilisateur ID : " + userId));

        // Construire l'adresse complète
        String fullAddress = String.format("%s, %s, %s, %s",
                deliveryAddress.getAddress(),
                deliveryAddress.getCity(),
                deliveryAddress.getZipCode(),
                deliveryAddress.getCountry());

        // Log l'adresse à géocoder pour débogage
        System.out.println("Adresse à géocoder : " + fullAddress);

        // Convertir l'adresse en coordonnées
        Map<String, Double> coordinates = geocodingService.getCoordinates(fullAddress); // Changement ici

        // Vérifier si les coordonnées sont valides
        Double latitude = coordinates.get("lat");
        Double longitude = coordinates.get("lon");

        if (latitude == null || longitude == null) {
            throw new RuntimeException("Coordonnées non valides pour l'adresse : " + fullAddress);
        }

        // Log des coordonnées obtenues
        System.out.println("Coordonnées obtenues - Latitude: " + latitude + ", Longitude: " + longitude);

        // Récupérer les points relais autour des coordonnées
        List<Map<String, Object>> pickupPoints;
        try {
            pickupPoints = overpassService.getNearbyPickupPoints(latitude, longitude, radius);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des points relais : " + e.getMessage(), e);
        }

        // Log du nombre de points relais trouvés
        System.out.println("Nombre de points relais trouvés : " + pickupPoints.size());

        // Convertir en PickupPointDTO pour le retour
        return pickupPoints.stream()
                .map(point -> {
                    try {
                        Long id = ((Number) point.get("id")).longValue(); // Assurez-vous de convertir en Long
                        String name = (String) point.get("name");
                        String address = (String) point.get("address");

                        // Assurez-vous que les lat et lon soient des Double
                        Double lat = (Double) point.get("lat");
                        Double lon = (Double) point.get("lon");

                        return new PickupPointDTO(id, name, address, lat, lon);
                    } catch (Exception e) {
                        // Log de l'erreur et retour d'un DTO nul ou d'une gestion d'erreur
                        System.err.println("Erreur lors de la conversion du point relais : " + e.getMessage());
                        return null; // Ou gérer comme vous le souhaitez
                    }
                })
                .filter(dto -> dto != null) // Filtrer les DTO nuls
                .collect(Collectors.toList());
    }
}
