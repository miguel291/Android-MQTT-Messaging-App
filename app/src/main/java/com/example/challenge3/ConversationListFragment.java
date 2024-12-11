package com.example.challenge3;

import android.app.Dialog;
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

        // Get the logged-in user directly from MainActivity
        final User loggedUser = ((MainActivity) requireActivity()).getLoggedUser();

        // Check if loggedUser is not null
        if (loggedUser == null) {
            Toast.makeText(requireContext(), "No user is logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve the list of users contacted by the logged user
        ArrayList<User> contactedUsers = ((MainActivity) requireActivity()).databaseFinal.getChats(loggedUser.getId());

        Adapter adapter = new Adapter(contactedUsers, user -> {
            if (user != null) {
                Chat chat = new Chat(1, loggedUser, user); // Create Chat object dynamically
                openConversation(chat); // Pass the Chat object to openConversation
            }
        }, this::showDeleteDialog);


        recyclerView = view.findViewById(R.id.chatList);

        if (contactedUsers.isEmpty()) {
            recyclerView.setVisibility(View.GONE); // Hide RecyclerView if empty
            createToast("No conversations found. Add a new chat!");
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        }

    }




    public void showCustomDialog() {

        Log.d("Debug", "ViewModel: " + viewModel);
        Log.d("Debug", "LoggedInUser: " + (viewModel != null ? viewModel.getLoggedInUser() : "null"));
        Log.d("Debug", "User ID: " + (viewModel.getLoggedInUser() != null ? viewModel.getLoggedInUser().getId() : "null"));

        final Dialog dialog = new Dialog(requireContext());
        //We have added a title in the custom layout. So let's disable the default title.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


        //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
        dialog.setCancelable(true);
        //Mention the name of the layout of your custom dialog.
        dialog.setContentView(R.layout.dialog_add_chat);

        //Initializing the views of the dialog.
        final EditText usernameET = dialog.findViewById(R.id.usernameDialog);
        Button confirmButton = dialog.findViewById(R.id.buttonDialog);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chatWith = usernameET.getText().toString();
                MainActivity mainActivity = (MainActivity) requireActivity();

                if(mainActivity.userExists(chatWith)){ // INSERT VALIDATION IF USER EXISTS

                    Log.i("DEBUG", "USERNAME: " + chatWith  + " LOGGED USER ID: " + mainActivity.getLoggedUser().getId());
                    mainActivity.showChatFragment(new Chat(1, mainActivity.getLoggedUser(), mainActivity.getUserById(chatWith)) );
                    dialog.dismiss();
                }else{
                    usernameET.setError("This username do not exist");
                }
            }
        });

        dialog.show();
    }

    public void showChatList(){

        // Dinamically create the chats using some DB function and show them into the recycler view
        MainActivity mainActivity = (MainActivity) requireActivity();
        mainActivity.dynamicChatCreation();
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
        /*new AlertDialog.Builder(requireContext())
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
                .show();*/
    }

}
