package com.todolist.backend.controller;

import com.todolist.backend.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.todolist.backend.entity.Roles.Constants.ROLE_USER;

@RestController
@RequiredArgsConstructor
@Secured(ROLE_USER)
@RequestMapping(path = "/users/{userId}/lists/{listId}/items", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemController {

    private final ItemService itemService;

    private final ModelMapper mapper;
}
