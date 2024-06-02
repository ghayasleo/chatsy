package com.example.chatsy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    UserModel otherUser;
    String chatroomId;
    ChatroomModel chatroomModel;
    Context context;
    ImageButton backBtn;
    TextView otherUsername;
    EditText sendMessageInput;
    ImageButton sendMessageBtn;
    ImageView imageView;

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
        imageView = findViewById(R.id.profile_pic_image_view);

        FirebaseUtils.getOtherProfilePicStorageRef(otherUser.getUserId()).getDownloadUrl()
                .addOnCompleteListener(t -> {
                    if(t.isSuccessful()){
                        Uri uri = t.getResult();
                        AndriodUtil.setProfilePic(this,uri,imageView);
                    }
                });


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
        chatroomModel.setLastMessage(message);

        //then setting into firebase
        FirebaseUtils.getChatroomReference(chatroomId).set(chatroomModel);

        ChatMessageModel chatMessageModel = new ChatMessageModel(message,FirebaseUtils.currentUserID(),Timestamp.now());

        FirebaseUtils.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                 .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()) {
                    sendNotification(String.valueOf(sendMessageInput.getText()));
                    sendMessageInput.setText("");
                }
            }
        });
    }

    public void sendNotification(String message) {
        String postUrl = "https://fcm.googleapis.com/v1/projects/chatsy-android-app/messages:send";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject mainObj = new JSONObject();
        FirebaseUtils.currentUserDetails().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                UserModel currentUserModel = task.getResult().toObject(UserModel.class);
                try {
                    JSONObject messageObject = new JSONObject();
                    JSONObject notificationObject = new JSONObject();
                    notificationObject.put("title", currentUserModel.getUserName());
                    notificationObject.put("body", message);
                    messageObject.put("token", otherUser.getFcmToken());
                    messageObject.put("notification", notificationObject);
                    mainObj.put("message", messageObject);
                    Log.i("MARK", "Test 1");

                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, mainObj, response -> {
                        //
                    }, volleyError -> {
                        Log.i("MARK", "Test 2");
                        Log.i("FATAL ERROR", "" + Arrays.toString(volleyError.getStackTrace()));
                    }) {
                        @NonNull
                        @Override
                        public Map<String, String> getHeaders() {
                            AccessToken accessToken = new AccessToken();
                            String accessKey = accessToken.getAccessToken();
                            Map<String, String> header = new HashMap<>();
                            header.put("content-type", "application/json");
                            header.put("authorization", "Bearer " + accessKey);
                            return header;
                        }
                    };

                    Log.i("MARK", "Test 3");
                    requestQueue.add(request);
                    Log.i("MARK", "Test 4");

                } catch(JSONException err) {
                    Log.e("FATAL ERROR", "" + err.getMessage());
                    err.printStackTrace();
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