package com.example.iobackend.Utils;

import com.example.iobackend.Model.NotificationTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class MyMailSender {
    @Autowired
    private JavaMailSender emailSender;

    public void sendEmail(String user_email, NotificationTemplate notificationTemplate) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@clowns.com");
        message.setTo(user_email);
        message.setSubject(notificationTemplate.getTitle());
        message.setText(notificationTemplate.getMessage());
        emailSender.send(message);
    }
}
