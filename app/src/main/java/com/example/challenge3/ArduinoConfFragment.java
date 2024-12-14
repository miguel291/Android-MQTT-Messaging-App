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
import android.content.SharedPreferences;

import android.text.TextUtils;
import android.util.Log;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ArduinoConfFragment extends Fragment {

    RecyclerView recyclerView;
    ConversationViewModel viewModel;
    SharedPreferences sharedPreferences;
    Set<String> selectListTemp;
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
        sharedPreferences = requireContext().getSharedPreferences("arduinoConf", Context.MODE_PRIVATE);
        String setUsers = sharedPreferences.getString("selectedUsers", "");
        selectListTemp = new HashSet<>(Arrays.asList(setUsers.split(",")));


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
            ArrayList<User> contactedUsers = activity.getUserWithChat(viewModel.getLoggedInUser().getId());
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

                    if(selectListTemp.contains(user.getId())) // if not in list
                        userCheckbox.setChecked(true); // Default unchecked
                    else
                        userCheckbox.setChecked(false); // Default unchecked


                    Log.i("USER CONF", "tou a criar " + user.getId());
                    userCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (isChecked) { // Checkbox selected

                            // get last list
                            String setUsers = sharedPreferences.getString("selectedUsers", "");
                            Set<String> selectList = new HashSet<>(Arrays.asList(setUsers.split(",")));

                            // add selected user
                            selectList.add(user.getId());

                            // save preferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            String setString = TextUtils.join(",", selectList);
                            editor.putString("selectedUsers", setString);
                            editor.apply();
                            Log.i("USER CONF", setString);


                        } else { // Checkbox deselected

                            // get last list
                            String setUsers = sharedPreferences.getString("selectedUsers", "");
                            Set<String> selectList = new HashSet<>(Arrays.asList(setUsers.split(",")));

                            // remove deselected user
                            selectList.remove(user.getId());

                            // save preferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            String setString = TextUtils.join(",", selectList);
                            editor.putString("selectedUsers", setString);
                            editor.apply();
                            Log.i("USER CONF", setString);
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
