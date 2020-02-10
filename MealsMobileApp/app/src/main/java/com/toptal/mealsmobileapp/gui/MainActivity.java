package com.toptal.mealsmobileapp.gui;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.toptal.mealsmobileapp.MealsApp;
import com.toptal.mealsmobileapp.R;
import com.toptal.mealsmobileapp.model.CurrentUser;
import com.toptal.mealsmobileapp.model.LoginRequest;
import com.toptal.mealsmobileapp.model.LoginResponse;
import com.toptal.mealsmobileapp.model.SignUpRequest;
import com.toptal.mealsmobileapp.model.User;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    private static final Gson gson = new GsonBuilder().create();

    private Menu menu;
    private LoginFragment loginFragment;
    private SignUpFragment signUpFragment;
    private SettingsFragment settingsFragment;
    private MealFragment mealFragment;
    private UserFragment userFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    @Override
    protected void onResume() {
        super.onResume();
        authenticate(null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_meals) {
            showMealsView();
            return true;
        }
        else if (id == R.id.action_manage) {
            showUsersView();
            return true;
        }
        else if (id == R.id.action_settings) {
            showSettings();
            return true;
        }
        else if (id == R.id.action_login_logout) {
            if (MealsApp.getCurrentUser(this) != null) {
                logoutActions();
            }
            showLoginFragment();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void authenticate(View view) {
        // coming from login view
        if (view != null && view.getId() == R.id.login_button) {
            // coming from login screen
            String username = ((EditText)findViewById(R.id.login_username)).getText().toString();
            String password = ((EditText)findViewById(R.id.login_password)).getText().toString();
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, R.string.username_or_password_empty, Toast.LENGTH_LONG).show();
            }
            else {
                loginViaApi(username, password, () -> {
                    ((EditText)findViewById(R.id.login_password)).setText("");
                    showMealsView();
                    hideKeyboard(view);
                }, () -> {});
            }
        }
        // at the start of application
        else {
            // first run
            CurrentUser currUser = MealsApp.getCurrentUser(this);
            if (currUser == null) {
                showLoginFragment();
            }
            // user prev logged in and their data stored
            else {
                loginViaApi(currUser.getUsername(),
                            currUser.getPassword(),
                            () -> {findViewById(R.id.retry_button).setVisibility(View.GONE);
                                   showMealsView();},
                            () -> findViewById(R.id.retry_button).setVisibility(View.VISIBLE));
            }
        }

    }


    public void signUp(View view) {
        String username = ((EditText)findViewById(R.id.login_username)).getText().toString();
        String password = ((EditText)findViewById(R.id.login_password)).getText().toString();
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.username_or_password_empty, Toast.LENGTH_LONG).show();
        }
        else {
            signUpViaApi(username, password, () -> {
                ((EditText)findViewById(R.id.login_password)).setText("");
                showMealsView();
                hideKeyboard(view);
            }, () -> {});
        }
    }


    public void showSettings() {
        if (settingsFragment == null) {
            settingsFragment = new SettingsFragment();
        }
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .addToBackStack(null)
                .replace(R.id.fragment_placeholder, settingsFragment)
                .commit();
    }


    public void closeSettings() {
        FragmentManager fm = getFragmentManager();
        fm.popBackStackImmediate();
        fm.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .remove(settingsFragment)
                .commit();
    }


    public void saveSettings(View view) {
        final Activity mainActivity = this;
        String caloriesStr = ((EditText)findViewById(R.id.max_daily_calories)).getText().toString();
        Float calories = Float.parseFloat(caloriesStr);
        CurrentUser currUser = MealsApp.getCurrentUser(this);
        if (calories.equals(currUser.getMaxDailyCalories())) {
            closeSettings();
            return;
        }
        MealsApp.getUserService(this)
                .updateMaxDailyCalories(calories, MealsApp.getCurrentUser(mainActivity).getJwt())
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.code() != HttpsURLConnection.HTTP_OK) {
                            Toast.makeText(mainActivity, R.string.error_save_settings, Toast.LENGTH_LONG).show();
                            return;
                        }
                        closeSettings();
                        currUser.setMaxDailyCalories(calories);
                        hideKeyboard(view);
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(mainActivity, R.string.server_unavailable, Toast.LENGTH_LONG).show();
                        MainActivity.this.showLoginFragment();
                    }
                });
    }


    public void showMealsView() {
        if (mealFragment == null) {
            mealFragment = new MealFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean("isAdminMode", false);
            bundle.putString("user", gson.toJson(MealsApp.getCurrentUser(this), User.class));
            mealFragment.setArguments(bundle);
        }
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .replace(R.id.fragment_placeholder, mealFragment)
                .addToBackStack(null)
                .commit();
    }


    public void showAdminMealsView(User user) {
        MealFragment mealFragment = new MealFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isAdminMode", true);
        bundle.putString("user", gson.toJson(user, User.class));
        mealFragment.setArguments(bundle);
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .replace(R.id.fragment_placeholder, mealFragment)
                .addToBackStack(null)
                .commit();
    }


    public void showUsersView() {
        if (userFragment == null) {
            userFragment = new UserFragment();
        }
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .replace(R.id.fragment_placeholder, userFragment)
                .addToBackStack(null)
                .commit();
    }


    public void showLoginFragment() {
        if (loginFragment == null) {
            loginFragment = new LoginFragment();
        }
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .replace(R.id.fragment_placeholder, loginFragment)
                .commit();
        findViewById(R.id.retry_button).setVisibility(View.GONE);
    }


    public void showSignUpFragment(View view) {
        if (signUpFragment == null) {
            signUpFragment = new SignUpFragment();
        }
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .replace(R.id.fragment_placeholder, signUpFragment)
                .commit();
    }


    public void loginViaApi(String username, String password, Runnable onSuccess, Runnable onFailure) {
        final Activity mainActivity = this;
        MealsApp.getAuthService(this)
                .login(new LoginRequest(username, password))
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        if (response.code() != HttpsURLConnection.HTTP_OK) {
                            Toast.makeText(mainActivity, R.string.wrong_credentials, Toast.LENGTH_LONG).show();
                            return;
                        }
                        LoginResponse loginResponse = response.body();
                        postLoginActions(loginResponse, username, password);
                        onSuccess.run();
                        menu.findItem(R.id.action_login_logout).setTitle(getString(R.string.action_logout));
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Toast.makeText(mainActivity, R.string.server_unavailable, Toast.LENGTH_LONG).show();
                        onFailure.run();
                    }
                });
    }

    public void signUpViaApi(String username, String password, Runnable onSuccess, Runnable onFailure) {
        final Activity mainActivity = this;
        MealsApp.getAuthService(this)
                .signUp(new SignUpRequest(username, password))
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        if (response.code() != HttpsURLConnection.HTTP_CREATED) {
                            Toast.makeText(mainActivity, R.string.username_taken, Toast.LENGTH_LONG).show();
                            return;
                        }
                        LoginResponse loginResponse = response.body();
                        postLoginActions(loginResponse, username, password);
                        onSuccess.run();
                        menu.findItem(R.id.action_login_logout).setTitle(getString(R.string.action_logout));
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Toast.makeText(mainActivity, R.string.server_unavailable, Toast.LENGTH_LONG).show();
                        onFailure.run();
                    }
                });
    }


    private void postLoginActions(LoginResponse loginResponse, String username, String password) {
        CurrentUser currUser = new CurrentUser(loginResponse.getId(),
                                               username,
                                               password,
                                               loginResponse.getRoles(),
                                               loginResponse.getMaxDailyCalories(),
                                               loginResponse.getJwt());
        MealsApp.saveCurrentUser(this, currUser);
        if (currUser.isAdmin() || currUser.isManager()) {
            menu.findItem(R.id.action_manage).setVisible(true);
        }
        else {
            menu.findItem(R.id.action_manage).setVisible(false);
        }
        menu.findItem(R.id.action_settings).setVisible(true);
        menu.findItem(R.id.action_meals).setVisible(true);
    }


    private void logoutActions() {
        MealsApp.removeCurrentUser(this);
        mealFragment = null;
        menu.findItem(R.id.action_login_logout).setTitle(getString(R.string.action_login));
        menu.findItem(R.id.action_meals).setVisible(false);
        menu.findItem(R.id.action_settings).setVisible(false);
        menu.findItem(R.id.action_manage).setVisible(false);
    }


    private void hideKeyboard(View view) {
        InputMethodManager inputManager = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

}
