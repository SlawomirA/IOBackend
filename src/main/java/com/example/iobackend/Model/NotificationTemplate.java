package com.example.iobackend.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Entity
@Getter
@Setter
@Slf4j
public class NotificationTemplate {

    @Id
    @Column
    private int templateId;

    @Column
    private String messageType;

    @Column
    private String message;

    @Column
    private int htmlFlag;

    @Column
    private String title;

    @Column
    private int userId;


}
