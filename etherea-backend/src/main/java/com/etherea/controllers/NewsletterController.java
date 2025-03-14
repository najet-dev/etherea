package com.etherea.controllers;

import com.etherea.services.NewsletterService;
import com.etherea.dtos.NewsletterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/newsletter")
@CrossOrigin(origins = "*")
public class NewsletterController {

    @Autowired
    private NewsletterService newsletterService;

    // s'inscription à la newsletter avec un DTO
    @PostMapping("/subscribe")
    public ResponseEntity<Map<String, String>> subscribe(@RequestBody NewsletterDTO newsletterDTO) {
        String message = newsletterService.subscribe(newsletterDTO);
        Map<String, String> response = new HashMap<>();

        if ("Cet utilisateur est déjà inscrit.".equals(message)) {
            response.put("message", message);
            return ResponseEntity.status(409).body(response); // Retourne un statut HTTP 409
        }

        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    //obtention des informations de la newsletter par ID
    @GetMapping("/{id}")
    public NewsletterDTO getNewsletter(@PathVariable Long id) {
        return newsletterService.getNewsletter(id);
    }
}
