package com.example.mailsenderservice.listener;

import com.example.mailsenderservice.message.EmailMessage;
import com.example.mailsenderservice.service.MailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Listener class for handling Kafka messages related to emails.
 */
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailListener {

    private final ObjectMapper objectMapper;
    private final MailService mailService;

    /**
     * Method to process received email messages from Kafka.
     *
     * @param message the message received from Kafka
     * @throws Exception if an error occurs while processing the message
     */
    @SneakyThrows
    @KafkaListener(topics = "${kafka.topic.emailTopic}")
    public void emailReceived(String message) {
        EmailMessage emailMessage = objectMapper.readValue(message, EmailMessage.class);
        mailService.processEmailReceived(emailMessage);
    }
}
