package com.todolist.backend.service;

import com.todolist.backend.entity.*;
import com.todolist.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public User getById(Integer userId) {
        return userRepo.findById(userId).orElse(null);
    }

    public User getByUsername(String username){ return userRepo.findByUsername(username); }

    public List<User> getAll(){ return userRepo.findAll(); }

    public User update(User user) {
        //user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(userRepo.findById(user.getId()).orElseThrow().getRoles());
        return userRepo.save(user);
    }

    public boolean verifyUniqueData(String username, String email) {
        List<User> users = userRepo.findByUsernameOrEmail(username, email);
        return users.isEmpty();
    }

    public User addRoleToUser(String username, String roleName){
        User user = userRepo.findByUsername(username);
        Role role = roleRepo.findByName(roleName);
        user.getRoles().add(role);
        return userRepo.save(user);
    }

    public User removeRoleToUser(String username, String roleName){
        User user = userRepo.findByUsername(username);
        user.getRoles().removeIf(role -> role.getName().equals(roleName));
        return userRepo.save(user);
    }
}
