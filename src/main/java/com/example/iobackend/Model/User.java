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
public class User {

    @Id
    @Column
    private int userId;

    @Column
    private String username;

    @Column
    private String password;

    @Column
    private Boolean enabled;

    @Column
    private String email;


}
