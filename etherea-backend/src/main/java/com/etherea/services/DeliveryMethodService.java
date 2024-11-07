package com.etherea.services;

import com.etherea.dtos.DeliveryMethodDTO;
import com.etherea.enums.DeliveryOption;
import com.etherea.exception.UserNotFoundException;
import com.etherea.models.*;
import com.etherea.repositories.DeliveryAddressRepository;
import com.etherea.repositories.UserRepository;
import com.etherea.utils.DeliveryDateCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DeliveryMethodService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;
    @Autowired
    private DeliveryDateCalculator deliveryDateCalculator;

    public DeliveryMethodDTO addDeliveryMethod(Long userId, DeliveryOption deliveryOption, String pickupPointName,
                                               String pickupPointAddress, Double pickupPointLatitude,
                                               Double pickupPointLongitude, double orderAmount) {
        // Récupération de l'utilisateur
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // Récupération de la dernière adresse de l'utilisateur
        DeliveryAddress userAddress = deliveryAddressRepository.findTopByUserIdOrderByIdDesc(userId)
                .orElseThrow(() -> new IllegalArgumentException("No address found for user with ID: " + userId));

        // Création de l'instance de DeliveryMethod en fonction de l'option de livraison choisie
        DeliveryMethod deliveryMethod;
        LocalDate startDate = LocalDate.now();

        switch (deliveryOption) {
            case HOME_STANDARD:
                deliveryMethod = new HomeStandardDelivery();
                break;
            case HOME_EXPRESS:
                deliveryMethod = new HomeExpressDelivery();
                break;
            case PICKUP_POINT:
                if (pickupPointName == null || pickupPointAddress == null || pickupPointLatitude == null || pickupPointLongitude == null) {
                    throw new IllegalArgumentException("Missing information for pickup point delivery");
                }
                deliveryMethod = new PickupPointDelivery(pickupPointName, pickupPointAddress, pickupPointLatitude, pickupPointLongitude);
                break;
            default:
                throw new IllegalArgumentException("Invalid delivery option");
        }

        // Conversion de la méthode de livraison en DTO avec la date et le coût calculés
        return DeliveryMethodDTO.fromDeliveryMethod(deliveryMethod, userAddress, startDate, orderAmount, deliveryDateCalculator);
    }
}
