package com.etherea.services;

import com.etherea.dtos.DeliveryAddressDTO;
import com.etherea.exception.UserNotFoundException;
import com.etherea.models.DeliveryAddress;
import com.etherea.models.User;
import com.etherea.repositories.DeliveryAddressRepository;
import com.etherea.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeliveryAddressService {
    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;
    @Autowired
    private UserRepository userRepository;

    /**
     * Adds a delivery address for a given user.
     *
     * @param userId the ID of the user
     * @param deliveryAddressDTO the delivery address to add
     */
    public void addDeliveryAddress(Long userId, DeliveryAddressDTO deliveryAddressDTO) {
        // Fetch the user by userId, throw exception if not found
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'ID : " + userId));

        // Convert the DTO to a DeliveryAddress entity
        DeliveryAddress deliveryAddress = deliveryAddressDTO.toDeliveryAddress();

        // Associate the delivery address with the user
        deliveryAddress.setUser(user);

        // Save the delivery address
        deliveryAddressRepository.save(deliveryAddress);
    }
}
