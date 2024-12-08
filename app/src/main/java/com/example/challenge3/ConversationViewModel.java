package com.example.challenge3;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class ConversationViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Chat>> chats = new MutableLiveData<>(new ArrayList<>());
   // private MutableLiveData<ArrayList<User>> users = new MutableLiveData<>(new ArrayList<>());
    // In your ViewModel, set the logged-in user when the user logs in
    private MutableLiveData<User> loggedInUser = new MutableLiveData<>();

    public ConversationViewModel() {
       //Initialize the viewmodel
    }

    public void setLoggedInUser(User user) {
        loggedInUser.setValue(user);
    }

    public User getLoggedInUser() {
        return loggedInUser.getValue();
    }


    public LiveData<ArrayList<Chat>> getChats() {
        return chats;
    }

    public void setChats(ArrayList<Chat> newChats) {
        chats.setValue(newChats);
    }
}
