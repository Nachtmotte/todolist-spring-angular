package com.todolist.backend.repository;

import com.todolist.backend.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    Page<Item> findByTodoListIdAndStateAndExpiredIsNullOrExpiredAfterAndTodoList_UserId(
            int TodoListId, boolean state, Timestamp timestamp, int userId, Pageable pageable);

    Page<Item> findByTodoListIdAndStateAndExpiredBeforeAndTodoList_UserId(
            int todoListId, boolean state, Timestamp timestamp, int userId, Pageable pageable);

    Page<Item> findByTodoListIdAndStateAndTodoList_UserId(
            int todoListId, boolean state, int userId, Pageable pageable);

    Item findByIdAndTodoListIdAndTodoList_UserId(int id, int todoListId, int userId);
}
