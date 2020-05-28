package program.matchit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MainGameActivity extends AppCompatActivity {

    Button buttonA, buttonB, buttonC;
    TextView questionText, triviaQuizText, timeText, resultText, coinText;
    GameQuestion currentQuestion;
    Pair currentPair;
    ArrayList<Pair> list;
    int qid = 0;
    int timeValue = 20;
    int coinValue = 0;
    CountDownTimer countDownTimer;
    FirebaseAuth firebaseAuth;

    private FirebaseDatabase firebaseDatabase;
    private static final int OPTA_QUES = 7;
    private static final int OPTB_QUES = 5;
    private static final int OPTC_QUES = 5;
    private static final int SET_SIZE_1 = 4;
    private static final int SET_SIZE_2 = 3;
    private static final int SET_SIZE_3 = 4;
    private Set<Integer> Set_1;
    private Set<Integer> Set_2;
    private Set<Integer> Set_3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);

        //Initializing variables
        questionText = (TextView) findViewById(R.id.triviaQuestion);
        buttonA = (Button) findViewById(R.id.buttonA);
        buttonB = (Button) findViewById(R.id.buttonB);
        buttonC = (Button) findViewById(R.id.buttonC);
        triviaQuizText = (TextView) findViewById(R.id.triviaQuizText);
        timeText = (TextView) findViewById(R.id.timeText);
        resultText = (TextView) findViewById(R.id.resultText);
        coinText = (TextView) findViewById(R.id.coinText);

        firebaseDatabase = FirebaseDatabase.getInstance();

        buttonA.setTypeface(Typeface.MONOSPACE, Typeface.NORMAL);
        buttonB.setTypeface(Typeface.MONOSPACE, Typeface.NORMAL);
        buttonC.setTypeface(Typeface.MONOSPACE, Typeface.NORMAL);

        //fetching questions from database
        list = new ArrayList<Pair>();

        Set_1 = new HashSet<Integer>(SET_SIZE_1);
        Set_2 = new HashSet<Integer>(SET_SIZE_2);
        Set_3 = new HashSet<Integer>(SET_SIZE_3);

        // Generating random numbers
        // Each number is added for each iteration
        Random random = new Random();
        while(Set_1.size() < SET_SIZE_1)  {
            while(!Set_1.add(random.nextInt(OPTA_QUES)));
        }
        while(Set_2.size() < SET_SIZE_2) {
            while(!Set_2.add(random.nextInt(OPTB_QUES)));
        }
        while(Set_3.size() < SET_SIZE_3) {
            while(!Set_3.add(random.nextInt(OPTC_QUES)));
        }

        for(int num = 1; num <= 3; num++) {
            switch(num) {
                case 1 : {
                    for(Integer i : Set_1)
                        list.add(new Pair("opta", i));
                    break;
                }
                case 2 : {
                    for(Integer i : Set_2)
                        list.add(new Pair("optb", i));
                    break;
                }
                case 3 : {
                    for(Integer i : Set_3)
                        list.add(new Pair("optc", i));
                    break;
                }
                default :
                    throw new IllegalStateException("Unexpected value: " + num);
            }
        }

        Collections.shuffle(list);

        currentPair = list.get(qid);

        //countDownTimer
        countDownTimer = new CountDownTimer(22000, 1000) {
            public void onTick(long millisUntilFinished) {
                //here you can have your logic to set text to timeText
                timeText.setText(String.valueOf(timeValue) + "\"");

                //With each iteration decrement the time by 1 sec
                timeValue -= 1;

                //This means the user is out of time so onFinished will called after this iteration
                if (timeValue == -1) {

                    //Since user is out of time setText as time up
                    resultText.setText(getString(R.string.timeup));

                    //Since user is out of time he won't be able to click any buttons
                    //therefore we will disable all four options buttons using this method
                    disableButton();
                }
            }

            //Now user is out of time
            public void onFinish() {
                //We will navigate him to the time up activity using below method
                timeUp();
            }
        }.start();

        //This method will set the que and four options
        updateQueAndOptions(currentPair.getStr(), currentPair.getId() + 1);
    }

    public void updateQueAndOptions(final String msg, int n) {

        DatabaseReference dbRef = firebaseDatabase.getReference("Words/" + msg + "/id" + n);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GameRow gameRow = dataSnapshot.getValue(GameRow.class);
                assert gameRow != null;
                currentQuestion = new GameQuestion(gameRow.getMeaning(), gameRow.getOpta(), gameRow.getOptb(), gameRow.getOptc(), msg);

                //This method will setText for que and options
                questionText.setText(currentQuestion.getMeaning());
                buttonA.setText(currentQuestion.getOpta());
                buttonB.setText(currentQuestion.getOptb());
                buttonC.setText(currentQuestion.getOptc());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainGameActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        timeValue = 20;

        //Now since the user has ans correct just reset timer back for another que- by cancel and start
        countDownTimer.cancel();
        countDownTimer.start();

        //set the value of coin text
        coinText.setText(String.valueOf(coinValue));

        //Now since user has ans correct increment the coinvalue
        coinValue++;
    }

    //Onclick listener for first button
    public void buttonA(View view) {
        //compare the option with the ans if yes then make button color green
        if (currentQuestion.getAnswer().equals("opta")) {
            buttonA.setBackgroundColor(Color.GREEN);

            //Check if user has not exceeds the que limit
            if (qid < list.size() - 1) {
                //Now disable all the option button since user ans is correct so
                //user won't be able to press another option button after pressing one button
                disableButton();

                //Show the dialog that ans is correct
                correctDialog();
            }

            //If user has exceeds the que limit just navigate him to GameWon activity
            else {
                gameWon();
            }
        }

        //User ans is wrong then just navigate him to the PlayAgain activity
        else {
            gameLostPlayAgain();
        }
    }

    //Onclick listener for sec button
    public void buttonB(View view) {
        if (currentQuestion.getAnswer().equals("optb")) {
            buttonB.setBackgroundColor(Color.GREEN);
            if (qid < list.size() - 1) {
                disableButton();
                correctDialog();
            } else {
                gameWon();
            }
        } else {
            gameLostPlayAgain();
        }
    }

    //Onclick listener for third button
    public void buttonC(View view) {
        if (currentQuestion.getAnswer().equals("optc")) {
            buttonC.setBackgroundColor(Color.GREEN);
            if (qid < list.size() - 1) {
                disableButton();
                correctDialog();
            } else {
                gameWon();
            }
        } else {
            gameLostPlayAgain();
        }
    }

    private void updateScore() {
        firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference userRef = firebaseDatabase.getReference("Users").child(firebaseAuth.getUid()).child("Score");
        userRef.setValue(String.valueOf(coinValue - 1));
    }

    //This method will navigate from current activity to GameWon
    public void gameWon() {
        updateScore();
        Intent intent = new Intent(this, GameWon.class);
        startActivity(intent);
        finish();
    }

    //This method is called when user ans is wrong
    //this method will navigate user to the activity PlayAgain
    public void gameLostPlayAgain() {
        updateScore();
        Intent intent = new Intent(this, PlayAgain.class);
        startActivity(intent);
        finish();
    }

    //This method is called when time is up
    //this method will navigate user to the activity Time_Up
    public void timeUp() {
        updateScore();
        Intent intent = new Intent(this, Time_Up.class);
        startActivity(intent);
        finish();
    }

    //If user press home button and come in the game from memory then this
    //method will continue the timer from the previous time it left
    @Override
    protected void onRestart() {
        super.onRestart();
        countDownTimer.start();
    }

    //When activity is destroyed then this will cancel the timer
    @Override
    protected void onStop() {
        super.onStop();
        countDownTimer.cancel();
    }

    //This will pause the time
    @Override
    protected void onPause() {
        super.onPause();
        countDownTimer.cancel();
    }

    //On BackPressed
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, SecondActivity.class);
        startActivity(intent);
        finish();
    }

    //This dialog is show to the user after he ans correct
    public void correctDialog() {
        final Dialog dialogCorrect = new Dialog(MainGameActivity.this);
        dialogCorrect.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (dialogCorrect.getWindow() != null) {
            ColorDrawable colorDrawable = new ColorDrawable(Color.TRANSPARENT);
            dialogCorrect.getWindow().setBackgroundDrawable(colorDrawable);
        }
        dialogCorrect.setContentView(R.layout.dialog_correct);
        dialogCorrect.setCancelable(false);
        dialogCorrect.show();

        //Since the dialog is show to user just pause the timer in background
        onPause();

        Button buttonNext = (Button) dialogCorrect.findViewById(R.id.dialogNext);

        //OnCLick listener to go next que
        buttonNext.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {
                //This will dismiss the dialog
                dialogCorrect.dismiss();
                //it will increment the question number
                qid++;
                //all buttons to white
                buttonA.setBackgroundColor(Color.WHITE);
                buttonB.setBackgroundColor(Color.WHITE);
                buttonC.setBackgroundColor(Color.WHITE);
                //get the que and 4 option and store in the currentQuestion
                currentPair = list.get(qid);
                //Now this method will set the new que and 4 options
                updateQueAndOptions(currentPair.getStr(), currentPair.getId() + 1);
                //Enable button - remember we had disable them when user ans was correct in there particular button methods
                enableButton();
            }
        });
    }

    //This method will disable all the option button
    public void disableButton() {
        buttonA.setEnabled(false);
        buttonB.setEnabled(false);
        buttonC.setEnabled(false);
    }

    //This method will all enable the option buttons
    public void enableButton() {
        buttonA.setEnabled(true);
        buttonB.setEnabled(true);
        buttonC.setEnabled(true);
    }
}
