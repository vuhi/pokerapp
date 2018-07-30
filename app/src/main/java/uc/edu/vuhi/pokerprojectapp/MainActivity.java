package uc.edu.vuhi.pokerprojectapp;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.webianks.library.scroll_choice.ScrollChoice;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import uc.edu.vuhi.pokerprojectapp.DTO.UserDTO;
import uc.edu.vuhi.pokerprojectapp.UTIL.Utility;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;
    private boolean isPlayed = false;

    @BindView(R.id.toolbarMain)
    Toolbar toolbarMain;

    @BindView(R.id.scrollMoney)
    ScrollChoice scrollMoney;

    @BindView(R.id.txtViewNotification)
    TextView txtViewNotification;

    @BindView(R.id.btnPlay)
    Button btnPlay;

    List<String> datas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseFirestore.getInstance();
        setSupportActionBar(toolbarMain);
        getSupportActionBar().setTitle("Main Page");
        loadDatas();
    }

    @OnClick(R.id.btnPlay)
    public void Play(){
        if(!isPlayed){
            btnPlay.setText("DRAW");
        }
        //Deal random card
    }

    private void loadDatas() {
        datas.add("000");
        datas.add("111");
        datas.add("222");
        datas.add("333");
        datas.add("444");
        datas.add("555");
        datas.add("666");
        datas.add("777");
        datas.add("888");
        datas.add("999");
        scrollMoney.addItems(datas,2);
        scrollMoney.setOnItemSelectedListener(new ScrollChoice.OnItemSelectedListener() {
            @Override
            public void onItemSelected(ScrollChoice scrollChoice, int position, String name) {
                txtViewNotification.setText(name);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Check if user is log in or not
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Utility.sendTo(MainActivity.this, LoginActivity.class, true);
        }
        //User log in
        else{
            mDatabase.collection("Users").document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        //User does not update record when register
                        if(!task.getResult().exists()){
                            Utility.sendTo(MainActivity.this, SetUpActivity.class, false);
                        }
                    }
                    else {
                        Toast.makeText(MainActivity.this, "An error occurred while retrieving user" , Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Menu option handler
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnLogout:
                logOut();
                return super.onOptionsItemSelected(item);

            case R.id.btnAccountSetting:
                Utility.sendTo(MainActivity.this, SetUpActivity.class, false);
                return super.onOptionsItemSelected(item);

            case R.id.btnRechargeToken:
                //Pop up dialog
                Toast.makeText(MainActivity.this, "RechargeToken clicked", Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);

            default:
                return false;
        }
    }

    private void logOut() {
        mAuth.signOut();
        Utility.sendTo(this, LoginActivity.class, true);
    }
}
