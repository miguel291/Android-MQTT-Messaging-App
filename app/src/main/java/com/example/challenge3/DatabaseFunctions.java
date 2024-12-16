package com.example.challenge3;

import static com.example.challenge3.DatabaseOpenHelper.COLUMN_CHAT_CONTENT;
import static com.example.challenge3.DatabaseOpenHelper.COLUMN_CHAT_RECEIVER;
import static com.example.challenge3.DatabaseOpenHelper.COLUMN_CHAT_SENDER;
import static com.example.challenge3.DatabaseOpenHelper.COLUMN_CHAT_TIMESTAMP;
import static com.example.challenge3.DatabaseOpenHelper.COLUMN_MESSAGE_ID;
import static com.example.challenge3.DatabaseOpenHelper.COLUMN_USER_USERNAME;
import static com.example.challenge3.DatabaseOpenHelper.TABLE_MESSAGE;
import static com.example.challenge3.DatabaseOpenHelper.TABLE_USER;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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
    public void createUser(String username){
        // get db instance
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // create object
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_USERNAME, username);

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
        String query = "SELECT * FROM " + DatabaseOpenHelper.TABLE_USER + " WHERE " + COLUMN_USER_USERNAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(username)});

        // get number of results
        int nrRows = cursor.getCount();

        cursor.close();

        return (nrRows > 0);
    }

    public boolean createMessage(String content, String senderId, String receiverId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if (!userExists(senderId)) {
            createUser(senderId);
            Log.i("DatabaseFunctions", "External sender created!");
        }
        if (!userExists(receiverId)) {
            createUser(receiverId);
            Log.i("DatabaseFunctions", "External receiver created!");
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_CHAT_CONTENT, content);
        values.put(COLUMN_CHAT_SENDER, senderId);
        values.put(COLUMN_CHAT_RECEIVER, receiverId);
        values.put(COLUMN_CHAT_TIMESTAMP, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));

        //Log.i("DATAFINAL", ("Message added: " + String.valueOf(result) + " content: " + content));

        //return result != -1;
        return true;
    }


    public void insertMessage(Message messageToInsert){
        // get db instance
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if (!userExists(messageToInsert.getSender().getId()) || !userExists(messageToInsert.getReceiver().getId())) {
            if(!userExists(messageToInsert.getSender().getId())){
                createUser(messageToInsert.getSender().getId());
                Log.i("DatabaseFunctions", "External sender created!");

            }else{
                Log.e("DatabaseFunctions", "Receiver does not exist in the database.");
            }
        }
        // create object
        ContentValues values = new ContentValues();
        values.put(COLUMN_CHAT_SENDER, messageToInsert.getSender().getId());
        values.put(COLUMN_CHAT_RECEIVER, messageToInsert.getReceiver().getId());
        values.put(COLUMN_CHAT_CONTENT, messageToInsert.getContent());
        values.put(COLUMN_CHAT_TIMESTAMP, messageToInsert.getTimestamp());

        // insert user
        long value = db.insert(TABLE_MESSAGE, null, values);
        Log.i("DATAFINAL", ("new message: " + value));
        Log.i("DATAFINAL", ("Message added: " + String.valueOf(value) + " content: " + messageToInsert.getContent()));

    }


    /*
        Delete all the messages between two provided users
     */
    public boolean deleteChatBetweenUser(String user1, String user2){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = COLUMN_CHAT_SENDER + " = ? AND " + COLUMN_CHAT_RECEIVER +
                " = ? OR " + COLUMN_CHAT_SENDER + " = ? AND " + COLUMN_CHAT_RECEIVER + " = ?" ;
        int result = db.delete(TABLE_MESSAGE,query,new String[]{String.valueOf(user1), String.valueOf(user2),
                String.valueOf(user2), String.valueOf(user1)});

        return true;
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
    // Key changes in database query consistency.
    /*public ArrayList<User> getUserChats(String loggedUserId) {
        ArrayList<User> contactedUsers = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            // Ottieni un'istanza leggibile del database
            db = dbHelper.getReadableDatabase();

            // Query SQL per determinare i contatti unici
            String query = "SELECT DISTINCT CASE " +
                    "WHEN " + COLUMN_CHAT_SENDER + " = ? THEN " + COLUMN_CHAT_RECEIVER + " " +
                    "WHEN " + COLUMN_CHAT_RECEIVER + " = ? THEN " + COLUMN_CHAT_SENDER + " " +
                    "END AS contact " +
                    "FROM " + TABLE_MESSAGE + " " +
                    "WHERE " + COLUMN_CHAT_SENDER + " = ? OR " + COLUMN_CHAT_RECEIVER + " = ?";

            // Esegui la query
            cursor = db.rawQuery(query, new String[]{loggedUserId, loggedUserId, loggedUserId, loggedUserId});

            // Itera sui risultati
            if (cursor.moveToFirst()) {
                do {
                    String contactId = cursor.getString(cursor.getColumnIndexOrThrow("contact"));
                    contactedUsers.add(new User(contactId));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseError", "Error retrieving user chats: ", e);
        } finally {
            // Chiudi il cursore e il database se non sono nulli
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return contactedUsers;
    }*/

    public void debugDatabase() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MESSAGE, null);
        if (cursor.moveToFirst()) {
            do {
                Log.d("DatabaseDebug", "SENDER: " + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CHAT_SENDER)) +
                        ", RECEIVER: " + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CHAT_RECEIVER)));
            } while (cursor.moveToNext());
        } else {
            Log.d("DatabaseDebug", "No rows in TABLE_MESSAGE");
        }
        cursor.close();
    }

    public void tableExists() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        if (cursor.moveToFirst()) {
            do {
                Log.d("DatabaseDebug", "Table: " + cursor.getString(0));
            } while (cursor.moveToNext());
        } else {
            Log.d("DatabaseDebug", "No tables found in the database.");
        }
        cursor.close();
    }


    public ArrayList<User> getUserChats(String loggedUserId) {
        ArrayList<User> contactedUsers = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT DISTINCT " + COLUMN_CHAT_RECEIVER + " AS contact " +
                "FROM " + TABLE_MESSAGE + " WHERE " + COLUMN_CHAT_SENDER + " = ? " +
                "UNION " +
                "SELECT DISTINCT " + COLUMN_CHAT_SENDER + " AS contact " +
                "FROM " + TABLE_MESSAGE + " WHERE " + COLUMN_CHAT_RECEIVER + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{loggedUserId, loggedUserId});
        Log.d("DatabaseDebug", "Query executed for user: " + loggedUserId);

        if (cursor.moveToFirst()) {
            do {
                String contactId = cursor.getString(cursor.getColumnIndexOrThrow("contact"));
                contactedUsers.add(new User(contactId));
                Log.d("DatabaseDebug", "Chat with: " + contactId);
            } while (cursor.moveToNext());
        } else {
            Log.d("DatabaseDebug", "No chats found for user: " + loggedUserId);
        }

        cursor.close();
        db.close();
        return contactedUsers;
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
            values.put(COLUMN_USER_USERNAME, user.getId());
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

    public void insertUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Check if the user already exists
        if (userExists(user.getId())) {
            Log.i("DatabaseFunctions", "User already exists: " + user.getId());
            return; // Skip inserting duplicate
        }

        // Insert new user
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_USERNAME, user.getId());

        long result = db.insert(DatabaseOpenHelper.TABLE_USER, null, values);
        if (result == -1) {
            Log.e("DatabaseFunctions", "Failed to insert user: " + user.getId());
        } else {
            Log.i("DatabaseFunctions", "User added: " + user.getId());
        }
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
    public void randomUsers() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String[] usernames = {"user1", "user2", "user3"};

        for (String username : usernames) {
            if (!userExists(username)) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_USER_USERNAME, username);
                long result = db.insert(TABLE_USER, null, values);
                if (result != -1) {
                    Log.i("DatabaseFunctions", "User added: " + username);
                }
            }
        }
    }

    public void printMessageTable() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_MESSAGE, null);

            if (cursor.moveToFirst()) {
                Log.d("TableMessageDebug", "----- TABLE_MESSAGE CONTENTS -----");
                do {
                    int messageId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE_ID));
                    String sender = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CHAT_SENDER));
                    String receiver = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CHAT_RECEIVER));
                    String content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CHAT_CONTENT));
                    String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CHAT_TIMESTAMP));

                    Log.d("TableMessageDebug", "ID: " + messageId +
                            ", Sender: " + sender +
                            ", Receiver: " + receiver +
                            ", Content: " + content +
                            ", Timestamp: " + timestamp);
                } while (cursor.moveToNext());
            } else {
                Log.d("TableMessageDebug", "TABLE_MESSAGE is empty.");
            }
        } catch (Exception e) {
            Log.e("TableMessageDebug", "Error reading TABLE_MESSAGE: ", e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
    }

    public void printUserTable() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_USER, null);

            if (cursor.moveToFirst()) {
                Log.d("TableUserDebug", "----- TABLE_USER CONTENTS -----");
                do {
                    String username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_USERNAME));
                    Log.d("TableUserDebug", "Username: " + username);
                } while (cursor.moveToNext());
            } else {
                Log.d("TableUserDebug", "TABLE_USER is empty.");
            }
        } catch (Exception e) {
            Log.e("TableUserDebug", "Error reading TABLE_USER: ", e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
    }


}
