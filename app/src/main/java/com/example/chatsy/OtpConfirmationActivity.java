package com.example.chatsy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatsy.utils.AndriodUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.type.TimeOfDay;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

public class OtpConfirmationActivity extends AppCompatActivity {

    String phoneNumber;
    Long timeOutSecond = 60L;
    String verificationCode;     //to verify the otp
    PhoneAuthProvider.ForceResendingToken resendingToken;   //again resend otp to user

    TextView tShowNumberCode;
    EditText eEnterOtpCode;
    TextView tResendHere;
    Button verifyButton;
    ProgressBar progressBarOtpConfirmation;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_confirmation);

        tShowNumberCode = findViewById(R.id.show_number_code);
        eEnterOtpCode = findViewById(R.id.enter_otp_code);
        tResendHere = findViewById(R.id.resend_here_verification);
        verifyButton = findViewById(R.id.verify_button);
        progressBarOtpConfirmation = findViewById(R.id.otp_confirmation_progress_bar);

        phoneNumber = getIntent().getExtras().getString("phoneNumber");
//        Toast.makeText(getApplicationContext(),phoneNumber,Toast.LENGTH_LONG).show();

        tShowNumberCode.setText(phoneNumber);

        sendOtp(phoneNumber,false);

        verifyButton.setOnClickListener(v -> {
            String enteredOtp = eEnterOtpCode.getText().toString();
            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationCode,enteredOtp);
            signIn(phoneAuthCredential);
            setInProgress(true);
        });

        tResendHere.setOnClickListener((v)->{
            sendOtp(phoneNumber,true);
        });

    }

    void sendOtp(String phoneNumber, boolean isReSend){
        setInProgress(true);
        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(timeOutSecond, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signIn(phoneAuthCredential);
                                AndriodUtil.showToast(getApplicationContext(),"Verification Completed.");
                                setInProgress(false);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                AndriodUtil.showToast(getApplicationContext(), "OTP Verification failed1");
                                setInProgress(false);
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                verificationCode = s;
                                resendingToken = forceResendingToken;
                                AndriodUtil.showToast(getApplicationContext(),"OTP sent successfully");
                                setInProgress(false);
                            }
                        });
        if(isReSend){
            //otp resend not for the first time
            //if it is resend we'll verifying with 'resendingToken' bcz we've restored that
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        }else{
            PhoneAuthProvider.verifyPhoneNumber(builder.build());  //otp send for the first time
        }
    }


    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBarOtpConfirmation.setVisibility(View.VISIBLE);
            verifyButton.setVisibility(View.GONE);
        }else {
            progressBarOtpConfirmation.setVisibility(View.GONE);
            verifyButton.setVisibility(View.VISIBLE);
        }

    }

    void signIn(PhoneAuthCredential phoneAuthCredential){
        setInProgress(true);
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                setInProgress(false);
                if(task.isSuccessful()){
                    Intent intent = new Intent(OtpConfirmationActivity.this, LoginUserNameActivity.class);
                    intent.putExtra("phone",phoneNumber);
                    startActivity(intent);

                }else {
                    AndriodUtil.showToast(getApplicationContext(),"OTP Verification failed.");
                }
            }
        });
    }


}