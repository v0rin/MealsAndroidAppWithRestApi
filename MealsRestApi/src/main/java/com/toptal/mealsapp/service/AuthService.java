package com.toptal.mealsapp.service;

import com.toptal.mealsapp.model.User;
import com.toptal.mealsapp.model.UserRoles;
import com.toptal.mealsapp.model.dto.LoginRequestDto;
import com.toptal.mealsapp.model.dto.LoginResponseDto;
import com.toptal.mealsapp.model.dto.SignUpRequestDto;
import com.toptal.mealsapp.repository.UserRepository;
import com.toptal.mealsapp.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final String NEW_USER_ROLES = UserRoles.ROLE_USER;
    private static final Double DEFAULT_CALORIES = 2500.0;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginResponseDto signup(SignUpRequestDto signUpRequestDto) {
        var newUser = new User(signUpRequestDto.getUsername(),
                               passwordEncoder.encode(signUpRequestDto.getPassword()),
                               NEW_USER_ROLES,
                               DEFAULT_CALORIES);
        User user = userRepository.save(newUser);
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(newUser.getUsername(), signUpRequestDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);
        String jwt = jwtProvider.generateToken(auth);
        return new LoginResponseDto(user.getId(), newUser.getRoles(), newUser.getMaxDailyCalories(), jwt);
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);
        String jwt = jwtProvider.generateToken(auth);
        User user = userRepository.findByUsername(loginRequestDto.getUsername()).get();
        return new LoginResponseDto(user.getId(), user.getRoles(), user.getMaxDailyCalories(), jwt);
    }

}
