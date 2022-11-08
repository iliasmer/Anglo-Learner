package iliasdm.com.anglolearner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class BadgeWall extends Activity {

    TextView badge_description_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.badge_wall);

        badge_description_tv = (TextView) findViewById(R.id.badge_description_tv);

        Intent intent = getIntent();
        String badgeName = intent.getStringExtra("BADGENAME");
        if (badgeName.equals("committed")) {
            badge_description_tv.setText("You have practiced for 5 days consecutively! You are committed, and we reward you for that!");
        }
        else if (badgeName.equals("conqueror")) {
            badge_description_tv.setText("Well done! You are a conqueror! You have completed the entire tree!");
        }
        else if (badgeName.equals("onfire")) {
            badge_description_tv.setText("You are on fire! You have maintained a streak of 5 correct answers!");
        }
        else if (badgeName.equals("thunderbolt")) {
            badge_description_tv.setText("You passed 5 tests in one day! Like a thunderbolt!");
        }
        else if (badgeName.equals("venividivici")) {
            badge_description_tv.setText("Veni, vidi, vici! You completed an entire course with no mistakes!");
        }
        else if (badgeName.equals("wise")) {
            badge_description_tv.setText("You have been in this app for more than 5 days. You are aready wiser!");
        }

    }
}
