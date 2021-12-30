package com.todolist.backend.service;

import com.todolist.backend.entity.*;
import com.todolist.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;

    private final RoleRepository roleRepo;

    public User save(User user){
        //user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role roleUser = roleRepo.findByName(Roles.ROLE_USER.name());
        user.getRoles().add(roleUser);
        return userRepo.save(user);
    }
}
