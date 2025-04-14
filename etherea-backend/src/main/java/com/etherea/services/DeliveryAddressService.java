package com.etherea.services;

import com.etherea.dtos.DeliveryAddressDTO;
import com.etherea.dtos.UserDTO;
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
    @Autowired
    private UserService userService;
    @Autowired
    private DefaultAddressService defaultAddressService;

    /**
     * Retrieves all delivery addresses for a given user.
     *
     * @param userId the ID of the user.
     * @return a list of DeliveryAddressDTO representing all delivery addresses.
     * @throws UserNotFoundException if the user is not found.
     */
    public List<DeliveryAddressDTO> getAllDeliveryAddresses(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

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
    @Transactional
    public DeliveryAddressDTO addDeliveryAddress(Long userId, DeliveryAddressDTO deliveryAddressDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        DeliveryAddress deliveryAddress = deliveryAddressDTO.toDeliveryAddress();
        deliveryAddress.setId(null);
        deliveryAddress.setUser(user);

        List<DeliveryAddress> existingAddresses = deliveryAddressRepository.findByUserId(userId);

        if (existingAddresses.isEmpty()) {
            // The first address is automatically set as default
            deliveryAddress.setDefault(true);
        } else {
            // Any new address added is NOT default
            // Regardless of the value of the isDefault field in the DTO
            deliveryAddress.setDefault(false);
        }

        DeliveryAddress savedAddress = deliveryAddressRepository.save(deliveryAddress);
        return DeliveryAddressDTO.fromDeliveryAddress(savedAddress);
    }

    /**
     * Updates the delivery address for a given user.
     *
     * @param userId             The ID of the user.
     * @param deliveryAddressDTO The DTO of the address to update.
     * @return The DTO of the updated address.
     * @throws UserNotFoundException if the user does not exist.
     * @throws DeliveryAddressNotFoundException if the address does not exist or does not belong to the user.
     */
    @Transactional
    public DeliveryAddressDTO updateDeliveryAddress(Long userId, DeliveryAddressDTO deliveryAddressDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // Find the address to update
        DeliveryAddress deliveryAddress = deliveryAddressRepository.findById(deliveryAddressDTO.getId())
                .filter(address -> address.getUser().getId().equals(userId))
                .orElseThrow(() -> new DeliveryAddressNotFoundException(
                        "Delivery address not found with ID: " + deliveryAddressDTO.getId() + " for user ID: " + userId));

        // If the address is set as default, update it
        if (deliveryAddressDTO.isDefault()) {
            defaultAddressService.setDefaultAddress(userId, deliveryAddress.getId());
        }

        // Update the address fields
        deliveryAddress.setAddress(deliveryAddressDTO.getAddress());
        deliveryAddress.setCity(deliveryAddressDTO.getCity());
        deliveryAddress.setZipCode(deliveryAddressDTO.getZipCode());
        deliveryAddress.setCountry(deliveryAddressDTO.getCountry());
        deliveryAddress.setPhoneNumber(deliveryAddressDTO.getPhoneNumber());

        // Save the updated address
        DeliveryAddress updatedAddress = deliveryAddressRepository.save(deliveryAddress);
        return DeliveryAddressDTO.fromDeliveryAddress(updatedAddress);
    }

    /**
     * Deletes a delivery address for a given user.
     *
     * @param userId    The ID of the user.
     * @param addressId The ID of the address to delete.
     * @throws UserNotFoundException if the user does not exist.
     * @throws DeliveryAddressNotFoundException if the address does not exist or does not belong to the user.
     */
    @Transactional
    public void deleteDeliveryAddress(Long userId, Long addressId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // Check and delete the address
        DeliveryAddress deliveryAddress = deliveryAddressRepository.findById(addressId)
                .filter(address -> address.getUser().getId().equals(userId))
                .orElseThrow(() -> new DeliveryAddressNotFoundException(
                        "Delivery address not found with ID: " + addressId + " for user ID: " + userId));

        deliveryAddressRepository.delete(deliveryAddress);
    }

}
