package com.todolist.backend.repository;

import com.todolist.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByUsername(String username);

    List<User> findByUsernameOrEmail(String username, String email);
}
