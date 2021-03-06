package com.todolist.backend.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "item")
public class Item {

    @Id
    @GeneratedValue
    private int id;

    private String text;

    private boolean state;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp created;

    private Timestamp expired;

    @ManyToOne
    @JoinColumn(name = "todo_list_id", updatable = false)
    private TodoList todoList;
}
