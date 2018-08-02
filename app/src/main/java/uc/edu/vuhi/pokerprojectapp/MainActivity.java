package uc.edu.vuhi.pokerprojectapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.SystemClock;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;
    private FirebaseUser currentUser;
    private DocumentReference currentUserQuery;
    private int currentPoint;

    private boolean isPlayed;
    private int currentBetOption;

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

    private List<String> betOptions;

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
        currentUser = mAuth.getCurrentUser();
        //Check if user is log in or not
        if (currentUser == null) {
            Utility.sendTo(MainActivity.this, LoginActivity.class, true);
        }
        //User log in
        else{
            currentUserQuery = mDatabase.collection("Users").document(currentUser.getUid());
            currentUserQuery.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        //User does not update record when register
                        if(!task.getResult().exists()){
                            Utility.sendTo(MainActivity.this, SetUpActivity.class, false);
                        }
                        //User entered information
                        else {
                            LocalDate currentDate = LocalDate.now();
                            //First time login +1000
                            if(task.getResult().getString("timeLog") == null) {
                                currentPoint = 1000;
                                currentUserQuery.update("point", currentPoint);
                                currentUserQuery.update("timeLog", currentDate.toString());
                                Toast.makeText(MainActivity.this, "First time login, +$1000",Toast.LENGTH_LONG).show();
                            }
                            else {
                                //Daily login +100
                                if (LocalDate.parse(task.getResult().getString("timeLog")).isBefore(currentDate)) {
                                    currentPoint = task.getResult().getLong("point").intValue() + 100;
                                    currentUserQuery.update("point", currentPoint);
                                    currentUserQuery.update("timeLog", currentDate.toString());
                                    Toast.makeText(MainActivity.this, "First time login, +$1000", Toast.LENGTH_LONG).show();
                                }
                                //Login multiple times a day
                                else {
                                    currentPoint = task.getResult().getLong("point").intValue();
                                }
                            }
                            txtViewNotification.setText("$"+String.valueOf(currentPoint));
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
                rechargeToken();
                return super.onOptionsItemSelected(item);

            default:
                return false;
        }
    }

    public void rechargeToken(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.recharge_token, null);

        final EditText txtAnswer = (EditText) mView.findViewById(R.id.txtAnswer);
        Button btnSubmitAnswer = (Button) mView.findViewById(R.id.btnSubmitAnswer);

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

        btnSubmitAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String answer = txtAnswer.getText().toString();
                if(answer.trim().equalsIgnoreCase("tomorrow")){
                    currentPoint = currentPoint + 500;
                    currentUserQuery.update("point", currentPoint).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                txtViewNotification.setText("$" + String.valueOf(currentPoint));
                                Toast.makeText(MainActivity.this, "Correct! +500", Toast.LENGTH_LONG).show();
                                Utility.delay(dialog, 2000, null);
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(MainActivity.this, "Incorrect!", Toast.LENGTH_LONG).show();
                }
            }
        });
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
            btnDraw.setEnabled(true);
        }else {
            btnEvaluate.setEnabled(true);
            btnDraw.setEnabled(false);
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
            btnDraw.setEnabled(false);
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
        hand.evaluateHand();
        int score = hand.getScore();
        String rank = hand.getRankName();

        currentPoint = currentBetOption*score + currentPoint;
        currentUserQuery.update("point", currentPoint).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    //User win
                    if(score > 0){
                        showResult("Congratulation", "You have " + rank);
                    }
                    //User lose
                    else {
                        showResult("Sorry, you lose", rank);
                    }
                    txtViewNotification.setText("$" + String.valueOf(currentPoint));
                }
                //Need to have broadcast receiver for network connection
            }
        });
    }

    private void showResult(String result, String message){
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(result);
        alertDialog.setMessage(message);

        Runnable myFunction = new Runnable() {
            public void run() {
                initiateGame();
            }
        };
        Utility.delay(alertDialog, 3100, myFunction);

        //Set the position of dialog
        alertDialog.getWindow().getAttributes().verticalMargin = 0.4F;
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

        betOptions = Arrays.asList("Select Bet","$10","$20","$30","$50","$80","$100");
        scrollMoney.addItems(betOptions,0);

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
                        currentBetOption = Integer.parseInt(betOptions.get(position).replace("$",""));
                        //Check if the fund is enough to bet
                        if(currentBetOption > currentPoint){
                            Toast.makeText(MainActivity.this, "Insufficient bet option" , Toast.LENGTH_SHORT).show();
                            btnDraw.setEnabled(false);
                        }else {
                            if(isPlayed){
                                btnEvaluate.setEnabled(true);
                            }
                            btnDraw.setEnabled(true);
                        }
                    }
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
