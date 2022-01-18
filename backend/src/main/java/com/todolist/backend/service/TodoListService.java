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

    public TodoList save(TodoList todoList, User user) {
        todoList.setUser(user);
        return todoListRepo.save(todoList);
    }

    public TodoList getByIdAndUserId(int todoListId, int userId){
        return todoListRepo.findByIdAndUserId(todoListId, userId);
    }

    public List<TodoList> getAllByUserId(int userId) {
        return todoListRepo.findAllByUserId(userId);
    }

    public TodoList update(TodoList todoList, String name){
        todoList.setName(name);
        return todoListRepo.save(todoList);
    }

    public void delete(TodoList todoList){
        todoListRepo.delete(todoList);
    }

    public void deleteAll(){ todoListRepo.deleteAll(); }
}
