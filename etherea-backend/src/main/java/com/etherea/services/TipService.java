package com.etherea.services;

import com.etherea.dtos.TipDTO;
import com.etherea.exception.TipNotFoundException;
import com.etherea.models.Tip;
import com.etherea.repositories.TipRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.Date;

@Service
public class TipService {
    @Autowired
    private TipRepository tipRepository;

    private static final Logger logger = LoggerFactory.getLogger(TipService.class);
    private static final String UPLOAD_DIR = "assets"; // Directory to store uploaded images

    /**
     * Retrieves all tips with pagination.
     *
     * @param page the page number to retrieve
     * @param size the number of tips per page
     * @return a paginated list of {@link TipDTO}
     */
    public Page<TipDTO> getTips(int page, int size) {
        Page<Tip> tipsPage = tipRepository.findAll(
                PageRequest.of(page, size)
        );
        return tipsPage.map(TipDTO::fromTip);
    }

    /**
     * Retrieves a tip by its ID.
     *
     * @param id the ID of the tip
     * @return a {@link TipDTO} representing the tip
     * @throws TipNotFoundException if no tip is found with the given ID
     */
    public TipDTO getTipById(Long id) {
        Tip tip = tipRepository.findById(id)
                .orElseThrow(() -> new TipNotFoundException("No tip found with ID: " + id));
        return TipDTO.fromTip(tip);
    }

    /**
     * Saves a new tip, including optional image upload.
     *
     * @param tipDTO the data of the tip to be saved
     * @param file the image file to upload (can be null)
     * @throws IllegalArgumentException if validation fails
     */
    @Transactional
    public void saveTip(TipDTO tipDTO, MultipartFile file) {
        validateTip(tipDTO);

        if (file != null && !file.isEmpty()) {
            String uploadedImagePath = handleFileUpload(file);
            if (uploadedImagePath != null) {
                tipDTO.setImage(uploadedImagePath);
            }
        }

        Tip tip = tipDTO.toTip();
        tip.setDateCreation(new Date());
        tipRepository.save(tip);
    }

    /**
     * Updates an existing tip by its ID.
     *
     * @param updatedTipDTO the updated tip data
     * @param file the new image file (optional)
     * @throws TipNotFoundException if the tip does not exist
     */
    @Transactional
    public void updateTip(TipDTO updatedTipDTO, MultipartFile file) {
        Long tipId = updatedTipDTO.getId();
        if (tipId == null) {
            throw new TipNotFoundException("Tip ID is required");
        }

        Tip existingTip = tipRepository.findById(tipId)
                .orElseThrow(() -> new TipNotFoundException("No tip found with ID: " + tipId));

        if (StringUtils.hasText(updatedTipDTO.getTitle())) {
            existingTip.setTitle(updatedTipDTO.getTitle());
        }
        if (StringUtils.hasText(updatedTipDTO.getDescription())) {
            existingTip.setDescription(updatedTipDTO.getDescription());
        }
        if (StringUtils.hasText(updatedTipDTO.getContent())) {
            existingTip.setContent(updatedTipDTO.getContent());
        }
        if (StringUtils.hasText(updatedTipDTO.getImage())) {
            existingTip.setImage(updatedTipDTO.getImage());
        }

        // Handle image upload
        if (file != null && !file.isEmpty()) {
            String uploadedImagePath = handleFileUpload(file);
            if (uploadedImagePath != null) {
                existingTip.setImage(uploadedImagePath);
            }
        }

        tipRepository.save(existingTip);
    }

    /**
     * Deletes a tip by its ID.
     *
     * @param id the ID of the tip to delete
     * @throws TipNotFoundException if the tip does not exist
     */
    public void deleteTip(Long id) {
        if (!tipRepository.existsById(id)) {
            throw new TipNotFoundException("Tip with ID " + id + " not found");
        }
        tipRepository.deleteById(id);
    }

    /**
     * Validates the required fields of a tip.
     *
     * @param tipDTO the tip data to validate
     * @throws IllegalArgumentException if any required field is missing or invalid
     */
    private void validateTip(TipDTO tipDTO) {
        if (tipDTO == null) throw new IllegalArgumentException("TipDTO cannot be null");
        if (!StringUtils.hasText(tipDTO.getTitle())) throw new IllegalArgumentException("Title is required");
        if (!StringUtils.hasText(tipDTO.getDescription())) throw new IllegalArgumentException("Description is required");
        if (!StringUtils.hasText(tipDTO.getContent())) throw new IllegalArgumentException("Content is required");
    }

    /**
     * Handles image upload by saving the file to the upload directory.
     *
     * @param file the image file to upload
     * @return the file path of the uploaded image, or null if the file is empty
     * @throws RuntimeException if an error occurs during file saving
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

            return filePath.toString(); // Return the path of the uploaded file
        } catch (IOException e) {
            logger.error("Error saving file: {}", e.getMessage(), e);
            throw new RuntimeException("File upload failed", e);
        }
    }

    /**
     * Creates the upload directory if it does not already exist.
     *
     * @throws RuntimeException if the directory cannot be created
     */
    private void createUploadDirectory() {
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists() && !uploadDir.mkdirs()) {
            throw new RuntimeException("Failed to create upload directory");
        }
    }
}