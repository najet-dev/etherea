package com.etherea.repositories;

import com.etherea.models.Command;
import com.etherea.models.DeliveryAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, Long> {
    List<DeliveryAddress> findByUserId(Long userId);
    Optional<DeliveryAddress> findTopByUserIdOrderByIdDesc(Long userId);
    @Modifying
    @Query("UPDATE DeliveryAddress da SET da.isDefault = false WHERE da.user.id = :userId AND da.isDefault = true")
    void clearDefaultAddress(@Param("userId") Long userId);
    @Query("SELECT a FROM DeliveryAddress a JOIN FETCH a.user WHERE a.id = :id")
    Optional<DeliveryAddress> findByIdWithUser(@Param("id") Long id);
}
