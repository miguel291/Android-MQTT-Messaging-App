package com.example.challenge3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ArduinoConfigAdapter extends RecyclerView.Adapter<ArduinoConfigAdapter.ViewHolder> {

    private ArrayList<User> userArrayList;


    public ArduinoConfigAdapter(ArrayList<User> allUsers) {
        this.userArrayList = allUsers;
    }

    @Override
    public int getItemCount() {
        return userArrayList != null ? userArrayList.size() : 0;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.arduino_config_line, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userArrayList.get(position);
        holder.bind(user);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(User user) {
            /*materialSwitch.setOnCheckedChangeListener { buttonView, isChecked
                // Responds to switch being checked/unchecked
            }*/


            /*itemView.setOnClickListener(v -> clickListener.onChatClick(chat));
            itemView.setOnLongClickListener(v -> {
                longClickListener.onChatLongClick(getAdapterPosition());
                return true;
            });*/
        }
    }
}


