package com.etherea.services;

import com.etherea.dtos.FavoriteDTO;
import com.etherea.exception.FavoriteNotFoundException;
import com.etherea.exception.ProductNotFoundException;
import com.etherea.exception.UserNotFoundException;
import com.etherea.models.Favorite;
import com.etherea.models.Product;
import com.etherea.models.User;
import com.etherea.repositories.FavoriteRepository;
import com.etherea.repositories.ProductRepository;
import com.etherea.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteService {
    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;

    /**
     * Retrieves the list of favorites for a user.
     *
     * @param userId the ID of the user
     * @return A list of DTOs representing the user's favorites.
     */
    public List<FavoriteDTO> getUserFavorites(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        List<Favorite> favorites = favoriteRepository.findByUser(user);

        return favorites.stream()
                .map(this::convertToFavoriteDTO)
                .collect(Collectors.toList());
    }

    private FavoriteDTO convertToFavoriteDTO(Favorite favorite) {
        FavoriteDTO favoriteDTO = new FavoriteDTO();
        favoriteDTO.setFavoriteId(favorite.getId());
        favoriteDTO.setUserId(favorite.getUser().getId());
        favoriteDTO.setProductId(favorite.getProduct().getId());
        return favoriteDTO;
    }

    /**
     * Adds a product to the user's list of favorites.
     *
     * @param userId the ID of the user
     * @param productId the ID of the product to add.
     */
    public void addFavorite(Long userId, Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));

        if (favoriteRepository.existsByUserAndProduct(user, product)) {
            throw new IllegalStateException("Product is already in favorites.");
        }

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProduct(product);

        favoriteRepository.save(favorite);
    }

    /**
     * Updates the user's list of favorites.
     *
     * @param userId the ID of the user
     * @param updatedProductIds the list of new product IDs to update.
     */
    public void updateFavorites(Long userId, List<Long> updatedProductIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // Retrieve the user's current list of favorites
        List<Favorite> existingFavorites = favoriteRepository.findByUser(user);

        // Iterate through the list of new favorites
        for (Long productId : updatedProductIds) {
            boolean isExistingFavorite = existingFavorites.stream()
                    .anyMatch(favorite -> favorite.getProduct().getId().equals(productId));

            // If the product is not already a favorite, add it
            if (!isExistingFavorite) {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));

                Favorite favorite = new Favorite();
                favorite.setUser(user);
                favorite.setProduct(product);

                favoriteRepository.save(favorite);
            }
        }
    }

    /**
     * Removes a product from the user's list of favorites.
     *
     * @param userId the ID of the user
     * @param productId the ID of the product to remove.
     */
    public void removeFavorite(Long userId, Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));

        Favorite favorite = favoriteRepository.findByUserAndProduct(user, product)
                .orElseThrow(() -> new FavoriteNotFoundException("Favorite not found."));

        favoriteRepository.delete(favorite);
    }
}
