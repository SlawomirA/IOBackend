package com.example.iobackend.Controller;


import com.example.iobackend.Model.NotificationLog;
import com.example.iobackend.Model.NotificationTemplate;
import com.example.iobackend.Model.Response.MyResponse;
import com.example.iobackend.Service.Impl.NotificationServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class NotificationController {
    @Autowired
    private NotificationServiceImpl notificationService;


    @GetMapping(value = "/logs/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all logs", description = "Retrieves all the logs from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "405", description = "Validation error")
    })
    private ResponseEntity<List<NotificationLog>> getAllNotificationsLogs(@RequestHeader("X-USER-ROLE") String role) {
        System.out.println("ROLE: "+role);
        if (!role.equals("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ArrayList<>());
        }
        return ResponseEntity.ok(notificationService.getAllNotificationsLogs());
    }


    @GetMapping(value = "/logs/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get log by id", description = "Gets all the logs by given id in the path")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "405", description = "Validation error")
    })
    private ResponseEntity<NotificationLog> getNotificationLogById( @Parameter(description = "ID of the notification log", example = "1") @PathVariable("id") Long notificationLogId) {
        Optional<NotificationLog> notificationLogOptional = notificationService.getNotificationLogById(notificationLogId);
        return notificationLogOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/templates/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all templates", description = "Gets all templates by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "405", description = "Validation error")
    })
    private ResponseEntity<List<NotificationTemplate>> getAllNotificationsTemplates(@RequestHeader("X-USER-ROLE") String role) {
        System.out.println("ROLE: "+role);
        if (!role.equals("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ArrayList<>());
        }
        return ResponseEntity.ok(notificationService.getAllNotificationsTemplates());
    }

    @GetMapping(value = "/templates/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Find template by id", description = "Finds notification template by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "405", description = "Validation error")
    })

    private ResponseEntity<NotificationTemplate> getNotificationTemplateById(@Parameter(description = "ID of the template", example = "1") @PathVariable("id") Long notificationTemplateId) {
        Optional<NotificationTemplate> notificationTemplateOptional = notificationService.getNotificationTemplateById(notificationTemplateId);
        return notificationTemplateOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/templates/add", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Add notification template", description = "Adds notification template to database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "405", description = "Validation error")
    })
    public ResponseEntity<MyResponse> createNotificationTemplate(@RequestBody NotificationTemplate notificationTemplate) {
        return notificationService.createNotificationTemplate(notificationTemplate);
    }


    @DeleteMapping(value = "/templates/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Deletes template", description = "Adds notification template to database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "405", description = "Validation error")
    })
    public ResponseEntity<MyResponse> deleteNotificationTemplate(@PathVariable("id") Long notificationTemplateID) {
        return notificationService.deleteNotificationTemplate(notificationTemplateID);
    }

    @PutMapping(value = "/templates/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Updates notification template", description = "Updates specific notification template")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "405", description = "Validation error")
    })
    public ResponseEntity<MyResponse> updateNotificationTemplate(@RequestHeader("X-USER-ROLE") String role,@Parameter(description = "ID of the template", example = "1") @PathVariable("id") Long notificationTemplateId, @RequestBody  NotificationTemplate notificationTemplate) {
        System.out.println("ROLE: "+role);
        if (!role.equals("ROLE_ADMIN")) {
            MyResponse response = new MyResponse(403,"Forbidden");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        return notificationService.updateNotificationTemplate(notificationTemplateId, notificationTemplate);
    }

    @PostMapping(value = "/notifications/send", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Sends email message", description = "Sends email message to specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "405", description = "Validation error")
    })
    public ResponseEntity<MyResponse> sendNotification(Long userId, Long notificationTemplateId) {
        // System.out.println("ROLE: "+role);
        // if (!role.equals("ROLE_ADMIN")) {
        //     MyResponse response = new MyResponse(403,"Forbidden");
        //     return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        // }
        return notificationService.sendNotification(userId, notificationTemplateId);
    }


    @PostMapping(value = "/notifications/sendPopup/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Sends email message", description = "Sends email message to specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "405", description = "Validation error"),
            @ApiResponse(responseCode = "409", description = "Conflict")
    })
    public ResponseEntity<?> sendPopup(@RequestHeader("X-USER-ROLE") String role, @PathVariable("id") Long notificationTemplateId) {
        System.out.println("ROLE: "+role);
        if (!role.equals("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ArrayList<>());
        }
        return notificationService.sendPopup(notificationTemplateId);
    }
}
