package iliasdm.com.anglolearner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Mistake extends Activity {

    TextView mistake_type_tv, wrong_answer_tv, correct_answer_tv;
    ImageView exit_iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mistake);

        mistake_type_tv = (TextView) findViewById(R.id.mistake_type_tv);
        wrong_answer_tv = (TextView) findViewById(R.id.wrong_answer_tv);
        correct_answer_tv = (TextView) findViewById(R.id.correct_answer_tv);
        exit_iv = (ImageView) findViewById(R.id.exit_iv);

        Intent intent = getIntent();

        String mistakeType = intent.getStringExtra("MISTAKETYPE");

        if (mistakeType.equals("Typo")) {
            mistake_type_tv.setText("You have made a typo.");
        }
        else if (mistakeType.equals("Syntax")) {
            mistake_type_tv.setText("You have made a syntactical mistake.");
        }
        else if (mistakeType.equals("Extra Word")) {
            mistake_type_tv.setText("You have used extra words.");
        }
        else if (mistakeType.equals("Missing Word")) {
            mistake_type_tv.setText("Some words are missing.");
        }

        wrong_answer_tv.setText(intent.getStringExtra("WRONGANSWER"));
        correct_answer_tv.setText(intent.getStringExtra("CORRECTANSWER"));

        exit_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
