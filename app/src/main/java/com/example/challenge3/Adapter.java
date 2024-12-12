package com.example.challenge3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private final ArrayList<User> users;
    private final OnItemClickListener clickListener;
    private final OnItemLongClickListener longClickListener;

    public Adapter(ArrayList<User> users, OnItemClickListener clickListener, OnItemLongClickListener longClickListener) {
        this.users = users;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        holder.usernameTextView.setText(user.getId());
        holder.itemView.setOnClickListener(v -> clickListener.onItemClick(user));
        holder.itemView.setOnLongClickListener(v -> {
            longClickListener.onItemLongClick(position);
            return true;
        });
    }



    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView usernameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(User user);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }
}

/*public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private List<Chat> chats;
    private final OnChatClickListener clickListener;
    private final OnChatLongClickListener longClickListener;

    public Adapter(List<Chat> chats, OnChatClickListener clickListener, OnChatLongClickListener longClickListener) {
        this.chats = chats;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    public interface OnChatClickListener {
        void onChatClick(Chat chat);
    }

    public interface OnChatLongClickListener {
        void onChatLongClick(int position);
    }

    public void updateChats(List<Chat> newChats) {
        this.chats = newChats;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return chats != null ? chats.size() : 0;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_line, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = chats.get(position);
        holder.bind(chat, clickListener, longClickListener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(Chat chat, OnChatClickListener clickListener, OnChatLongClickListener longClickListener) {
            itemView.setOnClickListener(v -> clickListener.onChatClick(chat));
            itemView.setOnLongClickListener(v -> {
                longClickListener.onChatLongClick(getAdapterPosition());
                return true;
            });
        }
    }
}*/


