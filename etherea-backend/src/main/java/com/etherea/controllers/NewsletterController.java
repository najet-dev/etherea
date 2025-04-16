package com.etherea.controllers;

import com.etherea.dtos.NewsletterSendDTO;
import com.etherea.services.EmailService;
import com.etherea.services.NewsletterService;
import com.etherea.dtos.NewsletterDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/newsletter")
@CrossOrigin(origins = "*")
public class NewsletterController {
    private final NewsletterService newsletterService;
    public final EmailService emailService;
    public NewsletterController(NewsletterService newsletterService, EmailService emailService) {
        this.newsletterService = newsletterService;
        this.emailService = emailService;
    }

    /**
     * Endpoint pour s'abonner à la newsletter.
     *
     * @param newsletterDTO DTO contenant l'email.
     * @return Réponse avec message de succès ou d'échec.
     */
    @PostMapping("/subscribe")
    public ResponseEntity<Map<String, String>> subscribe(@RequestBody NewsletterDTO newsletterDTO) {
        String message = newsletterService.subscribe(newsletterDTO);
        Map<String, String> response = new HashMap<>();

        if ("Cet email est déjà abonné.".equals(message)) {
            response.put("message", message);
            return ResponseEntity.status(409).body(response);
        }

        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint pour envoyer la newsletter à tous les abonnés.
     *
     * @param newsletterSendDTO Objet contenant le sujet et le contenu de la newsletter.
     * @return Réponse indiquant le succès ou l'échec de l'envoi.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendNewsletter(@RequestBody NewsletterSendDTO newsletterSendDTO) {
        Map<String, String> response = new HashMap<>();

        if (newsletterSendDTO.getSubject() == null || newsletterSendDTO.getSubject().isEmpty() ||
                newsletterSendDTO.getContent() == null || newsletterSendDTO.getContent().isEmpty()) {
            response.put("message", "Subject and content cannot be empty");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            newsletterService.sendNewsletterToAllSubscribers(newsletterSendDTO);
            response.put("message", "Newsletter successfully sent !");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("message", "Error sending newsletter : " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Endpoint pour se désinscrire de la newsletter via un email.
     *
     * @param email L'email de l'utilisateur à désinscrire.
     * @return Réponse indiquant le succès ou l'échec de la désinscription.
     */
    @GetMapping("/unsubscribe")
    public ResponseEntity<Map<String, String>> unsubscribe(@RequestParam String email) {
        Map<String, String> response = new HashMap<>();

        String message = newsletterService.unsubscribe(email);
        response.put("message", message);

        if ("Cet email n'est pas abonné.".equals(message)) {
            return ResponseEntity.status(404).body(response);
        }

        return ResponseEntity.ok(response);
    }
}
