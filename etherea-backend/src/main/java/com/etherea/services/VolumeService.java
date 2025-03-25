package com.etherea.services;

import com.etherea.dtos.VolumeDTO;
import com.etherea.enums.ProductType;
import com.etherea.exception.ProductNotFoundException;
import com.etherea.exception.VolumeNotFoundException;
import com.etherea.models.Product;
import com.etherea.models.Volume;
import com.etherea.repositories.ProductRepository;
import com.etherea.repositories.VolumeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VolumeService {
    @Autowired
    private VolumeRepository volumeRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<VolumeDTO> getAllVolumes() {
        List<Volume> volumes = volumeRepository.findAll();

        // Convert Volume entities to VolumeDTO objects with productName
        return volumes.stream()
                .map(volume -> new VolumeDTO(
                        volume.getId(),
                        volume.getProduct().getName(),
                        volume.getVolume(),
                        volume.getPrice())
                )
                .collect(Collectors.toList());
    }
    @Transactional
    public VolumeDTO addVolume(String productName, VolumeDTO volumeDTO) {
        Product product = productRepository.findByName(productName)
                .orElseThrow(() -> {
                    System.out.println("Produit non trouvé avec le nom: " + productName);
                    return new ProductNotFoundException("Produit non trouvé avec le nom: " + productName);
                });

        if (product.getType() != ProductType.HAIR) {
            System.out.println("Produit de type incorrect, attendu HAIR");
            throw new VolumeNotFoundException("Les volumes ne peuvent être ajoutés que pour les produits de type HAIR.");
        }

        Volume volume = volumeDTO.toVolume(product);
        volume = volumeRepository.save(volume);
        return new VolumeDTO(volume.getId(), product.getName(), volume.getVolume(), volume.getPrice());
    }
    @Transactional
    public VolumeDTO updateVolume(Long volumeId, VolumeDTO volumeDTO) {
        Volume existingVolume = volumeRepository.findById(volumeId)
                .orElseThrow(() -> new VolumeNotFoundException("Volume non trouvé avec ID: " + volumeId));

        // Update fields
        if (volumeDTO.getVolume() > 0) {
            existingVolume.setVolume(volumeDTO.getVolume());
        }
        if (volumeDTO.getPrice() != null && volumeDTO.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            existingVolume.setPrice(volumeDTO.getPrice());
        }

        existingVolume = volumeRepository.save(existingVolume);
        return new VolumeDTO(existingVolume.getId(), existingVolume.getProduct().getName(), existingVolume.getVolume(), existingVolume.getPrice());
    }

    public boolean deleteVolume(Long id) {
        if (volumeRepository.existsById(id)) {
            volumeRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
