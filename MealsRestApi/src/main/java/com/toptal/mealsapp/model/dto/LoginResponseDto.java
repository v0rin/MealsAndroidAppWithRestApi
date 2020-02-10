package com.toptal.mealsapp.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDto {

    private Long id;
    private String roles;
    private Double maxDailyCalories;
    private String jwt;

}
