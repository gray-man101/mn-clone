package com.example.mnclone.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public void sendWelcomeEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Welcome to our website");
        message.setText("Please complete registration <a href='http://localhost:8080/api/register?token=" + token + "'>complete registration</a>");
        emailSender.send(message);
    }

    public void sendEmail(String to, String subject, String text) {

    }
}
