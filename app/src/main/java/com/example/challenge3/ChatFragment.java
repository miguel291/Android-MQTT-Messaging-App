package com.example.challenge3;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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
    private String receiverId;

    private EditText messageInput;
    private ImageButton sendButton;

    int messageId = 0;

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
            if (chat != null && chat.getReceiver() != null) {
                receiverId = chat.getReceiver().getId();
            }
        }
    }

    @Override
   public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        // Set up RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.conversationList);
        ArrayList<Message> initialMessages = chat.getMessagesLiveData().getValue();
        Log.d("ChatFragment", "Chat received: " + chat.getId());

        Log.d("ChatFragment", "Initial Messages: " + initialMessages);

        if (initialMessages == null) {
            initialMessages = new ArrayList<>();
        }
        MessageAdapter adapter = new MessageAdapter(initialMessages, chat.getLoggedUser().getId());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Observe changes to LiveData
        chat.getMessagesLiveData().observe(getViewLifecycleOwner(), newMessages -> {
            adapter.updateMessages(newMessages);
            recyclerView.scrollToPosition(newMessages.size() - 1);
        });

        // Set up Toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        if (toolbar != null) {
            activity.setSupportActionBar(toolbar);

            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                activity.getSupportActionBar().setTitle(receiverId);
            }

            toolbar.setNavigationOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        }

        // Initialize message input and send button
        messageInput = view.findViewById(R.id.messageInput);
        sendButton = view.findViewById(R.id.sendButton);
        sendButton.setOnClickListener(v -> sendMessage());

        return view;
    }

    private void sendMessage() {
        String messageText = messageInput.getText().toString().trim();

        Log.i("ChatFragment", "Sending message: " + messageText);
        Log.i("ChatFragment", "LoggedUser: " + (chat.getLoggedUser() != null ? chat.getLoggedUser().getId() : "null"));
        Log.i("ChatFragment", "Receiver: " + (chat.getReceiver() != null ? chat.getReceiver().getId() : "null"));

        if (messageText.isEmpty()) {
            Toast.makeText(requireContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (chat.getLoggedUser() == null || chat.getReceiver() == null) {
            Toast.makeText(requireContext(), "Chat information is missing.", Toast.LENGTH_SHORT).show();
            Log.e("ChatFragment", "LoggedUser or Receiver is null.");
            return;
        }


        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        Message newMessage = new Message(messageId, chat.getLoggedUser(), chat.getReceiver(), messageText, timestamp);
        messageId++;

        if (!((MainActivity) requireActivity()).createMessage(messageText, chat.getLoggedUser().getId(), chat.getReceiver().getId())) {
            Toast.makeText(requireContext(), "Message not sent", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update LiveData for messages
        ArrayList<Message> updatedMessages = chat.getMessagesLiveData().getValue();
        if (updatedMessages == null) {
            updatedMessages = new ArrayList<>();
        }
        updatedMessages.add(newMessage);
        chat.getMessagesLiveData().setValue(updatedMessages);

        // Scroll to the last message
        RecyclerView recyclerView = requireView().findViewById(R.id.conversationList);
        recyclerView.scrollToPosition(updatedMessages.size() - 1);

        // Update the adapter
        MessageAdapter adapter = (MessageAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.updateMessages(updatedMessages);
        }

        ((MainActivity) requireActivity()).createMessage(newMessage.getContent(),
                                                            newMessage.getSender().getId(),
                                                            newMessage.getReceiver().getId());

        // Clear the input field
        messageInput.setText("");
    }
}
