package com.etherea.services;

import com.etherea.exception.EmailSendingException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for sending emails, including order confirmation emails and newsletters.
 */
@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    @Value("${spring.mail.username}")
    private String fromEmail;
    private final JavaMailSender mailSender;
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends an email to one or more recipients.
     * This generic method can be used to send order confirmations,
     * newsletters, or any other type of email.
     *
     * @param recipients List of recipients' email addresses.
     * @param subject    Subject of the email.
     * @param content    HTML content of the email.
     */
    public void sendEmail(List<String> recipients, String subject, String content) {
        if (recipients == null || recipients.isEmpty()) {
            throw new IllegalArgumentException("The recipient list cannot be empty.");
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(recipients.toArray(new String[0]));
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
            logger.info("Email successfully sent to {}", recipients);
        } catch (MessagingException e) {
            logger.error("Error while sending email to {}: {}", recipients, e.getMessage());
            throw new EmailSendingException("Unable to send email. Please try again later.", e);
        }
    }

    /**
     * Sends an order confirmation email to a recipient.
     *
     * @param to      Recipient's email address.
     * @param subject Subject of the email.
     * @param content HTML content of the email.
     */
    public void sendOrderConfirmation(String to, String subject, String content) {
        // Sends the order confirmation email
        sendEmail(List.of(to), subject, content); // Uses the generic method
    }

    /**
     * Sends a newsletter email to one or more subscribers.
     *
     * @param recipients List of subscribers to receive the newsletter.
     * @param subject    Subject of the newsletter.
     * @param content    HTML content of the newsletter.
     */
    public void sendNewsletter(List<String> recipients, String subject, String content) {
        // Sends the newsletter email
        sendEmail(recipients, subject, content); // Uses the generic method
    }
}
