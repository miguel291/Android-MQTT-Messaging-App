package com.example.challenge3;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class ConversationViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Message>> messages = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<ArrayList<User>> users = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<ArrayList<User>> contactedUsers = new MutableLiveData<>(new ArrayList<>());

    // In your ViewModel, set the logged-in user when the user logs in
    private User loggedInUser;

    public ConversationViewModel() {
       //Initialize the viewmodel
    }

    public void setMessages(ArrayList<Message> newMessages) {
        messages.setValue(newMessages);
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
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

    public void addUser(User user){
        users.getValue().add(user);
    }

    public LiveData<ArrayList<Message>> getMessages() {
        return messages;
    }

    public void addMessage(Message message) {
        messages.getValue().add(message);
    }


}
