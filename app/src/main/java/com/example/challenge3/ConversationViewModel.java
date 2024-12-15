package com.example.challenge3;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class ConversationViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Message>> messages = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<ArrayList<User>> users = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<ArrayList<User>> contactedUsers = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<Set<String>> selectListArduino = new MutableLiveData<>(new HashSet());

    // In your ViewModel, set the logged-in user when the user logs in
    private User loggedInUser;
    private User chattingWithUser;
    private Chat currentChat;

    public ConversationViewModel() {
    }

    public void setSelectListArduino(Set<String> selectListArduino) {
        this.selectListArduino.setValue(selectListArduino);
    }

    public MutableLiveData<Set<String>> getSelectListArduino() {
        return selectListArduino;
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
        ArrayList<Message> currentMessages = messages.getValue();
        if (currentMessages == null) {
            currentMessages = new ArrayList<>();
        }

        currentMessages.add(message);
        messages.setValue(currentMessages); // Notify observers

        // Automatic update of the contactedUsers
        User loggedInUser = getLoggedInUser();
        if (loggedInUser != null) {
            updateContactedUsers(loggedInUser);
        }
    }

    public void updateContactedUsers(User loggedInUser) {
        if (loggedInUser == null) return;

        // Retrieve and sort messages by time (most recent first)
        ArrayList<Message> sortedMessages = new ArrayList<>(messages.getValue());
        sortedMessages.sort((m1, m2) -> m2.getTimestamp().compareTo(m1.getTimestamp()));

        // Map to store the most recent message for each sender-receiver pair
        Map<String, Message> mostRecentMessages = new LinkedHashMap<>();

        for (Message message : sortedMessages) {
            // Create a unique key for each sender-receiver pair
            String pairKey = createPairKey(message.getSender(), message.getReceiver());
            if (!mostRecentMessages.containsKey(pairKey)) {
                mostRecentMessages.put(pairKey, message); // Add only the most recent message for this pair
            }
        }

        // Extract distinct users from the most recent messages
        ArrayList<User> updatedContactedUsers = new ArrayList<>();
        for (Message message : mostRecentMessages.values()) {
            if (message.getSender().getId().equals(loggedInUser.getId())) {
                if (!updatedContactedUsers.contains(message.getReceiver())) {
                    updatedContactedUsers.add(message.getReceiver());
                }
            } else if (message.getReceiver().getId().equals(loggedInUser.getId())) {
                if (!updatedContactedUsers.contains(message.getSender())) {
                    updatedContactedUsers.add(message.getSender());
                }
            }
        }

        // Update the contacted users
        contactedUsers.setValue(updatedContactedUsers);
    }

    // Helper method to create a unique key for a sender-receiver pair
    private String createPairKey(User sender, User receiver) {
        String id1 = sender.getId();
        String id2 = receiver.getId();
        // Combine the IDs in a consistent order (e.g., alphabetically)
        return id1.compareTo(id2) < 0 ? id1 + ":" + id2 : id2 + ":" + id1;
    }



    /*public void updateContactedUsersForNewMessage(User loggedInUser) {
        if (loggedInUser == null) return;

        ArrayList<User> updatedContactedUsers = new ArrayList<>();


        for (Message message : messages.getValue()) {
            if (message.getSender().getId().equals(loggedInUser.getId())) {
                if (!updatedContactedUsers.contains(message.getReceiver())) {
                    updatedContactedUsers.add(message.getReceiver());
                }
            } else if (message.getReceiver().getId().equals(loggedInUser.getId())) {
                if (!updatedContactedUsers.contains(message.getSender())) {
                    updatedContactedUsers.add(message.getSender());
                }
            }
        }

        contactedUsers.setValue(updatedContactedUsers); // Aggiorna i contatti
    }*/


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
