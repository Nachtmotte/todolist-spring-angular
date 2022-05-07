package com.todolist.backend.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "profile_picture")
public class ProfilePicture {

    @Id
    @GeneratedValue
    private int id;

    @Column(name = "name", unique = true)
    private String name;

    @Column(unique = true)
    private String url;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Override
    public String toString() {
        return "ProfilePicture{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
