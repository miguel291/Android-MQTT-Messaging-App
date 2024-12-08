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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

public class ConversationListFragment extends Fragment {

    RecyclerView recyclerView;
    ConversationViewModel viewModel;

    public ConversationListFragment() {
        // Required empty public constructor
    }

    public static ConversationListFragment newInstance() {
        return new ConversationListFragment();
    }

    public void loadViewModel(){
        viewModel = new ViewModelProvider(getActivity()).get(ConversationViewModel.class);
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

        loadViewModel();
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up the RecyclerView
        recyclerView = view.findViewById(R.id.chatList);
        Adapter adapter = new Adapter(new ArrayList<>(), this::openConversation, this::showDeleteDialog);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Observe LiveData for chat updates
        viewModel.getChats().observe(getViewLifecycleOwner(), chats -> {
            if (chats != null) {
                adapter.updateChats(chats); // Update adapter with new data
            }
        });

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
                    ((MainActivity) requireActivity()).showArduinoConf();
                } else if (id == R.id.action_add_chat) {
                        showUserListDialog();
                }
                return true;
            }
        }, getViewLifecycleOwner());
    }

    private void showUserListDialog() {

        Log.d("Debug", "ViewModel: " + viewModel);
        Log.d("Debug", "LoggedInUser: " + (viewModel != null ? viewModel.getLoggedInUser() : "null"));
        Log.d("Debug", "User ID: " + (viewModel.getLoggedInUser() != null ? viewModel.getLoggedInUser().getId() : "null"));

        DictionaryOpenHelper dbHelper = new DictionaryOpenHelper(requireContext());
        /*String loggedUserId = viewModel.getLoggedInUser().getId(); // get the logged user

        createToast("Logged User" + loggedUserId);*/

        /*ArrayList<User> users = dbHelper.getAllUsersExcept(loggedUserId);

        if (users.isEmpty()) {
            createToast("No users available to start a chat.");
            return;
        }

        // Converti gli utenti in un array di nomi
        String[] userNames = new String[users.size()];
        for (int i = 0; i < users.size(); i++) {
            userNames[i] = users.get(i).getId();
        }

        // Mostra un AlertDialog con la lista degli utenti
        new AlertDialog.Builder(requireContext())
                .setTitle("Select a user")
                .setItems(userNames, (dialog, which) -> {
                    User selectedUser = users.get(which);
                    startNewChat(selectedUser);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();*/
    }




    private void startNewChat(User selectedUser) {
        //DictionaryOpenHelper dbHelper = new DictionaryOpenHelper(requireContext());
        //User loggedUser = viewModel.getLoggedInUser();

        createToast("New chat button clicked!");
        /*New chat creation
        Chat newChat = dbHelper.createNewChat(loggedUser, selectedUser, "Hello " + selectedUser.getId() + "!");

        // Aggiungi la chat alla lista e aggiorna la LiveData
        ArrayList<Chat> currentChats = new ArrayList<>(viewModel.getChats().getValue());
        currentChats.add(newChat);
        viewModel.setChats(currentChats);

        createToast("Chat started with " + selectedUser.getId());*/

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
                    ArrayList<Chat> currentChats = new ArrayList<>(viewModel.getChats().getValue());
                    currentChats.remove(position); // Remove the chat
                    viewModel.setChats(currentChats); // Update LiveData
                    createToast("Chat deleted.");
                    dialog.dismiss();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

}
