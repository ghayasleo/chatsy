package com.example.chatsy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.chatsy.models.UserModel;
import com.example.chatsy.utils.AndriodUtil;
import com.example.chatsy.utils.FirebaseUtils;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if(FirebaseUtils.isLoggedIn() && getIntent().getExtras() != null){
//            from notification
            String userId = getIntent().getExtras().getString("userId");
            FirebaseUtils.allUsersCollectionReference().document(userId).get()
                    .addOnCompleteListener(task -> {
                       if(task.isSuccessful()){
                           UserModel model = task.getResult().toObject(UserModel.class);

                           Intent mainIntent = new Intent(this, MainActivity.class);
                           mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                           startActivity(mainIntent);

                           Intent intent = new Intent(this, ChatActivity.class);
                           intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                           AndriodUtil.passUserModelAsIntent(intent,model);
                           startActivity(intent);
                           finish();
                       }
                    });


        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(FirebaseUtils.isLoggedIn()){
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    }else {
                        startActivity(new Intent(SplashActivity.this, LoginScreenActivity.class));
                    }
                    finish();


                }
            },1000);
        }


    }
}