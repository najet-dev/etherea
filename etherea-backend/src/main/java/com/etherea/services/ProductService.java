package com.etherea.services;

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
import org.springframework.data.domain.Page;
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
    private final ProductRepository productRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private static final String UPLOAD_DIR = "assets";
    private final ModelMapper modelMapper = new ModelMapper();
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Retrieves a paginated list of FACE and HAIR products.
     *
     * @param page the page number to retrieve
     * @param size the number of products per page
     * @return a page of {@link ProductDTO} objects
     */
    public Page<ProductDTO> getProducts(int page, int size) {
        // Récupérer une page de produits avec pagination et tri
        Page<Product> productsPage = productRepository.findByTypeIn(
                List.of(ProductType.FACE, ProductType.HAIR),
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"))
        );

        // Convert Page<Product> to Page<ProductDTO>
        return productsPage.map(ProductDTO::fromProduct);
    }

    /**
     * Retrieves a paginated list of new products.
     *
     * @param page the page number
     * @param size the page size
     * @return a page of new {@link ProductDTO} objects
     */public Page<ProductDTO> getNewProducts(int page, int size) {
        // Retrieve page of users
        Page<Product> newProductsPage = productRepository.findByNewProductTrue(PageRequest.of(page, size));

        // Convert Page<User> to Page<UserDTO>
        return newProductsPage.map(ProductDTO::fromProduct);
    }

    /**
     * Retrieves products of a specific type with pagination support.
     *
     * @param pageable pagination and sorting information
     * @param type     the product type to filter
     * @return a page of {@link ProductDTO} filtered by type
     */
    public Page<ProductDTO> getProductsByType(Pageable pageable, ProductType type) {
        // Retrieve a product page by type with pagination and sorting
        Page<Product> productsPage = productRepository.findByType(type, pageable);

        // Convert Page<Product> to Page<ProductDTO>.
        return productsPage.map(ProductDTO::fromProduct);
    }

    /**
     * Retrieves a product by its ID, including volume details if it's a HAIR product.
     *
     * @param id the product ID
     * @return the matching {@link ProductDTO}
     * @throws ProductNotFoundException if no product is found with the given ID
     */
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

    /**
     * Searches for products by name (case-insensitive).
     *
     * @param name the name or partial name to search for
     * @return list of matching {@link ProductDTO} objects
     * @throws ProductNotFoundException if no products match the search term
     */
    public List<ProductDTO> getProductsByName(String name) {
        List<Product> products = productRepository.findByNameContainingIgnoreCase(name);
        if (products.isEmpty()) {
            throw new ProductNotFoundException("No products found with name containing: " + name);
        }
        return products.stream().map(ProductDTO::fromProduct).collect(Collectors.toList());
    }

    /**
     * Saves a new product to the database, optionally handling an image upload.
     *
     * @param productDTO the product data
     * @param file       the image file to upload (nullable)
     */
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

    /**
     * Updates an existing product, optionally replacing the image.
     *
     * @param updatedProductDTO the updated product data
     * @param file              the new image file (nullable)
     * @throws ProductNotFoundException if no product exists with the given ID
     * @throws IllegalArgumentException if required fields are missing
     */
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

    /**
     * Deletes a product by ID.
     *
     * @param id the ID of the product to delete
     * @throws ProductNotFoundException if the product does not exist
     */public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Product with ID " + id + " not found");
        }
        productRepository.deleteById(id);
    }

    /**
     * Validates the product based on its type and required fields.
     *
     * @param productDTO the product data to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateProduct(ProductDTO productDTO) {
        if (productDTO == null) throw new IllegalArgumentException("ProductDTO cannot be null");
        if (productDTO.getType() == ProductType.FACE && productDTO.getBasePrice() == null) {
            throw new IllegalArgumentException("Base price must be set for FACE products");
        }
        if (productDTO.getType() == ProductType.HAIR) {
            productDTO.setBasePrice(null);
        }
    }

    /**
     * Handles the upload and saving of an image file to the server.
     *
     * @param file the image file to upload
     * @return the path of the uploaded image file
     * @throws RuntimeException if upload fails
     */
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

    /**
     * Ensures the upload directory exists or creates it if necessary.
     *
     * @throws RuntimeException if directory creation fails
     */
    private void createUploadDirectory() {
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists() && !uploadDir.mkdirs()) {
            throw new RuntimeException("Failed to create upload directory");
        }
    }

    /**
     * Converts a {@link ProductDTO} to a {@link Product} entity using {@link ModelMapper}.
     *
     * @param productDTO the product DTO to convert
     * @return the corresponding Product entity
     */
    private Product convertToProduct(ProductDTO productDTO) {
        return modelMapper.map(productDTO, Product.class);
    }
}