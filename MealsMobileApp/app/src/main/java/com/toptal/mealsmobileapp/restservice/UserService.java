package com.toptal.mealsmobileapp.restservice;

import com.toptal.mealsmobileapp.model.User;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserService {

    @GET("users/admin")
    Call<List<User>> getAll(@Header("Authorization") String jwt);

    @POST("users/admin")
    Call<Void> create(@Body User user, @Header("Authorization") String jwt);

    @GET("users/admin/{id}")
    Call<User> getOne(@Path("id") Long userId, @Header("Authorization") String jwt);

    @PUT("users/admin/{id}")
    Call<Void> update(@Path("id") Integer userId, @Body User user, @Header("Authorization") String jwt);

    @DELETE("users/admin/{id}")
    Call<Void> delete(@Path("id") Integer userId, @Header("Authorization") String jwt);

    @PUT("users/update-max-daily-calories")
    Call<Void> updateMaxDailyCalories(@Body Float maxDailyCalories, @Header("Authorization") String jwt);

}
