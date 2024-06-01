package com.example.chatsy;
import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.chatsy.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.messaging.FirebaseMessaging;


import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    EditText editTextSearchBar;

    ChatFragment chatFragment;
    SettingFragment settingFragment;
    GroupFragment groupFragment;
    SearchFragment searchFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);

        chatFragment = new ChatFragment();
        settingFragment = new SettingFragment();
        groupFragment = new GroupFragment();
        searchFragment = new SearchFragment();

        bottomNavigationView = findViewById(R.id.bottom_navigation_bar);


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.menu_chat){
                    //replacing frame_layout with chatFragment
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, chatFragment).commit();
                }
                if(item.getItemId() == R.id.menu_group){
                    //replacing frame_layout with settingFragment
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, groupFragment).commit();
                }
                if(item.getItemId() == R.id.menu_setting){
                    //replacing frame_layout with groupFragment
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, settingFragment).commit();
                }
                if(item.getItemId() == R.id.menu_search){
                    //replacing frame_layout with groupFragment
                    //getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, searchFragment).commit();
                    startActivity(new Intent(MainActivity.this, SearchUserActivity.class));
                }
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.menu_chat);


        getFCMToken();



    }

    private void getFCMToken() {
        Log.i("YOLO", "YO WASSUP GANG");
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        String token = task.getResult();
                        Log.i("My Token: ",token);
                        FirebaseUtils.currentUserDetails().update("fcmToken", token);
                    } else {
                        Log.i("ERROR", "AN ERROR OCCURED");
                    }
                });
    }
}