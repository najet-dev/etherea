package com.etherea.repositories;

import com.etherea.models.Cart;
import com.etherea.models.CommandItem;
import com.etherea.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository  extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long userId);
    Optional<Cart> findByUser(User user);

}
