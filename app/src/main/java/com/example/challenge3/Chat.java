package com.example.challenge3;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Chat implements Serializable {
    private User user1;
    private User user2;
    private ArrayList<Message> messages;

    public Chat(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
        this.messages = new ArrayList<>();
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public User getUser1() {
        return user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }
}
