package com.example.iobackend.Repository;

import com.example.iobackend.Model.NotificationLog;
import com.example.iobackend.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
