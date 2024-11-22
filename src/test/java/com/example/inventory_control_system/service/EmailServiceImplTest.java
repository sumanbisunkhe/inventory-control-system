package com.example.inventory_control_system.service;

import com.example.inventory_control_system.service.impl.EmailServiceImpl;
import com.example.inventory_control_system.utils.CustomEmailMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Date;

import static org.mockito.Mockito.*;

class EmailServiceImplTest {

    @InjectMocks
    private EmailServiceImpl emailService;

    @Mock
    private JavaMailSender mailSender;

    private CustomEmailMessage emailMessage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        emailMessage = new CustomEmailMessage(
                "no-reply@example.com",
                "user@example.com",
                new Date(),
                "Test Subject",
                "This is a test email."
        );
    }

    @Test
    void testSendEmail() {
        // Arrange
        SimpleMailMessage expectedMailMessage = new SimpleMailMessage();
        expectedMailMessage.setFrom(emailMessage.getFrom());
        expectedMailMessage.setTo(emailMessage.getTo());
        expectedMailMessage.setSentDate(emailMessage.getSentDate());
        expectedMailMessage.setSubject(emailMessage.getSubject());
        expectedMailMessage.setText(emailMessage.getText());

        // Act
        emailService.sendEmail(emailMessage);

        // Assert
        verify(mailSender, times(1)).send(expectedMailMessage);
    }

    @Test
    void testSendEmailWithNullValues() {
        // Arrange: Create a message with missing fields
        CustomEmailMessage incompleteEmailMessage = new CustomEmailMessage();
        incompleteEmailMessage.setFrom(null);
        incompleteEmailMessage.setTo("user@example.com");
        incompleteEmailMessage.setSubject("Subject");
        incompleteEmailMessage.setText("Message content");

        SimpleMailMessage expectedMailMessage = new SimpleMailMessage();
        expectedMailMessage.setFrom(null);
        expectedMailMessage.setTo(incompleteEmailMessage.getTo());
        expectedMailMessage.setSubject(incompleteEmailMessage.getSubject());
        expectedMailMessage.setText(incompleteEmailMessage.getText());

        // Act
        emailService.sendEmail(incompleteEmailMessage);

        // Assert
        verify(mailSender, times(1)).send(expectedMailMessage);
    }
}
