
package com.example.challenge3;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ConversationViewModel viewModel;
    DictionaryOpenHelper dbHelper;
    SQLiteDatabase database;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Enable edge-to-edge display
        setContentView(R.layout.activity_main);

        // Initialize database helper and writable database
        dbHelper = new DictionaryOpenHelper(this);
        database = dbHelper.getWritableDatabase();

        // Set up the toolbar
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(ConversationViewModel.class);

        // Apply window insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Check if it's the first time the app is launched
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        boolean isFirstTime = sharedPreferences.getBoolean("IS_FIRST_TIME", true);

        if (isFirstTime) {
            // Show user form if the app is launched for the first time
            showUserForm();

            // Update shared preferences to indicate the app is no longer in the first launch
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("IS_FIRST_TIME", false);
            editor.apply();
        } else {
            // Show chat list if it's not the first launch
            showChatList();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            // Handle the settings action
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity, menu); // Inflate the menu only once
        return true;
    }

    public void showUserForm(){
        findViewById(R.id.toolbar).setVisibility(View.GONE);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main, UsernameFragment.class, null)
                .commit();
    }

    public void showChatList(){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main, ConversationListFragment.class, null)
                .commit();
    }

    public void showArduinoConf(){
        findViewById(R.id.toolbar).setVisibility(View.GONE);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main, ArduinoConfFragment.class, null)
                .commit();
    }

    public void showChatFragment(Chat chat) {
        findViewById(R.id.toolbar).setVisibility(View.VISIBLE); // Show toolbar or customize as needed
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main, ChatFragment.newInstance(chat))
                .addToBackStack(null) // Add to back stack if back navigation is needed
                .commit();
    }

}