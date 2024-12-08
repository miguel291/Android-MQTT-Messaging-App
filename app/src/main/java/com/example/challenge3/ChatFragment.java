package com.example.challenge3;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

public class ChatFragment extends Fragment {
    private static final String ARG_CHAT = "chat";
    private Chat chat;

    public static ChatFragment newInstance(Chat chat) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CHAT, chat);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            chat = (Chat) getArguments().getSerializable(ARG_CHAT);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        // Controlla che la chat sia valida
        if (chat == null) {
            Toast.makeText(requireContext(), "Chat data is missing", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
            return view;
        }

        // Configura dinamicamente il titolo della Toolbar
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(chat.getUser2() != null ? chat.getUser2().getId() : "Unknown User");
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back); // Usa la tua icona di navigazione
            toolbar.setNavigationOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        }

        // Configura il RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.conversationList);
        if (chat.getMessages() == null) {
            chat.setMessages(new ArrayList<>()); // Inizializza lista vuota se null
        }
        MessageAdapter adapter = new MessageAdapter(chat.getMessages());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        return view;
    }

}
