package iliasdm.com.anglolearner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

import static android.text.TextUtils.split;

public class Exercise extends Activity {

    String Username, levelNum, levelName;
    TextView LevelNum, LevelName;
    TextView Task , Sentence;
    EditText Answer;
    String userAnswer, correctAnswer;
    Button Submit_btn;
    DatabaseReference databaseSentences, databaseUserProgress;

    int sentencesPerTime = 2;
    int sentenceTotal = 0;
    String sentenceReached;
    int sentenceCounter = 1;
    int sentencesCompletedinThisSession = 1;
    //Variable to count sentences to review if answered wrong
    int sentenceToReview = 0;
    boolean saveTheDay = false;

    //Variables to identify mistake types
    boolean correctWithExtraSpaces = true;
    boolean onlySyntax = false;
    boolean onlySpelling = false;
    boolean extraWord = false;
    boolean missingWord = false;
    boolean restartChapter = false;
    boolean new_badge = false;
    ArrayList<String> new_badge_list = new ArrayList<>();

    String userProgressPath;

    private ArrayList<String> reviewSentences = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise);

        //We get the intent that led us to this activity
        Intent prevIntent = getIntent();
        //And we fetch the username, the level number and name that we stored as extras
        Username = prevIntent.getStringExtra("USERNAME");
        levelNum = prevIntent.getStringExtra("LEVELNUM");
        levelName = prevIntent.getStringExtra("LEVELNAME");

        LevelNum = (TextView) findViewById(R.id.LevelNum_tv);
        LevelName = (TextView) findViewById(R.id.LevelName_tv);
        Task = (TextView) findViewById(R.id.Task_tv);
        Sentence = (TextView) findViewById(R.id.Sentence_tv);
        Answer = (EditText) findViewById(R.id.Answer_et);
        Submit_btn = (Button) findViewById(R.id.Submit_btn);

        LevelNum.setText("Level " + levelNum + ":");
        LevelName.setText(levelName);

        //We create a path to the user's progress in this chapter
        userProgressPath = "users/" + Username + "/progress/" + levelNum;

        databaseUserProgress = FirebaseDatabase.getInstance().getReference(userProgressPath);
        databaseUserProgress.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sentenceReached = dataSnapshot.getValue(ProgressClass.class).getSentenceReached();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //We create a path to our sentences in the database, acoording to the Level of the chapter (LevelNum)
        final String sentencesPath = "levels/" + levelNum + "/sentences";
        databaseSentences = FirebaseDatabase.getInstance().getReference(sentencesPath);
        databaseSentences.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    //We count the number of sentences in the Chapter
                    sentenceTotal++;
                }
                showSentence(sentencesPath);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //When the sumbit button is clicked
        Submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userAnswer = Answer.getText().toString().trim();
                if (userAnswer.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please input an answer", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (userAnswer.equalsIgnoreCase(correctAnswer)) {
                        Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_SHORT).show();
                        submitCorrectAnswer();
                    }
                    else {
                        MistakeCheck();
                        if (!saveTheDay) reviewSentences.add(Integer.toString(Integer.valueOf(sentenceReached) + sentenceCounter - 1));
                    }
                    Answer.setText("");
                    showSentence(sentencesPath);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent TS = new Intent(Exercise.this, TreeScreen.class);
        TS.putExtra("USERNAME", Username);
        startActivity(TS);
        finish();
    }

    private void submitCorrectAnswer() {
        //When we get a correct answer we check our streak
        final DatabaseReference onFireReference = FirebaseDatabase.getInstance().getReference("users/" + Username);
        onFireReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //If the user does not have the On Fire Badge
                if (dataSnapshot.child("badges").getValue(BagdeClass.class).getOnfire().equals("false")) {
                    //then we increment the streak
                    int streak = Integer.valueOf(dataSnapshot.getValue(UserClass.class).getConsecutiveCorrectAnswers()) + 1;
                    onFireReference.child("consecutiveCorrectAnswers").setValue(Integer.toString(streak));
                    //and if the streak surpasses the limit
                    if (streak >= 5) {
                        //then the badge is acquired
                        onFireReference.child("badges").child("onfire").setValue("true");
                        Intent newBadge = new Intent(getBaseContext(), NewBadgeNotificationScreen.class);
                        newBadge.putExtra("BADGENAME", "onfire");
                        startActivity(newBadge);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void testPassed () {

        databaseUserProgress = FirebaseDatabase.getInstance().getReference(userProgressPath);
        databaseUserProgress.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String newSentenceReached = Integer.toString(Integer.valueOf(sentenceReached) + sentencesPerTime);
                if (restartChapter) {
                    newSentenceReached = Integer.toString(sentenceCounter -1);
                }
                databaseUserProgress.child("sentenceReached").setValue(newSentenceReached);
                if (Integer.valueOf(dataSnapshot.getValue(ProgressClass.class).getCompletion()) < 100) {

                    int percentage = (Integer.valueOf(newSentenceReached)*100)/sentenceTotal;

                    if (percentage < Integer.valueOf(dataSnapshot.getValue(ProgressClass.class).getCompletion())) {
                        databaseUserProgress.child("completion").setValue("100");

                        //We create a reference to our badges
                        final DatabaseReference venividivici = FirebaseDatabase.getInstance().getReference("users/" + Username + "/badges");
                        venividivici.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                //If the user has not acquired the Veni Vidi Vici Badge:
                                if (dataSnapshot.getValue(BagdeClass.class).getVenividivici().equals("false")) {
                                    //Then we create a reference to our progress in this level
                                    DatabaseReference noMistakeLessonReference = FirebaseDatabase.getInstance().getReference("users/" + Username + "/progress/" + levelNum);
                                    noMistakeLessonReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            //And if we have completed it without any mistakes
                                            if (dataSnapshot.getValue(ProgressClass.class).getNoMistakes().equals("true")) {
                                                //Then the badge is acquired
                                                venividivici.child("venividivici").setValue("true");
                                                Intent newBadge = new Intent(getBaseContext(), NewBadgeNotificationScreen.class);
                                                newBadge.putExtra("BADGENAME", "venividivici");
                                                startActivity(newBadge);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                    else {
                        databaseUserProgress.child("completion").setValue(Integer.toString(percentage));
                    }

                }

                //We create a reference to the user
                final DatabaseReference databasePractice = FirebaseDatabase.getInstance().getReference("users/" + Username);
                databasePractice.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //And each time he finishes a test we write the date down, so that we know when he last practiced
                        Calendar Date = Calendar.getInstance();
                        String currentDate = Date.get(Calendar.DAY_OF_MONTH) + "/" + Date.get(Calendar.MONTH) + "/" + Date.get(Calendar.YEAR);

                        //If the user has never done a test before then the streak starts from now
                        if (dataSnapshot.getValue(UserClass.class).getLastPractice().equals("--") || dataSnapshot.getValue(UserClass.class).getDayStreakSince().equals("--")) {
                            databasePractice.child("lastPractice").setValue(currentDate);
                            databasePractice.child("dayStreakSince").setValue(currentDate);
                            databasePractice.child("testsToday").setValue("1");
                        }
                        else { //If the user has completed a test in the past

                            if (dataSnapshot.child("badges").getValue(BagdeClass.class).getThunderbolt().equals("false")) {
                                String lastPracticeArr [] = dataSnapshot.getValue(UserClass.class).getLastPractice().split("/");
                                Calendar lastPractice = Calendar.getInstance();
                                lastPractice.set(Calendar.DAY_OF_MONTH, Integer.parseInt(lastPracticeArr[0]));
                                lastPractice.set(Calendar.MONTH, Integer.parseInt(lastPracticeArr[1]));
                                lastPractice.set(Calendar.YEAR, Integer.parseInt(lastPracticeArr[2]));
                                long diff = Date.getTimeInMillis() - lastPractice.getTimeInMillis();
                                long dayDiff = diff/(24*60*60*1000);

                                if (dayDiff >= 1) {
                                    databasePractice.child("testsToday").setValue("0");
                                }
                                else {
                                    int testCounter = Integer.valueOf(dataSnapshot.getValue(UserClass.class).getTestsToday()) + 1;
                                    databasePractice.child("testsToday").setValue(Integer.toString(testCounter));
                                    if (testCounter >= 5) {
                                        databasePractice.child("badges").child("thunderbolt").setValue("true");
                                        new_badge_list.add("thunderbolt");
                                        new_badge = true;
                                    }
                                }
                            }

                            if (dataSnapshot.child("badges").getValue(BagdeClass.class).getCommitted().equals("false")) {
                                String lastPracticeArr [] = dataSnapshot.getValue(UserClass.class).getLastPractice().split("/");
                                Calendar lastPractice = Calendar.getInstance();
                                lastPractice.set(Calendar.DAY_OF_MONTH, Integer.parseInt(lastPracticeArr[0]));
                                lastPractice.set(Calendar.MONTH, Integer.parseInt(lastPracticeArr[1]));
                                lastPractice.set(Calendar.YEAR, Integer.parseInt(lastPracticeArr[2]));
                                long diff = Date.getTimeInMillis() - lastPractice.getTimeInMillis();
                                long dayDiff = diff/(24*60*60*1000);

                                if (dayDiff >= 1) {
                                    databasePractice.child("dayStreakSince").setValue("--");
                                }
                                else {
                                    String dayStreakSinceArr [] = dataSnapshot.getValue(UserClass.class).getDayStreakSince().split("/");
                                    Calendar dayStreakSince = Calendar.getInstance();
                                    dayStreakSince.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dayStreakSinceArr[0]));
                                    dayStreakSince.set(Calendar.MONTH, Integer.parseInt(dayStreakSinceArr[1]));
                                    dayStreakSince.set(Calendar.YEAR, Integer.parseInt(dayStreakSinceArr[2]));
                                    long diffStreak = Date.getTimeInMillis() - dayStreakSince.getTimeInMillis();
                                    long dayDiffStreak = diffStreak/(24*60*60*1000);

                                    if (dayDiffStreak >= 5) {
                                        DatabaseReference databaseBadgeCommitted = FirebaseDatabase.getInstance().getReference("users/" + Username + "/badges");
                                        databaseBadgeCommitted.child("committed").setValue("true");
                                        new_badge_list.add("committed");
                                        new_badge = true;
                                    }
                                }
                            }
                            databasePractice.child("lastPractice").setValue(currentDate);
                        }

                        //Start new activity passing the username
                        final Intent treeScreenIntent = new Intent(Exercise.this, TreeScreen.class);
                        treeScreenIntent.putExtra("USERNAME", Username);
                        startActivity(treeScreenIntent);
                        if (new_badge) {
                            new_badge = false;
                            for (int i = 0; i < new_badge_list.size(); i++) {
                                Intent newBadge = new Intent(Exercise.this, NewBadgeNotificationScreen.class);
                                newBadge.putExtra("BADGENAME", new_badge_list.get(i));
                                startActivity(newBadge);
                            }
                        }

                        Intent testFinished = new Intent(Exercise.this, TestFinished.class);
                        startActivity(testFinished);
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void showSentence (String sentencesPath)
    {
        //If there are sentences left to examine we show them
        if (sentencesCompletedinThisSession <= sentencesPerTime) {

            if ((sentenceCounter + Integer.valueOf(sentenceReached)) > sentenceTotal) {
                sentenceCounter = 1;
                sentenceReached = "0";
                restartChapter = true;
            }

            String myNewPath = sentencesPath + "/" + Integer.toString(sentenceCounter + Integer.valueOf(sentenceReached));

            databaseSentences = FirebaseDatabase.getInstance().getReference(myNewPath);
            //We hold the number of the sentence to be examined next
            sentenceCounter++;
            sentencesCompletedinThisSession++;
            databaseSentences.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Task.setText(dataSnapshot.getValue(SentenceClass.class).getTask().trim());
                    Sentence.setText(dataSnapshot.getValue(SentenceClass.class).getOriginalSentence().trim());
                    correctAnswer = dataSnapshot.getValue(SentenceClass.class).getTargetSentence().trim();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        //If we have examined all the sentences
        else {
            if (reviewSentences.size() == 0) {
                //In this case we finish a test without mistakes
                testPassed();
            }
            else {
                //In this case the user had made some mistakes
                DatabaseReference mistakePath = FirebaseDatabase.getInstance().getReference("users/" + Username + "/progress/" + levelNum);
                mistakePath.child("noMistakes").setValue("false");
                Revision(sentencesPath);
            }
        }
    }


    private void Revision (String sentencesPath) {

        final String mySentencesPath = sentencesPath;
        int i = Integer.valueOf(reviewSentences.get(sentenceToReview));
        showRevisionSentence(sentencesPath, i);

        Submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userAnswer = Answer.getText().toString().trim();
                if (correctAnswer.equalsIgnoreCase(userAnswer)) {
                    Answer.setText("");
                    Toast.makeText(getBaseContext(), "Correct!", Toast.LENGTH_SHORT).show();
                    sentenceToReview++;
                    if (reviewSentences.size() > sentenceToReview) {
                        showRevisionSentence(mySentencesPath, Integer.valueOf(reviewSentences.get(sentenceToReview)));
                    }
                    else {
                        testPassed();
                    }
                }
                else {
                    Toast.makeText(getBaseContext(), "Wrong.. Test Failed", Toast.LENGTH_SHORT).show();
                    //Start new activity passing the username
                    final Intent treeScreenIntent = new Intent(Exercise.this, TreeScreen.class);
                    treeScreenIntent.putExtra("USERNAME", Username);
                    startActivity(treeScreenIntent);
                    finish();
                }
            }
        });
    }


    private void showRevisionSentence (String sentencesPath, int i) {

        String myNewPath = sentencesPath + "/" + Integer.valueOf(i);
        databaseSentences = FirebaseDatabase.getInstance().getReference(myNewPath);
        databaseSentences.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Task.setText(dataSnapshot.getValue(SentenceClass.class).getTask());
                Sentence.setText(dataSnapshot.getValue(SentenceClass.class).getOriginalSentence());
                correctAnswer = dataSnapshot.getValue(SentenceClass.class).getTargetSentence();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void MistakeCheck () {

        Intent intent = new Intent(Exercise.this, Mistake.class);
        intent.putExtra("CORRECTANSWER", correctAnswer);
        intent.putExtra("WRONGANSWER", userAnswer);

        correctAnswer = correctAnswer.replaceAll("\n", " ");
        userAnswer = userAnswer.replaceAll("\n", " ");

        String correctAnswersArr [] = correctAnswer.split(" +");
        String userAnswersArr [] = userAnswer.split(" +");


        if (correctAnswersArr.length == userAnswersArr.length) {

            for (int i = 0; i < correctAnswersArr.length; i++) {
                if (!(correctAnswersArr[i].equalsIgnoreCase(userAnswersArr[i]))) {
                    correctWithExtraSpaces = false;
                    break;
                }
            }

            if (correctWithExtraSpaces) {
                //TODO: CORRECT!!!
                saveTheDay = true;
                Toast.makeText(getBaseContext(),"Correct!", Toast.LENGTH_SHORT).show();
                submitCorrectAnswer();
            }

            else {
                Boolean found = false;
                for (int i = 0; i < correctAnswersArr.length; i++) {
                    found = false;
                    for (int j = 0; j < userAnswersArr.length; j++) {
                        if (correctAnswersArr[i].equalsIgnoreCase(userAnswersArr[j])) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        //TODO: Typo
                        final DatabaseReference correctAnswersStreakReference = FirebaseDatabase.getInstance().getReference("users/" + Username);
                        correctAnswersStreakReference.child("consecutiveCorrectAnswers").setValue("0");
                        intent.putExtra("MISTAKETYPE", "Typo");
                        startActivity(intent);
                        break;
                    }
                }
                if (found) {
                    //TODO: Syntax
                    final DatabaseReference correctAnswersStreakReference = FirebaseDatabase.getInstance().getReference("users/" + Username);
                    correctAnswersStreakReference.child("consecutiveCorrectAnswers").setValue("0");
                    intent.putExtra("MISTAKETYPE", "Syntax");
                    startActivity(intent);
                }
            }
        }
        else {

            if (correctAnswersArr.length > userAnswersArr.length) {
                missingWord = true;
                //TODO: Missing Word
                final DatabaseReference correctAnswersStreakReference = FirebaseDatabase.getInstance().getReference("users/" + Username);
                correctAnswersStreakReference.child("consecutiveCorrectAnswers").setValue("0");
                intent.putExtra("MISTAKETYPE", "Missing Word");
                startActivity(intent);
            }
            else {
                extraWord = true;
                //TODO: Extra word
                final DatabaseReference correctAnswersStreakReference = FirebaseDatabase.getInstance().getReference("users/" + Username);
                correctAnswersStreakReference.child("consecutiveCorrectAnswers").setValue("0");
                intent.putExtra("MISTAKETYPE", "Extra Word");
                startActivity(intent);
            }

        }

    }

}
