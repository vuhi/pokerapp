package uc.edu.vuhi.pokerprojectapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.varunest.sparkbutton.SparkButton;
import com.webianks.library.scroll_choice.ScrollChoice;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uc.edu.vuhi.pokerprojectapp.DTO.Card;
import uc.edu.vuhi.pokerprojectapp.DTO.Deck;
import uc.edu.vuhi.pokerprojectapp.DTO.Hand;
import uc.edu.vuhi.pokerprojectapp.UTIL.Utility;

public class MainActivity extends AppCompatActivity {

    /**
     * Fire base Authentication instance
     */
    private FirebaseAuth mAuth;

    /**
     * Fire base database instance, use to save information in fire base database
     */
    private FirebaseFirestore mDatabase;

    /**
     * Fire base query instance, use to query to current user
     */
    private DocumentReference currentUserQuery;

    private int currentPoint;

    /**
     * Number of discard card, use as a flag
     */
    private int numCardDiscard;

    /**
     * A flag to check if user initiate a game (true) or not (false)
     */
    private boolean isPlayed;

    /**
     * A variable to save to current bet option
     */
    private int currentBetOption;

    /**
     * List of image cards
     */
    private List<ImageButton> imgButtons;

    /**
     * List of image discard icon
     */
    private List<ImageView> imgViewDiscards;

    /**
     * List represent bet options
     */
    private List<String> betOptions;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseFirestore.getInstance();
        setSupportActionBar(toolbarMain);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.welcome);
        loadData();
        initiateGame();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //Validate user authentication
        if (currentUser == null) {
            Utility.sendTo(MainActivity.this, LoginActivity.class, true);
        }
        //User is authenticated
        else{
            currentUserQuery = mDatabase.collection("Users").document(currentUser.getUid());
            //Validate if user set up the information
            currentUserQuery.get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    //User does not set up information when register
                    if(!task.getResult().exists()){
                        Utility.sendTo(MainActivity.this, SetUpActivity.class, false);
                    }
                    //User has set up
                    else {
                        //Get current date(only), API 26 call
                        LocalDate currentDate = LocalDate.now();
                        //First time login +1000 currency
                        if(task.getResult().getString("timeLog") == null) {
                            currentPoint = 1000;
                            currentUserQuery.update("point", currentPoint);
                            currentUserQuery.update("timeLog", currentDate.toString());
                            Toast.makeText(MainActivity.this, R.string.firstTimeLoginString,Toast.LENGTH_LONG).show();
                        }
                        else {
                            //Daily login +100 currency
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
                    Toast.makeText(MainActivity.this, R.string.errorRetrievingUser , Toast.LENGTH_LONG).show();
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

    /**
     * Button handle function for recharge
     */
    public void rechargeToken(){
        //Initiate dialog with custom layout
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.recharge_token, null);
        final EditText txtAnswer = (EditText) mView.findViewById(R.id.txtAnswer);
        Button btnSubmitAnswer = (Button) mView.findViewById(R.id.btnSubmitAnswer);
        //Dismiss dialog
        mBuilder.setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss());
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        btnSubmitAnswer.setOnClickListener(v -> {
            String answer = txtAnswer.getText().toString();
            //Validate answer, correct answer +500 currency
            if(answer.trim().equalsIgnoreCase("tomorrow")){
                currentPoint = currentPoint + 500;
                currentUserQuery.update("point", currentPoint).addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        txtViewNotification.setText("$" + String.valueOf(currentPoint));
                        Toast.makeText(MainActivity.this, "Correct! +500", Toast.LENGTH_LONG).show();
                        Utility.delay(dialog, 2000, null);
                    }
                });
            }
            else {
                Toast.makeText(MainActivity.this, "Incorrect!", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Button handler for 5 image cards, hold or discard card
     * @param aView - image card
     */
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
        //Validate the number of discard to enable button
        if(numCardDiscard > 0) {
            setBtnEvaluateStatus(false);
            setBtnDrawStatus(true);
        }else {
            setBtnEvaluateStatus(true);
            setBtnDrawStatus(false);
        }
    }

    /**
     * Button handler for btnDraw, draw number of card base on the number of discard
     */
    @OnClick(R.id.btnDraw)
    public void play() {
        //User does not initiate game
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
        //User already initiated game
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
                //Disable image card to prevent user to discard
                imgButtons.get(i).setEnabled(false);
            }
            initiateImageViewDiscard();
        }
        setBtnEvaluateStatus(true);
    }

    /**
     * Button handler for btnEvaluate, evaluate card, get the score, update point
     */
    @OnClick(R.id.btnEvaluate)
    public void evaluateResult(){
        btnEvaluate.playAnimation();
        hand.evaluateHand();
        String rank = hand.getRankName();
        int score = hand.getScore();
        int scoreMoney = currentBetOption*score;
        currentPoint = scoreMoney + currentPoint;
        //Update point base on score, show result
        currentUserQuery.update("point", currentPoint).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                //User win
                if(score > 0){
                    showResult(rank, getString(R.string.userWin)+String.valueOf(scoreMoney));
                }
                //User lose
                else {
                    showResult(rank, getString(R.string.userLose)+String.valueOf(scoreMoney*-1));
                }
                txtViewNotification.setText("$" + String.valueOf(currentPoint));
            }
            //Need to have broadcast receiver for network connection
        });
    }

    /**
     * Show result function, pop up dialog with result
     * @param rank - Rank of hand
     * @param message - message, Win or Lose
     */
    private void showResult(String rank, String message){
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(rank);
        alertDialog.setMessage(message);
        Runnable myFunction = () -> initiateGame();
        //This is a temporary attempt to pass a function as parameter. I still don't know how to do it with interface
        Utility.delay(alertDialog, 2500, myFunction);
        //Set the position of dialog
        Objects.requireNonNull(alertDialog.getWindow()).getAttributes().verticalMargin = 0.4F;
        alertDialog.show();
    }

    /**
     * Function to initiate a new game
     */
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

    /**
     * Function to loading requirement data
     */
    @SuppressLint("ClickableViewAccessibility")
    private void loadData() {
        card = new Card();
        deck = new Deck(this);
        hand = new Hand();
        imgButtons = Arrays.asList(imgBtnCard, imgBtnCard1, imgBtnCard2, imgBtnCard3, imgBtnCard4);
        imgViewDiscards =  Arrays.asList(imgViewDiscard, imgViewDiscard1, imgViewDiscard2, imgViewDiscard3, imgViewDiscard4);

        betOptions = Arrays.asList("Select Bet","$10","$20","$30","$50","$80","$100");
        scrollMoney.addItems(betOptions,0);

        //Disable or enable the scroll choice
        scrollMoney.setOnTouchListener((v, event) -> isPlayed);

        //ScrollChoice is a 3rd party library, cannot bind with butter knife
        scrollMoney.setOnItemSelectedListener((scrollChoice, position, name) -> {
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
                //Convert bet option to an integer
                currentBetOption = Integer.parseInt(betOptions.get(position).replace("$",""));
                //Validate funds
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
        });
    }

    /**
     * Function to log out
     */
    private void logOut() {
        mAuth.signOut();
        Utility.sendTo(this, LoginActivity.class, true);
    }

    /**
     * Function to get random back card
     */
    private void setBackCardImage(){
        for (ImageButton imageButton : imgButtons) {
            imageButton.setImageResource(getRandomBackCardImage());
            imageButton.setEnabled(false);
        }
    }

    /**
     * Helper function to set viability of discard icon
     * @param bool - visible or invisible
     */
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

    /**
     * Helper function to get id of back card image
     * @return int
     */
    private int getRandomBackCardImage(){
        //Generate random number 0-5
        Random r = new Random();
        String index = String.valueOf(r.nextInt(5));
        //Retrieve location of img by string name
        return this.getResources().getIdentifier("back_0"+index, "drawable", this.getPackageName());
    }

    /**
     * Helper function to set tag of discard icon
     */
    private void initiateImageViewDiscard(){
        for(ImageView imageView: imgViewDiscards){
            imageView.setTag("Hold");
            imageView.setImageResource(getLoveIcon());
        }
    }

    /**
     * Helper function to get id of love icon
     * @return int
     */
    private int getLoveIcon(){
        return this.getResources().getIdentifier("love", "drawable", this.getPackageName());
    }

    /**
     * Helper function to get id of un-love icon
     * @return int
     */
    private int getUnLoveIcon(){
        return this.getResources().getIdentifier("un_love", "drawable", this.getPackageName());
    }

    /**
     * Helper function to handle the state of draw button, this is a 3rd party animated function
     * @param bool - disable or enable
     */
    private void setBtnDrawStatus(Boolean bool){
        if(bool){
            btnDraw.setInactiveImage(R.drawable.draw_2);
            btnDraw.setEnabled(true);
        }else {
            btnDraw.setInactiveImage(R.drawable.draw_2_disable);
            btnDraw.setEnabled(bool);
        }
    }

    /**
     * Helper function to handle the state of draw button, this is a 3rd party animated function
     * @param bool - disable or enable
     */
    private void setBtnEvaluateStatus(Boolean bool){
        if(bool){
            btnEvaluate.setEnabled(true);
            btnEvaluate.setInactiveImage(R.drawable.call);
        }else {
            btnEvaluate.setEnabled(bool);
            btnEvaluate.setInactiveImage(R.drawable.call_disable);
        }
    }
}
