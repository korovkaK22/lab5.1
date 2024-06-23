package com.example.mailsenderservice;

import com.example.mailsenderservice.message.EmailMessage;
import com.example.mailsenderservice.repository.EmailMessageRepository;
import com.example.mailsenderservice.service.MailService;
import com.example.mailsenderservice.util.EmailSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MailServiceTest {

    @Mock
    private EmailMessageRepository emailMessageRepository;

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private MailService mailService;

    private EmailMessage emailMessage;

    @BeforeEach
    void setUp() {
        emailMessage = EmailMessage.builder()
                .id("1")
                .subject("Test Subject")
                .content("Test Content")
                .receivers(List.of("test@example.com"))
                .sent(false)
                .attempts(0)
                .lastAttempt(Instant.now())
                .build();
    }

    @Test
    void testProcessEmailReceived() {
        when(emailMessageRepository.save(any(EmailMessage.class))).thenReturn(emailMessage);
        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        mailService.processEmailReceived(emailMessage);

        verify(emailMessageRepository, times(2)).save(any(EmailMessage.class));
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testProcessEmailReceivedWithException() {
        when(emailMessageRepository.save(any(EmailMessage.class))).thenReturn(emailMessage);
        doThrow(new MailException("Failed to send email") {}).when(javaMailSender).send(any(SimpleMailMessage.class));

        mailService.processEmailReceived(emailMessage);

        verify(emailMessageRepository, times(2)).save(any(EmailMessage.class));
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}
