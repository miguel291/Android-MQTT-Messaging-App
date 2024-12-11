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

        // Check the chat validity
        if (chat == null) {
            Toast.makeText(requireContext(), "Chat data is missing", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
            return view;
        }

        RecyclerView recyclerView = view.findViewById(R.id.conversationList);
        MessageAdapter adapter = new MessageAdapter(chat.getMessagesLiveData().getValue());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize messages if null
        if (chat.getMessagesLiveData() == null) {
            chat.setMessages(new ArrayList<>()); // Initialize empty list
        }

        // Configure the toolbar
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        Toolbar toolbar = view.findViewById(R.id.toolbar); // Use the toolbar from the fragment's layout
        if (toolbar != null) {
            activity.setSupportActionBar(toolbar);

            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                activity.getSupportActionBar().setTitle(chat.getReceiver() != null ? chat.getReceiver().getId() : "Unknown User");
            }

            // Set the back navigation listener
            toolbar.setNavigationOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        }

        // Observe changes to the messages list
        chat.getMessagesLiveData().observe(getViewLifecycleOwner(), newMessages -> {
            adapter.updateMessages(newMessages);
            recyclerView.scrollToPosition(newMessages.size() - 1); // Scroll to the latest message
        });

        // Configure the message input and send button
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
        int messageId = chat.getChatSize() + 1; // Generate a unique message ID

        if (messageText.isEmpty()) {
            Toast.makeText(requireContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new Message object
        Message newMessage = new Message(messageId, chat.getLoggedUser(), chat.getReceiver(), messageText, "2024-12-08 12:00:00"); // Replace with current timestamp dynamically

        // ACTUALLY REGISTERING MESSAGES
        ((MainActivity) requireActivity()).createMessage(messageText, receiver, receiver);

        // Add the message to the chat's LiveData
        chat.addMessage(newMessage);

        // Clear the input field
        messageInput.setText("");

        // Optionally, add logic to persist the message to a database
    }
}
