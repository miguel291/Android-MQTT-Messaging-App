package com.example.challenge3;

/*
    CREATE TABLE User (
    username VARCHAR(255) PRIMARY KEY
);

CREATE TABLE Chat (
    id INT PRIMARY KEY AUTO_INCREMENT,
    timestamp DATETIME NOT NULL,
    content VARCHAR(255),
    receiver VARCHAR(255),
    sender VARCHAR(255),
    FOREIGN KEY (receiver) REFERENCES User(username) ON DELETE CASCADE,
    FOREIGN KEY (sender) REFERENCES User(username) ON DELETE CASCADE
);
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOpenHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 3; // Increment version for the updated schema
    public static final String DATABASE_NAME = "ourchat.db";

    // Table names
    public static final String TABLE_USER = "user";
    public static final String TABLE_MESSAGE = "message";

    // Columns of User table
    public static final String COLUMN_USER_USERNAME = "username";

    // Columns of Chat table
    public static final String COLUMN_MESSAGE_ID = "id";
    public static final String COLUMN_CHAT_CONTENT = "content";
    public static final String COLUMN_CHAT_TIMESTAMP = "sent_timestamp";
    public static final String COLUMN_CHAT_SENDER = "sender";
    public static final String COLUMN_CHAT_RECEIVER = "receiver";

    // Create user table string
    private static final String TABLE_USER_CREATE =
            "CREATE TABLE " + TABLE_USER + " (" +
                    COLUMN_USER_USERNAME + " TEXT PRIMARY KEY);";

    // Create chat table string + its relations
    private static final String TABLE_MESSAGE_CREATE =
            "CREATE TABLE " + TABLE_MESSAGE + " (" +
                    COLUMN_MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CHAT_CONTENT + " TEXT NOT NULL, " +
                    COLUMN_CHAT_TIMESTAMP + " TEXT NOT NULL, " +
                    COLUMN_CHAT_SENDER + " TEXT NOT NULL, " +
                    COLUMN_CHAT_RECEIVER + " TEXT NOT NULL, " +
                    " FOREIGN KEY (receiver) REFERENCES User(username) ON DELETE CASCADE," +
                    " FOREIGN KEY (sender) REFERENCES User(username) ON DELETE CASCADE);";


    // create database context
    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // create chat table
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_USER_CREATE);
        db.execSQL(TABLE_MESSAGE_CREATE);
    }

    // replace database versions
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

}
