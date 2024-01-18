package com.example.iobackend.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Entity
@Getter
@Setter
@Slf4j
public class NotificationLog {
    @Id
    @Column
    private int logId;

    @Column
    private int templateId;

    @Column
    private int receiverId;

    @Column
    private Date logDate;

    @Column
    private String description;

    @Column
    private String content;

    @Column
    private int attachmentFlag;
}
