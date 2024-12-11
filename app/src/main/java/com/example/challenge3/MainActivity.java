package com.example.challenge3;

import android.content.SharedPreferences;
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

public class MainActivity extends AppCompatActivity {

    ConversationViewModel viewModel;
    DatabaseFunctions databaseFinal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        databaseFinal = new DatabaseFunctions(this);
        viewModel = new ViewModelProvider(this).get(ConversationViewModel.class);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        View rootView = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            Insets systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(systemInsets.left, systemInsets.top, systemInsets.right, systemInsets.bottom);
            return insets;
        });

        // Check if the user is already logged in
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        String userId = sharedPreferences.getString("USER_ID", null);
        Log.d("MainActivity", "USER_ID from SharedPreferences: " + userId);

        // Load data from DB into ViewModel
        loadUserFromDB(userId); // Load existing users
        loadMessagesFromDB(); // Messages should not depend on logged user

        if (userId != null) {
            User loggedUser = viewModel.getUserById(userId);

            if(loggedUser == null) {
                loggedUser = new User(userId);
                viewModel.addUser(loggedUser);
            }

            if (!databaseFinal.userExists(userId)) {
                databaseFinal.insertUser(loggedUser);
            }

            viewModel.setLoggedInUser(loggedUser);
            Log.d("MainActivity", "Logged-in user: " + loggedUser.getId());

            viewModel.setContactedUsers(databaseFinal.getChats(userId));
            showChatList();
        } else {
            Log.d("MainActivity", "No logged-in user found. Showing login form.");
            showUserForm();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity, menu); // Inflate the menu only once
        return true;
    }

    /*
        User login page
     */
    public void showUserForm(){
        findViewById(R.id.toolbar).setVisibility(View.GONE);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main, UsernameFragment.class, null)
                .commit();
    }

    /*
        Chat list page
    */
    public void showChatList(){
        findViewById(R.id.toolbar).setVisibility(View.GONE);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main, ConversationListFragment.class, null)
                .commit();
    }

    /*
        Arduino Configuration page
    */
    public void showArduinoConf(){
        findViewById(R.id.toolbar).setVisibility(View.GONE);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main, ArduinoConfFragment.class, null)
                .commit();
    }

    /*
        Chat page
    */
    public void showChatFragment(Chat chat) {
        databaseFinal.createUser(chat.getReceiver().getId());
        viewModel.setChattingWithUser(chat.getReceiver());
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

    /* Load users from db to ViewModel */
    public void loadUserFromDB(String userId) {

        // Create random users if no users exist
        if (databaseFinal.getAllUsers().isEmpty()) {
            databaseFinal.randomUsers();
        }

        // Retrieve all users from the database and set them in the ViewModel
        viewModel.setUsers(databaseFinal.getAllUsers());
    }

    public void loadMessagesFromDB(){
        viewModel.setMessages(databaseFinal.getAllMessages());
    }


    public boolean userExists(String username){
        return databaseFinal.userExists(username);
    }

    public User getUserById(String id){
        return viewModel.getUserById(id);
    }

    public boolean createMessage(String content, String sender_id, String receiver_id){
        return databaseFinal.createMessage(content,sender_id,receiver_id);
    }
}

