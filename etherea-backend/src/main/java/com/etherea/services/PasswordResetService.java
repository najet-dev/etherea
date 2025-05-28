package com.etherea.services;

import com.etherea.dtos.ForgotPasswordRequestDTO;
import com.etherea.dtos.ResetPasswordRequestDTO;
import com.etherea.exception.UserNotFoundException;
import com.etherea.models.ResetToken;
import com.etherea.models.User;
import com.etherea.repositories.ResetTokenRepository;
import com.etherea.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PasswordResetService {
    private final UserRepository userRepository;
    private final ResetTokenRepository resetTokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    public PasswordResetService(UserRepository userRepository, ResetTokenRepository resetTokenRepository, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.resetTokenRepository = resetTokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }
    /**
     * Sends a password reset link to the user’s email address.
     * If a valid reset token already exists, it is refreshed.
     *
     * @param request the request DTO containing the user email
     * @throws UserNotFoundException if the email does not match any registered user
     */
    public void sendPasswordResetLink(ForgotPasswordRequestDTO request) {
        User user = userRepository.findByUsername(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("No users found with this email"));

        Optional<ResetToken> existingToken = resetTokenRepository.findByUser(user);

        ResetToken resetToken;
        if (existingToken.isPresent()) {
            resetToken = existingToken.get();
            if (resetToken.isExpired() || resetToken.isUsed()) {
                resetToken.refreshToken(); // Refresh token if expired or used
            }
        } else {
            resetToken = new ResetToken(user);
        }

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
    /**
     * Resets the user's password based on the provided token and new password.
     * Validates token status, password match, and password strength before updating.
     *
     * @param request the reset request containing token, new password, and confirmation
     * @throws IllegalArgumentException if the token is invalid, expired, used, or if the password is weak or mismatched
     */
    public void resetPassword(ResetPasswordRequestDTO request) {
        ResetToken resetToken = resetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token"));

        if (resetToken.isExpired() || resetToken.isUsed()) {
            throw new IllegalArgumentException("The token has expired or has already been used");
        }

        User user = resetToken.getUser();

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords don't match");
        }

        if (!isValidPassword(request.getNewPassword())) {
            throw new IllegalArgumentException("The password must contain at least 8 characters, ”\n" + " + “one uppercase, one lowercase, one number and one special character");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Mark token as used
        resetToken.markAsUsed();
        resetTokenRepository.save(resetToken);
    }
    /**
     * Validates the password against security requirements.
     * Must contain at least 8 characters, one uppercase letter, one lowercase letter, one digit, and one special character.
     *
     * @param password the password to validate
     * @return {@code true} if the password is valid, {@code false} otherwise
     */
    private boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";
        return password.matches(passwordRegex);
    }
}
