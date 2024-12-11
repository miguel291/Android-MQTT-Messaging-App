package com.example.challenge3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.sql.Timestamp;

import java.text.SimpleDateFormat;

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
        values.put(DatabaseOpenHelper.COLUMN_CHAT_RECEIVER, receiver_id);
        values.put(DatabaseOpenHelper.COLUMN_CHAT_TIMESTAMP, timestamp_str);

        // insert user
        long value = db.insert(DatabaseOpenHelper.TABLE_MESSAGE, null,values);
        Log.i("DATAFINAL", ("Chat added: " + String.valueOf(value)));

        return value != -1;
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
