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
import com.example.iobackend.Utils.MyMailSender;
import org.springframework.messaging.MessagingException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import java.util.Date;
import java.util.List;
import java.util.Objects;
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
    public ResponseEntity<MyResponse> deleteNotificationTemplate(long notificationTemplateID) {
        try {
            Optional<NotificationTemplate> notificationTemplate = notificationTemplateRepository.findById(notificationTemplateID);
            notificationTemplateRepository.delete(notificationTemplate.get());
            // Separate transaction for logging
            logDeleteOperation(notificationTemplate.get());

            return new ResponseEntity<>(new MyResponse(200, "Success message"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new MyResponse(500, "Error: "+e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Transactional
    public ResponseEntity<MyResponse> createNotificationTemplate(NotificationTemplate notificationTemplate) {
        try {
            NotificationTemplate savedTemplate = notificationTemplateRepository.save(notificationTemplate);
            // Separate transaction for logging
            logNotification(savedTemplate);

            return new ResponseEntity<>(new MyResponse(200, "Success message"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new MyResponse(500, "Error: "+e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logNotification(NotificationTemplate savedTemplate) {
        NotificationLog notificationLog = new NotificationLog();
        notificationLog.setLogDate(new Date());
        notificationLog.setAttachmentFlag(0);
        notificationLog.setTemplateId(Math.toIntExact(savedTemplate.getTemplateId()));
        notificationLog.setContent(savedTemplate.getMessage());
        notificationLog.setDescription(Helper.MESSAGE_CREATE_NOTIFICATION_TEMPLATE + savedTemplate.getTemplateId());
        notificationLogRepository.saveAndFlush(notificationLog);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logDeleteOperation(NotificationTemplate deletedTemplate) {
        NotificationLog notificationLog = new NotificationLog();
        notificationLog.setLogDate(new Date());
        notificationLog.setAttachmentFlag(0);
        notificationLog.setTemplateId(deletedTemplate.getTemplateId());
        notificationLog.setContent(deletedTemplate.getMessage());
        notificationLog.setDescription(Helper.MESSAGE_DELETE_NOTIFICATION + deletedTemplate.getTemplateId());
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
            notificationLog.setContent(notificationTemplate.getMessage());
            notificationLog.setDescription(Helper.MESSAGE_UPDATE_NOTIFICATION_TEMPLATE + notificationTemplate.getUserId());
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
                if(notificationTemplate.getHtmlFlag() != 0) {
                    try {
                        mailSender.sendEmailHTML(userEmail,notificationTemplate);
                    } catch (MessagingException | jakarta.mail.MessagingException e) {
                        throw new RuntimeException(e);
                    }
                }
                else
                    mailSender.sendEmail(userEmail, notificationTemplate);

                NotificationLog notificationLog = new NotificationLog();
                notificationLog.setLogDate(new Date());
                notificationLog.setAttachmentFlag(0);
                notificationLog.setTemplateId(Math.toIntExact(notificationTemplateId));
                notificationLog.setContent(notificationTemplate.getMessage());
                notificationLog.setDescription(Helper.MESSAGE_SEND_NOTIFICATION + user.getUserId());
                addLog(notificationLog);

                MyResponse myResponse = new MyResponse(200, "Email sended successfully");
                return new ResponseEntity<>(myResponse, HttpStatus.OK);
            } else
                return new ResponseEntity<>(new MyResponse(404,"Notification template not found"), HttpStatus.NOT_FOUND);

        } else
            return new ResponseEntity<>(new MyResponse(404,"User not found"), HttpStatus.NOT_FOUND);

    }

    @Override
    public ResponseEntity<?> sendPopup(final Long notificationTemplateId) {
        Optional<NotificationTemplate> notificationTemplateOptional =
                notificationTemplateRepository.findById(notificationTemplateId);

        if (notificationTemplateOptional.isPresent()) {
            NotificationTemplate notificationTemplate = notificationTemplateOptional.get();
            if(Objects.equals(notificationTemplate.getMessageType(), "POPUP")) {
                NotificationLog notificationLog = new NotificationLog();
                notificationLog.setLogDate(new Date());
                notificationLog.setAttachmentFlag(0);
                notificationLog.setTemplateId(Math.toIntExact(notificationTemplateId));
                notificationLog.setContent(notificationTemplate.getMessage());
                notificationLog.setDescription(Helper.MESSAGE_SEND_POPUP + notificationTemplate.getTemplateId());
                addLog(notificationLog);

                return new ResponseEntity<>(notificationTemplate, HttpStatus.OK);
            } else
                return new ResponseEntity<>(new MyResponse(409,"Resource of wrong type"), HttpStatus.CONFLICT);


        } else
            return new ResponseEntity<>(new MyResponse(404,"Template not found"), HttpStatus.NOT_FOUND);

    }


    void addLog(NotificationLog notificationLog){
            notificationLogRepository.save(notificationLog);
    }
}
