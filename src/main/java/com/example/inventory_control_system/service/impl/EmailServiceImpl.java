package com.example.inventory_control_system.service.impl;

import com.example.inventory_control_system.service.EmailService;
import com.example.inventory_control_system.utils.CustomEmailMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendEmail(CustomEmailMessage emailMessage) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(emailMessage.getFrom());
        email.setTo(emailMessage.getTo());
        email.setSentDate(emailMessage.getSentDate());
        email.setSubject(emailMessage.getSubject());
        email.setText(emailMessage.getText());
        mailSender.send(email);
    }
}
