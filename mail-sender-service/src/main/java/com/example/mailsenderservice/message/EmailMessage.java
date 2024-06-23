package com.example.mailsenderservice.message;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;
import java.util.List;

/**
 * Entity class representing an email message.
 */
@Getter
@Setter
@Builder
@Jacksonized
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(indexName = "email_message")
public class EmailMessage {
    @Id
    String id;
    String subject;
    String content;
    List<String> receivers;
    String errorMessage;
    boolean sent;
    int attempts;
    @Field(type = FieldType.Date, format = {}, pattern = "epoch_millis")
    Instant lastAttempt;
}
