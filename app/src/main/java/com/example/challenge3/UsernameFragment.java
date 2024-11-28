package com.example.challenge3;

import android.os.Bundle;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_username, container, false);

        // Set up the Toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbarUsername);
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);

        // Username input and save button
        EditText idEditText = view.findViewById(R.id.editTextId);
        Button loginBtn = view.findViewById(R.id.btnLogin);

        loginBtn.setOnClickListener(v -> {
            String username = idEditText.getText().toString().trim();
            /*if (!username.isEmpty()) {
                Toast.makeText(activity, "Username saved: " + username, Toast.LENGTH_SHORT).show();
                // Navigate to the next fragment or save username logic here
            } else {
                Toast.makeText(activity, "Please enter a username", Toast.LENGTH_SHORT).show();
            }*/
        });

        return view;
    }
}
