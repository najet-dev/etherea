package com.etherea.repositories;

import com.etherea.enums.DeliveryType;
import com.etherea.models.DeliveryMethod;
import io.micrometer.common.lang.NonNullApi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryMethodRepository extends JpaRepository<DeliveryMethod, Long> {
    Optional<DeliveryMethod> findByTypeAndUserId(DeliveryType type, Long userId); // Ajout d'une recherche par utilisateur
    List<DeliveryMethod> findByUserId(Long userId); // Pour récupérer toutes les options de livraison d'un utilisateur
}
