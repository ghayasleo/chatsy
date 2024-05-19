package com.example.chatsy.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.chatsy.models.UserModel;
import com.google.firebase.firestore.auth.User;

public class AndriodUtil {

    public static void showToast(Context context, String message){

        Toast.makeText(context,message,Toast.LENGTH_LONG).show();

    }

    public static void passUserModelAsIntent(Intent intent, UserModel userModel){
        intent.putExtra("username",userModel.getUserName());
        intent.putExtra("phone",userModel.getPhoneNumber());
        intent.putExtra("userId",userModel.getUserId());
    }

    public static UserModel getUserModelFromIntent(Intent intent){
        UserModel userModel = new UserModel();
        userModel.setUserName(intent.getStringExtra("username"));
        userModel.setUserId(intent.getStringExtra("userId"));
        userModel.setPhoneNumber(intent.getStringExtra("phone"));
        return userModel;
    }
}
