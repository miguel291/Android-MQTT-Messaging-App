package com.example.challenge3;

import static android.icu.text.ListFormatter.Type.AND;
import static android.icu.text.ListFormatter.Type.OR;
import static com.example.challenge3.DatabaseOpenHelper.COLUMN_CHAT_CONTENT;
import static com.example.challenge3.DatabaseOpenHelper.COLUMN_CHAT_RECEIVER;
import static com.example.challenge3.DatabaseOpenHelper.COLUMN_CHAT_SENDER;
import static com.example.challenge3.DatabaseOpenHelper.COLUMN_CHAT_TIMESTAMP;
import static com.example.challenge3.DatabaseOpenHelper.COLUMN_MESSAGE_ID;
import static com.example.challenge3.DatabaseOpenHelper.TABLE_MESSAGE;
import static com.example.challenge3.DatabaseOpenHelper.TABLE_USER;

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



    /* Retrieve all the messages exchanged between sender_id and receiver_id */
    public ArrayList<Message> dynamicChatCreation(String senderId, String receiverId){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_MESSAGE +
                " WHERE " + COLUMN_CHAT_SENDER + " = ? AND " + COLUMN_CHAT_RECEIVER +
                " = ? OR " + COLUMN_CHAT_RECEIVER + " = ? AND " + COLUMN_CHAT_SENDER + " = ?" ;
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(senderId), String.valueOf(receiverId)});
        ArrayList<Message> messages = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                int messageId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE_ID));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CHAT_CONTENT));
                String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CHAT_TIMESTAMP));

                Message message = new Message(messageId, new User(senderId), new User(receiverId), content, timestamp);

                messages.add(message);
                Log.i("DATAFINAL", ("Message retrieved: " + message));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return messages;
    }


    // Retrieve all chats (only the users list) for a specific user (the logged one)
    public ArrayList<User> getChats(String loggedUser) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Query to fetch distinct users that have at least one message exchange with the logged user.
        String query = "SELECT DISTINCT CASE WHEN " + COLUMN_CHAT_SENDER + " = ? THEN " + COLUMN_CHAT_RECEIVER
                + " ELSE " + COLUMN_CHAT_SENDER
                + " END AS contact FROM " + TABLE_MESSAGE
                + " WHERE " + COLUMN_CHAT_SENDER + " = ? OR " + COLUMN_CHAT_RECEIVER + " = ?;";

        ArrayList<User> usersContacted = new ArrayList<>();
        Cursor cursor = db.rawQuery(query, new String[] { loggedUser, loggedUser, loggedUser });

        if (cursor.moveToFirst()) {
            do {
                String contactId = cursor.getString(cursor.getColumnIndexOrThrow("contact"));
                // Create a new User object. Adjust the User constructor as needed.
                usersContacted.add(new User(contactId));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        // At this point, usersContacted is already distinct due to the DISTINCT keyword in the query.
        return usersContacted;
    }


    /* Retrieve all messages where user_id is sender or receiver */
    public ArrayList<Message> getMessages(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<Message> messages = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_MESSAGE + " WHERE " + COLUMN_CHAT_SENDER + " = ?" + " OR " + COLUMN_CHAT_RECEIVER + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(username)});
        if (cursor.moveToFirst()) {
            do {
                Message message = new Message(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE_ID)),
                        new User(username),
                        new User(username), cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CHAT_CONTENT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CHAT_TIMESTAMP)));
                messages.add(message);

            }while(cursor.moveToNext());
        }
        return messages;
    }


    /* Get two users as input and retrieve all the messages between them*/
    public ArrayList<Message> getMessagesBetweenTwoUsers(String sender_id, String receiver_id) {
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
    public ArrayList<User> getAllUsersExcept(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USER + " WHERE username != ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});
        ArrayList<User> users = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                String userId = cursor.getString(cursor.getColumnIndexOrThrow("username"));
                users.add(new User(userId));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return users;
    }



    public void insertUsers(ArrayList<User> usersToInsert){
        // get db instance
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for (User user : usersToInsert) {
            // create object
            ContentValues values = new ContentValues();
            values.put(DatabaseOpenHelper.COLUMN_USER_USERNAME, user.getId());
            // insert user
            long value = db.insert(DatabaseOpenHelper.TABLE_USER, null, values);
            Log.i("DATAFINAL", ("User added: " + String.valueOf(value)));
        }
    }

    public ArrayList<User> getAllUsers(){
        // get db instance
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USER;
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<User> users = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                String userId = cursor.getString(cursor.getColumnIndexOrThrow("username"));
                users.add(new User(userId));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return users;
    }

    public void insertUser(User user){
        // get db instance
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // create object
        ContentValues values = new ContentValues();
        values.put(DatabaseOpenHelper.COLUMN_USER_USERNAME, user.getId());
        // insert user
        long value = db.insert(DatabaseOpenHelper.TABLE_USER, null,values);
        Log.i("DATAFINAL", ("User added: " + String.valueOf(value)));

    }

    public ArrayList<Message> getAllMessages() {
        // get db instance
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_MESSAGE ;
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<Message> messages = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                int messageId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String senderId = cursor.getString(cursor.getColumnIndexOrThrow("sender"));
                String receiverId = cursor.getString(cursor.getColumnIndexOrThrow("receiver"));
                String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
                String timestamp = cursor.getString(cursor.getColumnIndexOrThrow("sent_timestamp"));

                messages.add(new Message(messageId, new User(senderId), new User(receiverId), content, timestamp));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return messages;
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

    public void randomMessages(){
        // NEED IMPLEMENTATION
    }
}
