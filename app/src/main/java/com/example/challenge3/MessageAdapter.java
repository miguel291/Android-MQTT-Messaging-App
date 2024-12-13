
package com.example.challenge3;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private final List<Message> messages;
    private final String loggedInUserId; // Add this field

    public MessageAdapter(List<Message> messages, String loggedInUserId) {
        this.messages = messages;
        this.loggedInUserId = loggedInUserId; // Initialize in constructor
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_line, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);

        holder.messageText.setText(message.getContent());
        holder.timestamp.setText(message.getTimestamp());

        //boolean isSent = message.getSender().equals(loggedInUserId);
        boolean isSent = true;

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.messageContainer.getLayoutParams();
        if (message.getSender().getId().equals(loggedInUserId)) {
            params.gravity = Gravity.END;
            holder.messageContainer.setBackgroundResource(R.drawable.message_sent_background);
        } else {
            params.gravity = Gravity.START;
            holder.messageContainer.setBackgroundResource(R.drawable.message_received_background);
        }
        holder.messageContainer.setLayoutParams(params);
    }


    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void updateMessages(List<Message> newMessages) {
        if (newMessages != null) {
            Log.d("MessageAdapter", "Updating messages with: " + newMessages.size() + " new messages.");
            this.messages.clear();
            this.messages.addAll(newMessages);
            notifyDataSetChanged();
        }
    }



    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView messageText;
        private final TextView timestamp;
        private final LinearLayout messageContainer;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            timestamp = itemView.findViewById(R.id.timestamp);
            messageContainer = itemView.findViewById(R.id.messageContainer);
        }
    }
}
