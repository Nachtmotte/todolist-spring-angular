package com.todolist.backend.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "todo_list")
public class TodoList {

    @Id
    @GeneratedValue
    private int id;

    @Column(name = "name", unique=true)
    private String name;

    @CreatedDate
    @Column(updatable = false)
    private Timestamp created;

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false)
    private User user;

    @OneToMany(mappedBy = "todoList", cascade = CascadeType.REMOVE)
    private List<Item> items;
}
