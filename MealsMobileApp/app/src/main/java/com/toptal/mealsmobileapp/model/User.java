package com.toptal.mealsmobileapp.model;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;

@Data
public class User {
    private Integer id;
    private String username;
    private String password;
    private String roles;
    private Set<String> roleSet;
    private Float maxDailyCalories;

    public User(Integer id, String username, String password, String roles, Float maxDailyCalories) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.maxDailyCalories = maxDailyCalories;
        this.roles = roles;

        this.roleSet = new HashSet<>();
        for (String role : roles.split(",")) {
            roleSet.add(role);
        }
    }

    public boolean isAdmin() {
        return roles.contains(UserRoles.ROLE_ADMIN);
    }

    public boolean isManager() {
        return roles.contains(UserRoles.ROLE_MANAGER);
    }
}
