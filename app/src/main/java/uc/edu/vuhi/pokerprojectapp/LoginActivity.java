package uc.edu.vuhi.pokerprojectapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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

    @BindView(R.id.btnResetPswd)
    Button btnResetPswd;

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
            Utility.sendTo(LoginActivity.this, MainActivity.class);
        }
    }

    /**
     * login
     */
    @OnClick(R.id.btnLogin)
    public void logIn() {
        String txtEmailString = txtEmail.getText().toString();
        String txtPasswordString = txtPassword.getText().toString();
        //Check for empty
        if(!TextUtils.isEmpty(txtEmailString) && !TextUtils.isEmpty(txtPasswordString)){
            probLogin.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(txtEmailString, txtPasswordString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Utility.sendTo(LoginActivity.this, MainActivity.class);
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

    /**
     * send to register activity
     */
    @OnClick(R.id.btnRegister)
    public void register() {
        Utility.sendTo(LoginActivity.this, RegisterActivity.class);
    }

    /**
     * pop up dialog when click
     */
    @OnClick(R.id.btnResetPswd)
    public void resetPswd() {
        //Initiate dialog
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(LoginActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_reset_pswd, null);

        //Binding with butter-knife will result with crashing
        final EditText txtResetPasswdEmail = (EditText) mView.findViewById(R.id.txtResetPasswdEmail);
        Button btnResetPasswdSubmit = (Button) mView.findViewById(R.id.btnResetPasswdSubmit);

        //Dismiss dialog
        mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                   }
                });

        //Create view for dialog
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        //Handle button
        btnResetPasswdSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String resetPasswordEmail = txtResetPasswdEmail.getText().toString().trim();
                if (TextUtils.isEmpty(resetPasswordEmail)) {
                    Toast.makeText(getApplicationContext(), "Please enter your email!", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.sendPasswordResetEmail(resetPasswordEmail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "An email has sent to reset your password!", Toast.LENGTH_SHORT).show();
                                    //Delay dialog with "time"
                                    Utility.delay(dialog, 3000);
                                } else {
                                    Toast.makeText(LoginActivity.this, "Fail to send reset password email!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
