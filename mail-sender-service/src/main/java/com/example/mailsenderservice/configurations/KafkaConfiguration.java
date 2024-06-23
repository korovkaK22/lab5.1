package com.example.mailsenderservice.configurations;

import com.example.mailsenderservice.message.EmailMessage;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

/**
 * Configuration class for setting up Kafka components.
 */
@Configuration
public class KafkaConfiguration {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value("${kafka.topic.emailTopic}")
    private String emailTopic;

    /**
     * Creates a KafkaAdmin bean with the necessary configurations.
     *
     * @return a configured KafkaAdmin instance
     */
    @Bean
    public KafkaAdmin kafkaAdmin() {
        JsonDeserializer<EmailMessage> jsonDeserializer = new JsonDeserializer<>(EmailMessage.class);
        jsonDeserializer.addTrustedPackages("*");
        jsonDeserializer.setRemoveTypeHeaders(false);
        jsonDeserializer.setUseTypeMapperForKey(true);
        Map<String, Object> configs = Map.of(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, jsonDeserializer,
                JsonDeserializer.TRUSTED_PACKAGES, "*");
        return new KafkaAdmin(configs);
    }

    /**
     * Creates a NewTopic bean for the email topic.
     *
     * @return a NewTopic instance
     */
    @Bean
    public NewTopic emailReceivedTopic() {
        return new NewTopic(emailTopic, 2, (short) 1);
    }
}
