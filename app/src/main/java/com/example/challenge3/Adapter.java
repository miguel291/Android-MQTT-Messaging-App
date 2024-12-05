package com.example.challenge3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private final ArrayList<Chat> chats;

    // Constructor for initialization
    public Adapter(ArrayList<Chat> chats) {
        this.chats = chats;
    }

    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for individual chat items
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_conversation_list, parent, false);

        // Return a new ViewHolder
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
        // Get the current chat
        Chat chat = chats.get(position);

        // Set the sender's ID, message, and timestamp
        holder.sender.setText(chat.getSender().getId()); // Display sender ID
        holder.message.setText(chat.getMessage());       // Display message
        holder.timestamp.setText(chat.getTimestamp());   // Display timestamp
    }

    @Override
    public int getItemCount() {
        // Return the number of chats
        return chats.size();
    }

    // ViewHolder class to initialize the views
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView sender;
        TextView message;
        TextView timestamp;
        TextView lastMessage;

        public ViewHolder(View view) {
            super(view);

            // Initialize views from the layout
            sender = view.findViewById(R.id.ReceiverId);
            lastMessage = view.findViewById(R.id.lastMessage);
        }
    }
}

