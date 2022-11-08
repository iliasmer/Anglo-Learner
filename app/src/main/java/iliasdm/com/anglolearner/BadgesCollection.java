package iliasdm.com.anglolearner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BadgesCollection extends Activity {

    ArrayList<String> myBadges = new ArrayList<>();
    TextView nobadges_tv;
    String Username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.badges_collection);

        Intent intent = getIntent();
        Username = intent.getStringExtra("USERNAME");

        fillList();
    }

    private void fillList () {

        DatabaseReference myBadgesReference = FirebaseDatabase.getInstance().getReference("users/" + Username + "/badges");
        myBadgesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(BagdeClass.class).getCommitted().equals("true")) myBadges.add("committed");
                if (dataSnapshot.getValue(BagdeClass.class).getConqueror().equals("true")) myBadges.add("conqueror");
                if (dataSnapshot.getValue(BagdeClass.class).getOnfire().equals("true")) myBadges.add("onfire");
                if (dataSnapshot.getValue(BagdeClass.class).getThunderbolt().equals("true")) myBadges.add("thunderbolt");
                if (dataSnapshot.getValue(BagdeClass.class).getVenividivici().equals("true")) myBadges.add("venividivici");
                if (dataSnapshot.getValue(BagdeClass.class).getWise().equals("true")) myBadges.add("wise");


                initiateRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initiateRecyclerView () {

        if (myBadges.size() == 0) {
            nobadges_tv = (TextView) findViewById(R.id.nobadges_tv);
            nobadges_tv.setVisibility(View.VISIBLE);
        }
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        BadgesAdapter adapter = new BadgesAdapter(myBadges, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
