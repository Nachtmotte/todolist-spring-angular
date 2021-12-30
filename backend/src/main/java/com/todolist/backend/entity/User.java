package com.todolist.backend.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.sql.Timestamp;
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

    @Column(unique = true, updatable = false)
    private String email;

    @Column(unique = true, updatable = false)
    private String username;

    private String password;

    @CreatedDate
    @Column(updatable = false)
    private Timestamp created;

    private boolean verified;

    @OneToOne(mappedBy = "user")
    private ProfilePicture profilePicture;

    @OneToMany(mappedBy = "user")
    private List<TodoList> todoLists;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Collection<Role> roles = new ArrayList<>();

    @PrePersist
    private void prePersistUser() {
        this.setVerified(false);
    }
}
