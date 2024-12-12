package com.example.challenge3;

import android.content.Context;
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

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ArduinoConfFragment extends Fragment {

    RecyclerView recyclerView;
    ConversationViewModel viewModel;

    public ArduinoConfFragment() {
    }

    public static ArduinoConfFragment newInstance() {
        return new ArduinoConfFragment();
    }

    public void loadViewModel() {
        viewModel = new ViewModelProvider(getActivity()).get(ConversationViewModel.class);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_arduino_conf, container, false);

        // Bind and set the toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbarArduino);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setTitle("Arduino Configuration"); // Set toolbar title
        }

        loadViewModel();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up the menu
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_ard_conf, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_back) {
                    MainActivity activity = (MainActivity) requireActivity();
                    if (activity != null) {
                        activity.showChatList();
                    }
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner());

        // Set up the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.chatList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        MainActivity activity = (MainActivity) requireActivity();
        if (activity != null) {
            ArrayList<User> contactedUsers = activity.getUserChats(viewModel.getLoggedInUser().getId());
            recyclerView.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                @Override
                public int getItemViewType(int position) {
                    return 0; // Single view type
                }

                @NonNull
                @Override
                public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View itemView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_contacted_user, parent, false);
                    return new RecyclerView.ViewHolder(itemView) {};
                }

                @Override
                public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                    View itemView = holder.itemView;
                    TextView userName = itemView.findViewById(R.id.userName);
                    CheckBox userCheckbox = itemView.findViewById(R.id.userCheckbox);

                    // Populate user data
                    User user = contactedUsers.get(position);
                    userName.setText(user.getId());

                    // Handle checkbox state
                    userCheckbox.setOnCheckedChangeListener(null); // Reset listener
                    userCheckbox.setChecked(false); // Default unchecked

                    userCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (isChecked) {
                            // Checkbox selected
                            Toast.makeText(requireContext(), user.getId() + " selected", Toast.LENGTH_SHORT).show();
                        } else {
                            // Checkbox deselected
                            Toast.makeText(requireContext(), user.getId() + " deselected", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public int getItemCount() {
                    return contactedUsers.size();
                }
            });
        }
    }

    private void createToast(String message) {
        Context context = getContext();
        if (context != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }
}
