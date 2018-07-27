package uc.edu.vuhi.pokerprojectapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
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
    private FirebaseFirestore mStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);
        ButterKnife.bind(this);
        setSupportActionBar(toolbarSetUp);
        getSupportActionBar().setTitle("Account Setting Page");
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mStore = FirebaseFirestore.getInstance();
        probAccountSetting.setVisibility(View.INVISIBLE);
    }

    //Need to set up the page when loading if user is already register

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
    public void save(){
        //NickName need to be unique
        final String stringNickName = txtNickName.getText().toString();

        if(!TextUtils.isEmpty(stringNickName) && profileImg != null){
            final String userId = mAuth.getCurrentUser().getUid();
            StorageReference imgPath = mStorageRef.child("profile").child(userId+".jpg");
            probAccountSetting.setVisibility(View.VISIBLE);
            imgPath.putFile(profileImg).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){

                        Uri downloadPath = task.getResult().getDownloadUrl();

                        Map<String, String> user = new HashMap<>();
                        user.put("email", mAuth.getCurrentUser().getEmail());
                        user.put("nickName", stringNickName);
                        user.put("imagePath", downloadPath.toString());

                        mStore.collection("Users").document(userId).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(SetUpActivity.this, "Saving", Toast.LENGTH_LONG).show();
                                    Utility.sendTo(SetUpActivity.this, MainActivity.class);
                                } else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(SetUpActivity.this, "An error occurred while saving setting: " + error, Toast.LENGTH_LONG).show();

                                }
                            }
                        });

                    }
                    else{
                        String error = task.getException().getMessage();
                        Toast.makeText(SetUpActivity.this, "An error occurred while saving setting: " + error, Toast.LENGTH_LONG).show();
                    }
                    probAccountSetting.setVisibility(View.INVISIBLE);
                }
            });
        }
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
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(SetUpActivity.this, "An error occurred while cropping image", Toast.LENGTH_LONG).show();
            }
        }
    }


}
