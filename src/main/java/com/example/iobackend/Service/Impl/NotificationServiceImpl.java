package com.example.iobackend.Service.Impl;

import com.example.iobackend.Model.NotificationLog;
import com.example.iobackend.Model.NotificationTemplate;
import com.example.iobackend.Model.Response.MyResponse;
import com.example.iobackend.Model.User;
import com.example.iobackend.Repository.NotificationLogRepository;
import com.example.iobackend.Repository.NotificationTemplateRepository;
import com.example.iobackend.Repository.UserRepository;
import com.example.iobackend.Service.NotificationService;
import com.example.iobackend.Utils.Helper;
import com.example.iobackend.Utils.InteractionCreationer;
import com.example.iobackend.Utils.MyMailSender;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private NotificationLogRepository notificationLogRepository;
    @Autowired
    private NotificationTemplateRepository notificationTemplateRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MyMailSender mailSender;
//    @Autowired
//    private InteractionCreationer interactionCreationer;

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

    @Transactional
    public ResponseEntity<MyResponse> createNotificationTemplate(NotificationTemplate notificationTemplate) {
        try {
            NotificationTemplate savedTemplate = notificationTemplateRepository.save(notificationTemplate);
            // Separate transaction for logging
            logNotification(savedTemplate);

            return new ResponseEntity<>(new MyResponse(200, "Success message"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new MyResponse(500, "Error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logNotification(NotificationTemplate savedTemplate) {
        NotificationLog notificationLog = new NotificationLog();
        notificationLog.setLogDate(new Date());
        notificationLog.setAttachmentFlag(0);
        System.out.println("AAA: "+savedTemplate.getTemplateId());
        notificationLog.setTemplateId(Math.toIntExact(savedTemplate.getTemplateId()));
        notificationLog.setSenderId(1); // todo: authorization
        notificationLog.setContent(savedTemplate.getMessage());
        notificationLog.setDescription(Helper.MESSAGE_SEND_NOTIFICATION);
        notificationLogRepository.saveAndFlush(notificationLog);
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

            NotificationLog notificationLog = new NotificationLog();
            notificationLog.setLogDate(new Date());
            notificationLog.setAttachmentFlag(0);
            notificationLog.setTemplateId(Math.toIntExact(notificationId));
            notificationLog.setSenderId(1);     //todo: authorization
            notificationLog.setContent(notificationTemplate.getMessage());
            notificationLog.setDescription(Helper.MESSAGE_SEND_NOTIFICATION);
            addLog(new NotificationLog());

            MyResponse myResponse = new MyResponse(200, "Notification template updated successfully");
            return new ResponseEntity<>(myResponse, HttpStatus.OK);
        } else {
            // Handle case where the template with the specified ID is not found
            MyResponse myResponse = new MyResponse(404, "Notification template not found");
            return new ResponseEntity<>(myResponse, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<MyResponse> sendNotification(final Long userId, final Long notificationTemplateId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String userEmail = user.getEmail();

            NotificationTemplate notificationTemplate = notificationTemplateRepository.findById(notificationTemplateId).orElse(null);

            if (notificationTemplate != null) {
                mailSender.sendEmail(userEmail, notificationTemplate);

                NotificationLog notificationLog = new NotificationLog();
                notificationLog.setLogDate(new Date());
                notificationLog.setAttachmentFlag(0);
                notificationLog.setTemplateId(Math.toIntExact(notificationTemplateId));
                notificationLog.setSenderId(1);     //todo: authorization
                notificationLog.setContent(notificationTemplate.getMessage());
                notificationLog.setDescription(Helper.MESSAGE_SEND_NOTIFICATION);
                addLog(new NotificationLog());

                MyResponse myResponse = new MyResponse(200, "Email sended successfully");
                return new ResponseEntity<>(myResponse, HttpStatus.OK);
            } else
                return new ResponseEntity<>(new MyResponse(404,"Notification template not found"), HttpStatus.NOT_FOUND);

        } else
            return new ResponseEntity<>(new MyResponse(404,"User not found"), HttpStatus.NOT_FOUND);

    }

    void addLog(NotificationLog notificationLog){
            notificationLogRepository.save(notificationLog);
    }
}
