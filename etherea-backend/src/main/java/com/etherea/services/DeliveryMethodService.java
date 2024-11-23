package com.etherea.services;

import com.etherea.dtos.DeliveryMethodDTO;
import com.etherea.dtos.DeliveryAddressDTO;
import com.etherea.enums.DeliveryOption;
import com.etherea.exception.UserNotFoundException;
import com.etherea.models.DeliveryAddress;
import com.etherea.models.DeliveryMethod;
import com.etherea.repositories.DeliveryMethodRepository;
import com.etherea.repositories.UserRepository;
import com.etherea.utils.DeliveryDateCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeliveryMethodService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DeliveryMethodRepository deliveryMethodRepository;
    @Autowired
    private DeliveryDateCalculator deliveryDateCalculator;
    @Autowired
    private DeliveryAddressService deliveryAddressService;


    /**
     * Retourne une liste simple des options de livraison avec leurs coûts et dates estimées.
     *
     * @param userId L'ID de l'utilisateur (nécessaire pour les livraisons à domicile).
     * @return Liste des options de livraison sous forme de DTOs.
     */
    public List<DeliveryMethodDTO> getDeliveryOptions(Long userId) {
        // Récupérer l'adresse par défaut de l'utilisateur
        DeliveryAddressDTO defaultAddress = deliveryAddressService.getAllDeliveryAddresses(userId)
                .stream()
                .filter(DeliveryAddressDTO::isDefault) // Trouver l'adresse par défaut
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Aucune adresse par défaut n'a été trouvée pour l'utilisateur."));

        // Date actuelle
        LocalDate currentDate = LocalDate.now();

        // Construction des 3 options
        DeliveryMethodDTO homeStandard = new DeliveryMethodDTO.Builder()
                .setId(1L)
                .setDeliveryOption(DeliveryOption.HOME_STANDARD)
                .setExpectedDeliveryDate(currentDate.plusDays(7))
                .setCost(5.0)
                .setDeliveryAddress(defaultAddress)
                .build();

        DeliveryMethodDTO homeExpress = new DeliveryMethodDTO.Builder()
                .setId(2L)
                .setDeliveryOption(DeliveryOption.HOME_EXPRESS)
                .setExpectedDeliveryDate(currentDate.plusDays(2))
                .setCost(10.0)
                .setDeliveryAddress(defaultAddress) // Ajouter l'adresse utilisateur
                .build();

        DeliveryMethodDTO pickupPoint = new DeliveryMethodDTO.Builder()
                .setId(3L)
                .setDeliveryOption(DeliveryOption.PICKUP_POINT)
                .setExpectedDeliveryDate(currentDate.plusDays(8))
                .setCost(3.0)
                // Champs vides pour point relais
                .setPickupPointName("")
                .setPickupPointLatitude(null)
                .setPickupPointLongitude(null)
                .setPickupPointAddress("")
                .build();

        // Retourner les options sous forme de liste
        return Arrays.asList(homeStandard, homeExpress, pickupPoint);
    }
}
