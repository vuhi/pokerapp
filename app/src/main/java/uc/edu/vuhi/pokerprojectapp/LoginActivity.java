package uc.edu.vuhi.pokerprojectapp;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

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

    /**
     * Fire base Authentication instance
     */
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
        //If user is authenticated, send to the main page
        if(currentUser != null){
            Utility.sendTo(LoginActivity.this, MainActivity.class, true);
        }
    }

    /**
     * Button handler for login process
     */
    @OnClick(R.id.btnLogin)
    public void logIn() {
        String txtEmailString = txtEmail.getText().toString();
        String txtPasswordString = txtPassword.getText().toString();
        //Validate empty field
        if(!TextUtils.isEmpty(txtEmailString) && !TextUtils.isEmpty(txtPasswordString)){
            probLogin.setVisibility(View.VISIBLE);
            //Sign in with Fire base auth
            mAuth.signInWithEmailAndPassword(txtEmailString, txtPasswordString).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Utility.sendTo(LoginActivity.this, MainActivity.class, true);
                }
                else{
                    String exceptionMsg = task.getException().getMessage();
                    Toast.makeText(LoginActivity.this, "Error: " + exceptionMsg, Toast.LENGTH_SHORT).show();
                }
                probLogin.setVisibility(View.INVISIBLE);
            });
        }
        else{
            Toast.makeText(LoginActivity.this, "Please fill in the fields", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Button handler to send to register activity
     */
    @OnClick(R.id.btnRegister)
    public void register() {
        Utility.sendTo(LoginActivity.this, RegisterActivity.class, true);
    }

    /**
     * Button handler to reset password
     */
    @OnClick(R.id.btnResetPswd)
    public void resetPswd() {
        //Initiate dialog with custom layout
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(LoginActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_reset_pswd, null);

        //Binding with butter-knife will result with crashing
        final EditText txtResetPasswdEmail = (EditText) mView.findViewById(R.id.txtResetPasswdEmail);
        Button btnResetPasswdSubmit = (Button) mView.findViewById(R.id.btnResetPasswdSubmit);

        //Initiate dismiss dialog button
        mBuilder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());

        //Create view for dialog
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        //Handle button
        btnResetPasswdSubmit.setOnClickListener(v -> {
            String resetPasswordEmail = txtResetPasswdEmail.getText().toString().trim();
            //Validate empty field
            if (TextUtils.isEmpty(resetPasswordEmail)) {
                Toast.makeText(getApplicationContext(), R.string.enterEmailReminder, Toast.LENGTH_SHORT).show();
                return;
            }
            //Send email to reset password with fire base auth
            mAuth.sendPasswordResetEmail(resetPasswordEmail).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "An email has sent to reset your password!", Toast.LENGTH_SHORT).show();
                    //Delay dialog with "time"
                    Utility.delay(dialog, 3000, null);
                } else {
                    Toast.makeText(LoginActivity.this, "Fail to send reset password email!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
