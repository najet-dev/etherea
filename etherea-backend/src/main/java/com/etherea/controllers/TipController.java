package com.etherea.controllers;

import com.etherea.dtos.TipDTO;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/tips")
@CrossOrigin(origins = "*")
public class TipController {
    @Autowired
    private TipService tipService;
    private static final Logger logger = LoggerFactory.getLogger(TipController.class);

    /**
     * Récupérer tous les conseils avec pagination
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
     * Récupérer un conseil par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<TipDTO> getTipById(@PathVariable Long id) {
        TipDTO tipDTO = tipService.getTipById(id);
        return ResponseEntity.ok(tipDTO);
    }

    /**
     * Ajouter un nouveau conseil (avec upload d'image)
     */
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
     * Mettre à jour un conseil
     */
    @PutMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<String> updateTip(
            @RequestPart("tip") TipDTO tipDTO,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        tipService.updateTip(tipDTO, image);
        return ResponseEntity.ok("Tip updated successfully!");
    }

    /**
     * Supprimer un conseil
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTip(@PathVariable Long id) {
        tipService.deleteTip(id);
        return ResponseEntity.ok("Tip deleted successfully!");
    }
}
