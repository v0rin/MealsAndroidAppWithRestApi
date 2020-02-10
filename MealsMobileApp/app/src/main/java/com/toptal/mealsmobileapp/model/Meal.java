package com.toptal.mealsmobileapp.model;

import java.sql.Time;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Meal {
    private Long id;
    private Date date;
    private Time time;
    private String description;
    private Float calories;
    private Integer userId;
    private Float caloriesForTheDay;
}
