package com.etherea.services;

import com.etherea.dtos.DeliveryAddressDTO;
import com.etherea.exception.DeliveryAddressNotFoundException;
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
     * Retrieves a delivery address by its ID and the user's ID.
     *
     * @param userId    the ID of the user.
     * @param addressId the ID of the delivery address.
     * @return the DeliveryAddressDTO representing the delivery address.
     * @throws UserNotFoundException if the user is not found.
     * @throws DeliveryAddressNotFoundException if the address is not found or does not belong to the user.
     */
    public DeliveryAddressDTO getDeliveryAddressByIdAndUserId(Long userId, Long addressId) {
        // Find the delivery address and check if it belongs to the user
        DeliveryAddress deliveryAddress = deliveryAddressRepository.findById(addressId)
                .filter(address -> address.getUser().getId().equals(userId)) // Ensures the address belongs to the user
                .orElseThrow(() -> new DeliveryAddressNotFoundException(
                        "Delivery address not found with ID: " + addressId + " for user with ID: " + userId));

        // Convert the DeliveryAddress entity to a DTO and return it
        return DeliveryAddressDTO.fromDeliveryAddress(deliveryAddress);
    }

    /**
     * Adds a delivery address for a given user.
     *
     * @param userId             The ID of the user.
     * @param deliveryAddressDTO The delivery address to add.
     * @throws UserNotFoundException if the user is not found.
     */
    public void addDeliveryAddress(Long userId, DeliveryAddressDTO deliveryAddressDTO) {
        // Fetch the user by userId, throw an exception if not found
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // Convert the DTO to a DeliveryAddress entity
        DeliveryAddress deliveryAddress = deliveryAddressDTO.toDeliveryAddress();

        // Associate the delivery address with the user
        deliveryAddress.setUser(user);

        // Save the delivery address
        deliveryAddressRepository.save(deliveryAddress);
    }
}
