package com.example.chatsy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.chatsy.models.UserModel;
import com.example.chatsy.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

public class LoginUserNameActivity extends AppCompatActivity {

    EditText usernameInput;
    Button userNameDoneButton;
    ProgressBar loginUserNameProgressBar;
    UserModel userModel;

    String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user_name);

        usernameInput = findViewById(R.id.edit_text_enter_username);
        userNameDoneButton = findViewById(R.id.login_username_done_button);
        loginUserNameProgressBar = findViewById(R.id.login_username_progress_bar);

        phoneNumber = getIntent().getExtras().getString("phone");
        getUserName();

        userNameDoneButton.setOnClickListener((v -> {
            setUsername();
        }));

    }

    private void setUsername() {

        String username = usernameInput.getText().toString();
        if(username.isEmpty() || username.length() < 3){
            usernameInput.setError("User Name should be greater than 3 characters");
            return;
        }
        setInProgress(true);
        if(userModel != null){
            userModel.setUserName(username);
        }else{
            userModel = new UserModel(phoneNumber,username, Timestamp.now());
        }

        FirebaseUtils.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                setInProgress(false);
                if(task.isSuccessful()){
                    Intent intent = new Intent(LoginUserNameActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

            }
        });

    }

    private void getUserName() {
        setInProgress(true);
        FirebaseUtils.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                setInProgress(false);
                if(task.isSuccessful()){
                    userModel = task.getResult().toObject(UserModel.class);   //storing userModel data here
                    if(userModel != null){
                        usernameInput.setText(userModel.getUserName());
                    }
                }
            }
        });

    }


    void setInProgress(boolean inProgress){
        if(inProgress){
            loginUserNameProgressBar.setVisibility(View.VISIBLE);
            userNameDoneButton.setVisibility(View.GONE);
        }else {
            loginUserNameProgressBar.setVisibility(View.GONE);
            userNameDoneButton.setVisibility(View.VISIBLE);
        }

    }
}