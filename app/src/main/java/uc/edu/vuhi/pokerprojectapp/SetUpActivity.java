package uc.edu.vuhi.pokerprojectapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
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

    /**
     * Variable to store Uri of profile image, use as a flag for no image Uri
     */
    private Uri profileImg = null;

    /**
     * Fire base storage instance, use to upload file to fire base
     */
    private StorageReference mStorageRef;

    /**
     * Fire base Authentication instance
     */
    private FirebaseAuth mAuth;

    /**
     * Fire base database instance, use to save information in fire base database
     */
    private FirebaseFirestore mDatabase;
    private String userId;
    private UserDTO retrievedUser;

    /**
     * A flag to check if user select a new img
     */
    private boolean isImageChanged = false;

    /**
     * A flag to check if user is updating information or initiating a new record
     */
    private boolean isUpdate = false;
    private String currentNickName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_set_up);
        ButterKnife.bind(this);
        setSupportActionBar(toolbarSetUp);
        getSupportActionBar().setTitle(R.string.accountSettingTitle);
        //Set up back button
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseFirestore.getInstance();
        probAccountSetting.setVisibility(View.INVISIBLE);
        btnSaveAccountSetting.setEnabled(false);
        loadingUserInformation();
    }

    /**
     * Handle go back button, to go back to main page
     */
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

    /**
     * Select image from library or take a photo, then crop
     */
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

    /**
     * Handle permission for READ_EXTERNAL_STORAGE
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_EXTERNAL_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                imagePicker();
            } else {
                Toast.makeText(SetUpActivity.this, R.string.permissionDenied, Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Get image uri to set to the place holder, turn on the flag isImageChanged
     * @param requestCode - request code of image cropping
     * @param resultCode - user allow or not
     * @param data - contains image data
     */
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
                Toast.makeText(SetUpActivity.this, R.string.cropingImgError, Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Make sure user has a unique nickname (case sensitive), if not disable save button
     * @param editText - txtNickName Edit text
     * @param hasFocus - boolean represent focus of edit text
     */
    @OnFocusChange(R.id.txtNickName)
    public void validateNickName(View editText, boolean hasFocus){
        if(!hasFocus){
            String stringNickName = txtNickName.getText().toString();
            if(!stringNickName.equals(currentNickName) && !TextUtils.isEmpty(stringNickName)){
                mDatabase.collection("Users").whereEqualTo("nickname", stringNickName).get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        if(!task.getResult().getDocuments().isEmpty()){
                            Toast.makeText(SetUpActivity.this, R.string.nickNameError , Toast.LENGTH_LONG).show();
                        }else {
                            btnSaveAccountSetting.setEnabled(true);
                        }
                    }
                    else {
                        Toast.makeText(SetUpActivity.this, R.string.genericError , Toast.LENGTH_LONG).show();
                    }
                });
            }
        }else {
            btnSaveAccountSetting.setEnabled(false);
        }
    }

    /**
     * Save user information to database
     */
    @OnClick(R.id.btnSaveAccountSetting)
    public void saveAccountSetting() {
        final String stringNickName = txtNickName.getText().toString();
        //User upload new img
        if (isImageChanged) {
            if (!TextUtils.isEmpty(stringNickName) && profileImg != null) {
                probAccountSetting.setVisibility(View.VISIBLE);
                StorageReference imgPath = mStorageRef.child("profile").child(userId + ".jpg");
                imgPath.putFile(profileImg).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        saveToDataBase(task, stringNickName);
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(SetUpActivity.this, R.string.genericError + error, Toast.LENGTH_LONG).show();
                    }
                    probAccountSetting.setVisibility(View.INVISIBLE);
                });
            } else {
                Toast.makeText(SetUpActivity.this, R.string.fillInRemainder, Toast.LENGTH_LONG).show();
            }
        }
        //User does not upload new img
        else {
            //Prevent user save information without select image
            if (profileImg == null) {
                Toast.makeText(SetUpActivity.this, R.string.undateImgReminder, Toast.LENGTH_LONG).show();
            } else {
                saveToDataBase(null, stringNickName);
            }
        }
    }

    /**
     * Set up user information if there is any when loading
     */
    private void loadingUserInformation() {
        //Retrieve user information when loading activity
        mDatabase.collection("Users").document(userId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                //There is a record of user in database
                if(task.getResult().exists()){
                    //Retrieve information
                    retrievedUser = task.getResult().toObject(UserDTO.class);
                    //set up
                    profileImg = Uri.parse(retrievedUser.getImagePath());
                    currentNickName = retrievedUser.getNickname();
                    txtNickName.setText(currentNickName);
                    //Use glide library to set img view at run time.
                    Glide.with(SetUpActivity.this).load(retrievedUser.getImagePath()).into(circleImgProfile);
                    isUpdate = true;
                }
                //No record
                else {
                    Toast.makeText(SetUpActivity.this, R.string.fillInRemainder , Toast.LENGTH_LONG).show();
                    btnSaveAccountSetting.setEnabled(false);
                }
            }
            else {
                Toast.makeText(SetUpActivity.this, R.string.genericError , Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Perform new user initiation or a update record in database
     * @param task - task from fire-base when upload image
     * @param stringNickName - user's nickname
     */
    private void saveToDataBase(@NonNull Task<UploadTask.TaskSnapshot> task, String stringNickName){
        Uri downloadPath;
        //User Upload new image
        if(task != null){
            downloadPath = task.getResult().getDownloadUrl();
            UserDTO user = new UserDTO(mAuth.getCurrentUser().getEmail(),stringNickName, downloadPath.toString());
        }
        //User does not upload any image, use the old image retrieve from database
        else {
            downloadPath = profileImg;
        }
        //This is an update operation
        if(isUpdate){
            mDatabase.collection("Users").document(userId).update("imagePath", downloadPath.toString());
            mDatabase.collection("Users").document(userId)
                .update("nickname", stringNickName).addOnCompleteListener(task1 -> {
                    if(!task1.isSuccessful()){
                        Toast.makeText(SetUpActivity.this, R.string.genericError , Toast.LENGTH_LONG).show();
                    }
                });

        }
        //This is a new user initiation
        else {
            //Initiate a new usrDTO
            UserDTO user = new UserDTO(mAuth.getCurrentUser().getEmail(),stringNickName, downloadPath.toString());
            //Add user information to database
            mDatabase.collection("Users").document(userId).set(user).addOnCompleteListener(task12 -> {
                if(task12.isSuccessful()){
                    Toast.makeText(SetUpActivity.this, R.string.saving, Toast.LENGTH_SHORT).show();
                    Utility.sendTo(SetUpActivity.this, MainActivity.class, true);
                } else {
                    String error = task12.getException().getMessage();
                    Toast.makeText(SetUpActivity.this, R.string.genericError + error, Toast.LENGTH_LONG).show();
                }
            });
        }
        btnSaveAccountSetting.setEnabled(false);
    }

    /**
     * Invoke CropImage library to crop image from a camera or phone storage
     */
    private void imagePicker() {
        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1, 1)
            .start(SetUpActivity.this);
    }
}
