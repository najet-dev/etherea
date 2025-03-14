package com.etherea.services;

import com.etherea.models.Newsletter;
import com.etherea.dtos.NewsletterDTO;
import com.etherea.models.User;
import com.etherea.repositories.NewsletterRepository;
import com.etherea.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NewsletterService {
    @Autowired
    private NewsletterRepository newsletterRepository;
    @Autowired
    private UserRepository userRepository;

    // Méthode pour s'inscrire à la newsletter avec un DTO
    public String subscribe(NewsletterDTO newsletterDTO) {
        String email = newsletterDTO.getEmail();
        Optional<User> userOptional = userRepository.findByUsername(email);

        if (userOptional.isEmpty()) {
            return "Utilisateur non trouvé.";
        }

        User user = userOptional.get();

        // Vérifier si l'utilisateur est déjà inscrit
        if (newsletterRepository.findByUser(user).isPresent()) {
            return "Cet utilisateur est déjà inscrit.";
        }

        // Inscrire l'utilisateur
        Newsletter newsletter = new Newsletter(user);
        newsletterRepository.save(newsletter);

        return "Inscription réussie!";
    }

    // Méthode pour obtenir l'inscription en tant que DTO
    public NewsletterDTO getNewsletter(Long id) {
        Optional<Newsletter> newsletterOptional = newsletterRepository.findById(id);
        return newsletterOptional.map(NewsletterDTO::fromEntity).orElse(null);
    }
}
