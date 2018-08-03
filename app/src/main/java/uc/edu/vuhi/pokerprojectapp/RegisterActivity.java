package uc.edu.vuhi.pokerprojectapp;

import android.os.Bundle;
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

    /**
     * Fire base Authentication instance
     */
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
        //If user is authenticated
        if(currentUser != null){
            Utility.sendTo(RegisterActivity.this, MainActivity.class, true);
        }
    }

    /**
     * Button handler to go create new account
     */
    @OnClick(R.id.btnCreateNewAccount)
    public void createNewAccount() {
        String txtRegisterEmailString = txtRegisterEmail.getText().toString();
        String txtRegisterPasswordString = txtRegisterPassword.getText().toString();
        String txtConfirmPasswordString = txtConfirmPassword.getText().toString();

        //Validate empty field
        if (!TextUtils.isEmpty(txtRegisterEmailString) && !TextUtils.isEmpty(txtRegisterPasswordString)
                && !TextUtils.isEmpty(txtConfirmPasswordString)) {
            //Validate the confirm password, case sensitive
            if (txtRegisterPasswordString.equals(txtConfirmPasswordString)) {
                probRegister.setVisibility(View.VISIBLE);
                //Create user with fire base auth
                mAuth.createUserWithEmailAndPassword(txtRegisterEmailString, txtRegisterPasswordString)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Utility.sendTo(RegisterActivity.this, SetUpActivity.class, true);

                            } else {
                                String exceptionMsg = task.getException().getMessage();
                                Toast.makeText(RegisterActivity.this, "Error: " + exceptionMsg, Toast.LENGTH_LONG).show();
                            }
                            probRegister.setVisibility(View.INVISIBLE);
                        });
            } else {
                Toast.makeText(RegisterActivity.this, R.string.confirmPasswordError, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(RegisterActivity.this, R.string.fillInRemainder, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Button handler to go back to Login Activity
     */
    @OnClick(R.id.btnAlreadyHaveAccount)
    public void alreadyHaveAccount() {
        Utility.sendTo(RegisterActivity.this, LoginActivity.class, true);
    }
}
