package com.example.challenge3;

import androidx.lifecycle.MutableLiveData;

import java.io.Serializable;
import java.util.ArrayList;

public class Chat implements Serializable {
    private int id;
    private User loggedUser; //it's the sender
    private User receiver;
    private MutableLiveData<ArrayList<Message>> messages;

    // Constructor
    public Chat(int id, User loggedUser, User receiver) {
        this.id = id;
        this.loggedUser = loggedUser;
        this.receiver = receiver;
        this.messages = new MutableLiveData<>(new ArrayList<>()); // Initialize with an empty list
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MutableLiveData<ArrayList<Message>> getMessagesLiveData() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages.setValue(messages);
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    public User getReceiver() {
        return receiver;
    }

    public void addMessage(Message message) {
        ArrayList<Message> currentMessages = this.messages.getValue();
        if (currentMessages != null) {
            currentMessages.add(message);
        }
    }

    public boolean isMessageFromSender(Message message) {
        return message.getSender().getId() == (loggedUser.getId());
    }

    public int getChatSize() {
        ArrayList<Message> currentMessages = messages.getValue(); // Retrieve the current list
        return currentMessages != null ? currentMessages.size() : 0; // Return size or 0 if the list is null
    }

}
