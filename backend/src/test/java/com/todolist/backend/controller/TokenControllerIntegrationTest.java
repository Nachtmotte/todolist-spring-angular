package com.todolist.backend.controller;

import com.todolist.backend.entity.Role;
import com.todolist.backend.entity.User;
import com.todolist.backend.repository.RoleRepository;
import com.todolist.backend.security.util.JwtService;
import com.todolist.backend.service.UserService;
import org.junit.After;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.todolist.backend.entity.Roles.Constants.ROLE_USER;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@RunWith(SpringRunner.class)
@SpringBootTest
class TokenControllerIntegrationTest {

    @Autowired
    JwtService jwtService;

    @Autowired
    RoleRepository roleRepo;

    @Autowired
    UserService userService;

    @Autowired
    TokenController tokenController;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private User testUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();

        Role roleUser = new Role();
        roleUser.setName(ROLE_USER);
        roleRepo.save(roleUser);

        testUser = new User();
        testUser.setFirstname("test");
        testUser.setLastname("user");
        testUser.setEmail("test_user@email.com");
        testUser.setUsername("test.user");
        testUser.setPassword("TestUser2022");
        testUser = userService.save(testUser);
    }

    @After
    public void tearDown() {
        userService.deleteAll();
        roleRepo.deleteAll();
    }

    // -----------------------------------------------------
    //        -- Tests for GET REFRESH TOKEN
    // -----------------------------------------------------
    @Test
    void givenValidRefreshToken_whenGetRequest_thenShouldResponseOk() throws Exception {

        //given
        String refreshToken = generateRefreshToken();

        //when
        ResultActions result = mockMvc.perform(get("/token/refresh")
                .header("Authorization", "Bearer " + refreshToken));

        //then
        result.andDo(MockMvcResultHandlers.print());
        result.andExpect(status().isOk());
    }

    // -----------------------------------------------------
    //                 -- Private Method
    // -----------------------------------------------------
    private String generateRefreshToken() {
        return jwtService.createRefreshToken(Integer.toString(testUser.getId()));
    }
}