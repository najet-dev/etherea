package com.etherea.controllers;

import com.etherea.dtos.TipDTO;
import com.etherea.services.PaymentService;
import com.etherea.services.ProductService;
import com.etherea.services.TipService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/tips")
@CrossOrigin(origins = "*")
public class TipController {
    private final TipService tipService;
    private static final Logger logger = LoggerFactory.getLogger(TipController.class);
    public TipController(TipService tipService) {
        this.tipService = tipService;
    }

    /**
     * Retrieves a paginated list of tips.
     *
     * @param page the page number to retrieve (default is 0)
     * @param size the number of items per page (default is 10)
     * @return a paginated list of TipDTO objects
     */
    @GetMapping
    public ResponseEntity<Page<TipDTO>> getAllTips(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<TipDTO> tips = tipService.getTips(page, size);
        return ResponseEntity.ok(tips);
    }
    /**
     * Retrieves a specific tip by its ID.
     *
     * @param id the ID of the tip
     * @return the corresponding TipDTO object
     */
    @GetMapping("/{id}")
    public ResponseEntity<TipDTO> getTipById(@PathVariable Long id) {
        TipDTO tipDTO = tipService.getTipById(id);
        return ResponseEntity.ok(tipDTO);
    }
    /**
     * Creates a new tip with or without an image.
     *
     * @param file     the image associated with the tip (optional)
     * @param tipJson  the JSON representation of the TipDTO
     * @return a success message or an error message
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/add", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, String>> createTip(
            @RequestParam(value = "image", required = false) MultipartFile file,
            @RequestParam("tip") String tipJson) {

        if (StringUtils.isBlank(tipJson)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Tip data is required."));
        }

        try {
            // Désérialisation du JSON en objet TipDTO
            ObjectMapper objectMapper = new ObjectMapper();
            TipDTO tipDTO = objectMapper.readValue(tipJson, TipDTO.class);

            logger.info("Tip received: {}", tipDTO);

            // Enregistrer le tip avec ou sans image
            tipService.saveTip(tipDTO, file);
            return ResponseEntity.ok(Map.of("message", "Tip saved successfully"));

        } catch (JsonProcessingException e) {
            logger.error("Error deserializing tip JSON", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid tip JSON data."));
        } catch (Exception e) {
            logger.error("An unexpected error occurred", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unexpected error: " + e.getMessage()));
        }
    }

    /**
     * Updates an existing tip with or without a new image.
     *
     * @param tipJson the JSON representation of the TipDTO to update
     * @param image   the new image associated with the tip (optional)
     * @return a success message or an error message
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/update", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, String>> updateTip(
            @RequestParam("tip") String tipJson,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        try {
            // Désérialisation du JSON en UpdateTipDTO
            ObjectMapper objectMapper = new ObjectMapper();
            TipDTO updateTipDTO = objectMapper.readValue(tipJson, TipDTO.class);

            // Appel au service pour mettre à jour le Tip
            tipService.updateTip(updateTipDTO, image);

            // Retourner une réponse JSON
            return ResponseEntity.ok(Map.of("message", "Tip updated successfully!"));
        } catch (JsonProcessingException e) {
            logger.error("Erreur lors de la désérialisation du JSON", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid JSON for 'tip'"));
        } catch (Exception e) {
            logger.error("Erreur inattendue lors de la mise à jour", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error updating tip"));
        }
    }

    /**
     * Deletes a tip by its ID.
     *
     * @param id the ID of the tip to delete
     * @return a success message or an error message
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteTip(@PathVariable Long id) {
        try {
            tipService.deleteTip(id);
            return ResponseEntity.ok(Map.of("message", "Tip deleted successfully!"));
        } catch (Exception e) {
            // En cas d'erreur, renvoyer un message d'erreur au client
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error deleting the tip"));
        }
    }
}