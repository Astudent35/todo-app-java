package com.example.todoapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.todoapp.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);
    @Query("SELECT u FROM User u WHERE u.username = :login OR u.email = :login")
    User findByUsernameOrEmail(@Param("login") String login);
}
