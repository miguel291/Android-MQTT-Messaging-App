package com.example.challenge3;

import java.util.ArrayList;

public class User {
    String Id;
    ArrayList<Chat> ChatList;

    public User(String id) {
        this.Id = id;
        this.ChatList = new ArrayList<>();
    }

    public void addChat(Chat chat) {
        ChatList.add(chat);
    }

    public ArrayList<Chat> getChatList() {
        return ChatList;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        this.Id = id;
    }

}
