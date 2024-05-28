package com.example.chatsy;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chatsy.models.UserModel;
import com.example.chatsy.utils.AndriodUtil;
import com.example.chatsy.utils.FirebaseUtils;
import com.github.dhaval2404.imagepicker.ImagePicker;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class SettingFragment extends Fragment {

    ImageView profilePic;
    EditText usernameInput;
    EditText phoneInput;
    Button updateProfileBtn;
    ProgressBar progressBar;
    TextView logoutBtn;

    UserModel currentUserModel;
    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;


    public SettingFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if(data != null && data.getData() != null){
                            // get the uri from the user in selectedImageUri
                            selectedImageUri = data.getData();
                            AndriodUtil.setProfilePic(getContext(),selectedImageUri,profilePic);
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_setting, container, false);

        profilePic = view.findViewById(R.id.profile_image_view);
        usernameInput = view.findViewById(R.id.username_profile);
        phoneInput = view.findViewById(R.id.phone_number_profile);
        updateProfileBtn = view.findViewById(R.id.profile_update_btn);
        progressBar = view.findViewById(R.id.profile_progress_bar);
        logoutBtn = view.findViewById(R.id.logout_btn);

        getUserDate();

        updateProfileBtn.setOnClickListener(v -> {
            updateBtnClick();
        });

        logoutBtn.setOnClickListener(v -> {
            FirebaseUtils.logout();
            Intent intent = new Intent(getContext(),SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        profilePic.setOnClickListener(v -> {
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512,512).
                    createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePickLauncher.launch(intent);
                            return null;
                        }
                    });
        });

        return view;
    }

    void updateBtnClick(){

        String newUsernmae = usernameInput.getText().toString();
        if(newUsernmae.isEmpty() || newUsernmae.length() < 3){
            usernameInput.setError("User Name should be greater than 3 characters");
            return;
        }
        currentUserModel.setUserName(newUsernmae);
        setInProgress(true);

        if(selectedImageUri != null){
            FirebaseUtils.getCurrentProfilePicStorageRef().putFile(selectedImageUri).addOnCompleteListener(task -> {
                updateToFirestore();
            });
        }else{
            updateToFirestore();
        }



    }

    void updateToFirestore(){

        FirebaseUtils.currentUserDetails().set(currentUserModel)
                .addOnCompleteListener(task -> {
                    setInProgress(false);
                    if(task.isSuccessful()){
                        AndriodUtil.showToast(getContext(),"Update Successfully");
                    }else{
                        AndriodUtil.showToast(getContext(),"Update Failed");
                    }
                });

    }

    private void getUserDate() {

        setInProgress(true);

        FirebaseUtils.getCurrentProfilePicStorageRef().getDownloadUrl()
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                Uri uri = task.getResult();
                                AndriodUtil.setProfilePic(getContext(),uri,profilePic);
                            }
                        });

        FirebaseUtils.currentUserDetails().get().addOnCompleteListener(task -> {
            setInProgress(false);
            currentUserModel = task.getResult().toObject(UserModel.class);
            usernameInput.setText(currentUserModel.getUserName());
            phoneInput.setText(currentUserModel.getPhoneNumber());
        });

    }

    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            updateProfileBtn.setVisibility(View.GONE);
        }else {
            progressBar.setVisibility(View.GONE);
            updateProfileBtn.setVisibility(View.VISIBLE);
        }

    }
}