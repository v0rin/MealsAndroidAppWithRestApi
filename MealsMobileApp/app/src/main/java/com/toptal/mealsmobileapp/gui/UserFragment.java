package com.toptal.mealsmobileapp.gui;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.toptal.mealsmobileapp.MealsApp;
import com.toptal.mealsmobileapp.R;
import com.toptal.mealsmobileapp.model.User;
import com.toptal.mealsmobileapp.model.UserRoles;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserFragment extends ListFragment implements OnItemClickListener {

    private static final Double DEFAULT_CALORIES = 2500.0;
    private static final String DISPLAY_FAKE_PASSWORD = "A";
    private LayoutInflater inflater;
    private List<User> users = new ArrayList<>();
    private UserAdapter userAdapter;

    private User userToUpdate;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        View view = inflater.inflate(R.layout.users_fragment, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getUsers();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.getActivity().findViewById(R.id.add_user_button).setOnClickListener(v -> toggleAddUserForm());
        this.getActivity().findViewById(R.id.save_user_button).setOnClickListener(v -> addUser(v));
        this.getActivity().findViewById(R.id.update_user_button).setOnClickListener(v -> updateUser(v));

        userAdapter = new UserAdapter();
        setListAdapter(userAdapter);
        getListView().setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (MealsApp.getCurrentUser(this.getActivity()).isAdmin()) {
            ((MainActivity) this.getActivity()).showAdminMealsView(users.get(position));
        }
    }


    public void toggleAddUserForm() {
        Button btn = this.getActivity().findViewById(R.id.add_user_button);
        Activity activity = this.getActivity();
        if (btn.getText().equals(this.getActivity().getString(R.string.add))) {
            activity.findViewById(R.id.user_form).setVisibility(View.VISIBLE);
            activity.findViewById(R.id.save_user_button).setVisibility(View.VISIBLE);
            ((EditText)activity.findViewById(R.id.user_username_input)).setText("");
            ((EditText)activity.findViewById(R.id.user_password_input)).setText("");
            ((RadioButton)activity.findViewById(R.id.radio_role_user)).setChecked(true);
            ((EditText)activity.findViewById(R.id.user_max_daily_calories_input)).setText(Double.toString(DEFAULT_CALORIES));

            btn.setText(R.string.cancel);
        }
        else {
            activity.findViewById(R.id.save_user_button).setVisibility(View.GONE);
            activity.findViewById(R.id.update_user_button).setVisibility(View.GONE);
            activity.findViewById(R.id.user_form).setVisibility(View.GONE);
            btn.setText(R.string.add);
        }
    }


    public void getUsers() {
        final Activity activity = this.getActivity();

        MealsApp.getUserService(activity).getAll(MealsApp.getCurrentUser(activity).getJwt())
                .enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.code() != HttpsURLConnection.HTTP_OK) {
                    Toast.makeText(activity, R.string.retrieve_error, Toast.LENGTH_LONG).show();
                    return;
                }
                users = response.body();
                userAdapter.notifyDataSetInvalidated();
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(activity, R.string.server_unavailable, Toast.LENGTH_LONG).show();
                ((MainActivity)activity).showLoginFragment();
            }
        });
    }


    public void addUser(View view) {
        final User user = getAndValidateUserFromUserForm();
        if (user == null) {
            return;
        }

        final Activity activity = this.getActivity();
        MealsApp.getUserService(activity)
                .create(user, MealsApp.getCurrentUser(activity).getJwt())
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.code() != HttpsURLConnection.HTTP_CREATED) {
                            Toast.makeText(activity, R.string.create_error, Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(activity, R.string.create_success, Toast.LENGTH_LONG).show();
                            getUsers();
                            userAdapter.notifyDataSetInvalidated();
                            toggleAddUserForm();
                        }
                        hideKeyboard(view);
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(activity, R.string.server_unavailable, Toast.LENGTH_LONG).show();
                        ((MainActivity)activity).showLoginFragment();
                    }
                });
    }


    public void showUpdateUserForm(User user) {
        Button btn = this.getActivity().findViewById(R.id.add_user_button);
        if (btn.getText().equals(this.getActivity().getString(R.string.add))) {
            this.getActivity().findViewById(R.id.user_form).setVisibility(View.VISIBLE);
            this.getActivity().findViewById(R.id.update_user_button).setVisibility(View.VISIBLE);
            btn.setText(R.string.cancel);
            Activity activity = this.getActivity();
            ((EditText)activity.findViewById(R.id.user_username_input)).setText(user.getUsername());
            ((EditText)activity.findViewById(R.id.user_password_input)).setText(DISPLAY_FAKE_PASSWORD);

            if (user.getRoles().contains(UserRoles.ROLE_ADMIN)) {
                ((RadioButton)activity.findViewById(R.id.radio_role_admin)).setChecked(true);
            }
            else if (user.getRoles().contains(UserRoles.ROLE_MANAGER)) {
                ((RadioButton)activity.findViewById(R.id.radio_role_maanger)).setChecked(true);
            }
            else if (user.getRoles().contains(UserRoles.ROLE_USER)) {
                ((RadioButton)activity.findViewById(R.id.radio_role_user)).setChecked(true);
            }

            ((EditText)activity.findViewById(R.id.user_max_daily_calories_input)).setText(Float.toString(user.getMaxDailyCalories()));

            userToUpdate = user;
        }

    }


    public void updateUser(final View view) {
        final User updatedUser = getAndValidateUserFromUserForm();
        if (updatedUser == null) {
            return;
        }
        updatedUser.setId(userToUpdate.getId());

        final Activity activity = this.getActivity();

        MealsApp.getUserService(activity)
                .update(updatedUser.getId(), updatedUser, MealsApp.getCurrentUser(activity).getJwt())
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.code() != HttpsURLConnection.HTTP_OK) {
                            Toast.makeText(activity, R.string.update_error, Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(activity, R.string.update_success, Toast.LENGTH_LONG).show();
                            getUsers();
                            userAdapter.notifyDataSetInvalidated();
                            toggleAddUserForm();
                            userToUpdate = null;

                            // hide keyboard
                            hideKeyboard(view);
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(activity, R.string.server_unavailable, Toast.LENGTH_LONG).show();
                        ((MainActivity)activity).showLoginFragment();
                    }
                });
    }


    public void deleteUser(final User user) {
        final Activity activity = this.getActivity();
        MealsApp.getUserService(activity)
                .delete(user.getId(), MealsApp.getCurrentUser(activity).getJwt())
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.code() != HttpsURLConnection.HTTP_OK) {
                            Toast.makeText(activity, R.string.delete_error, Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(activity, R.string.delete_success, Toast.LENGTH_LONG).show();
                            getUsers();
                            userAdapter.notifyDataSetInvalidated();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(activity, R.string.server_unavailable, Toast.LENGTH_LONG).show();
                        ((MainActivity)activity).showLoginFragment();
                    }
                });
    }


    private User getAndValidateUserFromUserForm() {
        final Activity activity = this.getActivity();
        String username = ((EditText)activity.findViewById(R.id.user_username_input)).getText().toString();
        String password = ((EditText)activity.findViewById(R.id.user_password_input)).getText().toString();

        String roles = "";
        if (((RadioButton)activity.findViewById(R.id.radio_role_user)).isChecked()) {
            roles = UserRoles.ROLE_USER;
        }
        else if (((RadioButton)activity.findViewById(R.id.radio_role_maanger)).isChecked()) {
            roles = UserRoles.ROLE_MANAGER;
        }
        else if (((RadioButton)activity.findViewById(R.id.radio_role_admin)).isChecked()) {
            roles = UserRoles.ROLE_ADMIN;
        }

        String maxDailyCalories = ((EditText)activity.findViewById(R.id.user_max_daily_calories_input)).getText().toString();

        if (!password.equals(DISPLAY_FAKE_PASSWORD) && password.length() < 3) {
            Toast.makeText(activity, R.string.not_secure_password, Toast.LENGTH_LONG).show();
        }
        if (password.equals(DISPLAY_FAKE_PASSWORD)) {
            password = null;
        }

        if (username.isEmpty() || roles.isEmpty() || maxDailyCalories.isEmpty()) {
            Toast.makeText(activity, R.string.create_error, Toast.LENGTH_LONG).show();
            return null;
        }

        return new User(null, username, password, roles, Float.valueOf(maxDailyCalories));
    }


    private void hideKeyboard(View view) {
        InputMethodManager inputManager = (InputMethodManager)this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


    private class UserAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return users.size();
        }

        @Override
        public Object getItem(int position) {
            return users.get(position);
        }

        @Override
        public long getItemId(int position) {
            return users.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.user_item, parent, false);
            }

            User user = users.get(position);
            String role;
            if (user.getRoles().contains(UserRoles.ROLE_ADMIN)) {
                role = UserFragment.this.getActivity().getString(R.string.role_admin);
            }
            else if (user.getRoles().contains(UserRoles.ROLE_MANAGER)) {
                role = UserFragment.this.getActivity().getString(R.string.role_manager);
            }
            else {
                role = UserFragment.this.getActivity().getString(R.string.role_user);
            }
            ((TextView)convertView.findViewById(R.id.user_username)).setText(user.getUsername());
            ((TextView)convertView.findViewById(R.id.user_role)).setText(role);
            ((TextView)convertView.findViewById(R.id.user_max_daily_calories)).setText(user.getMaxDailyCalories() + " max daily calories");

            convertView.findViewById(R.id.edit_user_button).setOnClickListener(v -> showUpdateUserForm(user));
            convertView.findViewById(R.id.delete_user_button).setOnClickListener(v -> deleteUser(user));
            this.hasStableIds();
            return convertView;
        }
    }
}