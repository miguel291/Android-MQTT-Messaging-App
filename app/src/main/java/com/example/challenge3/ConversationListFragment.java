package com.example.challenge3;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
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

    public void loadViewModel() {
        viewModel = new ViewModelProvider(getActivity()).get(ConversationViewModel.class);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation_list, container, false);

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

        loadViewModel();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        User loggedUser = viewModel.getLoggedInUser(); // Declare as final
        if (loggedUser == null) {
            Toast.makeText(requireContext(), "No user is logged in. Please log in first.", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<User> contactedUsers = ((MainActivity) requireActivity()).databaseFinal.getChats(loggedUser.getId());
        Adapter adapter = new Adapter(contactedUsers, user -> {
            if (user != null) {
                Chat chat = new Chat(1, loggedUser, user);
                openConversation(chat);
            }
        }, position -> {
            // Handle delete logic
        });

        recyclerView = view.findViewById(R.id.chatList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }


    public void showCustomDialog() {
        Log.d("Debug", "ViewModel: " + viewModel);
        Log.d("Debug", "LoggedInUser: " + (viewModel != null ? viewModel.getLoggedInUser() : "null"));

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

            if ( (!chatWith.isEmpty()) && (!chatWith.equals(loggedUser.getId())) ) {
                mainActivity.showChatFragment(new Chat(1, loggedUser, new User(chatWith)));
                dialog.dismiss();
            } else {
                usernameET.setError("This username does not exist.");
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
