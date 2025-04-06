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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
@Service
public class VolumeService {
    @Autowired
    private VolumeRepository volumeRepository;

    @Autowired
    private ProductRepository productRepository;

    public Page<VolumeDTO> getAllVolumes(int page, int size) {
        Page<Volume> volumePage = volumeRepository.findAll(PageRequest.of(page, size));
        return volumePage.map(VolumeDTO::fromVolume); // Utilisation du helper fromVolume
    }
    @Transactional
    public VolumeDTO addVolume(VolumeDTO volumeDTO) {
        Long productId = volumeDTO.getProductId();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    System.out.println("Produit non trouvé avec l'ID: " + productId);
                    return new ProductNotFoundException("Produit non trouvé avec l'ID: " + productId);
                });

        if (product.getType() != ProductType.HAIR) {
            System.out.println("Produit de type incorrect, attendu HAIR");
            throw new VolumeNotFoundException("Les volumes ne peuvent être ajoutés que pour les produits de type HAIR.");
        }

        Volume volume = volumeDTO.toVolume(product);
        volume = volumeRepository.save(volume);
        return VolumeDTO.fromVolume(volume);
    }

    @Transactional
    public VolumeDTO updateVolume(Long volumeId, VolumeDTO volumeDTO) {
        Volume existingVolume = volumeRepository.findById(volumeId)
                .orElseThrow(() -> new VolumeNotFoundException("Volume non trouvé avec ID: " + volumeId));

        if (volumeDTO.getVolume() > 0) {
            existingVolume.setVolume(volumeDTO.getVolume());
        }

        if (volumeDTO.getPrice() != null && volumeDTO.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            existingVolume.setPrice(volumeDTO.getPrice());
        }

        existingVolume = volumeRepository.save(existingVolume);
        return VolumeDTO.fromVolume(existingVolume);
    }

    public boolean deleteVolume(Long id) {
        if (volumeRepository.existsById(id)) {
            volumeRepository.deleteById(id);
            return true;
        }
        return false;
    }
}