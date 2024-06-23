package com.example.taxidriverrestapplication.services;

import com.example.taxidriverrestapplication.emailmessage.EmailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailCompanySenderService {

    private final KafkaOperations<String, EmailMessage> kafkaOperations;

    private static final String EMAIL_CREATED_SUBJECT = "Company Created";
    private static final String EMAIL_UPDATED_SUBJECT = "Company Updated";
    private static final String EMAIL_DELETED_SUBJECT = "Company Deleted";

    private static final String EMAIL_CREATED_CONTENT = "A new company has been created with the name: ";
    private static final String EMAIL_UPDATED_CONTENT = "The company has been updated from name: ";
    private static final String EMAIL_DELETED_CONTENT = "The company with name: ";

    private static final String EMAIL_UPDATED_CONTENT_TO = " to name: ";
    private static final String EMAIL_DELETED_CONTENT_SUFFIX = " has been deleted.";

    private static final String TOPIC = "emails";
    private static final String KEY = "changes";

    public void createCompanyEmail(String companyName, List<String> receivers) {
        EmailMessage emailMessage = EmailMessage.builder()
                .subject(EMAIL_CREATED_SUBJECT)
                .content(EMAIL_CREATED_CONTENT + companyName)
                .receivers(receivers)
                .lastAttempt(Instant.now())
                .build();
        try {
            kafkaOperations.send(TOPIC, KEY, emailMessage);
            log.info("Sent email message: {}", emailMessage);
        } catch (Exception e) {
            log.warn("Failed to send email message: {}", emailMessage, e);
        }
    }

    public void updateCompanyEmail(String oldName, String newName, List<String> receivers) {
        EmailMessage emailMessage = EmailMessage.builder()
                .subject(EMAIL_UPDATED_SUBJECT)
                .content(EMAIL_UPDATED_CONTENT + oldName + EMAIL_UPDATED_CONTENT_TO + newName)
                .receivers(receivers)
                .lastAttempt(Instant.now())
                .build();
        try {
            kafkaOperations.send(TOPIC, KEY, emailMessage);
            log.info("Sent email message: {}", emailMessage);
        } catch (Exception e) {
            log.warn("Failed to send email message: {}", emailMessage, e);
        }
    }

    public void deleteCompanyEmail(String companyName, List<String> receivers) {
        EmailMessage emailMessage = EmailMessage.builder()
                .subject(EMAIL_DELETED_SUBJECT)
                .content(EMAIL_DELETED_CONTENT + companyName + EMAIL_DELETED_CONTENT_SUFFIX)
                .receivers(receivers)
                .lastAttempt(Instant.now())
                .build();
        try {
            kafkaOperations.send(TOPIC, KEY, emailMessage);
            log.info("Sent email message: {}", emailMessage);
        } catch (Exception e) {
            log.warn("Failed to send email message: {}", emailMessage, e);
        }
    }
}
