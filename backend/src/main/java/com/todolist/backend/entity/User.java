package com.todolist.backend.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "`user`")
public class User {

    @Id
    @GeneratedValue
    private int id;

    private String firstname;

    private String lastname;

    @Column(unique = true, updatable = false)
    private String email;

    @Column(unique = true)
    private String username;

    private String password;

    @CreationTimestamp
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
    private Set<Role> roles = new HashSet<>();

    @PrePersist
    private void prePersistUser() {
        this.setVerified(false);
    }
}
