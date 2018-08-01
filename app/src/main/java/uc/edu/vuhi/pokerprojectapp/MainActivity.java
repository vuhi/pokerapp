package uc.edu.vuhi.pokerprojectapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.DragEvent;
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
import butterknife.OnTouch;
import uc.edu.vuhi.pokerprojectapp.DTO.Card;
import uc.edu.vuhi.pokerprojectapp.DTO.Deck;
import uc.edu.vuhi.pokerprojectapp.DTO.Hand;
import uc.edu.vuhi.pokerprojectapp.DTO.UserDTO;
import uc.edu.vuhi.pokerprojectapp.UTIL.Utility;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;

    private boolean isPlayed;
    private boolean isDrawed = false;

    private List<ImageButton> imgButtons;
    private Card card;
    private Deck deck;
    private Hand hand;

    @BindView(R.id.toolbarMain)
    Toolbar toolbarMain;

    @BindView(R.id.scrollMoney)
    ScrollChoice scrollMoney;

    @BindView(R.id.txtViewNotification)
    TextView txtViewNotification;

    @BindView(R.id.btnDraw)
    Button btnDraw;

    @BindView(R.id.btnEvaluate)
    Button btnEvaluate;

    @BindView(R.id.imgBtnCard)
    ImageButton imgBtnCard;

    @BindView(R.id.imgBtnCard1)
    ImageButton imgBtnCard1;

    @BindView(R.id.imgBtnCard2)
    ImageButton imgBtnCard2;

    @BindView(R.id.imgBtnCard3)
    ImageButton imgBtnCard3;

    @BindView(R.id.imgBtnCard4)
    ImageButton imgBtnCard4;

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

        card = new Card();
        deck = new Deck(this);
        hand = new Hand();
        imgButtons = Arrays.asList(imgBtnCard, imgBtnCard1, imgBtnCard2, imgBtnCard3, imgBtnCard4);

        loadDatas();
        initiateGame();
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


    @OnClick({R.id.imgBtnCard, R.id.imgBtnCard1, R.id.imgBtnCard2, R.id.imgBtnCard3, R.id.imgBtnCard4})
    public void discardCard(View aView){
        //Get the tag value of the imageBtn, best way to due with multiple @Onclick
        int id = Integer.parseInt(aView.getTag().toString());

        //Check the state of the card in hand
        //This card is on hold -> discard it by showing the card back
        if(!hand.cards[id].getIsDiscard()){
            hand.cards[id].setIsDiscard(true);
            imgButtons.get(id).setImageResource(getRandomBackCardImage());
        }
        //This card is already discard -> hold the card by showing the card face
        else {
            hand.cards[id].setIsDiscard(false);
            imgButtons.get(id).setImageResource(hand.cards[id].getCardImageId());
        }

        if(hand.hasAnyDisCard()){
            btnEvaluate.setEnabled(false);
        }else {
            btnEvaluate.setEnabled(true);
        }
    }


    @OnClick(R.id.btnDraw)
    public void play() {

        if(!isPlayed){
            isPlayed = true;
            deck.shuffle();
            for (int i = 0; i < imgButtons.size(); i++) {
                card = deck.deal();
                hand.placeCardInHand(card, i);
                //Set img of card in hand
                imgButtons.get(i).setImageResource(card.getCardImageId());
            }
            //Enable imageButton to let user hold or discard card
            for (ImageButton imageButton : imgButtons) {
                imageButton.setEnabled(true);
            }
        }
        else {
            btnDraw.setEnabled(false);
            deck.shuffle();
            for (int i = 0; i < imgButtons.size(); i++) {
                if(hand.cards[i].getIsDiscard()){
                    card = deck.deal();
                    hand.placeCardInHand(card, i);
                    //Set img of card in hand
                    imgButtons.get(i).setImageResource(card.getCardImageId());
                }
            }
            //Enable imageButton to let user hold or discard card
            for (ImageButton imageButton : imgButtons) {
                imageButton.setEnabled(false);
            }
        }
        btnEvaluate.setEnabled(true);

    }

    @OnClick(R.id.btnEvaluate)
    public void evaluateResult(){
        txtViewNotification.setText("SHOW RESULT");
        hand.evaluateHand();
        int score = hand.getScore();
        String rank = hand.getRankName();
        if(score > 0){
            showResult("Congratulation", "You have "+rank);
        }
        else {
            showResult("Sorry", rank);
        }
    }

    private void showResult(String result, String message){
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(result);
        alertDialog.setMessage(message);
        Utility.delay(alertDialog, 2000);
        //->> Apply decorator pattern here
        initiateGame();
        alertDialog.show();
    }

    private void initiateGame() {
        isPlayed = false;
        scrollMoney.setSelectedItemPosition(0);
        btnDraw.setEnabled(false);
        btnEvaluate.setEnabled(false);
        setBackCardImage();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void loadDatas() {

        btnEvaluate.setEnabled(false);
        btnDraw.setEnabled(false);

        datas.add("Select Bet");
        datas.add("$10");
        datas.add("$20");
        datas.add("$30");
        datas.add("$40");
        datas.add("$50");
        datas.add("$60");
        datas.add("$70");
        datas.add("$80");
        datas.add("$90");
        datas.add("$100");
        scrollMoney.addItems(datas,0);

        //Disable or enable the scroll choice
        scrollMoney.setOnTouchListener(new ScrollChoice.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!isPlayed){
                    return false;
                }
                else {
                    return true;
                }
            }
        });

        scrollMoney.setOnItemSelectedListener(new ScrollChoice.OnItemSelectedListener() {
            @Override
            public void onItemSelected(ScrollChoice scrollChoice, int position, String name) {
                    if(position == 0){
                        if(!isPlayed){
                            btnDraw.setEnabled(true);
                        }
                        else {
                            btnDraw.setEnabled(false);
                            btnEvaluate.setEnabled(false);
                        }
                    }
                    else {
                        if(isPlayed){
                            btnEvaluate.setEnabled(true);
                        }
                        btnDraw.setEnabled(true);
                    }

                txtViewNotification.setText(name);
            }
        });
    }

    private void logOut() {
        mAuth.signOut();
        Utility.sendTo(this, LoginActivity.class, true);
    }
    private void setBackCardImage(){
        for (ImageButton imageButton : imgButtons) {
            imageButton.setImageResource(getRandomBackCardImage());
            imageButton.setEnabled(false);
        }
    }

    private int getRandomBackCardImage(){
        //Generate random number 0-5
        Random r = new Random();
        String index = String.valueOf(r.nextInt(5));
        //Retrieve location of img by string name
        int cardBackImgId = this.getResources().getIdentifier("back_0"+index, "drawable", this.getPackageName());
        return cardBackImgId;
    }
}
