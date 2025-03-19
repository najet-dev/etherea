package com.etherea.controllers;

import com.etherea.services.EmailService;
import com.etherea.services.NewsletterService;
import com.etherea.dtos.NewsletterDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/newsletter")
@CrossOrigin(origins = "*")
public class NewsletterController {
    private final NewsletterService newsletterService;
    private final EmailService emailService;

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
     * @param request Objet contenant le sujet et le contenu de la newsletter.
     * @return Réponse indiquant le succès ou l'échec de l'envoi.
     */
    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendNewsletter(@RequestBody Map<String, String> request) {
        String subject = request.get("subject");
        String content = request.get("content");

        Map<String, String> response = new HashMap<>();

        if (subject == null || subject.isEmpty() || content == null || content.isEmpty()) {
            response.put("message", "Le sujet et le contenu ne peuvent pas être vides.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            newsletterService.sendNewsletterToAllSubscribers(subject, content);
            response.put("message", "Newsletter envoyée avec succès !");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("message", "Erreur lors de l'envoi de la newsletter : " + e.getMessage());
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
