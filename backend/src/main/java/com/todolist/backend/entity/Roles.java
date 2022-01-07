package com.todolist.backend.entity;

public enum Roles {
    ROLE_ADMIN(Constants.ROLE_ADMIN),
    ROLE_USER(Constants.ROLE_USER);

    Roles(String roleString){
        if(!roleString.equals(this.name()))
            throw new IllegalArgumentException();
    }

    public static class Constants{
        public static final String ROLE_ADMIN = "ROLE_ADMIN";
        public static final String ROLE_USER = "ROLE_USER";
    }
}
