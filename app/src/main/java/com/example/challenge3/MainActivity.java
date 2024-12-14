package com.example.challenge3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;

import info.mqtt.android.service.MqttAndroidClient;

public class MainActivity extends AppCompatActivity {

    ConversationViewModel viewModel;
    DatabaseFunctions databaseFinal;
    MQTTHelper mqttHelper;
    private static final String CHATROOM_TOPIC = "chatroom1";
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize database helper and writable database
        databaseFinal = new DatabaseFunctions(this);
        viewModel = new ViewModelProvider(this).get(ConversationViewModel.class);
        mqttHelper = new MQTTHelper(this,"leo");
        mqttHelper.connect();

        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                Log.w("MQTT", "Completed: " + serverURI);

            }

            @Override
            public void connectionLost(Throwable cause) {
                Log.w("MQTT", "Failed: " + cause.getMessage());
                mqttHelper.connect();
            }

            /*
                Handles message arrive
                message should be:
                userReceiver_userSender_content_timestamp
                example:
                leo_miguel_here is a message_2024-12-14 12:39:47
             */
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.w("MQTT", "Message received!");
                Message newMessage = null;

                String[] parts = message.toString().split("_");
                Log.d("MQTT", "For user: " + parts[0].strip());
                Log.d("MQTT", "Current user: " + viewModel.getLoggedInUserId());
                if(parts[0].strip().matches(viewModel.getLoggedInUserId())){                    // Message is for current user
                    newMessage = new Message(0, new User(parts[1]), new User(parts[0]), parts[2], parts[3]);
                    /*createMessage(
                            parts[2],
                            parts[1],
                            parts[0],
                            parts[3]
                    );*/
                    updateChatDB(newMessage);
                    Log.d("MQTT", "Content: " + parts[2]);
                    Log.d("MQTT", "Timestamp: " + parts[3]);
                }
                else{
                    Log.d("MQTT", "Message is not for user");
                }

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });


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
        userId = sharedPreferences.getString("USER_ID", null);
        Log.d("MainActivity", "USER_ID from SharedPreferences: " + userId);

        // Load data from DB into ViewModel
        loadUserFromDB(); // Load existing users
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

            /* This is the chat List*/
            viewModel.setContactedUsers(databaseFinal.getUserChats(userId));
            showChatList();
        } else {
            Log.d("MainActivity", "No logged-in user found. Showing login form.");
            showUserForm();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mqttHelper != null) {
            mqttHelper.stop();
            Log.d("MQTT", "Disconnected from MQTT broker");
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
        connectToMqtt();
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

        if(!userExists(chat.getReceiver().getId()))
            databaseFinal.createUser(chat.getReceiver().getId());

        viewModel.setChattingWithUser(chat.getReceiver());
        viewModel.setCurrentChat(chat);

        findViewById(R.id.toolbar).setVisibility(View.GONE); // Show toolbar or customize as needed
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null) // Add to back stack if back navigation is needed
                .replace(R.id.main, ChatFragment.class, null)
                .commit();
    }


    public ConversationViewModel getViewModel() {
        return viewModel;
    }

    /* Load users from db to ViewModel */
    public void loadUserFromDB() {

        // Create random users if no users exist
        if (databaseFinal.getAllUsers().isEmpty()) {
            databaseFinal.randomUsers();
        }

        // Retrieve all users from the database and set them in the ViewModel
        viewModel.setUsers(databaseFinal.getAllUsers());
    }

    public void loadMessagesFromDB(){
        ArrayList<Message> al = databaseFinal.getAllMessages();
        viewModel.setMessages(databaseFinal.getAllMessages());
    }

    public boolean userExists(String username){
        return databaseFinal.userExists(username);
    }

    public boolean createMessage(String content, String sender_id, String receiver_id, String timestamp) throws Exception {
        mqttHelper.publishMessage(receiver_id + "_" + sender_id + "_" + content + "_" + timestamp);
        return databaseFinal.createMessage(content,sender_id,receiver_id);
    }

    public ArrayList<User> getUserWithChat(String userID){
        return databaseFinal.getUserChats(userID);
    }

    public boolean deleteChatBetweenUser(String user1, String user2){
        return databaseFinal.deleteChatBetweenUser(user1, user2);
    }

    public void updateChatDB(Message newMessage){
        //mqttHelper.publishMessage(newMessage.getReceiver().getId() + "_" + newMessage.getSender().getId() + "_" + newMessage.getContent() + "_" + newMessage.getTimestamp());
        databaseFinal.insertMessage(newMessage);
        loadMessagesFromDB();
    }

    public void addUserToDB(User newUser){
        databaseFinal.insertUser(newUser);
    }

    public void connectToMqtt(){
        Log.d("MQTT", "Attempting to connect to MQTT...");
    }

}

