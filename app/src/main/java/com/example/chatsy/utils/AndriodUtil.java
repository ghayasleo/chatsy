package com.example.chatsy.utils;

import android.content.Context;
import android.widget.Toast;

public class AndriodUtil {

    public static void showToast(Context context, String message){

        Toast.makeText(context,message,Toast.LENGTH_LONG);

    }
}
