package com.example.challenge3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;


public class DictionaryOpenHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 3; // Increment version for the updated schema
    public static final String DATABASE_NAME = "chatApp.db";

    // Tables and Columns
    public static final String TABLE_CHAT = "chat";
    public static final String COLUMN_CHAT_ID = "chat_id";
    public static final String COLUMN_MESSAGE_ID = "message_id";
    public static final String COLUMN_SENDER_ID = "sender_id";
    public static final String COLUMN_RECEIVER_ID = "receiver_id";
    public static final String COLUMN_MESSAGE = "message";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private static final String TABLE_CHAT_CREATE =
            "CREATE TABLE " + TABLE_CHAT + " (" +
                    COLUMN_MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CHAT_ID + " INTEGER NOT NULL, " +
                    COLUMN_SENDER_ID + " TEXT NOT NULL, " +
                    COLUMN_RECEIVER_ID + " TEXT NOT NULL, " +
                    COLUMN_MESSAGE + " TEXT NOT NULL, " +
                    COLUMN_TIMESTAMP + " TEXT NOT NULL);";

    public DictionaryOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CHAT_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT);
        onCreate(db);
    }

    // Insert a message into the database
    public long insertMessage(int chatId, int messageId, String senderId, String receiverId, String message, String timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CHAT_ID, chatId);
        values.put(COLUMN_MESSAGE_ID, messageId);
        values.put(COLUMN_SENDER_ID, senderId);
        values.put(COLUMN_RECEIVER_ID, receiverId);
        values.put(COLUMN_MESSAGE, message);
        values.put(COLUMN_TIMESTAMP, timestamp);
        return db.insert(TABLE_CHAT, null, values);
    }

    // Retrieve messages for a specific chat
    public ArrayList<Message> getMessages(int chatId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_CHAT + " WHERE " + COLUMN_CHAT_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(chatId)});
        ArrayList<Message> messages = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                int messageId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE_ID));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE));
                String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP));
                String senderId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SENDER_ID));
                String receiverId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECEIVER_ID));

                Message message = new Message(messageId, chatId, new User(senderId), new User(receiverId), content, timestamp);

                messages.add(message);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return messages;
    }

    // Create a new chat with an initial message
    public Chat createNewChat(User sender, User receiver, String initialMessage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        int chatId = (int) db.insert(TABLE_CHAT, null, values);
        if (chatId == -1) {
            throw new RuntimeException("Failed to create new chat.");
        }

        long messageId = insertMessage(chatId, 0, sender.getId(), receiver.getId(), initialMessage, "2024-12-08 12:00:00");

        Chat newChat = new Chat(chatId, sender, receiver);
        newChat.addMessage(new Message((int) messageId, chatId, sender, receiver, initialMessage, "2024-12-08 12:00:00"));
        return newChat;
    }

    // Retrieve all chats for a specific user
    public ArrayList<Chat> getChats(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT DISTINCT " + COLUMN_CHAT_ID + ", " + COLUMN_SENDER_ID + ", " + COLUMN_RECEIVER_ID +
                " FROM " + TABLE_CHAT + " WHERE " + COLUMN_SENDER_ID + " = ? OR " + COLUMN_RECEIVER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{userId, userId});
        ArrayList<Chat> chats = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                int chatId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CHAT_ID));
                String senderId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SENDER_ID));
                String receiverId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECEIVER_ID));
                Chat chat = new Chat(chatId, new User(senderId), new User(receiverId));
                chat.setMessages(getMessages(chatId));
                chats.add(chat);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return chats;
    }

    // Retrieve all users except the logged-in user
    public ArrayList<User> getAllUsersExcept(String loggedUserId) {
        SQLiteDatabase db = this.getReadableDatabase();
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
}
