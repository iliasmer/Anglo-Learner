package iliasdm.com.anglolearner;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import java.util.Calendar;

public class Login_Signup extends Activity {

    TextView details_tv;
    EditText details_et;
    Button go_btn;

    DatabaseReference databaseUsers; //a reference variable to our database
    DatabaseReference databaseProgress; //a reference to the user's progress in the database
    DatabaseReference databaseBadges; //a reference to the user's badges
    String givenUsername, givenPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_signup);

        details_tv = (TextView) findViewById(R.id.details_tv);
        details_et = (EditText) findViewById(R.id.details_et);
        go_btn = (Button) findViewById(R.id.go_btn);

        details_tv.setText("Enter Username");

        //we connect our reference cariable to firebase, passing "users" as a child node
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");

        //run if database has no users
        //UserClass user = new UserClass("Test", "0");
        //databaseUsers.child("Test").setValue(user);

        //We get the root intent to get the action (LOG IN or SIGN UP)
        Intent rootIntent = getIntent();
        final String action = rootIntent.getStringExtra("ACTION");

        go_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (details_et.getText().toString().trim().equals("")) {
                    Toast.makeText(getBaseContext(), "Wrong Input..", Toast.LENGTH_SHORT).show();
                    details_et.setText("");
                }
                else {
                    getDetails(action);
                }

            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent WS = new Intent(Login_Signup.this, WelcomeScreen.class);
        startActivity(WS);
        finish();
    }

    private void getDetails(String action) {

        if (details_tv.getText().toString().equals("Enter Username")) {
            givenUsername = details_et.getText().toString().trim();
            details_tv.setText("Enter Password");
            details_et.setText("");
        }
        else {
            givenPassword = details_et.getText().toString().trim();
            if (action.equals("signup")) signUp();
            else logIn();
        }
    }



    private void signUp() {

        databaseUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            boolean userExists = false; //variable to see if a user exists (login) or not (signup)
            String username;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    username = ds.getValue(UserClass.class).getUserName().trim();
                    if (username.equals(givenUsername))
                    {
                        userExists = true;
                        break;
                    }
                }
                if (userExists)
                {
                    Toast.makeText(getBaseContext(),"User Exists",Toast.LENGTH_SHORT).show();
                    details_et.setText("");
                    details_tv.setText("Enter Username");
                }
                else
                {
                    Calendar today = Calendar.getInstance();
                    String date = today.get(Calendar.DAY_OF_MONTH) + "/" + today.get(Calendar.MONTH) + "/" + today.get(Calendar.YEAR);

                    //we make the new user-type object
                    UserClass user = new UserClass(givenUsername, givenPassword, date, "--", "0", "--", "0");
                    //and we pass it on the Firebase database, using the username as a unique key
                    databaseUsers.child(givenUsername).setValue(user);
                    userExists = false;

                    //We follow the path to our new user
                    String myProgressPath = "users/" + givenUsername + "/progress";
                    //And we link the reference to that
                    databaseProgress = FirebaseDatabase.getInstance().getReference(myProgressPath);

                    //We follow the link to our badges
                    String myBadgesPath = "users/" + givenUsername + "/badges";
                    //And we link a database reference there
                    databaseBadges = FirebaseDatabase.getInstance().getReference(myBadgesPath);
                    //We create the badge object
                    BagdeClass myBadges = new BagdeClass("false", "false", "false", "false", "false", "false");
                    //And we set the object to the user's profile
                    databaseBadges.setValue(myBadges);

                    //We create a reference to the levels of our database
                    final DatabaseReference databaseLevels = FirebaseDatabase.getInstance().getReference("levels");
                    databaseLevels.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot dsLevels : dataSnapshot.getChildren())
                            {
                                //and we set the progress of each level for our new user to 0
                                ProgressClass progress = new ProgressClass("0", "0","true");
                                databaseProgress.child(dsLevels.getKey()).setValue(progress);

                                //When the user logs in or signs up we store the username in the phone's internal memory
                                //Thus, when the user opens the app again we know he is already logged in
                                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Login_Signup.this);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.clear().apply();
                                editor.putString("username", givenUsername);
                                editor.apply();
                            }

                            //Start new activity passing the username
                            final Intent treeScreenIntent = new Intent(getBaseContext(), TreeScreen.class);
                            treeScreenIntent.putExtra("USERNAME", givenUsername);
                            startActivity(treeScreenIntent);
                            finish();
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


    private void logIn() {

        databaseUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            boolean userExists = false; //variable to see if a user exists (login) or not (signup)
            String username, password;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    username = ds.getValue(UserClass.class).getUserName().trim();
                    password = ds.getValue(UserClass.class).getUserPassword().trim();

                    if (username.equals(givenUsername))
                    {
                        userExists = true;
                        break;
                    }

                }
                if (userExists)
                {
                    if (password.equals(givenPassword))
                    {
                        //When the user logs in or signs up we store the username in the phone's internal memory
                        //Thus, when the user opens the app again we know he is already logged in
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Login_Signup.this);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.clear().apply();
                        editor.putString("username", givenUsername);
                        editor.apply();

                        //Start new activity passing username
                        final Intent treeScreenIntent = new Intent(getBaseContext(), TreeScreen.class);
                        treeScreenIntent.putExtra("USERNAME", username);
                        startActivity(treeScreenIntent);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(getBaseContext(),"Incorrect Password",Toast.LENGTH_SHORT).show();
                        details_et.setText("");
                    }
                    userExists = false;
                }
                else
                {
                    Toast.makeText(getBaseContext(),"User Does Not Exist",Toast.LENGTH_SHORT).show();
                    details_et.setText("");
                    details_tv.setText("Enter Username");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
