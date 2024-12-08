package com.example.challenge3;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

public class ConversationListFragment extends Fragment {

    ArrayList<Chat> chats = new ArrayList<>();
    RecyclerView recyclerView;

    public ConversationListFragment() {
        // Required empty public constructor
    }

    public static ConversationListFragment newInstance() {
        return new ConversationListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversation_list, container, false);

        // Bind and set the toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbarcl);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setTitle("Chat List"); // Set toolbar title
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        /*FAKE DATA CREATION (TO REMOVE)*/
        // Create two fake users (same senderId)
        User sender = new User("Sender123"); // Same sender ID for both chats
        User receiver1 = new User("Receiver1");
        User receiver2 = new User("Receiver2");

        // Create fake chat objects
        Chat chat1 = new Chat(sender, receiver1);
        chat1.addMessage(new Message("Hello Receiver1!", "2024-12-08 10:00:00"));
        chat1.addMessage(new Message("How are you?", "2024-12-08 10:05:00"));

        Chat chat2 = new Chat(sender, receiver2);
        chat2.addMessage(new Message("Hi Receiver2!", "2024-12-08 11:00:00"));
        chat2.addMessage(new Message("What's up?", "2024-12-08 11:05:00"));

        // Add chats to the list
        chats.add(chat1);
        chats.add(chat2);


        // Set up the RecyclerView
        recyclerView = view.findViewById(R.id.chatList);
        Adapter adapter = new Adapter(chats, this::openConversation, this::showDeleteDialog);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Menu setup
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_conv_list, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();

                if (id == R.id.arduinoConfigButton) {
                    //createToast("Arduino Config button clicked");
                    //Change fragment
                    ((MainActivity) requireActivity()).showArduinoConf();
                } else if (id == R.id.action_add_chat) {
                    //Create a new chat
                    createToast("Add Chat button clicked");
                }
                return true;
            }
        }, getViewLifecycleOwner());
    }



    private void createToast(String message) {
        Context context = getContext();
        if (context != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    // Method to open a conversation in a new fragment
    private void openConversation(Chat chat) {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showChatFragment(chat);
        }
    }




    private void showDeleteDialog(int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Chat")
                .setMessage("Are you sure you want to delete this chat?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Remove the chat and notify the adapter
                    chats.remove(position);
                    Objects.requireNonNull(recyclerView.getAdapter()).notifyItemRemoved(position);
                    createToast("Chat deleted.");

                    // Dismiss the dialog (optional, as it will dismiss automatically)
                    dialog.dismiss();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // Just dismiss the dialog when "No" is clicked
                    dialog.dismiss();
                })
                .show();
    }
}
