package com.example.iobackend.Service.Impl;

import com.example.iobackend.Model.NotificationLog;
import com.example.iobackend.Model.NotificationTemplate;
import com.example.iobackend.Model.Response.MyResponse;
import com.example.iobackend.Repository.NotificationLogRepository;
import com.example.iobackend.Repository.NotificationTemplateRepository;
import com.example.iobackend.Service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private NotificationLogRepository notificationLogRepository;
    @Autowired
    private NotificationTemplateRepository notificationTemplateRepository;

    @Override
    public List<NotificationLog> getAllNotificationsLogs() {
        return notificationLogRepository.findAll();
    }

    @Override
    public Optional<NotificationLog> getNotificationLogById(Long notificationLogId) {
        return notificationLogRepository.findById(notificationLogId);
    }

    @Override
    public List<NotificationTemplate> getAllNotificationsTemplates() {
        return notificationTemplateRepository.findAll();
    }

    @Override
    public Optional<NotificationTemplate> getNotificationTemplateById(final Long notificationTemplateId) {
        return notificationTemplateRepository.findById(notificationTemplateId);
    }

    @Override
    public ResponseEntity<MyResponse> createNotificationTemplate(final NotificationTemplate notificationTemplate) {
        try {
            notificationTemplateRepository.saveAndFlush(notificationTemplate);
            return new ResponseEntity<>(new MyResponse(200, "Success message"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new MyResponse(500,"Error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<MyResponse> updateNotificationTemplate(final Long notificationId, final NotificationTemplate notificationTemplate) {
        Optional<NotificationTemplate> optionalNotificationTemplate = notificationTemplateRepository.findById(notificationId);

        if (optionalNotificationTemplate.isPresent()) {
            NotificationTemplate template = optionalNotificationTemplate.get();
            template.setMessage(notificationTemplate.getMessage());
            template.setMessageType(notificationTemplate.getMessageType());
            template.setTitle(notificationTemplate.getTitle());
            template.setUserId(notificationTemplate.getUserId());

            notificationTemplateRepository.save(template);

            MyResponse myResponse = new MyResponse(200, "Notification template updated successfully");
            return new ResponseEntity<>(myResponse, HttpStatus.OK);
        } else {
            // Handle case where the template with the specified ID is not found
            MyResponse myResponse = new MyResponse(404, "Notification template not found");
            return new ResponseEntity<>(myResponse, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public NotificationTemplate sendNotification(final int userId, final Long notificationTemplateId) {
        return null;
    }
}
