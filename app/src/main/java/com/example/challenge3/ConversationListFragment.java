package com.example.challenge3;

import android.app.Dialog;
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

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class ConversationListFragment extends Fragment {

    RecyclerView recyclerView;
    ConversationViewModel viewModel;
    Adapter adapter;
    private ArrayList<User> contactedUsers; // Keep the list in the fragment

    public ConversationListFragment() {
        // Required empty public constructor
    }

    public void loadViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(ConversationViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation_list, container, false);
        loadViewModel();

        Toolbar toolbar = view.findViewById(R.id.toolbarcl);
        AppCompatActivity activity = (AppCompatActivity) getActivity();

        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setTitle("Chat List");
            }
        }

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
                    showCustomDialog();
                }
                return true;
            }
        }, getViewLifecycleOwner());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshContactedUsers();
    }

    private void refreshContactedUsers() {
        User loggedUser = viewModel.getLoggedInUser();

        if (loggedUser == null) {
            Toast.makeText(requireContext(), "No user is logged in. Please log in first.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fetch the updated contacted users
        viewModel.updateContactedUsers(loggedUser);
        ArrayList<User> updatedContactedUsers = ((MainActivity) requireActivity()).getUserWithChat(loggedUser.getId());

        if (updatedContactedUsers == null) {
            updatedContactedUsers = new ArrayList<>();
        }


        viewModel.updateContactedUsers(loggedUser);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        User loggedUser = viewModel.getLoggedInUser();
        if (loggedUser == null) {
            Toast.makeText(requireContext(), "No user is logged in. Please log in first.", Toast.LENGTH_SHORT).show();
            return;
        }

        // adapter configuration
        adapter = new Adapter(new ArrayList<>(), user -> {
            if (user != null) {
                ArrayList<Message> allMessages = viewModel.getMessages().getValue();
                ArrayList<Message> filteredMessages = new ArrayList<>();

                if (allMessages != null) {
                    for (Message message : allMessages) {
                        if ((message.getSender().equals(loggedUser.getId()) && message.getReceiver().equals(user.getId())) ||
                                (message.getSender().equals(user.getId()) && message.getReceiver().equals(loggedUser.getId()))) {
                            filteredMessages.add(message);
                        }
                    }
                }

                Chat chat = new Chat(1, loggedUser, user);
                chat.setMessages(filteredMessages);

                openConversation(chat);
            }
        }, position -> {
            User user = viewModel.getContactedUsers().getValue().get(position);

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Choose an action")
                    .setMessage("Would you like to erase the chat with " + user.getId() + "?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        ArrayList<User> updatedUsers = viewModel.getContactedUsers().getValue();
                        updatedUsers.remove(position);
                        viewModel.setContactedUsers(updatedUsers);
                        ((MainActivity) requireActivity()).deleteChatBetweenUser(loggedUser.getId(), user.getId());
                        Toast.makeText(requireContext(), "Deleted Chat!", Toast.LENGTH_SHORT).show();
                    })
                    .setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        recyclerView = view.findViewById(R.id.chatList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Observe contactedUsers
        viewModel.getContactedUsers().observe(getViewLifecycleOwner(), updatedContactedUsers -> {
            if (updatedContactedUsers != null) {
                adapter.updateContactedUsers(updatedContactedUsers);
                adapter.notifyDataSetChanged();
            }
        });

        // Observe messages to automatically update the contacted users
        viewModel.getMessages().observe(getViewLifecycleOwner(), messages -> {
            if (messages != null) {
                viewModel.updateContactedUsers(loggedUser);
            }
        });
    }


    public void showCustomDialog() {
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_add_chat);

        final EditText usernameET = dialog.findViewById(R.id.usernameDialog);
        Button confirmButton = dialog.findViewById(R.id.buttonDialog);

        confirmButton.setOnClickListener(v -> {
            User loggedUser = viewModel.getLoggedInUser();
            String chatWith = usernameET.getText().toString();
            MainActivity mainActivity = (MainActivity) requireActivity();

            if ((!chatWith.isEmpty()) && (!chatWith.equals(loggedUser.getId()))) {
                dialog.dismiss();
                mainActivity.showChatFragment(new Chat(1, loggedUser, new User(chatWith)));
            } else {
                usernameET.setError("Username not valid!");
            }
        });

        dialog.show();
    }

    private void openConversation(Chat chat) {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showChatFragment(chat);
        }
    }
}
