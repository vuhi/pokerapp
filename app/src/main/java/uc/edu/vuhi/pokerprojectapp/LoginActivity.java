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


public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.txtEmail)
    EditText txtEmail;

    @BindView(R.id.txtPassword)
    EditText txtPassword;

    @BindView(R.id.btnLogin)
    Button btnLogin;

    @BindView(R.id.btnRegister)
    Button btnRegister;

    @BindView(R.id.probLogin)
    ProgressBar probLogin;

    private  FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            sendToMainActivity();
        }
    }

    @OnClick(R.id.btnLogin)
    public void logIn() {
        String txtEmailString = txtEmail.getText().toString();
        String txtPasswordString = txtPassword.getText().toString();
        if(!TextUtils.isEmpty(txtEmailString) && !TextUtils.isEmpty(txtPasswordString)){
            probLogin.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(txtEmailString, txtPasswordString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        sendToMainActivity();
                    }
                    else{
                        String exceptionMsg = task.getException().getMessage();
                        Toast.makeText(LoginActivity.this, "Error: " + exceptionMsg, Toast.LENGTH_SHORT).show();
                    }
                    probLogin.setVisibility(View.INVISIBLE);
                }
            });
        }
        else{
            Toast.makeText(LoginActivity.this, "Please fill in the fields", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btnRegister)
    public void register() {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
        finish();
    }

    private void sendToMainActivity(){
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
