package com.example.challenge3;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class ConversationViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Chat>> chats = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<ArrayList<User>> users = new MutableLiveData<>(new ArrayList<>());

    // In your ViewModel, set the logged-in user when the user logs in
    private User loggedInUser;

    public ConversationViewModel() {
       //Initialize the viewmodel
    }



    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
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


    public LiveData<ArrayList<Chat>> getChats() {
        return chats;
    }

    public void setChats(ArrayList<Chat> newChats) {
        chats.setValue(newChats);
    }
}
