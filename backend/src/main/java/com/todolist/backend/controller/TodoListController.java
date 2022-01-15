package com.todolist.backend.controller;

import com.todolist.backend.dto.todolist.TodoListDto;
import com.todolist.backend.dto.todolist.TodoListGetDto;
import com.todolist.backend.entity.TodoList;
import com.todolist.backend.entity.User;
import com.todolist.backend.service.TodoListService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import static com.todolist.backend.entity.Roles.Constants.ROLE_USER;

@RestController
@RequiredArgsConstructor
@Secured(ROLE_USER)
@RequestMapping(path = "/users/{userId}/lists", produces = MediaType.APPLICATION_JSON_VALUE)
public class TodoListController {

    private final TodoListService todoListService;

    private final ModelMapper mapper;

    @PostMapping()
    public ResponseEntity<Map<String, Object>> createList(
            Principal principal,
            @PathVariable("userId") Integer userId,
            @Valid @RequestBody TodoListDto requestList) {

        Map<String, Object> bodyResponse = new HashMap<>();

        int sessionId = Integer.parseInt(principal.getName());
        if (sessionId != userId) {
            bodyResponse.put("errorMessage", "Login to add profile picture to user");
            return new ResponseEntity<>(bodyResponse, HttpStatus.FORBIDDEN);
        }

        User currentUser = new User();
        currentUser.setId(userId);
        TodoList list = mapper.map(requestList, TodoList.class);
        list = todoListService.save(currentUser, list);
        TodoListGetDto todoListGetDto = mapper.map(list, TodoListGetDto.class);

        bodyResponse.put("list", todoListGetDto);
        return new ResponseEntity<>(bodyResponse, HttpStatus.CREATED);
    }
}
