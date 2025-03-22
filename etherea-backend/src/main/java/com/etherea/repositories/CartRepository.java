package com.etherea.repositories;

import com.etherea.enums.CartStatus;
import com.etherea.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository  extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long userId);

    List<Cart> findByUserIdAndStatus(Long userId, CartStatus status);
    Optional<Cart> findFirstByUserIdAndStatusOrderByIdDesc(Long userId, CartStatus status);
    @Query("SELECT c.user.id FROM Cart c WHERE c.id = :cartId")
    Optional<Long> findUserIdByCartId(@Param("cartId") Long cartId);
}

