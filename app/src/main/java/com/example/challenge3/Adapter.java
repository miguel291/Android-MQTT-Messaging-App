package com.example.challenge3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

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
}


