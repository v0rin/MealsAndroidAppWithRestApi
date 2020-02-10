package com.toptal.mealsapp.rest;

import com.toptal.mealsapp.model.dto.UserDto;
import com.toptal.mealsapp.service.UserService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService service;

    @GetMapping("/admin")
    public List<UserDto> all() {
        return service.findAll();
    }

    @PostMapping("/admin")
    public ResponseEntity create(@RequestBody UserDto dto) {
        service.create(dto);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @GetMapping("/admin/{id}")
    public UserDto getOne(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity update(@RequestBody UserDto dto, @PathVariable Long id) {
        service.update(dto, id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        service.deleteById(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PutMapping("/update-max-daily-calories")
    public ResponseEntity updateMaxDailyCalories(@RequestBody Double maxDailyCalories) {
        service.updateMaxDailyCalories(maxDailyCalories);
        return new ResponseEntity(HttpStatus.OK);
    }

}
