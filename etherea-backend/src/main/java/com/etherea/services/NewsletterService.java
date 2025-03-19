package com.etherea.services;

import com.etherea.models.Newsletter;
import com.etherea.dtos.NewsletterDTO;
import com.etherea.repositories.NewsletterRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class NewsletterService {

    @Value("${app.url}")
    private String baseUrl;  // Injecte l'URL du backend

    private final NewsletterRepository newsletterRepository;
    private final EmailService emailService;

    public NewsletterService(NewsletterRepository newsletterRepository, EmailService emailService) {
        this.newsletterRepository = newsletterRepository;
        this.emailService = emailService;
    }
    /**
     * How to subscribe to the newsletter with an email without a user account.
     *
     * @param newsletterDTO DTO containing the email.
     * @return Message indicating subscription status.
     */
    public String subscribe(NewsletterDTO newsletterDTO) {
        String email = newsletterDTO.getEmail();

        // Check if the email is already registered
        if (newsletterRepository.findByEmail(email).isPresent()) {
            return "Cet email est déjà abonné.";
        }

        // Enregistrer l'email indépendamment de l'existence d'un compte utilisateur
        newsletterRepository.save(new Newsletter(email));
        return "Inscription réussie !";
    }

    /**
     * Retrieves all subscribers and sends the newsletter.
     *
     * @param subject Newsletter subject.
     * @param content HTML content of the newsletter.
     */
    public void sendNewsletterToAllSubscribers(String subject, String content) {
        List<Newsletter> newsletters = newsletterRepository.findAll();
        List<String> emailAddresses = newsletters.stream()
                .map(Newsletter::getEmail)
                .toList();

        for (String email : emailAddresses) {
            // Add the unique unsubscribe link for each user
            String unsubscribeLink = baseUrl + "/newsletter/unsubscribe?email=" + email;
            String emailContent = content + "<br><br><a href=\"" + unsubscribeLink + "\">Se désinscrire</a>";

            emailService.sendNewsletter(Collections.singletonList(email), subject, emailContent);
        }
    }

    /**
     * Unsubscribe a user from the newsletter via email.
     *
     * @param email The email to unsubscribe.
     * @return Message indicating unsubscription status.
     */
    public String unsubscribe(String email) {
        Optional<Newsletter> newsletterOptional = newsletterRepository.findByEmail(email);

        if (newsletterOptional.isEmpty()) {
            return "Cet email n'est pas abonné.";
        }

        newsletterRepository.delete(newsletterOptional.get());
        return "Désinscription réussie.";
    }
}
