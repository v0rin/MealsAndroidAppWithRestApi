package com.toptal.mealsmobileapp.gui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.toptal.mealsmobileapp.MealsApp;
import com.toptal.mealsmobileapp.R;


public class SettingsFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ((EditText)view.findViewById(R.id.max_daily_calories)).setText(
                Float.toString(MealsApp.getCurrentUser(this.getActivity()).getMaxDailyCalories()));
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
