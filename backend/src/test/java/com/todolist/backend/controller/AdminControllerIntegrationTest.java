package com.todolist.backend.controller;

import com.todolist.backend.entity.Role;
import com.todolist.backend.entity.User;
import com.todolist.backend.security.util.JwtService;
import com.todolist.backend.service.RoleService;
import com.todolist.backend.service.UserService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.stream.Collectors;

import static com.todolist.backend.entity.Roles.Constants.ROLE_ADMIN;
import static com.todolist.backend.entity.Roles.Constants.ROLE_USER;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@RunWith(SpringRunner.class)
@SpringBootTest
class AdminControllerIntegrationTest {

    @Autowired
    JwtService jwtService;

    @Autowired
    RoleService roleService;

    @Autowired
    UserService userService;

    @Autowired
    AdminController adminController;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private User testUser;

    private Role roleUser;

    private Role roleAdmin;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();

        roleUser = new Role();
        roleUser.setName(ROLE_USER);
        roleUser = roleService.save(roleUser);
        roleAdmin = new Role();
        roleAdmin.setName(ROLE_ADMIN);
        roleAdmin = roleService.save(roleAdmin);

        testUser = new User();
        testUser.setFirstname("test");
        testUser.setLastname("user");
        testUser.setEmail("test_user@email.com");
        testUser.setUsername("test.user");
        testUser.setPassword("TestUser2022");
        testUser = userService.save(testUser);
        roleService.addRoleToUser(testUser, roleAdmin);
    }

    @After
    public void tearDown() {
        userService.deleteAll();
        roleService.deleteAll();
    }

    // -----------------------------------------------------
    //           -- Tests for ADD ROLE TO USER
    // -----------------------------------------------------
    @Test
    public void addRoleToUser() {
    }

    /*
    @Test
    public void removeRoleFromUser() {
    }

    @Test
    public void getUsers() {
    }

    @Test
    public void getRoles() {
    }
    */

    // -----------------------------------------------------
    //                 -- Private Method
    // -----------------------------------------------------
    private String generateToken() {
        return jwtService.createAccessToken(Integer.toString(testUser.getId()),
                testUser.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
    }
}