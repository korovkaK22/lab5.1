package com.example.mailsenderservice.configurations;


import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Objects;
import java.util.Properties;

/**
 * Configuration class for setting up the JavaMailSender bean.
 */
@Configuration
public class JavaMailSenderConfiguration {

    @Value("${mail.debug}")
    private String debug;

    /**
     * Creates and configures a JavaMailSender bean.
     *
     * @return a configured JavaMailSender instance
     */
    @Bean
    public JavaMailSender getMailSender() {
        Dotenv dotenv = Dotenv.load();

        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        javaMailSender.setHost(dotenv.get("SPRING_MAIL_HOST"));
        javaMailSender.setPort(Integer.parseInt(Objects.requireNonNull(dotenv.get("SPRING_MAIL_PORT"))));
        javaMailSender.setUsername(dotenv.get("SPRING_MAIL_USERNAME"));
        javaMailSender.setPassword(dotenv.get("SPRING_MAIL_PASSWORD"));
        Properties properties = javaMailSender.getJavaMailProperties();
        properties.setProperty("mail.transport.protocol", dotenv.get("SPRING_MAIL_PROTOCOL"));
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.ssl.enable", "true");
        properties.setProperty("mail.debug", debug);
        return javaMailSender;
    }
}
