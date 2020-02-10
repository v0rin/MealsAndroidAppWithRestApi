package com.toptal.mealsapp.rest;

import com.toptal.mealsapp.model.dto.LoginRequestDto;
import com.toptal.mealsapp.model.dto.LoginResponseDto;
import com.toptal.mealsapp.model.dto.SignUpRequestDto;
import com.toptal.mealsapp.repository.UserRepository;
import com.toptal.mealsapp.security.JwtProvider;
import com.toptal.mealsapp.service.AuthService;
import com.toptal.mealsapp.service.MealService;
import com.toptal.mealsapp.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuthService authService;
    @MockBean
    private UserService userService;
    @MockBean
    private MealService mealService;
    @MockBean
    private JwtProvider jwtProvider;
    @MockBean
    private UserRepository userRepository;

    @Before
    public void setUp() {
        Mockito.reset(authService);
        Mockito.reset(userService);
        Mockito.reset(jwtProvider);
        Mockito.reset(userRepository);
    }

    @Test
    public void signup() throws Exception {
        // given
        String signUpBody = "{\"username\":\"manager\", \"password\":\"123\"}";
        var signUpRequestDto = new SignUpRequestDto("manager", "123");
        when(authService.signup(signUpRequestDto))
                .thenReturn(new LoginResponseDto(1L, "ROLE_USER", 2500.0, "some_jwt"));

        // when & then
        mockMvc.perform(post("/api/auth/signup")
                .accept(MediaType.APPLICATION_JSON)
                .content(signUpBody)
                .contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.roles", is("ROLE_USER")))
                .andExpect(jsonPath("$.maxDailyCalories", is(2500.0)))
                .andExpect(jsonPath("$.jwt", is("some_jwt")));;
        verify(authService).signup(signUpRequestDto);
    }

    @Test
    public void login() throws Exception {
        // given
        String loginBody = "{\"username\":\"user\", \"password\":\"123\"}";
        var loginRequestDto = new LoginRequestDto("user", "123");
        when(authService.login(loginRequestDto))
                .thenReturn(new LoginResponseDto(1L, "ROLE_USER", 2500.0, "some_jwt"));

        // when & then
        mockMvc.perform(post("/api/auth/login")
                .accept(MediaType.APPLICATION_JSON)
                .content(loginBody)
                .contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.roles", is("ROLE_USER")))
                .andExpect(jsonPath("$.maxDailyCalories", is(2500.0)))
                .andExpect(jsonPath("$.jwt", is("some_jwt")));
        verify(authService).login(loginRequestDto);
    }
}