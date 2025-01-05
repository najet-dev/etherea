package com.etherea.repositories;

import com.etherea.models.DeliveryMethod;
import io.micrometer.common.lang.NonNullApi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@NonNullApi
@Repository
public interface DeliveryMethodRepository extends JpaRepository<DeliveryMethod, Long> {
    Optional<DeliveryMethod> findById(Long id);

}
