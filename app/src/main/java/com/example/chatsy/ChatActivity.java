package com.example.chatsy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.chatsy.adapter.ChatRecyclerAdapter;
import com.example.chatsy.models.ChatMessageModel;
import com.example.chatsy.models.ChatroomModel;
import com.example.chatsy.models.UserModel;
import com.example.chatsy.utils.AndriodUtil;
import com.example.chatsy.utils.FirebaseUtils;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class ChatActivity extends AppCompatActivity {

    UserModel otherUser;
    String chatroomId;
    ChatroomModel chatroomModel;

    ImageButton backBtn;
    TextView otherUsername;
    EditText sendMessageInput;
    ImageButton sendMessageBtn;

    RecyclerView recyclerView;
    ChatRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat2);

        //get UserModel
        otherUser = AndriodUtil.getUserModelFromIntent(getIntent());
        chatroomId = FirebaseUtils.getChatroomId(FirebaseUtils.currentUserID(),otherUser.getUserId());

        backBtn = findViewById(R.id.back_btn_chat);
        otherUsername = findViewById(R.id.other_username);
        sendMessageInput = findViewById(R.id.send_message_edit_text);
        sendMessageBtn = findViewById(R.id.send_message_btn);
        recyclerView = findViewById(R.id.recycler_view_chatting);

        backBtn.setOnClickListener(v -> {
            onBackPressed();
        });

        otherUsername.setText(otherUser.getUserName());

        sendMessageBtn.setOnClickListener(v -> {
            String message = sendMessageInput.getText().toString().trim();
            if(message.isEmpty())
                return;
            sendMessageToUser(message);
        });

        getOrCreateChatroomModel();
        setupChatRecyclerview();

    }

    private void setupChatRecyclerview() {

        Query query = FirebaseUtils.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp",Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class).build();

        adapter = new ChatRecyclerAdapter(options ,getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();

        //automatic scroll when new message send
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });

    }

    private void sendMessageToUser(String message) {
        //setting lastMessageSenderId & lastMessageTimestamp chatroomModel
        chatroomModel.setLastMessageSenderId(FirebaseUtils.currentUserID());
        chatroomModel.setLastMessageTimestamp(Timestamp.now());

        //then setting into firebase
        FirebaseUtils.getChatroomReference(chatroomId).set(chatroomModel);

        ChatMessageModel chatMessageModel = new ChatMessageModel(message,FirebaseUtils.currentUserID(),Timestamp.now());

        FirebaseUtils.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                 .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
                    sendMessageInput.setText("");
                }
            }
        });


    }

    private void getOrCreateChatroomModel() {
        FirebaseUtils.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                //gives chatroomModel from DB
                chatroomModel = task.getResult().toObject(ChatroomModel.class);
                if(chatroomModel == null){
                    //first time chat
                    chatroomModel = new ChatroomModel(
                            chatroomId,
                            Arrays.asList(FirebaseUtils.currentUserID(),otherUser.getUserId()),
                            Timestamp.now(),
                            ""
                    );
                    //adding into Firebase
                    FirebaseUtils.getChatroomReference(chatroomId).set(chatroomModel);
                }
            }
        });
    }
}