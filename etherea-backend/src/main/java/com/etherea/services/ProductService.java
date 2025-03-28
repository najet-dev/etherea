package com.etherea.services;

import com.etherea.dtos.UpdateProductDTO;
import com.etherea.dtos.VolumeDTO;
import com.etherea.enums.ProductType;
import com.etherea.exception.ProductNotFoundException;
import com.etherea.models.Product;
import com.etherea.repositories.ProductRepository;
import com.etherea.dtos.ProductDTO;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private static final String UPLOAD_DIR = "assets";
    private final ModelMapper modelMapper = new ModelMapper();

    public List<ProductDTO> getProducts(int limit) {
        List<Product> products = productRepository.findByTypeIn(
                List.of(ProductType.FACE, ProductType.HAIR),
                PageRequest.of(0, limit, Sort.by(Sort.Direction.ASC, "id"))
        );
        return products.stream().map(ProductDTO::fromProduct).collect(Collectors.toList());
    }
    public List<ProductDTO> getProductsByType(Pageable pageable, ProductType type) {
        return productRepository.findByType(type, pageable)
                .getContent()
                .stream()
                .map(ProductDTO::fromProduct)
                .collect(Collectors.toList());
    }
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("No product found with ID: " + id));

        ProductDTO productDTO = ProductDTO.fromProduct(product);

        // Si le produit est de type HAIR, récupérer les volumes associés
        if (product.getType() == ProductType.HAIR) {
            List<VolumeDTO> volumeDTOs = product.getVolumes().stream()
                    .map(VolumeDTO::fromVolume)
                    .collect(Collectors.toList());
            productDTO.setVolumes(volumeDTOs);
        }

        return productDTO;
    }
    @Transactional
    public void saveProduct(ProductDTO productDTO, MultipartFile file) {
        validateProduct(productDTO);
        if (file != null && !file.isEmpty()) {
            String uploadedImagePath = handleFileUpload(file);
            if (uploadedImagePath != null) {
                productDTO.setImage(uploadedImagePath);
            }
        }
        Product product = convertToProduct(productDTO);
        productRepository.save(product);
    }
    @Transactional
    public void updateProduct(ProductDTO updatedProductDTO, MultipartFile file) {
        Long productId = updatedProductDTO.getId();
        if (productId == null) {
            throw new ProductNotFoundException("Product ID is required");
        }
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("No product found with ID: " + productId));

        if (StringUtils.hasText(updatedProductDTO.getName())) {
            existingProduct.setName(updatedProductDTO.getName());
        }
        if (StringUtils.hasText(updatedProductDTO.getDescription())) {
            existingProduct.setDescription(updatedProductDTO.getDescription());
        }
        if (updatedProductDTO.getStockQuantity() >= 0) {
            existingProduct.setStockQuantity(updatedProductDTO.getStockQuantity());
        }
        if (updatedProductDTO.getStockStatus() != null) {
            existingProduct.setStockStatus(updatedProductDTO.getStockStatus());
        }
        if (StringUtils.hasText(updatedProductDTO.getBenefits())) {
            existingProduct.setBenefits(updatedProductDTO.getBenefits());
        }
        if (StringUtils.hasText(updatedProductDTO.getUsageTips())) {
            existingProduct.setUsageTips(updatedProductDTO.getUsageTips());
        }
        if (StringUtils.hasText(updatedProductDTO.getIngredients())) {
            existingProduct.setIngredients(updatedProductDTO.getIngredients());
        }
        if (StringUtils.hasText(updatedProductDTO.getCharacteristics())) {
            existingProduct.setCharacteristics(updatedProductDTO.getCharacteristics());
        }
        if (StringUtils.hasText(updatedProductDTO.getImage())) {
            existingProduct.setImage(updatedProductDTO.getImage());
        }
        if (updatedProductDTO.getType() != null) {
            existingProduct.setType(updatedProductDTO.getType());
        }

        if (existingProduct.getType() == ProductType.FACE) {
            if (updatedProductDTO.getBasePrice() == null) {
                throw new IllegalArgumentException("Base price must be set for FACE products.");
            }
            existingProduct.setBasePrice(updatedProductDTO.getBasePrice());
        } else if (existingProduct.getType() == ProductType.HAIR) {
            existingProduct.setBasePrice(null);
        }

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
    private void validateProduct(ProductDTO productDTO) {
        if (productDTO == null) throw new IllegalArgumentException("ProductDTO cannot be null");
        if (productDTO.getType() == ProductType.FACE && productDTO.getBasePrice() == null) {
            throw new IllegalArgumentException("Base price must be set for FACE products");
        }
        if (productDTO.getType() == ProductType.HAIR) {
            productDTO.setBasePrice(null);
        }
    }
    private String handleFileUpload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            createUploadDirectory();
            String fileName = Paths.get(file.getOriginalFilename()).getFileName().toString();
            Path uploadPath = Paths.get(UPLOAD_DIR);
            Path filePath = uploadPath.resolve(fileName);

            try (InputStream fileInputStream = file.getInputStream()) {
                Files.copy(fileInputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
            return filePath.toString(); // Retourne le chemin du fichier
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
    private Product convertToProduct(ProductDTO productDTO) {
        return modelMapper.map(productDTO, Product.class);
    }
}
