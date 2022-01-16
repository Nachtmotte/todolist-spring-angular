package com.todolist.backend.repository;

import com.todolist.backend.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    Item findByIdAndTodoList_UserId(int id, int userId);

    List<Item> findAllByTodoListIdAndTodoList_UserId(int todoListId, int userId);
}
