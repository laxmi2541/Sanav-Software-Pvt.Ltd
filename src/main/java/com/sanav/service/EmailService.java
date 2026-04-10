package com.sanav.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendResetEmail(String to, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("SANAV SOFTWARE - Admin Password Reset");
            message.setText("Hello Admin,\n\n" +
                    "A password reset was requested for your account.\n" +
                    "Please click the link below to reset your password. This link is valid for 15 minutes.\n\n" +
                    "http://localhost:5500/admin-reset-password.html?token=" + token + "\n\n" +
                    "If you did not request this, please ignore this email.\n\n" +
                    "Regards,\nSANAV SOFTWARE Security");

            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send email to " + to + ". Ensure application.properties has correct SMTP credentials.");
        }
    }

    public void sendReplyEmail(String to, String subject, String replyBody) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("RE: " + subject);
            message.setText(replyBody + "\n\n--\nRegards,\nAntigravity Support");

            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send reply to " + to + ". " + e.getMessage());
        }
    }
}
