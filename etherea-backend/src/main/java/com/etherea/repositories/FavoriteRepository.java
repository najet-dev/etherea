package com.etherea.repositories;

import com.etherea.models.Favorite;
import com.etherea.models.Product;
import com.etherea.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository  extends JpaRepository<Favorite, Long> {
    boolean existsByUserAndProduct(User user, Product product);
    Optional<Favorite> findByUserAndProduct(User user, Product product);
    List<Favorite> findByUser(User user);
}
