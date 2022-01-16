package com.todolist.backend.repository;

import com.todolist.backend.entity.TodoList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoListRepository extends JpaRepository<TodoList, Integer> {

    TodoList findByIdAndUserId(int id, int userId);

    List<TodoList> findAllByUserId(int userId);
}
