package com.example.mailsenderservice.service;


import com.example.mailsenderservice.util.EmailSender;
import com.example.mailsenderservice.message.EmailMessage;
import com.example.mailsenderservice.repository.EmailMessageRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Service class for handling email-related operations.
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MailService {

    private final EmailSender mailSender;

    private final EmailMessageRepository emailMessageRepository;

    /**
     * Processes a received email message.
     * @param message the received email message
     */
    public void processEmailReceived(EmailMessage message) {
        EmailMessage savedMessage = emailMessageRepository.save(message);
        for (String s: message.getReceivers()){
            try{
                mailSender.send(s, message.getSubject(), message.getContent());
                savedMessage.setSent(true);
            } catch (MailException e){
                savedMessage.setErrorMessage(e.getClass().getName() + ": " + e.getMessage());
                message.setLastAttempt(LocalDateTime.now().toInstant(ZoneOffset.UTC));
            }
            emailMessageRepository.save(savedMessage);
        }
    }
}
