package com.example.challenge3;

import java.util.ArrayList;

public class User {
    String id;
    ArrayList<Chat> ChatList;

    public User(String id) {
        this.id = id;
        this.ChatList = new ArrayList<>();
    }


    public void addChat(Chat chat) {
        ChatList.add(chat);
    }

    public ArrayList<Chat> getChatList() {
        return ChatList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }




}
