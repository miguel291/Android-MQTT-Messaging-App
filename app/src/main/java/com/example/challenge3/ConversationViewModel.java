package com.example.challenge3;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class ConversationViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Chat>> chats = new MutableLiveData<>(new ArrayList<>());
    private User loggedInUser;


    public ConversationViewModel() {
       //Initialize the viewmodel
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(User user) {
        loggedInUser = user;
    }

    public LiveData<ArrayList<Chat>> getChats() {
        return chats;
    }

    public void setChats(ArrayList<Chat> newChats) {
        chats.setValue(newChats);
    }

}
