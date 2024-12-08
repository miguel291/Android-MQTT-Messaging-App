
package com.example.challenge3;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

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
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(ConversationViewModel.class);

        // Set up the toolbar
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        // Apply window insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Check if it's the first time the app is launched
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        boolean isFirstTime = sharedPreferences.getBoolean("IS_FIRST_TIME", true);

        // Check if the user is already logged in
        String userId = sharedPreferences.getString("USER_ID", null);

        if (userId != null) {
            dbLoadingIntoObject(); // Load chats for the logged-in user
        }

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
        findViewById(R.id.toolbar).setVisibility(View.GONE);
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
        findViewById(R.id.toolbar).setVisibility(View.GONE); // Show toolbar or customize as needed
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main, ChatFragment.newInstance(chat))
                .addToBackStack(null) // Add to back stack if back navigation is needed
                .commit();
    }

    public ConversationViewModel getViewModel() {
        return viewModel;
    }

    public void dbLoadingIntoObject() {
        User loggedInUser = viewModel.getLoggedInUser();

        if (loggedInUser != null) {
            try {
                ArrayList<Chat> chatsFromDb = dbHelper.getChats(loggedInUser.getId());
                viewModel.setChats(chatsFromDb);
            } catch (Exception e) {
                Toast.makeText(this, "Error loading chats: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }

        // Initialize the database helper
        DictionaryOpenHelper dbHelper = new DictionaryOpenHelper(this);

        // Insert fake data into the database and load it into LiveData
        Chat chat = new Chat(1, new User("Sender1"), new User("Sender2"));

        // Verify the messages are loaded into LiveData
        chat.getMessagesLiveData().observe(this, messages -> {
            Log.d("MainActivity", "Loaded messages: " + messages.size());
            for (Message message : messages) {
                Log.d("MainActivity", "Message: " + message.getContent());
            }
        });
    }


    public void updateDb(){

    }

    public User getLoggedUser() {
        return viewModel.getLoggedInUser();
    }

    public void setLoggedInUser() {
        if (viewModel != null) {
            SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
            String userId = sharedPreferences.getString("USER_ID", null);
            if (userId != null) {
                /*User loggedInUser = dbHelper.getUserById(userId);
                viewModel.setLoggedInUser(loggedInUser);*/
            }
        }
        else{
            Log.i("ERROR", "Logged in user not found, DB loading failed");
        }
    }
}

