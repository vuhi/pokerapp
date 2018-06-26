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

public class RegisterActivity extends AppCompatActivity {

    private EditText txtRegisterEmail;

    private EditText txtRegisterPassword;

    private EditText txtConfirmPassword;

    private Button btnCreateNewAccount;

    private Button btnAlreadyHaveAccount;

    private ProgressBar probRegister;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        txtRegisterEmail = (EditText)findViewById(R.id.txtRegisterEmail);
        txtRegisterPassword = (EditText)findViewById(R.id.txtRegisterPassword);
        txtConfirmPassword = (EditText)findViewById(R.id.txtConfirmPassword);
        btnCreateNewAccount = (Button)findViewById(R.id.btnCreateNewAccount);
        btnAlreadyHaveAccount = (Button)findViewById(R.id.btnAlreadyHaveAccount);
        probRegister = (ProgressBar)findViewById(R.id.probRegister);

        btnCreateNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtRegisterEmailString = txtRegisterEmail.getText().toString();
                String txtRegisterPasswordString = txtRegisterPassword.getText().toString();
                String txtConfirmPasswordString = txtConfirmPassword.getText().toString();

                if(!TextUtils.isEmpty(txtRegisterEmailString) && !TextUtils.isEmpty(txtRegisterPasswordString)
                        && !TextUtils.isEmpty(txtConfirmPasswordString))
                {
                    if(txtRegisterPasswordString.equals(txtConfirmPasswordString))
                    {
                        probRegister.setVisibility(View.VISIBLE);
                        mAuth.createUserWithEmailAndPassword(txtRegisterEmailString, txtRegisterPasswordString)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful())
                                {
                                    sendToMainActivity();
                                }else{
                                    String exceptionMsg = task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this, "Error: " + exceptionMsg, Toast.LENGTH_LONG).show();
                                }
                                probRegister.setVisibility(View.INVISIBLE);
                            }
                        });
                    }else{
                        Toast.makeText(RegisterActivity.this, "Confirm password does not match", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(RegisterActivity.this, "Please fill in the fields", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnAlreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToLoginActivity();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            sendToMainActivity();
        }
    }

    private void sendToMainActivity() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private void sendToLoginActivity() {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
