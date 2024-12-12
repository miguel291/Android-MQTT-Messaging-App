package com.example.challenge3;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ConversationViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Message>> messages = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<ArrayList<User>> users = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<ArrayList<User>> contactedUsers = new MutableLiveData<>(new ArrayList<>());

    // In your ViewModel, set the logged-in user when the user logs in
    private User loggedInUser;
    private User chattingWithUser;
    private Chat currentChat;

    public ConversationViewModel() {
       //Initialize the viewmodel
    }

    public void setMessages(ArrayList<Message> newMessages) {
        messages.setValue(newMessages);
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public Chat getCurrentChat() {
        return currentChat;
    }

    public User getChattingWithUser() {
        return chattingWithUser;
    }

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }

    public void setChattingWithUser(User user) {
        this.chattingWithUser = user;
    }

    public void setCurrentChat(Chat chat) {
        this.currentChat = chat;
    }

    public LiveData<ArrayList<User>> getContactedUsers() {
        return contactedUsers;
    }

    public void setContactedUsers(ArrayList<User> newUsers) {
        contactedUsers.setValue(newUsers);
    }

    public String getLoggedInUserId() {
        return loggedInUser.getId();
    }

    public LiveData<ArrayList<User>> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> newUsers) {
        users.setValue(newUsers);
    }

    public User getUserById(String userId) {
        for (User user : users.getValue()) {
            if (user.getId().equals(userId)) {
                return user;
            }
        }
        return null;
    }

    public ArrayList<User> getAllUsers() {
        ArrayList<User> usersArrayList = new ArrayList<User>();
        for (User user : users.getValue()) {
            usersArrayList.add(user);
        }
        return usersArrayList;
    }

    public void addUser(User user){
        users.getValue().add(user);
    }

    public LiveData<ArrayList<Message>> getMessages() {
        return messages;
    }

    public void addMessage(Message message) {
        messages.getValue().add(message);
    }


    public ArrayList<Message> getMessagesBetweenUsers(String userId1, String userId2) {
        ArrayList<Message> messagesList = new ArrayList<>();

        for (Message message : messages.getValue()) {
            if ((message.getSender().getId().equals(userId1) && message.getReceiver().getId().equals(userId2)) ||
                    (message.getSender().getId().equals(userId2) && message.getReceiver().getId().equals(userId1))) {
                messagesList.add(message);
            }
        }
        return messagesList;
    }


}
