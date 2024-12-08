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
        // Inflate the correct layout for individual chat items
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_line, parent, false); // Use item_chat.xml
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
        // Get the current chat
        Chat chat = chats.get(position);

        // Set the sender's ID and receiver's ID
        //holder.sender.setText(chat.getUser1().getId()); // Display sender ID
        holder.receiver.setText(chat.getUser2().getId()); // Display receiver ID

        // Get the most recent message, if any
        ArrayList<Message> messages = chat.getMessages();
        if (!messages.isEmpty()) {
            Message lastMessage = messages.get(messages.size() - 1); // Get the last message
            holder.message.setText(lastMessage.getContent()); // Display the last message content
            holder.timestamp.setText(lastMessage.getTimestamp()); // Display the last message timestamp
        } else {
            holder.message.setText("No messages yet"); // Display placeholder if no messages
            holder.timestamp.setText(""); // Clear the timestamp if no messages
        }
    }


    @Override
    public int getItemCount() {
        // Return the number of chats
        return chats.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView receiver;
        TextView message;
        TextView timestamp;

        public ViewHolder(View view) {
            super(view);

            // Initialize views from the layout
            receiver = view.findViewById(R.id.ReceiverId); // Ensure this ID exists in your layout
            message = view.findViewById(R.id.lastMessage); // Ensure this ID exists in your layout
            timestamp = view.findViewById(R.id.timestamp); // Ensure this ID exists in your layout
        }
    }
}



