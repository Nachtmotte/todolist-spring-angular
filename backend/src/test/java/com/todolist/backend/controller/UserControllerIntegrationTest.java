package com.todolist.backend.controller;

import com.todolist.backend.entity.Role;
import com.todolist.backend.entity.User;
import com.todolist.backend.repository.RoleRepository;
import com.todolist.backend.security.util.JwtService;
import com.todolist.backend.service.UserService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.stream.Collectors;

import static com.todolist.backend.entity.Roles.Constants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerIntegrationTest {

    @Autowired
    JwtService jwtService;

    @Autowired
    RoleRepository roleRepo;

    @Autowired
    UserService userService;

    @Autowired
    UserController userController;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private User user;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();

        Role roleAdmin = new Role();
        Role roleUser = new Role();
        roleAdmin.setName(ROLE_ADMIN);
        roleUser.setName(ROLE_USER);
        roleRepo.save(roleAdmin);
        roleRepo.save(roleUser);

        user = new User();
        user.setFirstname("user");
        user.setLastname("test");
        user.setEmail("user_test@email.com");
        user.setUsername("user.test");
        user.setPassword("UserTest2022");
        user = userService.save(user);
    }

    @After
    public void tearDown() {
        userService.deleteAll();
        roleRepo.deleteAll();
    }

    @Test
    public void givenValidUserData_whenPostRequest_thenShouldResponseCreated() throws Exception {

        //given
        String user = "{" +
                "\"firstname\": \"Pablo\", " +
                "\"lastname\": \"Sanchez\", " +
                "\"email\": \"pablo_sanchez@email.com\", " +
                "\"username\": \"pablo.sanchez\", " +
                "\"password\" : \"Pablo2022\"}";

        //when
        ResultActions result = mockMvc.perform(post("/users")
                        .content(user)
                        .contentType(APPLICATION_JSON_VALUE));

        //then
        result.andExpect(status().isCreated());
    }

    @Test
    public void givenExistingUserData_whenPostRequest_thenShouldResponseConflict() throws Exception {
        //given
        String user = "{" +
                "\"firstname\": \"user\", " +
                "\"lastname\": \"test\", " +
                "\"email\": \"user_test@email.com\", " +
                "\"username\": \"user.test\", " +
                "\"password\" : \"UserTest2022\"}";

        //when
        ResultActions result = mockMvc.perform(post("/users")
                .content(user)
                .contentType(APPLICATION_JSON_VALUE));

        //then
        result.andExpect(status().isConflict());
    }

    @Test
    public void givenValidUserIdAndValidToken_whenGetRequest_thenShouldResponseOk() throws Exception {
        //given
        String userId = Integer.toString(user.getId());

        //when
        ResultActions result = mockMvc.perform(get("/users/" + userId)
                .header("Authorization", "Bearer " + generateToken()));

        //then
        result.andExpect(status().isOk());
    }

    @Test
    public void givenValidUserIdWithoutToken_whenGetRequest_thenShouldResponseForbidden() throws Exception {
        //given
        String userId = Integer.toString(user.getId());

        //when
        ResultActions result = mockMvc.perform(get("/users/" + userId));

        //then
        result.andExpect(status().isForbidden());
    }

    @Test
    public void givenInvalidUserIdAndValidToken_whenGetRequest_thenShouldResponseForbidden() throws Exception {
        //given
        String userId = Integer.toString(user.getId() + 1);

        //when
        ResultActions result = mockMvc.perform(get("/users/" + userId)
                .header("Authorization", "Bearer " + generateToken()));

        //then
        result.andExpect(status().isForbidden());
    }

    private String generateToken(){
        return jwtService.createAccessToken(Integer.toString(user.getId()),
                user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
    }
}
