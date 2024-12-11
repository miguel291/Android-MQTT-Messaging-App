package com.example.challenge3;

import static com.example.challenge3.DatabaseOpenHelper.COLUMN_CHAT_CONTENT;
import static com.example.challenge3.DatabaseOpenHelper.COLUMN_CHAT_RECEIVER;
import static com.example.challenge3.DatabaseOpenHelper.COLUMN_CHAT_SENDER;
import static com.example.challenge3.DatabaseOpenHelper.COLUMN_CHAT_TIMESTAMP;
import static com.example.challenge3.DatabaseOpenHelper.COLUMN_MESSAGE_ID;
import static com.example.challenge3.DatabaseOpenHelper.TABLE_MESSAGE;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.sql.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DatabaseFunctions {
    private final DatabaseOpenHelper dbHelper;

    // string as: 2021-03-24 16:48:05
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");


    public DatabaseFunctions(Context context){
        dbHelper = new DatabaseOpenHelper(context);
    }

    /*
        Insert the user in the database
     */
    public void loginUser(String username){
        // get db instance
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // create object
        ContentValues values = new ContentValues();
        values.put(DatabaseOpenHelper.COLUMN_USER_USERNAME, username);

        // insert user
        long value = db.insert(DatabaseOpenHelper.TABLE_USER, null,values);
        Log.i("DATAFINAL", ("User added: " + String.valueOf(value)));
    }

    /*
        Checks if user exists
     */
    public boolean userExists(String username){
        // get db instance
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // query for check user
        String query = "SELECT * FROM " + DatabaseOpenHelper.TABLE_USER + " WHERE " + DatabaseOpenHelper.COLUMN_USER_USERNAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(username)});

        // get number of results
        int nrRows = cursor.getCount();

        cursor.close();

        return (nrRows > 0);
    }

    public boolean createMessage(String content, String sender_id, String receiver_id){
        // get db instance
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // get timestamp
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String timestamp_str = sdf.format(timestamp);

        // create object
        ContentValues values = new ContentValues();
        values.put(DatabaseOpenHelper.COLUMN_CHAT_CONTENT, content);
        values.put(DatabaseOpenHelper.COLUMN_CHAT_SENDER, sender_id);
        values.put(COLUMN_CHAT_RECEIVER, receiver_id);
        values.put(DatabaseOpenHelper.COLUMN_CHAT_TIMESTAMP, timestamp_str);

        // insert user
        long value = db.insert(TABLE_MESSAGE, null,values);
        Log.i("DATAFINAL", ("Chat added: " + String.valueOf(value)));

        return value != -1;
    }


    public ArrayList<Message> dynamicChatCreation(String senderId, String receiverId){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_MESSAGE +
                " WHERE " + COLUMN_CHAT_SENDER + " = ? AND " + COLUMN_CHAT_RECEIVER + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(senderId), String.valueOf(receiverId)});
        ArrayList<Message> messages = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                int messageId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE_ID));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CHAT_CONTENT));
                String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CHAT_TIMESTAMP));

                Message message = new Message(messageId, new User(senderId), new User(receiverId), content, timestamp);

                messages.add(message);
                Log.i("DATAFINAL", ("Message added: " + message));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return messages;

    }


    // Retrieve all chats (only the users list) for a specific user
    public ArrayList<User> getChats(String loggedUser) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_MESSAGE + " WHERE " + COLUMN_CHAT_SENDER + " = ?" + " AND " + COLUMN_CHAT_RECEIVER + " = ?";

        ArrayList<User> usersToquery = new ArrayList<>();
        usersToquery = getAllUsersExcept(loggedUser); // get all users except me
        ArrayList<User> usersContacted = new ArrayList<>();

        for (int i = 0; i < usersToquery.size(); i++) {
            String user = usersToquery.get(i).getId();
            Cursor cursor = db.rawQuery(query, new String[]{loggedUser, user}); // user need to change (iterate list)


            if (cursor.moveToFirst()) {
                do {
                    User user = new User(senderId.equals(userId) ? receiverId : senderId);

                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        return users;
    }

    /* Get two users as input and retrieve all the messages between theme*/
    public ArrayList<Message> getMessages(String sender_id, String receiver_id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<Message> messages = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_MESSAGE + " WHERE " + COLUMN_CHAT_SENDER + " = ?" + " AND " + COLUMN_CHAT_RECEIVER + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(sender_id), String.valueOf(receiver_id)});

        if (cursor.moveToFirst()) {
            do {

                Message message = new Message(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE_ID)),
                        new User(sender_id),
                        new User(receiver_id), cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CHAT_CONTENT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CHAT_TIMESTAMP)));

                messages.add(message);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return messages;
    }

    // Retrieve all users except the logged-in user
    public ArrayList<User> getAllUsersExcept(String loggedUserId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM users WHERE user_id != ?";
        Cursor cursor = db.rawQuery(query, new String[]{loggedUserId});
        ArrayList<User> users = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                String userId = cursor.getString(cursor.getColumnIndexOrThrow("user_id"));
                users.add(new User(userId));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return users;
    }



    /* Insert 3 random users into the DB*/
    public void randomUsers(){
        // get db instance
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // create object
        ContentValues values = new ContentValues();
        values.put(DatabaseOpenHelper.COLUMN_USER_USERNAME, "user1");

        // insert user
        long value = db.insert(DatabaseOpenHelper.TABLE_USER, null,values);
        Log.i("DATAFINAL", ("User added: " + String.valueOf(value)));

        values = new ContentValues();
        values.put(DatabaseOpenHelper.COLUMN_USER_USERNAME, "user2");
        value = db.insert(DatabaseOpenHelper.TABLE_USER, null,values);

        Log.i("DATAFINAL", ("User added: " + String.valueOf(value)));

        values = new ContentValues();
        values.put(DatabaseOpenHelper.COLUMN_USER_USERNAME, "user3");
        value = db.insert(DatabaseOpenHelper.TABLE_USER, null,values);

        Log.i("DATAFINAL", ("User added: " + String.valueOf(value)));

    }

}
