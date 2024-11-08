package com.etherea.services;

import com.etherea.dtos.AddDeliveryMethodRequestDTO;
import com.etherea.dtos.DeliveryMethodDTO;
import com.etherea.enums.DeliveryOption;
import com.etherea.exception.UserNotFoundException;
import com.etherea.models.*;
import com.etherea.repositories.DeliveryAddressRepository;
import com.etherea.repositories.DeliveryMethodRepository;
import com.etherea.repositories.HomeStandardDeliveryRepository;
import com.etherea.repositories.HomeExpressDeliveryRepository;
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
    @Autowired
    private DeliveryMethodRepository deliveryMethodRepository;
    @Autowired
    private HomeStandardDeliveryRepository homeStandardDeliveryRepository;
    @Autowired
    private HomeExpressDeliveryRepository homeExpressDeliveryRepository;

    public DeliveryMethodDTO getDeliveryMethod(Long userId, Long deliveryMethodId) {
        // Vérifie si l'utilisateur existe
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // Récupère la méthode de livraison par ID
        DeliveryMethod deliveryMethod = deliveryMethodRepository.findById(deliveryMethodId)
                .orElseThrow(() -> new IllegalArgumentException("No delivery method found with ID: " + deliveryMethodId));

        // Récupère l'adresse associée à la méthode de livraison
        DeliveryAddress deliveryAddress = deliveryMethod instanceof HomeStandardDelivery ?
                ((HomeStandardDelivery) deliveryMethod).getDeliveryAddress() :
                deliveryMethod instanceof HomeExpressDelivery ?
                        ((HomeExpressDelivery) deliveryMethod).getDeliveryAddress() :
                        null;

        // Conversion en DTO
        return DeliveryMethodDTO.fromDeliveryMethod(deliveryMethod, deliveryAddress, LocalDate.now(), 0, deliveryDateCalculator);
    }
    public DeliveryMethodDTO addDeliveryMethod(AddDeliveryMethodRequestDTO request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Utilisateur introuvable avec l'ID : " + request.getUserId()));

        DeliveryAddress userAddress = deliveryAddressRepository.findTopByUserIdOrderByIdDesc(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Aucune adresse trouvée pour l'utilisateur avec l'ID : " + request.getUserId()));

        DeliveryMethod deliveryMethod;
        LocalDate startDate = LocalDate.now();

        switch (request.getDeliveryOption()) {
            case HOME_STANDARD:
                HomeStandardDelivery standardDelivery = new HomeStandardDelivery();
                standardDelivery.setDeliveryAddress(userAddress);
                deliveryMethod = homeStandardDeliveryRepository.save(standardDelivery);
                break;
            case HOME_EXPRESS:
                HomeExpressDelivery expressDelivery = new HomeExpressDelivery();
                expressDelivery.setDeliveryAddress(userAddress);
                deliveryMethod = homeExpressDeliveryRepository.save(expressDelivery);
                break;
            case PICKUP_POINT:
                if (request.getPickupPointName() == null || request.getPickupPointAddress() == null ||
                        request.getPickupPointLatitude() == null || request.getPickupPointLongitude() == null) {
                    throw new IllegalArgumentException("Informations manquantes pour la livraison au point relais");
                }
                deliveryMethod = new PickupPointDelivery(
                        request.getPickupPointName(),
                        request.getPickupPointAddress(),
                        request.getPickupPointLatitude(),
                        request.getPickupPointLongitude()
                );
                deliveryMethod = deliveryMethodRepository.save(deliveryMethod);
                break;
            default:
                throw new IllegalArgumentException("Option de livraison invalide");
        }

        // Utilisation de DeliveryMethodDTO.fromDeliveryMethod pour gérer chaque type de manière adéquate
        return DeliveryMethodDTO.fromDeliveryMethod(deliveryMethod, userAddress, startDate, request.getOrderAmount(), deliveryDateCalculator);
    }

}
