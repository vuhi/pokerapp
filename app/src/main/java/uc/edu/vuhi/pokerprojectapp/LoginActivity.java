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


public class LoginActivity extends AppCompatActivity {

    //Butter Knife does not work for some reason!
    //@BindView(R.id.txtEmail)
    private EditText txtEmail;

    //@BindView(R.id.txtPassword)
    private EditText txtPassword;

    //@BindView(R.id.btnLogin)
    private Button btnLogin;

    //@BindView(R.id.btnRegister)
    private Button btnRegister;

    //@BindView(R.id.probLogin)
    private ProgressBar probLogin;

    private  FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        probLogin = (ProgressBar) findViewById(R.id.probLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtEmailString = txtEmail.getText().toString();
                String txtPasswordString = txtPassword.getText().toString();

                if(!TextUtils.isEmpty(txtEmailString) && !TextUtils.isEmpty(txtPasswordString)){
                    probLogin.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(txtEmailString, txtPasswordString)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        sendToMainActivity();
                                    }
                                    else{
                                        String exceptionMsg = task.getException().getMessage();
                                        Toast.makeText(LoginActivity.this, "Error: " + exceptionMsg, Toast.LENGTH_LONG).show();
                                    }
                                    probLogin.setVisibility(View.INVISIBLE);
                                }
                            });
                }
                else{
                    Toast.makeText(LoginActivity.this, "Please fill in your email or password", Toast.LENGTH_SHORT).show();
                }
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

    private void sendToMainActivity(){
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
