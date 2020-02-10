package com.toptal.mealsapp.rest;

import com.toptal.mealsapp.model.dto.LoginRequestDto;
import com.toptal.mealsapp.model.dto.LoginResponseDto;
import com.toptal.mealsapp.model.dto.SignUpRequestDto;
import com.toptal.mealsapp.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public LoginResponseDto signup(@RequestBody SignUpRequestDto signUpRequestDto) {
        return authService.signup(signUpRequestDto);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponseDto login(@RequestBody LoginRequestDto loginRequestDto) {
        return authService.login(loginRequestDto);
    }
}
