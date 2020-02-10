package com.toptal.mealsapp.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class MealDto {

    private Long id;
    private LocalDate date;
    private LocalTime time;
    private String description;
    private Double calories;
    private Long userId;
    private Double caloriesForTheDay;

}
