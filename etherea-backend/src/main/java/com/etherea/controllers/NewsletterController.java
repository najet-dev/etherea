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
    public final EmailService emailService;

    public NewsletterController(NewsletterService newsletterService, EmailService emailService) {
        this.newsletterService = newsletterService;
        this.emailService = emailService;
    }

    /**
     * Endpoint to subscribe to the newsletter using a DTO.
     *
     * @param newsletterDTO DTO containing the user's email.
     * @return Response entity with a success or error message.
     */
    @PostMapping("/subscribe")
    public ResponseEntity<Map<String, String>> subscribe(@RequestBody NewsletterDTO newsletterDTO) {
        String message = newsletterService.subscribe(newsletterDTO);
        Map<String, String> response = new HashMap<>();

        if ("This user is already subscribed.".equals(message)) {
            response.put("message", message);
            return ResponseEntity.status(409).body(response);
        }

        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to get newsletter information by ID.
     *
     * @param id The newsletter subscription ID.
     * @return The newsletter DTO.
     */
    @GetMapping("/{id}")
    public NewsletterDTO getNewsletter(@PathVariable Long id) {
        return newsletterService.getNewsletter(id);
    }

    /**
     * Endpoint to send the newsletter to all subscribers.
     *
     * @param request Object containing the subject and content of the newsletter.
     * @return Response entity with a success or error message.
     */
    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendNewsletter(@RequestBody Map<String, String> request) {
        String subject = request.get("subject");
        String content = request.get("content");

        Map<String, String> response = new HashMap<>();

        if (subject == null || subject.isEmpty() || content == null || content.isEmpty()) {
            response.put("message", "Subject and content cannot be empty.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            newsletterService.sendNewsletterToAllSubscribers(subject, content);
            response.put("message", "Newsletter successfully sent!");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("message", "Error sending newsletter: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Endpoint to unsubscribe a user from the newsletter via their email.
     *
     * @param email The email of the user to unsubscribe.
     * @return Response entity with a confirmation message.
     */
    @GetMapping("/unsubscribe")
    public ResponseEntity<Map<String, String>> unsubscribe(@RequestParam String email) {
        Map<String, String> response = new HashMap<>();

        String message = newsletterService.unsubscribe(email);
        response.put("message", message);

        if ("User not found in the newsletter list.".equals(message)) {
            return ResponseEntity.status(404).body(response);
        }

        return ResponseEntity.ok(response);
    }
}
