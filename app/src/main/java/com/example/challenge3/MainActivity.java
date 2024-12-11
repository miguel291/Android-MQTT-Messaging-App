package com.example.challenge3;

import android.content.Context;
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
    SQLiteDatabase database;
    DatabaseFunctions databaseFinal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this); // Enable edge-to-edge display
        setContentView(R.layout.activity_main);

        // Initialize database helper and writable database
        databaseFinal = new DatabaseFunctions(this);

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

        /* Load data from DB to objects (viewModel) */
        loadUserFromDB();
        loadMessagesFromDB();

        // Check if the user is already logged in, if true gets its id
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        String userId = sharedPreferences.getString("USER_ID", null);

        // if there is already a user id in the preferences
        if (userId != null) {
            User newUser = new User(userId);
            viewModel.addUser(newUser);
            databaseFinal.insertUser(newUser);
            viewModel.setLoggedInUser(newUser);

            // Load Chats for the logged user
            databaseFinal.getChats(newUser.getId());

            createToast("Welcome " + userId);

            showChatList();
        } else{
            // Show user form if the app is launched for the first time
            createToast("Welcome, please login (insert your alphanumeric code)");
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

    /* Load users from db to viewmodel */
    public void loadUserFromDB(){
        databaseFinal.randomUsers(); // create random users
        viewModel.setUsers(databaseFinal.getAllUsers());
    }

    public void loadMessagesFromDB(){
        databaseFinal.randomMessages(); // create random messages
        viewModel.setMessages(databaseFinal.getAllMessages());
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

    private void createToast(CharSequence text){
        Context context = this.getBaseContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

   /* public void saveUser(User user){
        // Load
        viewModel.setUsers(databaseFinal.getAllUsers());
        viewModel.addUser(user);
        viewModel.setLoggedInUser(user);

        Log.i("DEBUG", "Users loaded: " + viewModel.getUsers().toString());
    }*/

    public boolean userExists(String username){
        return databaseFinal.userExists(username);
    }

    public User getUserById(String id){
        return viewModel.getUserById(id);
    }

    public void dynamicChatCreation(){

    }

    public boolean createMessage(String content, String sender_id, String receiver_id){
        return databaseFinal.createMessage(content,sender_id,receiver_id);
    }
}

