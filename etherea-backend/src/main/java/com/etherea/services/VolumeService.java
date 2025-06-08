package com.etherea.services;

import com.etherea.dtos.VolumeDTO;
import com.etherea.enums.ProductType;
import com.etherea.exception.ProductNotFoundException;
import com.etherea.exception.VolumeNotFoundException;
import com.etherea.models.Product;
import com.etherea.models.Volume;
import com.etherea.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
@Service
public class VolumeService {
    private final ProductRepository productRepository;
    private final VolumeRepository volumeRepository;
    public VolumeService(ProductRepository productRepository, VolumeRepository volumeRepository) {
        this.productRepository = productRepository;
        this.volumeRepository = volumeRepository;
    }

    /**
     * Retrieves a paginated list of volumes.
     *
     * @param page the page number to retrieve (0-based)
     * @param size the number of items per page
     * @return a page of VolumeDTOs converted from Volume entities
     */
    public Page<VolumeDTO> getAllVolumes(int page, int size) {
        Page<Volume> volumePage = volumeRepository.findAll(PageRequest.of(page, size));
        return volumePage.map(VolumeDTO::fromVolume); // Utilisation du helper fromVolume
    }

    /**
     * Adds a new volume associated with a product of type HAIR.
     *
     * @param volumeDTO the DTO containing the volume information to add
     * @return the created and saved VolumeDTO
     * @throws ProductNotFoundException if the associated product does not exist
     * @throws VolumeNotFoundException if the product is not of type HAIR (only HAIR products can have volumes)
     */
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

    /**
     * Updates an existing volume.
     *
     * @param volumeId the ID of the volume to update
     * @param volumeDTO the DTO containing the updated volume information
     * @return the updated VolumeDTO
     * @throws VolumeNotFoundException if no volume with the given ID exists
     */
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

    /**
     * Deletes a volume by its ID.
     *
     * @param id the ID of the volume to delete
     * @return true if the volume was found and deleted, false otherwise
     */
    public boolean deleteVolume(Long id) {
        if (volumeRepository.existsById(id)) {
            volumeRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
