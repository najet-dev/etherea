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

@Service
public class DeliveryAddressService {
    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;
    @Autowired
    private UserRepository userRepository;

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

        // Mettre à jour toutes les autres adresses de l'utilisateur pour qu'elles ne soient plus par défaut
        List<DeliveryAddress> existingAddresses = deliveryAddressRepository.findByUserId(userId);
        for (DeliveryAddress existingAddress : existingAddresses) {
            existingAddress.setDefault(false);
            deliveryAddressRepository.save(existingAddress);
        }

        // Définir la nouvelle adresse comme l'adresse par défaut
        deliveryAddress.setDefault(true);
        DeliveryAddress savedAddress = deliveryAddressRepository.save(deliveryAddress);

        // Retourner le DTO de l'adresse ajoutée
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
        // Vérifier que l'utilisateur existe
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // Récupérer la dernière adresse ajoutée pour l'utilisateur
        DeliveryAddress latestAddress = deliveryAddressRepository.findTopByUserIdOrderByIdDesc(userId)
                .orElseThrow(() -> new DeliveryAddressNotFoundException(
                        "No delivery addresses found for user with ID: " + userId));

        // Vérifier que l'adresse à mettre à jour est bien la dernière ajoutée
        if (!latestAddress.getId().equals(deliveryAddressDTO.getId())) {
            throw new DeliveryAddressNotFoundException(
                    "The address with ID: " + deliveryAddressDTO.getId() + " is not the latest address for user with ID: " + userId);
        }

        // Mettre à jour les champs de l'adresse existante avec les nouvelles valeurs
        latestAddress.setAddress(deliveryAddressDTO.getAddress());
        latestAddress.setCity(deliveryAddressDTO.getCity());
        latestAddress.setZipCode(deliveryAddressDTO.getZipCode());
        latestAddress.setCountry(deliveryAddressDTO.getCountry());
        latestAddress.setPhoneNumber(deliveryAddressDTO.getPhoneNumber());

        // Gestion de l'adresse par défaut
        if (deliveryAddressDTO.isDefault()) {
            // Si l'adresse à mettre à jour doit être définie comme par défaut, désactiver les autres adresses
            List<DeliveryAddress> userAddresses = deliveryAddressRepository.findByUserId(userId);
            for (DeliveryAddress address : userAddresses) {
                if (!address.getId().equals(latestAddress.getId())) {
                    address.setDefault(false);
                    deliveryAddressRepository.save(address);
                }
            }
            // Définir l'adresse actuelle comme par défaut
            latestAddress.setDefault(true);
        }

        // Sauvegarder l'adresse mise à jour
        DeliveryAddress updatedAddress = deliveryAddressRepository.save(latestAddress);

        // Retourner le DTO de l'adresse mise à jour
        return DeliveryAddressDTO.fromDeliveryAddress(updatedAddress);
    }


}
