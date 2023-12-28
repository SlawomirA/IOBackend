package com.example.iobackend.Utils;

import com.example.iobackend.Model.NotificationTemplate;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
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


    public void sendEmailHTML(String user_email, NotificationTemplate notificationTemplate) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();

        message.setFrom(new InternetAddress("sender@example.com"));
        message.setRecipients(MimeMessage.RecipientType.TO, user_email);
        message.setSubject(notificationTemplate.getTitle());

        message.setContent(notificationTemplate.getMessage(), "text/html; charset=utf-8");

        emailSender.send(message);
    }
}
