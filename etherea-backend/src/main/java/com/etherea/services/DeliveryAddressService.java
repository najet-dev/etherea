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
    @Transactional
    public DeliveryAddressDTO addDeliveryAddress(Long userId, DeliveryAddressDTO deliveryAddressDTO) {
        User user = findUserById(userId);
        DeliveryAddress deliveryAddress = deliveryAddressDTO.toDeliveryAddress();
        deliveryAddress.setUser(user);

        // Désactiver l'ancienne adresse par défaut s'il y en a une
        deliveryAddressRepository.clearDefaultAddress(userId);

        // Définir la nouvelle adresse comme l'adresse par défaut
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

        DeliveryAddress deliveryAddress = deliveryAddressRepository.findById(deliveryAddressDTO.getId())
                .filter(address -> address.getUser().getId().equals(userId))
                .orElseThrow(() -> new DeliveryAddressNotFoundException(
                        "Delivery address not found with ID: " + deliveryAddressDTO.getId() + " for user ID: " + userId));

        deliveryAddress.setAddress(deliveryAddressDTO.getAddress());
        deliveryAddress.setCity(deliveryAddressDTO.getCity());
        deliveryAddress.setZipCode(deliveryAddressDTO.getZipCode());
        deliveryAddress.setCountry(deliveryAddressDTO.getCountry());
        deliveryAddress.setPhoneNumber(deliveryAddressDTO.getPhoneNumber());

        if (deliveryAddressDTO.isDefault()) {
            setDefaultAddress(userId, deliveryAddress.getId());
        }

        DeliveryAddress updatedAddress = deliveryAddressRepository.save(deliveryAddress);
        return DeliveryAddressDTO.fromDeliveryAddress(updatedAddress);
    }

    // Méthode pour définir une adresse par défaut
    public void setDefaultAddress(Long userId, Long addressId) {
        List<DeliveryAddress> userAddresses = deliveryAddressRepository.findByUserId(userId);
        userAddresses.forEach(address -> address.setDefault(address.getId().equals(addressId)));
        deliveryAddressRepository.saveAll(userAddresses);
    }

    // Méthode pour trouver un utilisateur par ID
    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
    }
}
