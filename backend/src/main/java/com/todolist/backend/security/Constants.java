package com.todolist.backend.security;

public class Constants {

    //Base path
    private static final String usersPath = "/users";
    private static final String listsPath = "/lists";
    private static final String itemsPath = "/items";
    private static final String rolesPath = "/roles";
    private static final String profilePicturePath = "/profile-picture";

    //URLs
    //Without Authorization
    public static final String LOGIN = "/login";
    public static final String CREATE_USER = usersPath;
    public static final String REFRESH_TOKEN = "/token/refresh";

    //All roles
    public static final String GET_PROFILE = usersPath + "/{\\d+}";
    public static final String UPDATE_USER = usersPath + "/{\\d+}";
    public static final String ADD_PROFILE_PICTURE = usersPath + "/{\\d+}" + profilePicturePath;
    public static final String DELETE_PROFILE_PICTURE = usersPath + "/{\\d+}" + profilePicturePath;

    //Admin
    public static final String ADD_ROLE = usersPath + "/{\\d+}" + rolesPath + "/{\\d+}";
    public static final String REMOVE_ROLE = usersPath + "/{\\d+}" + rolesPath + "/{\\d+}";
    public static final String GET_USERS = usersPath + "?page={\\d+}";
    public static final String GET_ROLES = rolesPath;

    //User
    //lists
    public static final String POST_LIST = usersPath + "/{\\d+}" + listsPath;
    public static final String GET_LISTS = usersPath + "/{\\d+}" + listsPath;
    public static final String UPDATE_LIST = usersPath + "/{\\d+}" + listsPath + "/{\\d+}";
    public static final String DELETE_LIST = usersPath + "/{\\d+}" + listsPath + "/{\\d+}";
    //items
    public static final String POST_ITEM = usersPath + "/{\\d+}" + listsPath + "/{\\d+}" + itemsPath;
    public static final String GET_ITEMS = usersPath + "/{\\d+}" + listsPath + "/{\\d+}" + itemsPath + "?page={\\d+}&orderby=**";
    public static final String UPDATE_ITEM = usersPath + "/{\\d+}" + listsPath + "/{\\d+}" + itemsPath + "/{\\d+}";
    public static final String DELETE_ITEM = usersPath + "/{\\d+}" + listsPath + "/{\\d+}" + itemsPath + "/{\\d+}";
}
