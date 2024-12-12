package com.example.challenge3;

import androidx.lifecycle.MutableLiveData;

import java.io.Serializable;
import java.util.ArrayList;

public class Chat implements Serializable {
    private int id;
    private User loggedUser; // The sender
    private User receiver;
    private MutableLiveData<ArrayList<Message>> messages;

    // Constructor
    public Chat(int id, User loggedUser, User receiver) {
        this.id = id;
        this.loggedUser = loggedUser;
        this.receiver = receiver;
        this.messages = new MutableLiveData<>(new ArrayList<>()); // Initialize with a new empty list
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MutableLiveData<ArrayList<Message>> getMessagesLiveData() {
        if (messages == null) {
            messages = new MutableLiveData<>(new ArrayList<>());
        }
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages.setValue(new ArrayList<>(messages)); // Use a new list to trigger LiveData observers
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    public User getReceiver() {
        return receiver;
    }

    public void addMessage(Message message) {
        ArrayList<Message> currentMessages = this.messages.getValue();
        if (currentMessages == null) {
            currentMessages = new ArrayList<>();
        }
        currentMessages.add(message);
        this.messages.setValue(new ArrayList<>(currentMessages)); // Ensure LiveData updates correctly
    }

    public boolean isMessageFromSender(Message message) {
        return message.getSender().getId().equals(loggedUser.getId());
    }

    public int getChatSize() {
        ArrayList<Message> currentMessages = messages.getValue();
        return currentMessages != null ? currentMessages.size() : 0;
    }
}

