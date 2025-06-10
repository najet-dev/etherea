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
     * Subscribes a user to the newsletter.
     *
     * @param newsletterDTO the email address to subscribe
     * @return HTTP 200 with a success message if subscribed successfully,
     *         or HTTP 409 if the email is already subscribed
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
     * Sends a newsletter email to all subscribers.
     * Only accessible to users with the ADMIN role.
     *
     * @param newsletterSendDTO the subject and content of the newsletter to send
     * @return HTTP 200 with a success message if sent successfully,
     *         HTTP 400 if subject or content is missing,
     *         HTTP 500 if an error occurs during sending
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
     * Unsubscribes a user from the newsletter.
     *
     * @param email the email address to unsubscribe
     * @return HTTP 200 with a success message if unsubscribed,
     *         or HTTP 404 if the email is not found among subscribers
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