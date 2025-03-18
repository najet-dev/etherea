package com.etherea.services;

import com.etherea.dtos.ForgotPasswordRequestDTO;
import com.etherea.dtos.ResetPasswordRequestDTO;
import com.etherea.exception.UserNotFoundException;
import com.etherea.models.ResetToken;
import com.etherea.models.User;
import com.etherea.repositories.ResetTokenRepository;
import com.etherea.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PasswordResetService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ResetTokenRepository resetTokenRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    public void sendPasswordResetLink(ForgotPasswordRequestDTO request) {
        User user = userRepository.findByUsername(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("Aucun utilisateur trouvé avec cet email"));

        // Supprimer les anciens tokens du même utilisateur
        resetTokenRepository.deleteByUser(user);

        // Générer un nouveau token
        ResetToken resetToken = new ResetToken(user);
        resetTokenRepository.save(resetToken);

        String resetLink = "http://localhost:4200/reset-password?token=" + resetToken.getToken();

        String emailContent = "<html><body style=\"font-family: Arial, sans-serif; color: #333;\">"
                + "<div style=\"max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;\">"
                + "<h2 style=\"color: #2c3e50;\">Réinitialisation du mot de passe</h2>"
                + "<p>Bonjour <strong>" + user.getFirstName() + "</strong>,</p>"
                + "<p>Vous avez oublié votre mot de passe ?</p>"
                + "<p>Aucun problème ! Pour le réinitialiser, cliquez sur le bouton ci-dessous :</p>"
                + "<p style=\"text-align: center;\">"
                + "<a href=\"" + resetLink + "\" style=\"background-color: #83d286; color: black; padding: 10px 20px; text-decoration: none; border-radius: 5px; display: inline-block;\">Réinitialiser le mot de passe</a>"
                + "</p>"
                + "<p>Si vous ne souhaitez pas réinitialiser votre mot de passe, vous pouvez ignorer ce message et votre mot de passe restera le même.</p>"
                + "<p>Ce lien expirera dans <strong>15 minutes</strong>.</p>"
                + "<hr style=\"border: 0; border-top: 1px solid #ddd;\">"
                + "<p style=\"text-align: center;\">L'équipe <strong>Etherea</strong></p>"
                + "</div></body></html>";

        emailService.sendEmail(
                List.of(user.getUsername()),
                "Réinitialisation du mot de passe",
                emailContent
        );
    }
    public void resetPassword(ResetPasswordRequestDTO request) {
        ResetToken resetToken = resetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Token invalide ou expiré"));

        if (resetToken.isExpired() || resetToken.isUsed()) {
            throw new IllegalArgumentException("Le token a expiré ou a déjà été utilisé.");
        }

        User user = resetToken.getUser();

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Les mots de passe ne correspondent pas.");
        }

        if (!isValidPassword(request.getNewPassword())) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 8 caractères, "
                    + "une majuscule, une minuscule, un chiffre et un caractère spécial.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Marquer le token comme utilisé et l'invalider
        resetToken.markAsUsed();
        resetTokenRepository.save(resetToken);
    }

    /**
     * Vérifie si un mot de passe respecte les critères de sécurité :
     * - Minimum 8 caractères
     * - Au moins une majuscule
     * - Au moins une minuscule
     * - Au moins un chiffre
     * - Au moins un caractère spécial (@#$%^&+=!)
     */
    private boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";
        return password.matches(passwordRegex);
    }

}
