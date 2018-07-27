package uc.edu.vuhi.pokerprojectapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uc.edu.vuhi.pokerprojectapp.UTIL.Utility;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.txtRegisterEmail)
    EditText txtRegisterEmail;

    @BindView(R.id.txtRegisterPassword)
    EditText txtRegisterPassword;

    @BindView(R.id.txtConfirmPassword)
    EditText txtConfirmPassword;

    @BindView(R.id.btnCreateNewAccount)
    Button btnCreateNewAccount;

    @BindView(R.id.btnAlreadyHaveAccount)
    Button btnAlreadyHaveAccount;

    @BindView(R.id.probRegister)
    ProgressBar probRegister;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Utility.sendTo(RegisterActivity.this, MainActivity.class);
        }
    }

    @OnClick(R.id.btnCreateNewAccount)
    public void createNewAccount() {
        String txtRegisterEmailString = txtRegisterEmail.getText().toString();
        String txtRegisterPasswordString = txtRegisterPassword.getText().toString();
        String txtConfirmPasswordString = txtConfirmPassword.getText().toString();

        if (!TextUtils.isEmpty(txtRegisterEmailString) && !TextUtils.isEmpty(txtRegisterPasswordString)
                && !TextUtils.isEmpty(txtConfirmPasswordString)) {
            if (txtRegisterPasswordString.equals(txtConfirmPasswordString)) {
                probRegister.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(txtRegisterEmailString, txtRegisterPasswordString)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Utility.sendTo(RegisterActivity.this, SetUpActivity.class);

                                } else {
                                    String exceptionMsg = task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this, "Error: " + exceptionMsg, Toast.LENGTH_LONG).show();
                                }
                                probRegister.setVisibility(View.INVISIBLE);
                            }
                        });
            } else {
                Toast.makeText(RegisterActivity.this, "Confirm password does not match", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(RegisterActivity.this, "Please fill in the fields", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btnAlreadyHaveAccount)
    public void alreadyHaveAccount() {
        Utility.sendTo(RegisterActivity.this, LoginActivity.class);
    }
}
