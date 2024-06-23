package com.example.mailsenderservice.util;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Utility class for sending emails.
 */
@Component
@RequiredArgsConstructor
public class EmailSender {

    private final JavaMailSender javaMailSender;

    /**
     * Sends an email with the specified details.
     *
     * @param emailTo the recipient's email address
     * @param subject the subject of the email
     * @param message the content of the email
     */
    public void send(String emailTo, String subject, String message) {
        Dotenv dotenv = Dotenv.load();
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(dotenv.get("SPRING_MAIL_USERNAME"));
        mailMessage.setTo(emailTo);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        javaMailSender.send(mailMessage);
    }
}
