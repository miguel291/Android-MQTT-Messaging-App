package com.example.challenge3;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

public class UsernameFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private EditText editTextId;
    MainActivity activity = (MainActivity) requireActivity();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_username, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbarUsername);
        AppCompatActivity activityCompat = (AppCompatActivity) getActivity();
        if (activityCompat != null) {
            activityCompat.setSupportActionBar(toolbar);
            activityCompat.getSupportActionBar().setTitle("Chat"); // Set toolbar title
        }

        // Username input and save button
        editTextId = view.findViewById(R.id.editTextId);
        Button loginBtn = view.findViewById(R.id.btnLogin);

        // Get app preferences
       sharedPreferences = requireContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);

        // Set the button's click listener
        loginBtn.setOnClickListener(v -> {
            String id = editTextId.getText().toString().trim(); // Get the string value
            if (isAlphanumeric(id)) {

                User newUser = null;
                if(activity.userExists(id)) {
                    newUser = activity.getViewModel().getUserById(id);
                }
                else{
                    newUser = new User(id);
                }
                saveUser(newUser);

                activity.showChatList(); // Switch fragment
                createToast("Welcome " + id);
            } else {
                editTextId.setError("Username must be an alphanumeric code");
            }
        });
        return view;
    }

    /*
        Check if is Alphanumeric ID
     */
    private boolean isAlphanumeric(String username) {
        return username.matches("^[a-zA-Z0-9]+$");
    }

    /*
        Save user to SharedPreferences
     */
    private void saveUser(User user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("USER_ID", user.getId());
        editor.apply();
        activity.getViewModel().setLoggedInUser(user);
        activity.addUserToDB(user);
    }

    /*
        Create a toast
     */
    private void createToast(CharSequence text){
        Context context = getContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
