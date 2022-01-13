package com.todolist.backend.service;

import com.todolist.backend.entity.*;
import com.todolist.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final UserRepository userRepo;

    private final RoleRepository roleRepo;

    public List<Role> getAll() {
        return roleRepo.findAll();
    }

    public Role getById(Integer id){
        return roleRepo.findById(id).orElse(null);
    }

    public User addRoleToUser(User user, Role role) {
        user.getRoles().add(role);
        return userRepo.save(user);
    }

    public User removeRoleFromUser(User user, int roleId) {
        user.getRoles().removeIf(r -> r.getId() == roleId);
        return userRepo.save(user);
    }

    public Role save(Role role){
        return roleRepo.save(role);
    }

    public void deleteAll(){
        roleRepo.deleteAll();
    }
}
