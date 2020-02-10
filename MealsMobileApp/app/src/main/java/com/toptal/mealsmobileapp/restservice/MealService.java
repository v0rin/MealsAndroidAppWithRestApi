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

public interface MealService {

    @Headers({"Accept: application/json"})
    @GET("meals")
    Call<List<Meal>> all(@Header("Authorization") String jwt);

    @Headers({"Accept: application/json"})
    @GET("meals?")
    Call<List<Meal>> filteredByDateAndTime(@Query("startDate") String startDate,
                                           @Query("endDate") String endDate,
                                           @Query("startTime") String startTime,
                                           @Query("endTime") String endTime,
                                           @Header("Authorization") String jwt);

    @Headers({"Accept: application/json"})
    @POST("meals")
    Call<Void> create(@Body Meal meal, @Header("Authorization") String jwt);

    @Headers({"Accept: application/json"})
    @PUT("meals/{id}")
    Call<Void> update(@Path("id") Long id, @Body Meal meal, @Header("Authorization") String jwt);

    @Headers({"Accept: application/json"})
    @DELETE("meals/{id}")
    Call<Void> delete(@Path("id") Long id, @Header("Authorization") String jwt);

}
