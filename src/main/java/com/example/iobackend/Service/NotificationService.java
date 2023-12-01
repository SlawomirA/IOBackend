package com.example.iobackend.Service;

import com.example.iobackend.Model.NotificationLog;
import com.example.iobackend.Model.NotificationTemplate;
import com.example.iobackend.Model.Response.MyResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface NotificationService {
    List<NotificationLog> getAllNotificationsLogs();
    Optional<NotificationLog> getNotificationLogById(Long notificationLogId);
    List<NotificationTemplate> getAllNotificationsTemplates();
    Optional<NotificationTemplate> getNotificationTemplateById(Long notificationTemplateId);
    ResponseEntity<MyResponse> createNotificationTemplate(NotificationTemplate notificationTemplate);
    ResponseEntity<MyResponse> updateNotificationTemplate(Long notificationId, NotificationTemplate notificationTemplate);
    ResponseEntity<MyResponse> sendNotification(Long userId, Long notificationTemplateId);
}
