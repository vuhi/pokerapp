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
import android.support.v4.content.ContextCompat;
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
import com.varunest.sparkbutton.SparkButton;
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
import android.widget.ImageView;
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
    private int numCardDiscard;

    private boolean isPlayed;
    private int currentBetOption;

    private List<ImageButton> imgButtons;
    private List<ImageView> imgViewDiscards;
    private Card card;
    private Deck deck;
    private Hand hand;

    @BindView(R.id.toolbarMain)
    Toolbar toolbarMain;

    @BindView(R.id.scrollMoney)
    ScrollChoice scrollMoney;

    @BindView(R.id.txtViewNotification)
    TextView txtViewNotification;

    @BindView(R.id.btnEvaluate)
    SparkButton btnEvaluate;

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

    @BindView(R.id.imgViewDiscard)
    ImageView imgViewDiscard;

    @BindView(R.id.imgViewDiscard1)
    ImageView imgViewDiscard1;

    @BindView(R.id.imgViewDiscard2)
    ImageView imgViewDiscard2;

    @BindView(R.id.imgViewDiscard3)
    ImageView imgViewDiscard3;

    @BindView(R.id.imgViewDiscard4)
    ImageView imgViewDiscard4;

    @BindView(R.id.btnDraw)
    SparkButton btnDraw;

    private List<String> betOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseFirestore.getInstance();
        setSupportActionBar(toolbarMain);
        getSupportActionBar().setTitle("Welcome");

        card = new Card();
        deck = new Deck(this);
        hand = new Hand();
        imgButtons = Arrays.asList(imgBtnCard, imgBtnCard1, imgBtnCard2, imgBtnCard3, imgBtnCard4);
        imgViewDiscards =  Arrays.asList(imgViewDiscard, imgViewDiscard1, imgViewDiscard2, imgViewDiscard3, imgViewDiscard4);

        loadData();
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
                                Toast.makeText(MainActivity.this, R.string.firstTimeLoginString,Toast.LENGTH_LONG).show();
                            }
                            else {
                                //Daily login +100
                                if (LocalDate.parse(task.getResult().getString("timeLog")).isBefore(currentDate)) {
                                    currentPoint = task.getResult().getLong("point").intValue() + 100;
                                    currentUserQuery.update("point", currentPoint);
                                    currentUserQuery.update("timeLog", currentDate.toString());
                                    Toast.makeText(MainActivity.this, R.string.dailyLoginString, Toast.LENGTH_LONG).show();
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
        String tag = imgViewDiscards.get(id).getTag().toString();

        //Check the state of the card in hand
        //This card is on hold -> discard it by showing the card back
        if(tag.trim().equalsIgnoreCase("hold")){
            numCardDiscard++;
            imgViewDiscards.get(id).setTag("discard");
            imgViewDiscards.get(id).setImageResource(getUnLoveIcon());
            imgButtons.get(id).setImageResource(getRandomBackCardImage());
        }
        //This card is already discard -> hold the card by showing the card face
        else {
            numCardDiscard--;
            imgViewDiscards.get(id).setTag("hold");
            imgViewDiscards.get(id).setImageResource(getLoveIcon());
            imgButtons.get(id).setImageResource(hand.cards[id].getCardImageId());
        }

        if(numCardDiscard > 0) {
            setBtnEvaluateStatus(false);
            setBtnDrawStatus(true);
        }else {
            setBtnEvaluateStatus(true);
            setBtnDrawStatus(false);
        }
    }


    @OnClick(R.id.btnDraw)
    public void play() {

        if(!isPlayed){
            isPlayed = true;
            setVisibilityDiscardIcon(true);

            btnDraw.playAnimation();

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
            setBtnDrawStatus(false);
        }
        else {
            setBtnDrawStatus(false);
            btnDraw.playAnimation();
            deck.shuffle();
            for (int i = 0; i < imgButtons.size(); i++) {
                if(imgViewDiscards.get(i).getTag().toString().equalsIgnoreCase("discard")){
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
            initiateImageViewDiscard();
        }
        setBtnEvaluateStatus(true);

    }

    @OnClick(R.id.btnEvaluate)
    public void evaluateResult(){
        btnEvaluate.playAnimation();
        hand.evaluateHand();
        int score = hand.getScore();
        String rank = hand.getRankName();
        int scoreMoney = currentBetOption*score;
        currentPoint = scoreMoney + currentPoint;
        currentUserQuery.update("point", currentPoint).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    //User win
                    if(score > 0){
                        showResult(rank, "Congratulation, you win $"+String.valueOf(scoreMoney));
                    }
                    //User lose
                    else {
                        showResult(rank, "Sorry, you lose $"+String.valueOf(scoreMoney*-1));
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
        Utility.delay(alertDialog, 2500, myFunction);
        //Set the position of dialog
        alertDialog.getWindow().getAttributes().verticalMargin = 0.4F;
        alertDialog.show();
    }

    private void initiateGame() {
        isPlayed = false;
        numCardDiscard = 0;
        scrollMoney.setSelectedItemPosition(0);
        setBtnDrawStatus(false);
        setBtnEvaluateStatus(false);
        setBackCardImage();
        initiateImageViewDiscard();
        setVisibilityDiscardIcon(false);
        btnDraw.setInactiveImage(R.drawable.draw_2_disable);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void loadData() {
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
                            setBtnDrawStatus(false);
                        }
                        else {
                            setBtnDrawStatus(false);
                            setBtnEvaluateStatus(false);
                        }
                    }
                    else {
                        currentBetOption = Integer.parseInt(betOptions.get(position).replace("$",""));
                        //Check if the fund is enough to bet
                        if(currentBetOption > currentPoint){
                            Toast.makeText(MainActivity.this, "Insufficient bet option" , Toast.LENGTH_SHORT).show();
                            setBtnDrawStatus(false);
                        }else {
                            if(isPlayed){
                                setBtnEvaluateStatus(true);
                            }
                            setBtnDrawStatus(true);
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

    private void setVisibilityDiscardIcon(boolean bool){
        if(bool){
            for(ImageView imageView: imgViewDiscards){
                imageView.setVisibility(View.VISIBLE);
            }
        }
        else {
            for(ImageView imageView: imgViewDiscards){
                imageView.setVisibility(View.INVISIBLE);
            }
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

    private void initiateImageViewDiscard(){
        for(ImageView imageView: imgViewDiscards){
            imageView.setTag("Hold");
            imageView.setImageResource(getLoveIcon());
        }
    }

    private int getLoveIcon(){
        int loveId = this.getResources().getIdentifier("love", "drawable", this.getPackageName());
        return loveId;
    }

    private int getUnLoveIcon(){
        int unLoveId = this.getResources().getIdentifier("un_love", "drawable", this.getPackageName());
        return unLoveId;
    }

    private int getBtnDrawIcon(boolean bool){
        if(bool){
            int enable = this.getResources().getIdentifier("draw_2", "drawable", this.getPackageName());
            return enable;
        }else {
            int disable = this.getResources().getIdentifier("draw_2_disable", "drawable", this.getPackageName());
            return disable;
        }

    }

    private int getBtnEvaluateIcon(boolean bool){
        if(bool){
            int enable = this.getResources().getIdentifier("call", "drawable", this.getPackageName());
            return enable;
        }else {
            int disable = this.getResources().getIdentifier("call_disable", "drawable", this.getPackageName());
            return disable;
        }

    }

    private void setBtnDrawStatus(Boolean bool){
        if(bool){
            btnDraw.setInactiveImage(R.drawable.draw_2);
            btnDraw.setEnabled(bool);
        }else {
            btnDraw.setInactiveImage(R.drawable.draw_2_disable);
            btnDraw.setEnabled(bool);
        }
    }

    private void setBtnEvaluateStatus(Boolean bool){
        if(bool){
            btnEvaluate.setEnabled(bool);
            btnEvaluate.setInactiveImage(R.drawable.call);
        }else {
            btnEvaluate.setEnabled(bool);
            btnEvaluate.setInactiveImage(R.drawable.call_disable);
        }
    }
}
