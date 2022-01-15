package com.todolist.backend.service;

import com.todolist.backend.entity.TodoList;
import com.todolist.backend.entity.User;
import com.todolist.backend.repository.TodoListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoListService {

    private final TodoListRepository todoListRepo;

    public TodoList save(User user, TodoList todoList) {
        todoList.setUser(user);
        return todoListRepo.save(todoList);
    }

    public TodoList getById(int todoListId) {
        return todoListRepo.getById(todoListId);
    }

    public List<TodoList> getAll() {
        return todoListRepo.findAll();
    }

    public TodoList update(TodoList todoList){
        return todoListRepo.save(todoList);
    }

    public void delete(TodoList todoList){
        todoListRepo.delete(todoList);
    }
}
