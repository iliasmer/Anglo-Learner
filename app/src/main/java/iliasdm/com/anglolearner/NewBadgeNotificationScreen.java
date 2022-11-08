package iliasdm.com.anglolearner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class NewBadgeNotificationScreen extends Activity {

    TextView new_badge_tv;
    ImageView new_badge_iv;
    Button next_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_badge_notification_screen);

        new_badge_tv = (TextView) findViewById(R.id.new_badge_tv);
        new_badge_iv = (ImageView) findViewById(R.id.new_badge_iv);

        Intent rootIntent = getIntent();
        String badgeName = rootIntent.getStringExtra("BADGENAME");

        int id = getResources().getIdentifier("iliasdm.com.anglolearner:drawable/" + badgeName, null, null);
        new_badge_iv.setImageResource(id);

        next_btn = (Button) findViewById(R.id.next_btn);

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
