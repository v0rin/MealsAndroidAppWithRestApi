package com.toptal.mealsmobileapp.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class CurrentUser extends User {

    private String jwt;

    public CurrentUser(Integer id, String username, String password, String roles, Float maxDailyCalories, String jwt) {
        super(id, username, password, roles, maxDailyCalories);
        this.jwt = jwt;
    }
}
