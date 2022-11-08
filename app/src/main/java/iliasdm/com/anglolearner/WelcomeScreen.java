package iliasdm.com.anglolearner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class WelcomeScreen extends AppCompatActivity {

    Button signUpbtn, logInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_screen);

        final Intent LogInsignUpIntent = new Intent(WelcomeScreen.this, Login_Signup.class);

        //If the username is stored in the phone's internal memory,
        //then it means that the user is already logged in
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(WelcomeScreen.this);
        String spUsername = sp.getString("username", "");
        if (!spUsername.equals("")) {
            Intent skip = new Intent(WelcomeScreen.this, TreeScreen.class);
            skip.putExtra("USERNAME", spUsername);
            startActivity(skip);
            finish();
        }

        //this button is used to identify a Sign Up action
        signUpbtn = (Button) findViewById(R.id.signup_btn);
        //the Sign Up form is called
        signUpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogInsignUpIntent.putExtra("ACTION","signup");
                startActivity(LogInsignUpIntent);
                finish();
            }
        });

        //this button is used to identify a Log In action
        logInBtn = (Button) findViewById(R.id.login_btn);
        //the Sign Up form is called
        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogInsignUpIntent.putExtra("ACTION","login");
                startActivity(LogInsignUpIntent);
                finish();
            }
        });
    }
}
