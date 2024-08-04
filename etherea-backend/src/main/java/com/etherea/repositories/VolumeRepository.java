package com.etherea.repositories;

import com.etherea.models.Volume;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VolumeRepository extends JpaRepository<Volume, Long> {
}
