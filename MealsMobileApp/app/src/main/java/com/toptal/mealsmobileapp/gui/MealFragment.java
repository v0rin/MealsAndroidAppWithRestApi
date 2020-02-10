package com.toptal.mealsmobileapp.gui;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.toptal.mealsmobileapp.MealsApp;
import com.toptal.mealsmobileapp.R;
import com.toptal.mealsmobileapp.model.DateJsonSerializer;
import com.toptal.mealsmobileapp.model.Meal;
import com.toptal.mealsmobileapp.model.TimeJsonSerializer;
import com.toptal.mealsmobileapp.model.User;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MealFragment extends ListFragment {

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private static final Gson gson = new GsonBuilder().create();

    private LayoutInflater inflater;
    private List<Meal> meals = new ArrayList<>();
    private MealAdapter mealAdapter;

    private Meal mealToUpdate;


    private User user;
    private boolean isAdminMode;
    private View fragmentView;


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        this.fragmentView = inflater.inflate(R.layout.meals_fragment, container, false);

        return fragmentView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();
        this.isAdminMode = bundle.getBoolean("isAdminMode");
        if (isAdminMode) {
            this.user = gson.fromJson(bundle.getString("user"), User.class);
        }
        else {
            this.user = MealsApp.getCurrentUser(this.getActivity());
        }

        fragmentView.findViewById(R.id.filter_meals_button).setOnClickListener(v -> getMeals());

        fragmentView.findViewById(R.id.add_meal_button).setOnClickListener(v -> toggleAddMealForm());
        fragmentView.findViewById(R.id.save_meal_button).setOnClickListener(v -> addMeal());
        fragmentView.findViewById(R.id.update_meal_button).setOnClickListener(v -> updateMeal(v));

        EditText filterDateFromEditText = fragmentView.findViewById(R.id.meal_filter_date_from);
        filterDateFromEditText.setOnClickListener(v -> showDatePicker(v));

        EditText filterDateToEditText = fragmentView.findViewById(R.id.meal_filter_date_to);
        filterDateToEditText.setOnClickListener(v -> showDatePicker(v));

        fragmentView.findViewById(R.id.meal_filter_date_to).setOnClickListener(v -> showDatePicker(v));
        fragmentView.findViewById(R.id.meal_filter_time_from).setOnClickListener(v -> showTimePicker(v));
        fragmentView.findViewById(R.id.meal_filter_time_to).setOnClickListener(v -> showTimePicker(v));

        mealAdapter = new MealAdapter();
        setListAdapter(mealAdapter);
    }


    @Override
    public void onStart() {
        super.onStart();
        getMeals();
    }


    public void showDatePicker(View view) {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setTargetEditText((EditText)view);
        datePickerFragment.show(this.getActivity().getFragmentManager(), "datePicker");
    }


    public void showTimePicker(View view) {
        TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.setTargetEditText((EditText)view);
        timePickerFragment.show(this.getActivity().getFragmentManager(), "timePicker");
    }


    public void toggleAddMealForm() {
        Button btn = fragmentView.findViewById(R.id.add_meal_button);
        if (btn.getText().equals(this.getActivity().getString(R.string.add))) {
            fragmentView.findViewById(R.id.meal_form).setVisibility(View.VISIBLE);
            fragmentView.findViewById(R.id.save_meal_button).setVisibility(View.VISIBLE);

            ((EditText)fragmentView.findViewById(R.id.meal_date_input)).setText(DateJsonSerializer.dateToStr(new Date()));
            ((EditText)fragmentView.findViewById(R.id.meal_time_input)).setText(TimeJsonSerializer.timeToStr(new Time(System.currentTimeMillis())));

            btn.setText(R.string.cancel);
        }
        else {
            fragmentView.findViewById(R.id.save_meal_button).setVisibility(View.GONE);
            fragmentView.findViewById(R.id.update_meal_button).setVisibility(View.GONE);
            fragmentView.findViewById(R.id.meal_form).setVisibility(View.GONE);
            btn.setText(R.string.add);
        }
    }


    public void getMeals() {
        final Activity activity = this.getActivity();

        String startDate = ((EditText)fragmentView.findViewById(R.id.meal_filter_date_from)).getText().toString();
        String endDate = ((EditText)fragmentView.findViewById(R.id.meal_filter_date_to)).getText().toString();
        String startTime = ((EditText)fragmentView.findViewById(R.id.meal_filter_time_from)).getText().toString();
        String endTime = ((EditText)fragmentView.findViewById(R.id.meal_filter_time_to)).getText().toString();

        Call<List<Meal>> call;
        try {
            if (DateJsonSerializer.strToDate(startDate) == null ||
                    DateJsonSerializer.strToDate(endDate) == null ||
                    TimeJsonSerializer.strToTime(startTime) == null ||
                    TimeJsonSerializer.strToTime(endTime) == null) {
                if (isAdminMode) {
                    call = MealsApp.getAdminMealService(activity).all(user.getId(), MealsApp.getCurrentUser(activity).getJwt());
                }
                else {
                    call = MealsApp.getMealService(activity).all(MealsApp.getCurrentUser(activity).getJwt());
                }
            }
            else {
                if (isAdminMode) {
                    call = MealsApp.getAdminMealService(activity).filteredByDateAndTime(
                            user.getId(), startDate, endDate, startTime, endTime, MealsApp.getCurrentUser(activity).getJwt());
                }
                else {
                    call = MealsApp.getMealService(activity).filteredByDateAndTime(
                            startDate, endDate, startTime, endTime, MealsApp.getCurrentUser(activity).getJwt());
                }
            }
        }
        catch (Exception e) {
            Toast.makeText(activity, R.string.incorrect_filters, Toast.LENGTH_LONG).show();
            if (isAdminMode) {
                call = MealsApp.getAdminMealService(activity).all(user.getId(), MealsApp.getCurrentUser(activity).getJwt());
            }
            else {
                call = MealsApp.getMealService(activity).all(MealsApp.getCurrentUser(activity).getJwt());
            }
        }

        call.enqueue(new Callback<List<Meal>>() {
            @Override
            public void onResponse(Call<List<Meal>> call, Response<List<Meal>> response) {
                if (response.code() != HttpsURLConnection.HTTP_OK) {
                    Toast.makeText(activity, R.string.retrieve_error, Toast.LENGTH_LONG).show();
                    return;
                }
                meals = response.body();
                mealAdapter.notifyDataSetInvalidated();
            }

            @Override
            public void onFailure(Call<List<Meal>> call, Throwable t) {
                Toast.makeText(activity, R.string.server_unavailable, Toast.LENGTH_LONG).show();
                ((MainActivity)activity).showLoginFragment();
            }
        });
    }


    public void addMeal() {
        final Meal meal = getAndValidateMealFromMealForm();
        if (meal == null) {
            return;
        }

        final Activity activity = this.getActivity();
        Call<Void> call;
        if (isAdminMode) {
            meal.setUserId(user.getId());
            call = MealsApp.getAdminMealService(activity)
                    .create(meal, MealsApp.getCurrentUser(activity).getJwt());
        }
        else {
            call = MealsApp.getMealService(activity)
                    .create(meal, MealsApp.getCurrentUser(activity).getJwt());
        }

        call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.code() != HttpsURLConnection.HTTP_CREATED) {
                            Toast.makeText(activity, R.string.create_error, Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(activity, R.string.create_success, Toast.LENGTH_LONG).show();
                            getMeals();
                            mealAdapter.notifyDataSetInvalidated();
                            toggleAddMealForm();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(activity, R.string.server_unavailable, Toast.LENGTH_LONG).show();
                        ((MainActivity)activity).showLoginFragment();
                    }
                });
    }


    public void showUpdateMealForm(Meal meal) {
        Button btn = fragmentView.findViewById(R.id.add_meal_button);
        if (btn.getText().equals(this.getActivity().getString(R.string.add))) {
            fragmentView.findViewById(R.id.meal_form).setVisibility(View.VISIBLE);
            fragmentView.findViewById(R.id.update_meal_button).setVisibility(View.VISIBLE);
            btn.setText(R.string.cancel);
            ((EditText)fragmentView.findViewById(R.id.meal_date_input)).setText(DateJsonSerializer.dateToStr(meal.getDate()));
            ((EditText)fragmentView.findViewById(R.id.meal_time_input)).setText(TimeJsonSerializer.timeToStr(meal.getTime()));
            ((EditText)fragmentView.findViewById(R.id.meal_description_input)).setText(meal.getDescription());
            ((EditText)fragmentView.findViewById(R.id.meal_calories_input)).setText(Float.toString(meal.getCalories()));

            mealToUpdate = meal;
        }

    }


    public void updateMeal(final View view) {
        final Meal updatedMeal = getAndValidateMealFromMealForm();
        if (updatedMeal == null) {
            return;
        }
        updatedMeal.setId(mealToUpdate.getId());
        updatedMeal.setUserId(mealToUpdate.getUserId());

        final Activity activity = this.getActivity();

        Call<Void> call;
        if (isAdminMode) {
            call = MealsApp.getAdminMealService(activity)
                    .update(updatedMeal.getId(), updatedMeal, MealsApp.getCurrentUser(activity).getJwt());
        }
        else {
            call = MealsApp.getMealService(activity)
                    .update(updatedMeal.getId(), updatedMeal, MealsApp.getCurrentUser(activity).getJwt());
        }

        call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.code() != HttpsURLConnection.HTTP_OK) {
                            Toast.makeText(activity, R.string.update_error, Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(activity, R.string.update_success, Toast.LENGTH_LONG).show();
                            getMeals();
                            mealAdapter.notifyDataSetInvalidated();
                            toggleAddMealForm();
                            mealToUpdate = null;

                            // hide keyboard
                            InputMethodManager inputManager = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(activity, R.string.server_unavailable, Toast.LENGTH_LONG).show();
                        ((MainActivity)activity).showLoginFragment();
                    }
                });
    }


    public void deleteMeal(final Meal meal) {
        final Activity activity = this.getActivity();

        Call<Void> call;
        if (isAdminMode) {
            call = MealsApp.getAdminMealService(activity)
                    .delete(meal.getId(), MealsApp.getCurrentUser(activity).getJwt());
        }
        else {
            call = MealsApp.getMealService(activity)
                    .delete(meal.getId(), MealsApp.getCurrentUser(activity).getJwt());
        }

        call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.code() != HttpsURLConnection.HTTP_OK) {
                            Toast.makeText(activity, R.string.delete_error, Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(activity, R.string.delete_success, Toast.LENGTH_LONG).show();
                            getMeals();
                            mealAdapter.notifyDataSetInvalidated();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(activity, R.string.server_unavailable, Toast.LENGTH_LONG).show();
                        ((MainActivity)activity).showLoginFragment();
                    }
                });
    }


    private Meal getAndValidateMealFromMealForm() {
        final Activity activity = this.getActivity();
        String date = ((EditText)fragmentView.findViewById(R.id.meal_date_input)).getText().toString();
        String time = ((EditText)fragmentView.findViewById(R.id.meal_time_input)).getText().toString();
        String description = ((EditText)fragmentView.findViewById(R.id.meal_description_input)).getText().toString();
        String calories = ((EditText)fragmentView.findViewById(R.id.meal_calories_input)).getText().toString();

        if (calories.isEmpty() || description.isEmpty()) {
            Toast.makeText(activity, R.string.create_error, Toast.LENGTH_LONG).show();
            return null;
        }

        Meal meal;
        try {
            meal = new Meal(null, DateJsonSerializer.strToDate(date), TimeJsonSerializer.strToTime(time), description, Float.valueOf(calories), null, null);
        }
        catch (ParseException e) {
            Toast.makeText(activity, R.string.create_error, Toast.LENGTH_LONG).show();
            return null;
        }
        return meal;
    }


    private class MealAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return meals.size();
        }

        @Override
        public Object getItem(int position) {
            return meals.get(position);
        }

        @Override
        public long getItemId(int position) {
            return meals.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.meal_item, parent, false);
            }

            Meal meal = meals.get(position);

            ((TextView)convertView.findViewById(R.id.meal_description)).setText(meal.getDescription());
            ((TextView)convertView.findViewById(R.id.meal_date)).setText(format.format(meal.getDate()));
            ((TextView)convertView.findViewById(R.id.meal_time)).setText(meal.getTime().toString());
            ((TextView)convertView.findViewById(R.id.meal_calories)).setText(meal.getCalories().toString());

            convertView.findViewById(R.id.edit_meal_button).setOnClickListener(v -> showUpdateMealForm(meal));
            convertView.findViewById(R.id.delete_meal_button).setOnClickListener(v -> deleteMeal(meal));

            if (meal.getCaloriesForTheDay() > user.getMaxDailyCalories()) {
                convertView.findViewById(R.id.meal_item_layout).setBackgroundColor(Color.parseColor("#fc035e"));
            }
            else {
                convertView.findViewById(R.id.meal_item_layout).setBackgroundColor(Color.parseColor("#04dba2"));
            }

            return convertView;
        }
    }
}