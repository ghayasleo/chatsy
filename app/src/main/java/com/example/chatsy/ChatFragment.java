package com.example.chatsy;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.chatsy.adapter.RecentChatRecyclerAdapter;
import com.example.chatsy.adapter.SearchUserRecyclerAdapter;
import com.example.chatsy.models.ChatroomModel;
import com.example.chatsy.models.UserModel;
import com.example.chatsy.utils.FirebaseUtils;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class ChatFragment extends Fragment {

    RecyclerView recyclerView;
    RecentChatRecyclerAdapter adapter;
    EditText searchBar;
    ImageButton searchBtn;

    public ChatFragment() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_chat_fragment);
        searchBtn = view.findViewById(R.id.search_user_btn);
        searchBar = view.findViewById(R.id.search_userInput_EditText);

        searchBar.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SearchUserActivity.class);
            startActivity(intent);

        });

        searchBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SearchUserActivity.class);
            startActivity(intent);
        });

        setupRecyclerView();

        return view;
    }


    private void setupRecyclerView() {

        Query query = FirebaseUtils.allChatroomCollectionReference()
                .whereArrayContains("userId",FirebaseUtils.currentUserID()).orderBy("lastMessageTimestamp",Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatroomModel> options = new FirestoreRecyclerOptions.Builder<ChatroomModel>()
                .setQuery(query, ChatroomModel.class).build();

        adapter = new RecentChatRecyclerAdapter(options ,getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    //data changes will be reflected immediately
    @Override
    public void onStart() {
        super.onStart();
        if(adapter != null){
            adapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(adapter != null){
            adapter.stopListening();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(adapter != null){
            adapter.notifyDataSetChanged();
        }
    }
}