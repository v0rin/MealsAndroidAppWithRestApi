package com.toptal.mealsmobileapp;

import android.app.Activity;
import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.toptal.mealsmobileapp.model.CurrentUser;
import com.toptal.mealsmobileapp.model.DateJsonSerializer;
import com.toptal.mealsmobileapp.model.TimeJsonSerializer;
import com.toptal.mealsmobileapp.restservice.AdminMealService;
import com.toptal.mealsmobileapp.restservice.MealService;
import com.toptal.mealsmobileapp.restservice.AuthService;
import com.toptal.mealsmobileapp.restservice.UserService;

import java.util.Date;
import java.sql.Time;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MealsApp extends Application {

    private AuthService authService;
    private UserService userService;
    private MealService mealService;
    private AdminMealService adminMealService;

    private CurrentUser currentUser;

    public static MealsApp getInstance(Activity activity) {
        return (MealsApp)activity.getApplication();
    }

    public static CurrentUser getCurrentUser(Activity activity) {
        return getInstance(activity).currentUser != null ?
                getInstance(activity).currentUser : StoredState.getCurrentUser(activity);
    }

    public static void saveCurrentUser(Activity activity, CurrentUser user) {
        StoredState.saveCurrentUser(activity, user);
        getInstance(activity).currentUser = user;
    }

    public static void removeCurrentUser(Activity activity) {
        StoredState.removeCurrentUser(activity);
        getInstance(activity).currentUser = null;
    }

    public static AuthService getAuthService(Activity activity) {
        return getInstance(activity).getAuthService();
    }

    public static UserService getUserService(Activity activity) {
        return getInstance(activity).getUserService();
    }

    public static MealService getMealService(Activity activity) {
        return getInstance(activity).getMealService();
    }

    public static AdminMealService getAdminMealService(Activity activity) {
        return getInstance(activity).getAdminMealService();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        OkHttpClient httpClient = new OkHttpClient
                .Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateJsonSerializer())
                .registerTypeAdapter(Time.class, new TimeJsonSerializer())
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient)
                .baseUrl(Config.REST_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        authService = retrofit.create(AuthService.class);
        userService = retrofit.create(UserService.class);
        mealService = retrofit.create(MealService.class);
        adminMealService = retrofit.create(AdminMealService.class);
    }

    public AuthService getAuthService() {
        return authService;
    }

    public UserService getUserService() {
        return userService;
    }

    public MealService getMealService() {
        return mealService;
    }

    public AdminMealService getAdminMealService() {
        return adminMealService;
    }
}
