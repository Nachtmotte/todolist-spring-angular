package com.todolist.backend.security.util;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomUser extends User {

    private final int id;

    public CustomUser(int userId, String username, String password, Collection<? extends GrantedAuthority> authorities){
        super(username, password, authorities);
        this.id = userId;
    }

    public int getId() {
        return id;
    }
}
