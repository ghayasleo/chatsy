package com.example.chatsy.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatsy.ChatActivity;
import com.example.chatsy.R;
import com.example.chatsy.SearchUserActivity;
import com.example.chatsy.models.UserModel;
import com.example.chatsy.utils.AndriodUtil;
import com.example.chatsy.utils.FirebaseUtils;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class SearchUserRecyclerAdapter extends FirestoreRecyclerAdapter<UserModel, SearchUserRecyclerAdapter.UserModelViewHolder>{

    Context context;

    public SearchUserRecyclerAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options, Context context) {
        super(options);
        this.context = context;
    }

    //method1
    @Override
    protected void onBindViewHolder(@NonNull UserModelViewHolder holder, int position, @NonNull UserModel model) {
        //here we'll take data from model(db) and assign it to holder
        holder.usernameText.setText(model.getUserName());
        holder.phoneText.setText(model.getPhoneNumber());

        if(model.getUserId().equals(FirebaseUtils.currentUserID())){
            holder.usernameText.setText(model.getUserName() +" (Me)");
        }

        holder.itemView.setOnClickListener(v -> {
            // moving toward chatActivity after clicking on searched item
            Intent intent = new Intent(context, ChatActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            AndriodUtil.passUserModelAsIntent(intent,model);
            context.startActivity(intent);
        });
    }

    //method2
    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //here we have to assign 'search_user_recycler_row to class UserModelViewHolder through this method
        View view = LayoutInflater.from(context).inflate(R.layout.search_user_recycler_row,parent,false);
        return new UserModelViewHolder(view);
    }

    //method 1 and 2 ... these two methods came from firestoreRecyclerAdapter....

    class UserModelViewHolder extends RecyclerView.ViewHolder {

        TextView usernameText;
        TextView phoneText;
        ImageView profilePic;

        public UserModelViewHolder(@NonNull View itemView) {
           super(itemView);

           usernameText = itemView.findViewById(R.id.user_name_text);
           phoneText = itemView.findViewById(R.id.phone_text);
           profilePic = itemView.findViewById(R.id.profile_pic_image_view);





        }
    }
}
