package com.todolist.backend.entity;

import lombok.*;
import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String firstname;

    private String lastname;

    @Column(unique=true, updatable = false)
    private String email;

    @Column(unique=true, updatable = false)
    private String username;

    private String password;

    @OneToOne(mappedBy = "user")
    private ProfilePicture profilePicture;

    @OneToMany(mappedBy = "user")
    private List<TodoList> todoLists;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Collection<Role> roles = new ArrayList<>();
}
