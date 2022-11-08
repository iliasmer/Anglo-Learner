package iliasdm.com.anglolearner;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class TreeScreen extends Activity {

    ImageView logout_iv, profile_iv;
    TextView welcomeTv;
    String myUsername;
    DatabaseReference databaseLevels;
    DatabaseReference databaseBadges;
    DatabaseReference databaseUsers;

    boolean conqueror = false;
    boolean hasEnteredLoopWise = false;
    boolean hasEnteredLoopConqueror = false;

    private ArrayList<String> LevelNumList = new ArrayList<>();
    private ArrayList<String> PercentageList = new ArrayList<>();
    private ArrayList<String> ChapterList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tree_screen);

        welcomeTv = (TextView) findViewById(R.id.welcome_tv);
        logout_iv = (ImageView) findViewById(R.id.logout_iv);
        profile_iv = (ImageView) findViewById(R.id.profile_iv);

        logout_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(TreeScreen.this);
                SharedPreferences.Editor editor = sp.edit();
                editor.clear().apply();
                Intent backToWelcomeScreen = new Intent(TreeScreen.this, WelcomeScreen.class);
                startActivity(backToWelcomeScreen);
                finish();
            }
        });

        Intent intent = getIntent();
        myUsername = intent.getStringExtra("USERNAME");
        welcomeTv.setText("Welcome, " + myUsername + "!");

        profile_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent badgesIntent = new Intent(TreeScreen.this, BadgesCollection.class);
                badgesIntent.putExtra("USERNAME", myUsername);
                startActivity(badgesIntent);
            }
        });

        fillChaptersList();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void fillChaptersList () {

        //We create a reference to the levels in our database
        databaseLevels = FirebaseDatabase.getInstance().getReference("levels");
        databaseLevels.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    //We get the level number of each chapter
                    LevelNumList.add(ds.getKey());
                    //We get the level title of each chapter
                    ChapterList.add(ds.getValue(LevelClass.class).getLevelName().trim());

                    //We need to get the percentage of completion of this user for each chapter
                    //So we create a path to our user's progress in the database
                    String myProgress = "users/" + myUsername + "/progress";
                    DatabaseReference databaseProgress = FirebaseDatabase.getInstance().getReference(myProgress);
                    databaseProgress.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot dsProg : dataSnapshot.getChildren())
                            {
                                //We get the amount of progress for each chapter into the list
                                PercentageList.add(dsProg.getValue(ProgressClass.class).getCompletion().trim());
                                initRecyclerView();
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

    private void initRecyclerView () {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LevelsAdapter adapter = new LevelsAdapter(LevelNumList, PercentageList, ChapterList, myUsername, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchBadges();
    }

    private void searchBadges () {
        //We create a link to the user's badges
        String myBadgesPath = "users/" + myUsername + "/badges";
        //And we make a reference to it
        databaseBadges = FirebaseDatabase.getInstance().getReference(myBadgesPath);
        databaseBadges.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshotBadge) {
                //First we are looking to see if the user has acquired the "Wise" Badge,
                //Which he gets if he has been in the app for at least 5 days
                //If this badge has not been acquired, then
                if (dataSnapshotBadge.getValue(BagdeClass.class).getWise().equals("false")) {
                    //we create a reference to the user
                    databaseUsers = FirebaseDatabase.getInstance().getReference("users/" + myUsername);
                    databaseUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //and we take the day in which he signed up
                            String signupDateArr [] = dataSnapshot.getValue(UserClass.class).getSignupDate().split("/");
                            Calendar currentDate = Calendar.getInstance();
                            Calendar signupDate = Calendar.getInstance();
                            signupDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(signupDateArr[0]));
                            signupDate.set(Calendar.MONTH, Integer.parseInt(signupDateArr[1]));
                            signupDate.set(Calendar.YEAR, Integer.parseInt(signupDateArr[2]));

                            //and we calculate the diference between that and the current date
                            long diff = currentDate.getTimeInMillis() - signupDate.getTimeInMillis();
                            long diffDays = diff/(24*60*60*1000);

                            //If he has been on the app for at least 5 days he gets the badge
                            if (diffDays >= 5 && !hasEnteredLoopWise) {
                                databaseBadges.child("wise").setValue("true");
                                Intent newBadge = new Intent(getBaseContext(), NewBadgeNotificationScreen.class);
                                newBadge.putExtra("BADGENAME", "wise");
                                startActivity(newBadge);
                                hasEnteredLoopWise = true;
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                //Next, we see if the user has acquired the "Conqueror" Badge,
                //Which he gets if he completes the entire class tree
                //If the Badge has not been acquired, then
                if (dataSnapshotBadge.getValue(BagdeClass.class).getConqueror().equals("false")) {
                    DatabaseReference databaseUsersProgress = FirebaseDatabase.getInstance().getReference("users/" + myUsername + "/progress");
                    databaseUsersProgress.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            conqueror = true;
                            for (DataSnapshot ds: dataSnapshot.getChildren()) {
                                if (!ds.getValue(ProgressClass.class).getCompletion().equals("100")) {
                                    conqueror = false;
                                    break;
                                }
                            }
                            if (conqueror && !hasEnteredLoopConqueror) {
                                hasEnteredLoopConqueror = true;
                                databaseBadges.child("conqueror").setValue("true");
                                Intent newBadge = new Intent(getBaseContext(), NewBadgeNotificationScreen.class);
                                newBadge.putExtra("BADGENAME", "conqueror");
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
}
