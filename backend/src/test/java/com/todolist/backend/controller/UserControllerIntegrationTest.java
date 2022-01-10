package com.todolist.backend.controller;

import com.todolist.backend.entity.Role;
import com.todolist.backend.repository.RoleRepository;
import com.todolist.backend.security.util.JwtService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.todolist.backend.entity.Roles.Constants.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerIntegrationTest {

    @MockBean
    JwtService jwtService;

    @Autowired
    RoleRepository roleRepo;

    @Autowired
    UserController userController;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setup(){
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()). build();
        Role roleAdmin = new Role();
        Role roleUser = new Role();
        roleAdmin.setName(ROLE_ADMIN);
        roleUser.setName(ROLE_USER);
        roleRepo.save(roleAdmin);
        roleRepo.save(roleUser);
    }

    @Test
    public void whenPostRequestToUsersAndValidUser_thenCorrectResponse() throws Exception {

        //given
        String user = "{" +
                "\"firstname\": \"Pablo\", " +
                "\"lastname\": \"Sanchez\", " +
                "\"email\": \"pablo_sanchez@email.com\", " +
                "\"username\": \"pablo.sanchez\", " +
                "\"password\" : \"Pablo2022\"}";

        //when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .content(user)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))

        //then
                        .andExpect(MockMvcResultMatchers.status().isCreated());
        result.andDo(MockMvcResultHandlers.print());
    }
}
