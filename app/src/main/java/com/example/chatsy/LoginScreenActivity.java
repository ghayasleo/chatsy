package com.example.chatsy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

import java.security.AlgorithmParameterGenerator;
import java.util.HashMap;
import java.util.Map;

public class LoginScreenActivity extends AppCompatActivity {

    CountryCodePicker countryCodePicker;
    EditText phoneNumber;
    Button sendOtpButton;
    ProgressBar loginProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
      //  FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);


        countryCodePicker = findViewById(R.id.country_code_picker);
        phoneNumber = findViewById(R.id.edit_text_phone_number);
        sendOtpButton = findViewById(R.id.send_otp_button);
        loginProgressBar = findViewById(R.id.login_progress_bar);

        loginProgressBar.setVisibility(View.GONE);

        countryCodePicker.registerCarrierNumberEditText(phoneNumber); //connects country_code_picker to EditText phoneNumber

        sendOtpButton.setOnClickListener((v -> {
            if(!countryCodePicker.isValidFullNumber()){
                phoneNumber.setError("Invalid Phone Number");
                return;
            }
            Intent intent = new Intent(LoginScreenActivity.this, OtpConfirmationActivity.class);  //move to next activity
            intent.putExtra("phoneNumber",countryCodePicker.getFullNumberWithPlus());  //send input to next activity
            startActivity(intent);


           // System.out.println("otpScreen activity run successfully.");


//            Map<String, String> data = new HashMap<>();
//
//            FirebaseFirestore.getInstance().collection("Test").add(data);
        }));


    }
}