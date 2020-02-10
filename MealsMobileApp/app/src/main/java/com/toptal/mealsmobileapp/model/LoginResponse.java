package com.toptal.mealsmobileapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {

    private Integer id;
    private String roles;
    private Float maxDailyCalories;
    private String jwt;

}
