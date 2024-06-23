package com.example.mailsenderservice.service;


import com.example.mailsenderservice.util.EmailSender;
import com.example.mailsenderservice.message.EmailMessage;
import com.example.mailsenderservice.repository.EmailMessageRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.MailException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Service class for scheduling tasks to handle failed email messages.
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FailedMailsSheduler {

    private final  EmailSender mailSender;
    private final EmailMessageRepository emailMessageRepository;

    /**
     * Schedules a task to retry sending failed email messages every 5 minutes.
     */
    @Scheduled(cron = "0 */5 * * * *")
    public void sendErrorMessages() {
        List<EmailMessage> unsentMessages = emailMessageRepository.findBySentFalse();

        for (EmailMessage message : unsentMessages) {
            for (String s : message.getReceivers()) {
                try {
                    mailSender.send(s, message.getSubject(), message.getContent());
                    message.setSent(true);
                } catch (MailException e) {
                    message.setErrorMessage(e.getClass().getName() + ": " + e.getMessage());
                    message.setAttempts(message.getAttempts() + 1);
                    message.setLastAttempt(Instant.now());
                }
                emailMessageRepository.save(message);
            }
        }

    }
}
