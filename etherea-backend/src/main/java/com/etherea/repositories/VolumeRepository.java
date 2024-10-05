package com.etherea.repositories;

import com.etherea.models.Volume;
import org.springframework.data.jpa.repository.JpaRepository;
<<<<<<< HEAD

public interface VolumeRepository extends JpaRepository<Volume, Long> {
=======
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VolumeRepository extends JpaRepository<Volume, Long> {
    void deleteByProductId(Long productId);
    List<Volume> findByProductId(Long productId);


>>>>>>> ProductVolume
}
