package com.toptal.mealsmobileapp.restservice;

import com.toptal.mealsmobileapp.model.Meal;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AdminMealService {

    @Headers({"Accept: application/json"})
    @GET("meals/admin")
    Call<List<Meal>> all(@Query("userId") Integer userId, @Header("Authorization") String jwt);

    @Headers({"Accept: application/json"})
    @GET("meals/admin?")
    Call<List<Meal>> filteredByDateAndTime(@Query("userId") Integer userId,
                                           @Query("startDate") String startDate,
                                           @Query("endDate") String endDate,
                                           @Query("startTime") String startTime,
                                           @Query("endTime") String endTime,
                                           @Header("Authorization") String jwt);

    @Headers({"Accept: application/json"})
    @POST("meals/admin")
    Call<Void> create(@Body Meal meal, @Header("Authorization") String jwt);

    @Headers({"Accept: application/json"})
    @PUT("meals/admin/{id}")
    Call<Void> update(@Path("id") Long id, @Body Meal meal, @Header("Authorization") String jwt);

    @Headers({"Accept: application/json"})
    @DELETE("meals/admin/{id}")
    Call<Void> delete(@Path("id") Long id, @Header("Authorization") String jwt);

}
