package com.toptal.mealsmobileapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.toptal.mealsmobileapp.model.CurrentUser;

import java.util.stream.Collectors;

public class StoredState {
    public static final String SHARED_PREFS_KEY = "com.toptal.mealsmobileapp";
    public static final String CURR_USER_ID_KEY = "CURR_USER_ID_KEY";
    public static final String CURR_USER_USERNAME_KEY = "CURR_USER_USERNAME_KEY";
    public static final String CURR_USER_PASSWORD_KEY = "CURR_USER_PASSWORD_KEY";
    public static final String CURR_USER_ROLES_KEY = "CURR_USER_ROLES_KEY";
    public static final String CURR_USER_MAX_DAILY_CALORIES_KEY = "CURR_USER_MAX_DAILY_CALORIES_KEY";
    public static final String CURR_USER_JWT_KEY = "CURR_USER_JWT_KEY";

    public static CurrentUser getCurrentUser(Activity activity) {
        SharedPreferences preferences = activity.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);

        String username = preferences.getString(CURR_USER_USERNAME_KEY, null);
        if (username == null) {
            return null;
        }

        int id = preferences.getInt(CURR_USER_ID_KEY, -1);
        String password = preferences.getString(CURR_USER_PASSWORD_KEY, null);
        String roles = preferences.getString(CURR_USER_ROLES_KEY, null);
        float maxDailyCalories = preferences.getFloat(CURR_USER_MAX_DAILY_CALORIES_KEY, -1);
        String jwt = preferences.getString(CURR_USER_JWT_KEY, null);

        return new CurrentUser(id, username, password, roles, maxDailyCalories, jwt);
    }

    public static void saveCurrentUser(Activity activity, CurrentUser user) {
        SharedPreferences preferences = activity.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        preferences.edit()
                .putString(CURR_USER_USERNAME_KEY, user.getUsername())
                .putInt(CURR_USER_ID_KEY, user.getId())
                .putString(CURR_USER_PASSWORD_KEY, user.getPassword())
                .putString(CURR_USER_ROLES_KEY, user.getRoles())
                .putFloat(CURR_USER_MAX_DAILY_CALORIES_KEY, user.getMaxDailyCalories())
                .putString(CURR_USER_JWT_KEY, user.getJwt())
                .commit();

    }

    public static void removeCurrentUser(Activity activity) {
        SharedPreferences preferences = activity.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        preferences.edit()
                .putString(CURR_USER_USERNAME_KEY, null)
                .putInt(CURR_USER_ID_KEY, -1)
                .putString(CURR_USER_PASSWORD_KEY, null)
                .putString(CURR_USER_ROLES_KEY, null)
                .putFloat(CURR_USER_MAX_DAILY_CALORIES_KEY, -1)
                .putString(CURR_USER_JWT_KEY, null)
                .commit();
    }
}
