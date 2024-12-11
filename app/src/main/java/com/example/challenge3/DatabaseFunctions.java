package com.example.challenge3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DatabaseFunctions {
    private final DatabaseOpenHelper dbHelper;

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
        Log.i("DATABASE", ("User added: " + String.valueOf(value)));
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



}
