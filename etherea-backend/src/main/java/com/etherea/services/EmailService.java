package com.etherea.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Service for sending emails, specifically order confirmation emails.
 */
@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    /**
     * Sends an order confirmation email.
     *
     * @param to      The recipient's email address.
     * @param subject The subject of the email.
     * @param content The HTML content of the email.
     */
    public void sendOrderConfirmation(String to, String subject, String content) {
        try {
            // Create a MIME message
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Set email attributes
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true); // 'true' enables HTML content

            // Send the email
            mailSender.send(message);

            System.out.println("Email sent to: " + to);
        } catch (MessagingException e) {
            System.out.println("Error sending e-mail: " + e.getMessage());
        }
    }
}
