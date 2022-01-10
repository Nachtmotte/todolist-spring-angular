package com.todolist.backend.service;

import com.todolist.backend.entity.*;
import com.todolist.backend.repository.*;
import com.todolist.backend.security.util.CustomUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepo;

    private final RoleRepository roleRepo;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username not found");
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));
        return new CustomUser(user.getId(), user.getUsername(), user.getPassword(), authorities);
    }

    public User save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role roleUser = roleRepo.findByName(Roles.ROLE_USER.name());
        user.getRoles().add(roleUser);
        return userRepo.save(user);
    }

    public User getById(Integer userId) {
        return userRepo.findById(userId).orElse(null);
    }

    public Page<User> getAll(Pageable pageable) {
        return userRepo.findAll(pageable);
    }

    public User update(User user, String firstname, String lastname, String username, String password) {
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        return userRepo.save(user);
    }

    public User updatePassword(User user, String password){
        user.setPassword(passwordEncoder.encode(password));
        return userRepo.save(user);
    }

    public boolean isUniqueData(String username, String email) {
        List<User> users = userRepo.findByUsernameOrEmail(username, email);
        return users.isEmpty();
    }

    public boolean isUniqueDataForUpdate(Integer userId, String username) {
        User user = userRepo.findByUsername(username);
        return user == null || user.getId() == userId;
    }

    public boolean areDifferentPasswords(String encryptedPassword, String passwordToCompare) {
        return !passwordEncoder.matches(passwordToCompare, encryptedPassword);
    }
}
