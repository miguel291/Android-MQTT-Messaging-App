package com.example.challenge3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;

public class DictionaryOpenHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2; // Incrementa la versione per aggiornare il DB
    public static final String DATABASE_NAME = "chatApp.db";

    // Tabelle
    public static final String TABLE_CHAT = "chat";
    public static final String COLUMN_CHAT_ID = "chat_id"; // Nome più esplicito
    public static final String COLUMN_SENDER_ID = "sender_id";
    public static final String COLUMN_RECEIVER_ID = "receiver_id";
    public static final String COLUMN_MESSAGE = "message";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private static final String TABLE_CHAT_CREATE =
            "CREATE TABLE " + TABLE_CHAT + " (" +
                    COLUMN_CHAT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
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

    // Inserisci un messaggio
    public long insertMessage(int chatId, String senderId, String receiverId, String message, String timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CHAT_ID, chatId);
        values.put(COLUMN_SENDER_ID, senderId);
        values.put(COLUMN_RECEIVER_ID, receiverId);
        values.put(COLUMN_MESSAGE, message);
        values.put(COLUMN_TIMESTAMP, timestamp);
        return db.insert(TABLE_CHAT, null, values);
    }

    // Recupera i messaggi per una chat
    public ArrayList<Message> getMessages(int chatId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_CHAT + " WHERE " + COLUMN_CHAT_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(chatId)});
        ArrayList<Message> messages = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                String content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE));
                String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP));
                String senderId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SENDER_ID));
                String receiverId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECEIVER_ID));

                Message message = new Message(content, timestamp);
                message.setSender(new User(senderId));
                message.setReceiver(new User(receiverId));
                messages.add(message);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return messages;
    }

    public Chat createNewChat(User sender, User receiver, String initialMessage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("sender_id", sender.getId());
        values.put("receiver_id", receiver.getId());
        values.put("message", initialMessage);
        values.put("timestamp", "2024-12-08 12:00:00");

        long chatId = db.insert("chat", null, values);
        if (chatId == -1) {
            throw new RuntimeException("Failed to create new chat.");
        }

        Chat newChat = new Chat((int) chatId, sender, receiver);
        newChat.addMessage(new Message(initialMessage, "2024-12-08 12:00:00"));
        return newChat;
    }


    // Recupera tutte le chat per un utente
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

    // Recupera tutti gli utenti tranne quello loggato
    public ArrayList<User> getAllUsersExcept(String loggedUserId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM users WHERE user_id != ?";
        Cursor cursor = db.rawQuery(query, new String[]{loggedUserId});
        ArrayList<User> users = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                String userId = cursor.getString(cursor.getColumnIndexOrThrow("user_id"));
                String userName = cursor.getString(cursor.getColumnIndexOrThrow("user_name"));
                users.add(new User(userId));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return users;
    }

}
