package com.etherea.services;

import com.etherea.dtos.DeliveryAddressDTO;
import com.etherea.exception.DeliveryAddressNotFoundException;
import com.etherea.exception.UserNotFoundException;
import com.etherea.models.DeliveryAddress;
import com.etherea.models.User;
import com.etherea.repositories.DeliveryAddressRepository;
import com.etherea.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeliveryAddressService {
    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;
    @Autowired
    private UserRepository userRepository;

    /**
     * Retrieves all delivery addresses for a given user.
     *
     * @param userId the ID of the user.
     * @return a list of DeliveryAddressDTO representing all delivery addresses.
     * @throws UserNotFoundException if the user is not found.
     */
    public List<DeliveryAddressDTO> getAllDeliveryAddresses(Long userId) {
        User user = findUserById(userId);

        // Get all addresses for the user
        List<DeliveryAddress> addresses = deliveryAddressRepository.findByUserId(userId);

        // Convert to DTO and return
        return addresses.stream()
                .map(DeliveryAddressDTO::fromDeliveryAddress)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a delivery address by its ID and the user's ID.
     *
     * @param userId    the ID of the user
     * @param addressId the ID of the delivery address
     * @return the DeliveryAddressDTO representing the delivery address
     * @throws UserNotFoundException if the user is not found
     * @throws DeliveryAddressNotFoundException if the address is not found or does not belong to the user
     */
    public DeliveryAddressDTO getDeliveryAddressByIdAndUserId(Long userId, Long addressId) {
        User user = findUserById(userId);

        // Retrieve the address and ensure it belongs to the user
        DeliveryAddress deliveryAddress = deliveryAddressRepository.findById(addressId)
                .filter(address -> address.getUser().getId().equals(userId))
                .orElseThrow(() -> new DeliveryAddressNotFoundException(
                        "Delivery address not found with ID: " + addressId + " for user with ID: " + userId));

        return DeliveryAddressDTO.fromDeliveryAddress(deliveryAddress);
    }

    /**
     * Adds a delivery address for a given user.
     *
     * @param userId             The ID of the user.
     * @param deliveryAddressDTO The delivery address to add.
     * @throws UserNotFoundException if the user is not found.
     */
    public DeliveryAddressDTO addDeliveryAddress(Long userId, DeliveryAddressDTO deliveryAddressDTO) {
        User user = findUserById(userId);

        DeliveryAddress deliveryAddress = deliveryAddressDTO.toDeliveryAddress();
        deliveryAddress.setUser(user);

        // If a default address already exists, set it to false
        List<DeliveryAddress> existingAddresses = deliveryAddressRepository.findByUserId(userId);
        if (existingAddresses.stream().anyMatch(DeliveryAddress::isDefault)) {
            existingAddresses.forEach(address -> address.setDefault(false));
            deliveryAddressRepository.saveAll(existingAddresses);
        }

        // Set the new address as the default and save
        deliveryAddress.setDefault(true);
        DeliveryAddress savedAddress = deliveryAddressRepository.save(deliveryAddress);

        return DeliveryAddressDTO.fromDeliveryAddress(savedAddress);
    }

    /**
     * Updates the latest delivery address for a given user and sets it as the default address if specified.
     *
     * @param userId             The ID of the user.
     * @param deliveryAddressDTO The delivery address to update.
     * @return The updated DeliveryAddressDTO.
     * @throws UserNotFoundException if the user is not found.
     * @throws DeliveryAddressNotFoundException if the address is not found, does not belong to the user.
     */
    @Transactional
    public DeliveryAddressDTO updateDeliveryAddress(Long userId, DeliveryAddressDTO deliveryAddressDTO) {
        User user = findUserById(userId);

        // Retrieve the last address added for the user
        DeliveryAddress latestAddress = deliveryAddressRepository.findTopByUserIdOrderByIdDesc(userId)
                .orElseThrow(() -> new DeliveryAddressNotFoundException(
                        "No delivery addresses found for user with ID: " + userId));

        // Ensure it's the latest address to be updated
        if (!latestAddress.getId().equals(deliveryAddressDTO.getId())) {
            throw new DeliveryAddressNotFoundException(
                    "The address with ID: " + deliveryAddressDTO.getId() + " is not the latest address for user with ID: " + userId);
        }

        // Update address fields
        latestAddress.setAddress(deliveryAddressDTO.getAddress());
        latestAddress.setCity(deliveryAddressDTO.getCity());
        latestAddress.setZipCode(deliveryAddressDTO.getZipCode());
        latestAddress.setCountry(deliveryAddressDTO.getCountry());
        latestAddress.setPhoneNumber(deliveryAddressDTO.getPhoneNumber());

        // Manage default address
        if (deliveryAddressDTO.isDefault()) {
            List<DeliveryAddress> userAddresses = deliveryAddressRepository.findByUserId(userId);
            userAddresses.forEach(address -> {
                if (!address.getId().equals(latestAddress.getId())) {
                    address.setDefault(false);
                }
            });
            latestAddress.setDefault(true);
        }

        // Save and return the updated address
        DeliveryAddress updatedAddress = deliveryAddressRepository.save(latestAddress);
        return DeliveryAddressDTO.fromDeliveryAddress(updatedAddress);
    }

    // Helper method to find user by ID
    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
    }
}
