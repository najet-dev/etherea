package com.etherea.controllers;

import com.etherea.dtos.VolumeDTO;
import com.etherea.exception.ProductNotFoundException;
import com.etherea.services.VolumeService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/volumes")
@CrossOrigin
public class VolumeController {
    private final VolumeService volumeService;
    public VolumeController(VolumeService volumeService) {
        this.volumeService = volumeService;
    }

    /**
     * Retrieves a paginated list of all available volumes.
     *
     * @param page the page number to retrieve (default is 0)
     * @param size the number of volumes per page (default is 10)
     * @return a paginated list of VolumeDTO objects, or HTTP 204 if no volumes are found
     */
    @GetMapping
    public ResponseEntity<Page<VolumeDTO>> getAllVolumes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<VolumeDTO> volumes = volumeService.getAllVolumes(page, size);

        if (volumes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.ok(volumes);
    }

    /**
     * Adds a new volume to the system. Only accessible by admins.
     *
     * @param volumeDTO the volume data to be added
     * @return the created VolumeDTO with HTTP 201 status if successful,
     *         HTTP 404 if related product not found,
     *         HTTP 400 if invalid input,
     *         HTTP 500 if an internal error occurs
     */
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VolumeDTO> addVolume(@RequestBody VolumeDTO volumeDTO) {
        try {
            VolumeDTO createdVolume = volumeService.addVolume(volumeDTO);
            return new ResponseEntity<>(createdVolume, HttpStatus.CREATED);
        } catch (ProductNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates an existing volume by its ID. Only accessible by admins.
     *
     * @param volumeId the ID of the volume to update
     * @param volumeDTO the updated volume data
     * @return the updated VolumeDTO with HTTP 200 status if successful,
     *         HTTP 404 if the volume is not found,
     *         HTTP 400 for invalid input,
     *         HTTP 500 if an internal error occurs
     */
    @PutMapping("/{volumeId}")
    @PreAuthorize("hasRole('ADMIN')")
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

    /**
     * Deletes a volume by its ID. Only accessible by admins.
     *
     * @param id the ID of the volume to delete
     * @return a success message if the volume is deleted,
     *         or a not found message if the volume does not exist
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
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