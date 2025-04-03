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
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'ID: " + userId));

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
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'ID: " + userId));

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
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'ID: " + userId));

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
     * Met à jour l'adresse de livraison pour un utilisateur donné.
     *
     * @param userId             L'ID de l'utilisateur.
     * @param deliveryAddressDTO Le DTO de l'adresse à mettre à jour.
     * @return Le DTO de l'adresse mise à jour.
     * @throws UserNotFoundException si l'utilisateur n'existe pas.
     * @throws DeliveryAddressNotFoundException si l'adresse n'existe pas ou n'appartient pas à l'utilisateur.
     */
    @Transactional
    public DeliveryAddressDTO updateDeliveryAddress(Long userId, DeliveryAddressDTO deliveryAddressDTO) {
        // Récupération de l'utilisateur via UserService
        UserDTO userDTO = userService.getUserById(userId);

        // Recherche de l'adresse à mettre à jour
        DeliveryAddress deliveryAddress = deliveryAddressRepository.findById(deliveryAddressDTO.getId())
                .filter(address -> address.getUser().getId().equals(userId))
                .orElseThrow(() -> new DeliveryAddressNotFoundException(
                        "Adresse de livraison non trouvée avec l'ID: " + deliveryAddressDTO.getId() + " pour l'utilisateur ID: " + userId));

        // Si l'adresse est définie comme par défaut, la mettre à jour
        if (deliveryAddressDTO.isDefault()) {
            defaultAddressService.setDefaultAddress(userId, deliveryAddress.getId());
        }

        // Mise à jour des champs de l'adresse
        deliveryAddress.setAddress(deliveryAddressDTO.getAddress());
        deliveryAddress.setCity(deliveryAddressDTO.getCity());
        deliveryAddress.setZipCode(deliveryAddressDTO.getZipCode());
        deliveryAddress.setCountry(deliveryAddressDTO.getCountry());
        deliveryAddress.setPhoneNumber(deliveryAddressDTO.getPhoneNumber());

        // Sauvegarde de l'adresse mise à jour
        DeliveryAddress updatedAddress = deliveryAddressRepository.save(deliveryAddress);
        return DeliveryAddressDTO.fromDeliveryAddress(updatedAddress);
    }
}



