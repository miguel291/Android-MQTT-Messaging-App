package com.example.challenge3;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class UsernameFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private EditText editTextUsername;
    private Button loginBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_username, container, false);

        // Username input and save button
        editTextUsername = view.findViewById(R.id.editTextId);
        loginBtn = view.findViewById(R.id.btnLogin);
        sharedPreferences = requireContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);

        // Set the button's click listener
        loginBtn.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            if (isAlphanumeric(username)) {
                saveUsername(username);

                // Directly call MainActivity's method
                MainActivity activity = (MainActivity) requireActivity();
                activity.showChatList(); // Switch fragment
            } else {
                editTextUsername.setError("Username must be an alphanumeric code");
            }
        });

        return view;
    }

    private boolean isAlphanumeric(String username) {
        return username.matches("^[a-zA-Z0-9]+$");
    }

    private void saveUsername(String username) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("USER_ID", username);
        editor.putBoolean("IS_FIRST_TIME", false);
        editor.apply();
        createToast("Welcome " + username);
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
