package com.etherea.services;

import com.etherea.dtos.VolumeDTO;
import com.etherea.enums.ProductType;
import com.etherea.exception.ProductNotFoundException;
import com.etherea.models.Product;
import com.etherea.models.Volume;
import com.etherea.repositories.ProductRepository;
import com.etherea.dtos.ProductDTO;
import com.etherea.repositories.VolumeRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private VolumeRepository volumeRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private static final String UPLOAD_DIR = "assets";

    /**
     * Retrieves a list of products, split between facial and hair products, and shuffles them.
     *
     * @param limit The maximum number of products to retrieve.
     * @return A shuffled list of ProductDTO objects.
     */
    public List<ProductDTO> getProducts(int limit) {
        List<Product> facialProducts = productRepository.findByType(ProductType.FACE, PageRequest.of(0, limit / 2, Sort.by(Sort.Direction.ASC, "id"))).getContent();
        List<Product> hairProducts = productRepository.findByType(ProductType.HAIR, PageRequest.of(0, limit / 2, Sort.by(Sort.Direction.ASC, "id"))).getContent();

        List<Product> allProducts = new ArrayList<>();
        allProducts.addAll(facialProducts);
        allProducts.addAll(hairProducts);

        // Shuffle the list of all products
        Collections.shuffle(allProducts);

        return allProducts.stream()
                .map(ProductDTO::fromProduct)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves products of a specific type with pagination.
     *
     * @param pageable The pagination information.
     * @param type The type of product (FACE, HAIR, etc.).
     * @return A list of ProductDTOs matching the given type.
     */
    public List<ProductDTO> getProductsByType(Pageable pageable, ProductType type) {
        Page<Product> productPage = productRepository.findByType(type, pageable);
        return productPage.getContent().stream()
                .map(ProductDTO::fromProduct)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a product by its ID.
     *
     * @param id The ID of the product.
     * @return A ProductDTO representing the product.
     * @throws ProductNotFoundException if no product is found with the given ID.
     */
    public ProductDTO getProductById(Long id) {
        return productRepository.findById(id)
                .map(ProductDTO::fromProduct)
                .orElseThrow(() -> {
                    logger.error("No product found with ID: {}", id);
                    return new ProductNotFoundException("No product found with ID: " + id);
                });
    }

    /**
     * Saves a product to the repository and handles the product image upload.
     *
     * @param productDTO The product data transfer object.
     * @param file The image file associated with the product.
     * @return A ResponseEntity with a success or error message.
     */
    public ResponseEntity<String> saveProduct(ProductDTO productDTO, MultipartFile file) {
        if (productDTO == null) {
            return ResponseEntity.badRequest().body("ProductDTO cannot be null");
        }

        // Upload the image
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

        // Convert ProductDTO to Product
        Product product = convertToProduct(productDTO);

        // Assign base price
        product.setBasePrice(productDTO.getBasePrice());

        // Add volumes
        if (productDTO.getVolumes() != null && !productDTO.getVolumes().isEmpty()) {
            List<Volume> volumes = productDTO.getVolumes().stream()
                    .map(volumeDTO -> {
                        Volume volume = new Volume();
                        volume.setVolume(volumeDTO.getVolume());
                        volume.setPrice(volumeDTO.getPrice());
                        volume.setProduct(product); // Associate the volume with the product
                        return volume;
                    }).collect(Collectors.toList());
            product.setVolumes(volumes);
        }
        // Save the product
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

    /**
     * Updates an existing product with new data and optional image.
     *
     * @param productId The ID of the product to update.
     * @param updatedProductDTO The new data for the product.
     * @param file The new image file (optional).
     */
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

    /**
     * Updates product volumes based on the provided list.
     *
     * @param existingProduct The product being updated.
     * @param updatedVolumes The updated volume data.
     */
    @Transactional
    public void updateProductVolumes(Product existingProduct, List<VolumeDTO> updatedVolumes) {
        if (updatedVolumes == null) return; // If no volumes to update, exit

        // Create a map of existing volumes by ID for easy access
        Map<Long, Volume> existingVolumeMap = existingProduct.getVolumes().stream()
                .collect(Collectors.toMap(Volume::getId, v -> v));

        // Handle volumes to update or add
        for (VolumeDTO volumeDTO : updatedVolumes) {
            if (volumeDTO.getId() != null) {
                // Update existing volume
                Volume existingVolume = existingVolumeMap.get(volumeDTO.getId());
                if (existingVolume != null) {
                    existingVolume.setVolume(volumeDTO.getVolume());
                    existingVolume.setPrice(volumeDTO.getPrice());
                    volumeRepository.save(existingVolume);
                    existingVolumeMap.remove(volumeDTO.getId()); // Mark as processed
                }
            } else {
                // Create a new volume
                Volume newVolume = new Volume();
                newVolume.setVolume(volumeDTO.getVolume());
                newVolume.setPrice(volumeDTO.getPrice());
                newVolume.setProduct(existingProduct);
                existingProduct.getVolumes().add(newVolume);
                volumeRepository.save(newVolume);
            }
        }
        // Remove volumes no longer in the update
        for (Volume volumeToDelete : existingVolumeMap.values()) {
            existingProduct.getVolumes().remove(volumeToDelete);
            volumeRepository.delete(volumeToDelete);
        }
    }

    /**
     * Updates the product's basic details from the ProductDTO.
     *
     * @param existingProduct The product to update.
     * @param productDTO The new data to apply to the product.
     */
    @Transactional
    public void updateProductFromDTO(Product existingProduct, ProductDTO productDTO) {
        logger.info("Updating product with ID: {}", existingProduct.getId());

        existingProduct.setName(productDTO.getName());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setType(productDTO.getType());
        existingProduct.setBasePrice(productDTO.getBasePrice());
        existingProduct.setStockStatus(productDTO.getStockStatus());
        existingProduct.setBenefits(productDTO.getBenefits());
        existingProduct.setUsageTips(productDTO.getUsageTips());
        existingProduct.setIngredients(productDTO.getIngredients());
        existingProduct.setCharacteristics(productDTO.getCharacteristics());

        logger.debug("Updated product details: {}", existingProduct);
    }

    /**
     * Updates the product's image.
     *
     * @param existingProduct The product to update.
     * @param file The new image file.
     */
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
            logger.error("Error updating product image: {}", e.getMessage(), e);
            throw new RuntimeException("Error updating product image", e);
        }
    }

    /**
     * Deletes a product by its ID.
     *
     * @param id The ID of the product to delete.
     * @throws ProductNotFoundException if the product does not exist.
     */
    public void deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
        } else {
            logger.warn("Product with id {} not found", id);
            throw new ProductNotFoundException("Product with id " + id + " not found");
        }
    }
}
