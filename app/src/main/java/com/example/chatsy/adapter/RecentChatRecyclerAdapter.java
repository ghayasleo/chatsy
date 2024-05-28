package com.example.chatsy.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatsy.ChatActivity;
import com.example.chatsy.R;
import com.example.chatsy.SearchUserActivity;
import com.example.chatsy.models.ChatroomModel;
import com.example.chatsy.models.UserModel;
import com.example.chatsy.utils.AndriodUtil;
import com.example.chatsy.utils.FirebaseUtils;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatroomModel, RecentChatRecyclerAdapter.ChatroomModelViewHolder>{

    Context context;

    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatroomModel> options, Context context) {
        super(options);
        this.context = context;

    }

    //method1
    @Override
    protected void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position, @NonNull ChatroomModel model) {

        FirebaseUtils.getOtherUserFromChatroom(model.getUserId())
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){

                        boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(FirebaseUtils.currentUserID());

                        UserModel otherUserModel = task.getResult().toObject(UserModel.class);

                        FirebaseUtils.getOtherProfilePicStorageRef(otherUserModel.getUserId()).getDownloadUrl()
                                .addOnCompleteListener(t -> {
                                    if(t.isSuccessful()){
                                        Uri uri = t.getResult();
                                        AndriodUtil.setProfilePic(context,uri,holder.profilePic);
                                    }
                                });

                        holder.usernameText.setText(otherUserModel.getUserName());
                        if(lastMessageSentByMe)
                            holder.lastMessageText.setText("You : "+model.getLastMessage());
                        else
                            holder.lastMessageText.setText(model.getLastMessage());
                        holder.lastMessageTime.setText(FirebaseUtils.timestampToString(model.getLastMessageTimestamp()));

                        holder.itemView.setOnClickListener(v -> {
                            // moving toward chatActivity after clicking on searched item
                            Intent intent = new Intent(context, ChatActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            AndriodUtil.passUserModelAsIntent(intent,otherUserModel);
                            context.startActivity(intent);
                        });
                    }
                });

    }

    //method2
    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //here we have to assign 'search_user_recycler_row to class ChatroomModelViewHolder through this method
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row,parent,false);
        return new ChatroomModelViewHolder(view);
    }

    //method 1 and 2 ... these two methods came from firestoreRecyclerAdapter....

    class ChatroomModelViewHolder extends RecyclerView.ViewHolder {

        TextView usernameText;
        TextView lastMessageText;
        TextView lastMessageTime;
        ImageView profilePic;

        public ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);

            usernameText = itemView.findViewById(R.id.user_name_text);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
            lastMessageTime = itemView.findViewById(R.id.last_message_text_time);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);


        }

    }
}
