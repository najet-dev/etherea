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
     * Récupère la liste des favoris d'un utilisateur.
     *
     * @param userId L'ID de l'utilisateur.
     * @return Une liste de DTO représentant les favoris de l'utilisateur.
     */
    public List<FavoriteDTO> getUserFavorites(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID : " + userId));

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
     * Ajoute un produit aux favoris d'un utilisateur.
     *
     * @param userId    L'ID de l'utilisateur.
     * @param productId L'ID du produit à ajouter.
     */
    public void addFavorite(Long userId, Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID : " + userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));

        if (favoriteRepository.existsByUserAndProduct(user, product)) {
            throw new IllegalStateException("Product already in favorites");
        }

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProduct(product);

        favoriteRepository.save(favorite);
    }
    /**
     * Met à jour les favoris d'un utilisateur.
     *
     * @param userId         L'ID de l'utilisateur.
     * @param updatedProductIds La liste des nouveaux IDs de produits à mettre à jour.
     */
    public void updateFavorites(Long userId, List<Long> updatedProductIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID : " + userId));

        // Récupérer les favoris actuels de l'utilisateur
        List<Favorite> existingFavorites = favoriteRepository.findByUser(user);

        // Parcourir la liste des nouveaux favoris
        for (Long productId : updatedProductIds) {
            boolean isExistingFavorite = existingFavorites.stream()
                    .anyMatch(favorite -> favorite.getProduct().getId().equals(productId));

            // Si le produit n'est pas déjà un favori, l'ajouter
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
     * Supprime un produit des favoris d'un utilisateur.
     *
     * @param userId    L'ID de l'utilisateur.
     * @param productId L'ID du produit à supprimer.
     */
    public void removeFavorite(Long userId, Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID : " + userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));

        Favorite favorite = favoriteRepository.findByUserAndProduct(user, product)
                .orElseThrow(() -> new FavoriteNotFoundException("Favorite not found"));

        favoriteRepository.delete(favorite);
    }
}
