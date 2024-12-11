package com.example.challenge3;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ChatFragment extends Fragment {
    private static final String ARG_CHAT = "chat";
    private Chat chat;
    private String receiver;

    private EditText messageInput;
    private ImageButton sendButton;

    public static ChatFragment newInstance(Chat chat) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CHAT, chat);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            chat = (Chat) getArguments().getSerializable(ARG_CHAT);
            receiver = chat.getReceiver().getId();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        // Set up RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.conversationList);
        MessageAdapter adapter = new MessageAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Observe changes to LiveData
        chat.getMessagesLiveData().observe(getViewLifecycleOwner(), newMessages -> {
            adapter.updateMessages(newMessages);
            recyclerView.scrollToPosition(newMessages.size() - 1); // Scroll to the latest message
        });

        // Set up Toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        if (toolbar != null) {
            activity.setSupportActionBar(toolbar);

            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                activity.getSupportActionBar().setTitle(chat.getReceiver().getId());
            }

            // Add navigation click listener to handle back action
            toolbar.setNavigationOnClickListener(v -> {
                requireActivity().getSupportFragmentManager().popBackStack();
            });
        }

        // Initialize message input and send button
        messageInput = view.findViewById(R.id.messageInput);
        sendButton = view.findViewById(R.id.sendButton);
        sendButton.setOnClickListener(v -> sendMessage());

        return view;
    }



    /**
     * Sends a message and updates the chat's messages list.
     */
    private void sendMessage() {
        String messageText = messageInput.getText().toString().trim();

        if (messageText.isEmpty()) {
            Toast.makeText(requireContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        int messageId = chat.getChatSize() + 1; // Generate a unique message ID
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());

        // Create a new Message object
        Message newMessage = new Message(messageId, chat.getLoggedUser(), chat.getReceiver(), messageText, timestamp);

        // Save the message to the database
        if (!((MainActivity) requireActivity()).createMessage(messageText, chat.getLoggedUser().getId(), chat.getReceiver().getId())) {
            Toast.makeText(requireContext(), "Message not sent", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update LiveData for messages
        ArrayList<Message> updatedMessages = new ArrayList<>(chat.getMessagesLiveData().getValue());
        updatedMessages.add(newMessage);
        chat.getMessagesLiveData().setValue(updatedMessages);

        // Clear the input field
        messageInput.setText("");
    }


}
