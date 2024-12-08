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
    private final OnChatClickListener clickListener;
    private final OnChatLongClickListener longClickListener;

    public Adapter(ArrayList<Chat> chats, OnChatClickListener clickListener, OnChatLongClickListener longClickListener) {
        this.chats = chats;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_line, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = chats.get(position);
        holder.bind(chat, clickListener, longClickListener);
    }

    @Override
    public int getItemCount() {
        return chats.size();
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

    public interface OnChatClickListener {
        void onChatClick(Chat chat);
    }

    public interface OnChatLongClickListener {
        void onChatLongClick(int position);
    }
}



