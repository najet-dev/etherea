package com.etherea.controllers;

import com.etherea.dtos.VolumeDTO;
import com.etherea.exception.ProductNotFoundException;
import com.etherea.services.VolumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/volumes")
@CrossOrigin
public class VolumeController {
    @Autowired
    private VolumeService volumeService;

    // Méthode pour récupérer tous les volumes
    @GetMapping
    public ResponseEntity<List<VolumeDTO>> getAllVolumes() {
        List<VolumeDTO> volumes = volumeService.getAllVolumes();

        if (volumes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.ok(volumes);
    }
    @PostMapping("/products/{productName}")
    public ResponseEntity<VolumeDTO> addVolume(@PathVariable String productName, @RequestBody VolumeDTO volumeDTO) {
        try {
            VolumeDTO createdVolume = volumeService.addVolume(productName, volumeDTO);
            return new ResponseEntity<>(createdVolume, HttpStatus.CREATED);
        } catch (ProductNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Méthode pour mettre à jour un volume
    @PutMapping("/{volumeId}")
    public ResponseEntity<VolumeDTO> updateVolume(@PathVariable Long volumeId, @RequestBody VolumeDTO volumeDTO) {
        try {
            VolumeDTO updatedVolume = volumeService.updateVolume(volumeId, volumeDTO);
            return new ResponseEntity<>(updatedVolume, HttpStatus.OK);
        } catch (ProductNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Méthode pour supprimer un volume
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteVolume(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();

        if (volumeService.deleteVolume(id)) {
            response.put("message", "Volume successfully deleted");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Volume not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
