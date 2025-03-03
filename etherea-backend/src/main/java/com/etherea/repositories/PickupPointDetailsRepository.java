package com.etherea.repositories;

import com.etherea.models.PickupPointDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PickupPointDetailsRepository extends JpaRepository<PickupPointDetails, Long> {
    Optional<PickupPointDetails> findByPickupPointNameAndPickupPointAddress(String pickupPointName, String pickupPointAddress);
}
