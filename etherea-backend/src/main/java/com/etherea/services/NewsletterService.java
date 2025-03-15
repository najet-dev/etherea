package com.etherea.services;

import com.etherea.models.Newsletter;
import com.etherea.dtos.NewsletterDTO;
import com.etherea.models.User;
import com.etherea.repositories.NewsletterRepository;
import com.etherea.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class NewsletterService {
    @Value("${app.url}")
    private String baseUrl;  // Injects the backend URL
    private final NewsletterRepository newsletterRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;

    public NewsletterService(NewsletterRepository newsletterRepository, EmailService emailService, UserRepository userRepository) {
        this.newsletterRepository = newsletterRepository;
        this.emailService = emailService;
        this.userRepository = userRepository;
    }

    /**
     * Method to subscribe to the newsletter using a DTO.
     *
     * @param newsletterDTO DTO containing the user's email.
     * @return A message indicating the subscription status.
     */
    public String subscribe(NewsletterDTO newsletterDTO) {
        String email = newsletterDTO.getEmail();
        User user = userRepository.findByUsername(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (newsletterRepository.findByUser(user).isPresent()) {
            return "This user is already subscribed.";
        }

        newsletterRepository.save(new Newsletter(user));
        return "Subscription successful!";
    }

    /**
     * Retrieves the subscription as a DTO.
     *
     * @param id The ID of the newsletter subscription.
     * @return The newsletter DTO or null if not found.
     */
    public NewsletterDTO getNewsletter(Long id) {
        Optional<Newsletter> newsletterOptional = newsletterRepository.findById(id);
        return newsletterOptional.map(NewsletterDTO::fromEntity).orElse(null);
    }

    /**
     * Retrieves all newsletter subscribers and sends the email.
     *
     * @param subject Subject of the newsletter.
     * @param content HTML content of the newsletter.
     */
    public void sendNewsletterToAllSubscribers(String subject, String content) {
        List<Newsletter> newsletters = newsletterRepository.findAll();
        List<String> emailAddresses = newsletters.stream()
                .map(newsletter -> newsletter.getUser().getUsername()) // Assuming username is the email
                .toList();

        for (String email : emailAddresses) {
            // Add the unique unsubscribe link for each user
            String unsubscribeLink = baseUrl + "/newsletter/unsubscribe?email=" + email;
            String emailContent = content + "<br><br><a href=\"" + unsubscribeLink + "\">Unsubscribe</a>";

            emailService.sendNewsletter(Collections.singletonList(email), subject, emailContent);
        }
    }

    /**
     * Unsubscribes a user from the newsletter.
     *
     * @param email The email of the user to unsubscribe.
     * @return A message indicating the unsubscription status.
     */
    public String unsubscribe(String email) {
        Optional<User> userOptional = userRepository.findByUsername(email);
        if (userOptional.isEmpty()) {
            return "User not found.";
        }

        User user = userOptional.get();
        Optional<Newsletter> newsletterOptional = newsletterRepository.findByUser(user);

        if (newsletterOptional.isEmpty()) {
            return "User not found in the newsletter list.";
        }

        newsletterRepository.delete(newsletterOptional.get());
        return "Unsubscription successful.";
    }
}
