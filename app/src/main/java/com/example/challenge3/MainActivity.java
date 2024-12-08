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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        dbHelper = new DictionaryOpenHelper(this);
        database = dbHelper.getWritableDatabase();

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        viewModel = new ViewModelProvider(this).get(ConversationViewModel.class);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Check if it's the first time the app is launched
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        boolean isFirstTime = sharedPreferences.getBoolean("IS_FIRST_TIME", true);

        if (isFirstTime) {
            showUserForm();
        } else {
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

    public void insertChat(String senderId, String receiverId, String message, String timestamp) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DictionaryOpenHelper.COLUMN_SENDER_ID, senderId);
        contentValues.put(DictionaryOpenHelper.COLUMN_RECEIVER_ID, receiverId);
        contentValues.put(DictionaryOpenHelper.COLUMN_MESSAGE, message);
        contentValues.put(DictionaryOpenHelper.COLUMN_TIMESTAMP, timestamp);
        database.insert(DictionaryOpenHelper.TABLE_CHAT, null, contentValues);
    }

    public ArrayList<Chat> getAllChats() {
        ArrayList<Chat> chats = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = database.rawQuery("SELECT * FROM " + DictionaryOpenHelper.TABLE_CHAT, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // Use getColumnIndexOrThrow to ensure the column exists
                    int senderIdIndex = cursor.getColumnIndexOrThrow(DictionaryOpenHelper.COLUMN_SENDER_ID);
                    int receiverIdIndex = cursor.getColumnIndexOrThrow(DictionaryOpenHelper.COLUMN_RECEIVER_ID);
                    int messageIndex = cursor.getColumnIndexOrThrow(DictionaryOpenHelper.COLUMN_MESSAGE);
                    int timestampIndex = cursor.getColumnIndexOrThrow(DictionaryOpenHelper.COLUMN_TIMESTAMP);

                    String senderId = cursor.getString(senderIdIndex);
                    String receiverId = cursor.getString(receiverIdIndex);
                    String message = cursor.getString(messageIndex);
                    String timestamp = cursor.getString(timestampIndex);

                    chats.add(new Chat(new User(senderId), new User(receiverId)));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log exception
        } finally {
            if (cursor != null) {
                cursor.close(); // Ensure the cursor is closed
            }
        }
        return chats;
    }



}