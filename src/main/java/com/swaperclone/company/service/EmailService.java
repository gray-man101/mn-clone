package com.swaperclone.company.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public void sendWelcomeEmail(String to, String token) {
        sendEmail(to, "Welcome to our website", String.format("Please complete registration " +
                "<a href='http://localhost:8080/api/register?token=%s'>complete registration</a>.", token));
    }

    public void notifyCustomerAboutPartialRefund(String to, BigDecimal amount) {
        sendEmail(to, "Partial refund", String.format("You have receive partial refund in the amount of %.2f due " +
                "to failed loan status.", amount));
    }

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }
}
