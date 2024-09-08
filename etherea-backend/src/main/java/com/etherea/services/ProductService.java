package com.etherea.services;

import com.etherea.dtos.VolumeDTO;
import com.etherea.enums.ProductType;
import com.etherea.enums.StockStatus;
import com.etherea.exception.ProductNotFoundException;
import com.etherea.exception.VolumeNotFoundException;
import com.etherea.models.Product;
import com.etherea.models.Volume;
import com.etherea.repositories.ProductRepository;
import com.etherea.dtos.ProductDTO;
import com.etherea.repositories.VolumeRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.modelmapper.ModelMapper;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private VolumeRepository volumeRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private static final String UPLOAD_DIR = "assets";

    public List<ProductDTO> getProducts(int limit) {
        List<Product> facialProducts = productRepository.findByType(ProductType.FACE, PageRequest.of(0, limit / 2, Sort.by(Sort.Direction.ASC, "id"))).getContent();
        List<Product> hairProducts = productRepository.findByType(ProductType.HAIR, PageRequest.of(0, limit / 2, Sort.by(Sort.Direction.ASC, "id"))).getContent();

        List<Product> allProducts = new ArrayList<>();
        allProducts.addAll(facialProducts);
        allProducts.addAll(hairProducts);

        // Mélanger la liste de tous les produits
        Collections.shuffle(allProducts);

        return allProducts.stream()
                .map(ProductDTO::fromProduct)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getProductsByType(Pageable pageable, ProductType type) {
        Page<Product> productPage = productRepository.findByType(type, pageable);
        return productPage.getContent().stream()
                .map(ProductDTO::fromProduct)
                .collect(Collectors.toList());
    }

    public ProductDTO getProductById(Long id) {
        return productRepository.findById(id)
                .map(ProductDTO::fromProduct)
                .orElseThrow(() -> {
                    logger.error("No product found with ID: {}", id);
                    return new ProductNotFoundException("No product found with ID: " + id);
                });
    }
    public ResponseEntity<String> saveProduct(ProductDTO productDTO, MultipartFile file) {
        if (productDTO == null) {
            return ResponseEntity.badRequest().body("ProductDTO cannot be null");
        }

        // Upload de l'image
        if (file != null && !file.isEmpty()) {
            createUploadDirectory();
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            if (fileName.contains("..")) {
                return ResponseEntity.badRequest().body("Invalid file name");
            }
            try {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                String filePath = uploadPath.resolve(fileName).toString();
                try (InputStream fileInputStream = file.getInputStream()) {
                    Files.copy(fileInputStream, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
                }
                productDTO.setImage(filePath);
            } catch (IOException e) {
                logger.error("Error saving file: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error saving file: " + e.getMessage());
            }
        }

        // Convertir ProductDTO en Product
        Product product = convertToProduct(productDTO);

        // Ajouter les volumes
        if (productDTO.getVolumes() != null && !productDTO.getVolumes().isEmpty()) {
            List<Volume> volumes = productDTO.getVolumes().stream()
                    .map(volumeDTO -> {
                        Volume volume = new Volume();
                        volume.setVolume(volumeDTO.getVolume());
                        volume.setPrice(volumeDTO.getPrice());
                        volume.setProduct(product); // Associer le volume au produit
                        return volume;
                    }).collect(Collectors.toList());
            product.setVolumes(volumes);
        }

        // Sauvegarder le produit (cela sauvegardera également les volumes associés grâce au cascade = CascadeType.ALL)
        productRepository.save(product);

        return ResponseEntity.ok("Product saved successfully");
    }
    private Product convertToProduct(ProductDTO productDTO) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(productDTO, Product.class);
    }

    private void createUploadDirectory() {
        File uploadDirFile = new File(UPLOAD_DIR);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }
    }
    @Transactional
    public void updateProduct(Long productId, ProductDTO updatedProductDTO, MultipartFile file) {
        logger.info("Starting updateProduct for product ID: {}", productId);

        if (productId == null || updatedProductDTO == null) {
            throw new IllegalArgumentException("Product ID and ProductDTO cannot be null");
        }

        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("No product found with ID: " + productId));

        logger.debug("Retrieved existing product: {}", existingProduct);

        updateProductFromDTO(existingProduct, updatedProductDTO);

        updateProductVolumes(existingProduct, updatedProductDTO.getVolumes());

        if (file != null && !file.isEmpty()) {
            updateProductImage(existingProduct, file);
            logger.debug("Updated product image for product ID: {}", productId);
        }

        productRepository.save(existingProduct);
        logger.info("Finished updateProduct for product ID: {}", productId);
    }

    @Transactional
    public void updateProductVolumes(Product existingProduct, List<VolumeDTO> updatedVolumes) {
        logger.info("Starting updateProductVolumes for product ID: {}", existingProduct.getId());

        // Récupérer les volumes existants du produit
        List<Volume> existingVolumes = new ArrayList<>(existingProduct.getVolumes());
        Map<Long, Volume> existingVolumeMap = existingVolumes.stream()
                .collect(Collectors.toMap(Volume::getId, v -> v));

        Set<Long> updatedVolumeIds = updatedVolumes.stream()
                .filter(volumeDTO -> volumeDTO.getId() != null)
                .map(VolumeDTO::getId)
                .collect(Collectors.toSet());

        // Mettre à jour les volumes existants ou ajouter les nouveaux
        for (VolumeDTO volumeDTO : updatedVolumes) {
            if (volumeDTO.getId() != null && existingVolumeMap.containsKey(volumeDTO.getId())) {
                // Mise à jour du volume existant
                Volume existingVolume = existingVolumeMap.get(volumeDTO.getId());
                existingVolume.setVolume(volumeDTO.getVolume());
                existingVolume.setPrice(volumeDTO.getPrice());
                volumeRepository.save(existingVolume); // Assurez-vous de sauvegarder les modifications
                logger.debug("Updated existing volume: {}", existingVolume);
            } else if (volumeDTO.getId() == null) {
                // Ajout d'un nouveau volume
                Volume newVolume = new Volume();
                newVolume.setVolume(volumeDTO.getVolume());
                newVolume.setPrice(volumeDTO.getPrice());
                newVolume.setProduct(existingProduct);
                existingProduct.getVolumes().add(newVolume);
                volumeRepository.save(newVolume); // Assurez-vous de sauvegarder le nouveau volume
                logger.debug("Added new volume: {}", newVolume);
            }
        }

        // Supprimer les volumes qui ne sont plus présents
        for (Volume existingVolume : existingVolumes) {
            if (!updatedVolumeIds.contains(existingVolume.getId())) {
                existingProduct.getVolumes().remove(existingVolume);
                volumeRepository.delete(existingVolume); // Assurez-vous de supprimer le volume
                logger.debug("Removed volume: {}", existingVolume);
            }
        }

        logger.info("Finished updateProductVolumes for product ID: {}", existingProduct.getId());
    }
    @Transactional
    public void updateProductFromDTO(Product existingProduct, ProductDTO productDTO) {
        logger.info("Updating product with ID: {}", existingProduct.getId());

        existingProduct.setName(productDTO.getName());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setType(productDTO.getType());
        existingProduct.setStockStatus(productDTO.getStockStatus());
        existingProduct.setBenefits(productDTO.getBenefits());
        existingProduct.setUsageTips(productDTO.getUsageTips());
        existingProduct.setIngredients(productDTO.getIngredients());
        existingProduct.setCharacteristics(productDTO.getCharacteristics());

        logger.debug("Updated product details: {}", existingProduct);
    }

    private void updateProductImage(Product existingProduct, MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        if (fileName.contains("..")) {
            throw new IllegalArgumentException("Invalid file name");
        }

        try {
            createUploadDirectory();
            Path uploadPath = Paths.get(UPLOAD_DIR);
            String filePath = uploadPath.resolve(fileName).toString();

            try (InputStream fileInputStream = file.getInputStream()) {
                Files.copy(fileInputStream, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
            }

            existingProduct.setImage(filePath);
        } catch (IOException e) {
            logger.error("Error updating product image: {}", e.getMessage());
            throw new RuntimeException("Error updating product image", e);
        }
    }
    public void deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
        } else {
            logger.warn("Product with id {} not found", id);
            throw new ProductNotFoundException("Product with id " + id + " not found");
        }
    }
}
