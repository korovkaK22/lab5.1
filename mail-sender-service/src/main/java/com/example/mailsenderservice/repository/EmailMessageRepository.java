package com.example.mailsenderservice.repository;

import com.example.mailsenderservice.message.EmailMessage;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * Repository interface for managing EmailMessage entities in Elasticsearch.
 */
public interface EmailMessageRepository extends ElasticsearchRepository<EmailMessage, String> {

    /**
     * Finds all email messages that have not been sent.
     *
     * @return a list of unsent email messages
     */
    List<EmailMessage> findBySentFalse();
}
