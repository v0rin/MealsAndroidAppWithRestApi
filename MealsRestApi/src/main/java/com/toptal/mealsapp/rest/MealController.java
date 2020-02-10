package com.toptal.mealsapp.rest;

import com.toptal.mealsapp.model.dto.MealDto;
import com.toptal.mealsapp.service.MealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


@RestController
@RequestMapping("/api/meals")
public class MealController {

    @Autowired
    private MealService service;

    @GetMapping("")
    List<MealDto> getFilteredByDateAndTime(@RequestParam(required = false) String startDate,
                                           @RequestParam(required = false) String endDate,
                                           @RequestParam(required = false) String startTime,
                                           @RequestParam(required = false) String endTime) {
        List<MealDto> list;
        if (startDate != null && endDate != null && startTime != null && endTime != null) {
            list = service.getFilteredByDateAndTimeForCurrentUser(
                    LocalDate.parse(startDate), LocalDate.parse(endDate), LocalTime.parse(startTime), LocalTime.parse(endTime));
        }
        else {
            list = service.findAllForCurrentUser();
        }
        return list;
    }

    @PostMapping("")
    ResponseEntity create(@RequestBody MealDto dto) {
        service.create(dto);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    ResponseEntity update(@RequestBody MealDto dto, @PathVariable Long id) {
        service.update(dto, id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    ResponseEntity delete(@PathVariable Long id) {
        service.deleteById(id);
        return new ResponseEntity(HttpStatus.OK);
    }

}
