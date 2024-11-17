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
        // Vérifier que l'utilisateur existe
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // Récupérer toutes les adresses associées à l'utilisateur
        List<DeliveryAddress> addresses = deliveryAddressRepository.findByUserId(userId);

        // Convertir les adresses en DTO et les retourner
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        DeliveryAddress deliveryAddress = deliveryAddressDTO.toDeliveryAddress();
        deliveryAddress.setUser(user);

        // Update all other user addresses so that they are no longer default addresses
        List<DeliveryAddress> existingAddresses = deliveryAddressRepository.findByUserId(userId);
        for (DeliveryAddress existingAddress : existingAddresses) {
            existingAddress.setDefault(false);
            deliveryAddressRepository.save(existingAddress);
        }

        // Set new address as default address
        deliveryAddress.setDefault(true);
        DeliveryAddress savedAddress = deliveryAddressRepository.save(deliveryAddress);

        // Return the DTO of the added address
        return DeliveryAddressDTO.fromDeliveryAddress(savedAddress);
    }
    /**
     * Updates the latest delivery address for a given user and sets it as the default address if specified.
     *
     * @param userId             The ID of the user.
     * @param deliveryAddressDTO The delivery address to update.
     * @return The updated DeliveryAddressDTO.
     * @throws UserNotFoundException if the user is not found.
     * @throws DeliveryAddressNotFoundException if the address is not found, does not belong to the user,
     *                                          or is not the latest address.
     */
    public DeliveryAddressDTO updateDeliveryAddress(Long userId, DeliveryAddressDTO deliveryAddressDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // Retrieve the last address added for the user
        DeliveryAddress latestAddress = deliveryAddressRepository.findTopByUserIdOrderByIdDesc(userId)
                .orElseThrow(() -> new DeliveryAddressNotFoundException(
                        "No delivery addresses found for user with ID: " + userId));

        // Check that the address to be updated is the last one added
        if (!latestAddress.getId().equals(deliveryAddressDTO.getId())) {
            throw new DeliveryAddressNotFoundException(
                    "The address with ID: " + deliveryAddressDTO.getId() + " is not the latest address for user with ID: " + userId);
        }

        // Update existing address fields with new values
        latestAddress.setAddress(deliveryAddressDTO.getAddress());
        latestAddress.setCity(deliveryAddressDTO.getCity());
        latestAddress.setZipCode(deliveryAddressDTO.getZipCode());
        latestAddress.setCountry(deliveryAddressDTO.getCountry());
        latestAddress.setPhoneNumber(deliveryAddressDTO.getPhoneNumber());

        // Default address management
        if (deliveryAddressDTO.isDefault()) {
            // If the address to be updated is to be set as default, disable other addresses
            List<DeliveryAddress> userAddresses = deliveryAddressRepository.findByUserId(userId);
            for (DeliveryAddress address : userAddresses) {
                if (!address.getId().equals(latestAddress.getId())) {
                    address.setDefault(false);
                    deliveryAddressRepository.save(address);
                }
            }
            // Set current address as default
            latestAddress.setDefault(true);
        }
        // Save updated address
        DeliveryAddress updatedAddress = deliveryAddressRepository.save(latestAddress);

        // Return the updated address DTO
        return DeliveryAddressDTO.fromDeliveryAddress(updatedAddress);
    }
}
