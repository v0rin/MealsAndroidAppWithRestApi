package com.toptal.mealsapp.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
@Data
public class User implements RestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(length = 50, unique=true)
    private String username;

    @NotNull
    @Column(length = 250)
    private String password;

    @NotNull
    @Column(length = 250)
    private String roles;

    private Double maxDailyCalories;

    public User() {}

    public User(String username, String password, String roles, Double maxDailyCalories) {
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.maxDailyCalories = maxDailyCalories;
    }
}
