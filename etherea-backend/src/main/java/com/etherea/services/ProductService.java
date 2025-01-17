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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
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
    private final ModelMapper modelMapper = new ModelMapper();
    public List<ProductDTO> getProducts(int limit) {
        List<Product> facialProducts = productRepository.findByType(
                ProductType.FACE,
                PageRequest.of(0, limit / 2, Sort.by(Sort.Direction.ASC, "id"))
        ).getContent();

        List<Product> hairProducts = productRepository.findByType(
                ProductType.HAIR,
                PageRequest.of(0, limit / 2, Sort.by(Sort.Direction.ASC, "id"))
        ).getContent();

        List<Product> allProducts = new ArrayList<>();
        allProducts.addAll(facialProducts);
        allProducts.addAll(hairProducts);
        Collections.shuffle(allProducts);

        return allProducts.stream()
                .map(ProductDTO::fromProduct)
                .collect(Collectors.toList());
    }
    public List<ProductDTO> getProductsByType(Pageable pageable, ProductType type) {
        return productRepository.findByType(type, pageable)
                .getContent()
                .stream()
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
    @Transactional
    public ResponseEntity<String> saveProduct(ProductDTO productDTO, MultipartFile file) {
        if (productDTO == null) {
            return ResponseEntity.badRequest().body("ProductDTO cannot be null");
        }

        String uploadedImagePath = handleFileUpload(file);
        if (uploadedImagePath != null) {
            productDTO.setImage(uploadedImagePath);
        }

        Product product = convertToProduct(productDTO);
        productRepository.save(product);

        return ResponseEntity.ok("Product saved successfully");
    }
    @Transactional
    public void updateProduct(Long productId, ProductDTO updatedProductDTO, MultipartFile file) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("No product found with ID: " + productId));

        updateProductFromDTO(existingProduct, updatedProductDTO);

        if (file != null && !file.isEmpty()) {
            String uploadedImagePath = handleFileUpload(file);
            if (uploadedImagePath != null) {
                existingProduct.setImage(uploadedImagePath);
            }
        }

        productRepository.save(existingProduct);
    }
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Product with ID " + id + " not found");
        }
        productRepository.deleteById(id);
    }
    private String handleFileUpload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            createUploadDirectory();
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            if (fileName.contains("..")) {
                throw new IllegalArgumentException("Invalid file name");
            }

            Path uploadPath = Paths.get(UPLOAD_DIR);
            Path filePath = uploadPath.resolve(fileName);

            try (InputStream fileInputStream = file.getInputStream()) {
                Files.copy(fileInputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
            return filePath.toString();
        } catch (IOException e) {
            logger.error("Error saving file: {}", e.getMessage(), e);
            throw new RuntimeException("File upload failed", e);
        }
    }
    private void createUploadDirectory() {
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists() && !uploadDir.mkdirs()) {
            throw new RuntimeException("Failed to create upload directory");
        }
    }
    private void updateProductFromDTO(Product product, ProductDTO productDTO) {
        modelMapper.map(productDTO, product);

        if (productDTO.getVolumes() != null) {
            updateProductVolumes(product, productDTO.getVolumes());
        }
    }
    private void updateProductVolumes(Product product, List<VolumeDTO> updatedVolumes) {
        Map<Long, Volume> existingVolumeMap = product.getVolumes().stream()
                .collect(Collectors.toMap(Volume::getId, v -> v));

        for (VolumeDTO volumeDTO : updatedVolumes) {
            if (volumeDTO.getId() != null) {
                Volume existingVolume = existingVolumeMap.get(volumeDTO.getId());
                if (existingVolume != null) {
                    existingVolume.setVolume(volumeDTO.getVolume());
                    existingVolume.setPrice(volumeDTO.getPrice());
                    existingVolumeMap.remove(volumeDTO.getId());
                }
            } else {
                Volume newVolume = new Volume();
                newVolume.setVolume(volumeDTO.getVolume());
                newVolume.setPrice(volumeDTO.getPrice());
                newVolume.setProduct(product);
                product.getVolumes().add(newVolume);
            }
        }

        for (Volume volumeToRemove : existingVolumeMap.values()) {
            product.getVolumes().remove(volumeToRemove);
            volumeRepository.delete(volumeToRemove);
        }
    }
    private Product convertToProduct(ProductDTO productDTO) {
        return modelMapper.map(productDTO, Product.class);
    }
}
