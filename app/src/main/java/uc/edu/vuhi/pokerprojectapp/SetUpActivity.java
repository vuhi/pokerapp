package uc.edu.vuhi.pokerprojectapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import de.hdodenhof.circleimageview.CircleImageView;
import uc.edu.vuhi.pokerprojectapp.DTO.UserDTO;
import uc.edu.vuhi.pokerprojectapp.UTIL.Utility;

public class SetUpActivity extends AppCompatActivity {

    public static final int READ_EXTERNAL_PERMISSION_REQUEST_CODE = 1000;

    @BindView(R.id.toolbarSetUp)
    Toolbar toolbarSetUp;

    @BindView(R.id.circleImgProfile)
    CircleImageView circleImgProfile;

    @BindView(R.id.txtNickName)
    EditText txtNickName;

    @BindView(R.id.btnSaveAccountSetting)
    Button btnSaveAccountSetting;

    @BindView(R.id.probAccountSetting)
    ProgressBar probAccountSetting;

    private Uri profileImg = null;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;
    private String userId;
    private UserDTO retrievedUser;
    private boolean isImageChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);
        ButterKnife.bind(this);
        setSupportActionBar(toolbarSetUp);
        getSupportActionBar().setTitle("Account Setting Page");
        //Set up back button
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseFirestore.getInstance();
        probAccountSetting.setVisibility(View.INVISIBLE);
        btnSaveAccountSetting.setEnabled(false);

        //Retrieve user information when loading activity
        mDatabase.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    //There is a record of user in database
                    if(task.getResult().exists()){
                        //Retrieve information
                        retrievedUser = task.getResult().toObject(UserDTO.class);
                        //set up
                        profileImg = Uri.parse(retrievedUser.getImagePath());
                        txtNickName.setText(retrievedUser.getNickname());
                        //Use glide library to set img view at run time.
                        Glide.with(SetUpActivity.this).load(retrievedUser.getImagePath()).into(circleImgProfile);
                        btnSaveAccountSetting.setEnabled(true);
                    }
                    //No record
                    else {
                        Toast.makeText(SetUpActivity.this, "Please fill in your information" , Toast.LENGTH_LONG).show();
                        btnSaveAccountSetting.setEnabled(false);
                    }
                }
                else {
                    Toast.makeText(SetUpActivity.this, "An error occurred while retrieving user" , Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //Enable button when there is text inside
    @OnTextChanged(R.id.txtNickName)
    public void enable(){
        if(!TextUtils.isEmpty(txtNickName.getText().toString())){
            btnSaveAccountSetting.setEnabled(true);
        }else {
            btnSaveAccountSetting.setEnabled(false);
        }
    }

    @OnClick(R.id.circleImgProfile)
    public void setProfile(){
        //Backward compatible check
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(SetUpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(SetUpActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_PERMISSION_REQUEST_CODE);
            } else {
                imagePicker();
            }
        } else {
            imagePicker();
        }
    }

    @OnClick(R.id.btnSaveAccountSetting)
    public void saveAccountSetting(){

        final String stringNickName = txtNickName.getText().toString(); //Need to check unique NickName (Not implement yet)

        /*mDatabase.collection("Users").whereEqualTo("nickname", stringNickName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().getDocuments().isEmpty()){
                        Toast.makeText(SetUpActivity.this, "no data" , Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(SetUpActivity.this, "exist" , Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(SetUpActivity.this, "An error occurred" , Toast.LENGTH_LONG).show();
                }
            }
        });*/

        if(isImageChanged){
            if(!TextUtils.isEmpty(stringNickName) && profileImg != null){
                probAccountSetting.setVisibility(View.VISIBLE);
                StorageReference imgPath = mStorageRef.child("profile").child(userId+".jpg");
                imgPath.putFile(profileImg).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            saveToDataBase(task, stringNickName);
                        }
                        else{
                            String error = task.getException().getMessage();
                            Toast.makeText(SetUpActivity.this, "An error occurred while saving setting: " + error, Toast.LENGTH_LONG).show();
                        }
                        probAccountSetting.setVisibility(View.INVISIBLE);
                    }
                });
            }
            else {
                Toast.makeText(SetUpActivity.this, "Please fill in your information" , Toast.LENGTH_LONG).show();
            }
        }
        //User does not upload new img
        else {
            //Prevent user save information without select image
            if(profileImg == null){
                Toast.makeText(SetUpActivity.this, "Please update your image" , Toast.LENGTH_LONG).show();
            }
            else {
                saveToDataBase( null, stringNickName);
            }
        }
    }


    private void saveToDataBase(@NonNull Task<UploadTask.TaskSnapshot> task, String stringNickName){

        Uri downloadPath;
        //Upload new image
        if(task != null){
            downloadPath = task.getResult().getDownloadUrl();
            UserDTO user = new UserDTO(mAuth.getCurrentUser().getEmail(),stringNickName, downloadPath.toString());
        }
        //Does not upload any image, use the old image retrieve from database
        else {
            downloadPath = profileImg;
        }

        UserDTO user = new UserDTO(mAuth.getCurrentUser().getEmail(),stringNickName, downloadPath.toString());
        //Add user information to database
        mDatabase.collection("Users").document(userId).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(SetUpActivity.this, "Saving", Toast.LENGTH_LONG).show();
                    Utility.sendTo(SetUpActivity.this, MainActivity.class, false);
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(SetUpActivity.this, "An error occurred while saving setting: " + error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_EXTERNAL_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                imagePicker();
            } else {
                Toast.makeText(SetUpActivity.this, "Permission Denied!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void imagePicker() {
        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1, 1)
            .start(SetUpActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                profileImg = result.getUri();
                circleImgProfile.setImageURI(profileImg);
                //Select new image
                isImageChanged = true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(SetUpActivity.this, "An error occurred while cropping image", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Return to previous activity
                finish();
                return super.onOptionsItemSelected(item);

            default:
                return false;
        }
    }
}
