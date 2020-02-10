package com.toptal.mealsmobileapp.restservice;

import com.toptal.mealsmobileapp.model.LoginRequest;
import com.toptal.mealsmobileapp.model.LoginResponse;
import com.toptal.mealsmobileapp.model.SignUpRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface AuthService {

    @Headers({"Accept: application/json"})
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @Headers({"Accept: application/json"})
    @POST("auth/signup")
    Call<LoginResponse> signUp(@Body SignUpRequest signUpRequest);

}
