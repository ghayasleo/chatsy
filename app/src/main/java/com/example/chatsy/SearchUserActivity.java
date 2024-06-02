package com.example.chatsy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.chatsy.adapter.SearchUserRecyclerAdapter;
import com.example.chatsy.models.UserModel;
import com.example.chatsy.utils.FirebaseUtils;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class SearchUserActivity extends AppCompatActivity {

//    ImageButton backButton;
    ImageButton searchButton;
    EditText searchUserName;
    RecyclerView recyclerView;
    TextView backButton;
    SearchUserRecyclerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        backButton = findViewById(R.id.back_btn_search_user);
        searchButton = findViewById(R.id.search_user_btn);
        searchUserName = findViewById(R.id.search_userInput_EditText);
        recyclerView = findViewById(R.id.search_recycler_view);

        searchUserName.requestFocus();

        backButton.setOnClickListener((v -> {
            onBackPressed();
        }));

        searchButton.setOnClickListener(v -> {
            String searchTerm = searchUserName.getText().toString();
            if(searchTerm.isEmpty() || searchTerm.length() < 3){
                searchUserName.setError("Invalid Input");
                return;
            }
            setupSearchRecycleView(searchTerm);
        });

    }

    private void setupSearchRecycleView(String searchTerm) {

        Query query = FirebaseUtils.allUsersCollectionReference()
                .whereGreaterThanOrEqualTo("userName",searchTerm);

        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class).build();

        adapter = new SearchUserRecyclerAdapter(options ,getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    //data changes will be reflected immediately
    @Override
    protected void onStart() {
        super.onStart();
        if(adapter != null){
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter != null){
            adapter.stopListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter != null){
            adapter.startListening();
        }
    }
}