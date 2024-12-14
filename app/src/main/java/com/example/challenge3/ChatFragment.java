package com.example.challenge3;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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
    private String receiverId;

    private EditText messageInput;
    private ImageButton sendButton;
    ConversationViewModel viewModel;

    int messageId = 0;

    public void loadViewModel() {
        viewModel = new ViewModelProvider(getActivity()).get(ConversationViewModel.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.conversationList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize adapter with an empty list
        MessageAdapter adapter = new MessageAdapter(new ArrayList<>(), viewModel.getCurrentChat().getLoggedUser().getId());
        recyclerView.setAdapter(adapter);

        // Maintain a single list to store all messages
        ArrayList<Message> allMessages = new ArrayList<>();

        // Load initial messages
        //ArrayList<Message> initialMessages = viewModel.getMessages().getValue();
        ArrayList<Message> initialMessages = viewModel.getMessagesBetweenUsers(viewModel.getCurrentChat().getLoggedUser().getId(),
                viewModel.getCurrentChat().getReceiver().getId());

        if (initialMessages != null && !initialMessages.isEmpty()) {
            allMessages.addAll(initialMessages); // Add initial messages to allMessages
            adapter.updateMessages(allMessages); // Update the adapter
            recyclerView.scrollToPosition(allMessages.size() - 1); // Scroll to the last message
        }

        // Observe LiveData for new messages
        viewModel.getCurrentChat().getMessagesLiveData().observe(getViewLifecycleOwner(), newMessages -> {
            if (newMessages != null && !newMessages.isEmpty()) {
                Log.d("ChatFragment", "New Messages received: " + newMessages.size());

                allMessages.add(newMessages.get(newMessages.size()-1));

                adapter.updateMessages(allMessages); // Update the adapter
                recyclerView.scrollToPosition(allMessages.size() - 1); // Scroll to the last message
            }
        });

        // Toolbar setup
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
        sendButton.setOnClickListener(v -> {
            try {
                sendMessage();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }



    @Override
   public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        loadViewModel();


        if (viewModel.getCurrentChat() != null && viewModel.getCurrentChat().getReceiver() != null) {
            receiverId = viewModel.getCurrentChat().getReceiver().getId();
        }

        return view;
    }




    private void sendMessage() throws Exception {
        String messageText = messageInput.getText().toString().trim();

        if (messageText.isEmpty()) {
            Toast.makeText(requireContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (viewModel.getCurrentChat().getLoggedUser() == null || viewModel.getCurrentChat().getReceiver() == null) {
            Toast.makeText(requireContext(), "Chat information is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new message
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(new java.util.Date());
        Message newMessage = new Message(messageId, new User(viewModel.getCurrentChat().getLoggedUser().getId()), new User(viewModel.getCurrentChat().getReceiver().getId()), messageText, timestamp);
        messageId++;

        // Simulate message sending (e.g., via MainActivity)
        boolean messageSent = ((MainActivity) requireActivity()).createMessage(
                messageText,
                viewModel.getCurrentChat().getLoggedUser().getId(),
                viewModel.getCurrentChat().getReceiver().getId(),
                timestamp
        );

        if (!messageSent) {
            Toast.makeText(requireContext(), "Message not sent", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve existing messages and add the new one
        ArrayList<Message> currentMessages = viewModel.getCurrentChat().getMessagesLiveData().getValue();
        if (currentMessages == null) {
            currentMessages = new ArrayList<>();
        }

        // Add the new message to the current list
        currentMessages.add(newMessage);

        // Update LiveData
        viewModel.getCurrentChat().getMessagesLiveData().setValue(new ArrayList<>(currentMessages)); // Ensure the LiveData is updated with a new instance

        MainActivity mainActivity = (MainActivity) requireActivity();
        mainActivity.updateChatDB(newMessage);

        // Clear the input field
        messageInput.setText("");

        Log.d("ChatFragment", "Message sent: " + messageText);
        Log.d("ChatFragment", "Updated message list size: " + currentMessages.size());
    }
}
