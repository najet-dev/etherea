package com.etherea.services;

import com.etherea.dtos.FavoriteDTO;
import com.etherea.models.Favorite;
import com.etherea.models.Product;
import com.etherea.models.User;
import com.etherea.repositories.FavoriteRepository;
import com.etherea.repositories.ProductRepository;
import com.etherea.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * @throws IllegalArgumentException Si l'utilisateur n'est pas trouvé.
     */
    public List<FavoriteDTO> getUserFavorites(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

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
     * @throws IllegalArgumentException Si l'utilisateur ou le produit n'est pas trouvé, ou si le produit est déjà en favori.
     */
    public void addFavorite(Long userId, Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        if (favoriteRepository.existsByUserAndProduct(user, product)) {
            throw new IllegalStateException("Product already in favorites");
        }

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProduct(product);

        favoriteRepository.save(favorite);
    }
    /**
     * Supprime un produit des favoris d'un utilisateur.
     *
     * @param userId    L'ID de l'utilisateur.
     * @param productId L'ID du produit à supprimer.
     * @throws IllegalArgumentException Si l'utilisateur ou le produit n'est pas trouvé, ou si le favori n'est pas trouvé.
     */
    public void removeFavorite(Long userId, Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        Favorite favorite = favoriteRepository.findByUserAndProduct(user, product)
                .orElseThrow(() -> new IllegalArgumentException("Favorite not found"));

        favoriteRepository.delete(favorite);
    }
}
