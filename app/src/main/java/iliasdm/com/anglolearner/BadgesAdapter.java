package iliasdm.com.anglolearner;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

public class BadgesAdapter extends RecyclerView.Adapter<BadgesAdapter.BadgesViweHolder>{

    private ArrayList<String> myBadges;
    private Context context;

    public BadgesAdapter(ArrayList<String> myBadges, Context context) {
        this.myBadges = myBadges;
        this.context = context;
    }

    @NonNull
    @Override
    public BadgesViweHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.badge_item, parent, false);
        BadgesViweHolder badge = new BadgesViweHolder(view);

        return badge;
    }

    @Override
    public void onBindViewHolder(@NonNull BadgesViweHolder holder, int position) {

        final String pictureName = myBadges.get(position);
        int id = context.getResources().getIdentifier("iliasdm.com.anglolearner:drawable/" + pictureName, null, null);
        holder.badge_iv.setImageResource(id);
        holder.badge_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent badgeWallIntent = new Intent(context, BadgeWall.class);
                badgeWallIntent.putExtra("BADGENAME", pictureName);
                context.startActivity(badgeWallIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return myBadges.size();
    }

    public class BadgesViweHolder extends RecyclerView.ViewHolder {

        ImageView badge_iv;
        ConstraintLayout badge_layout;

        public BadgesViweHolder(View itemView) {
            super(itemView);

             badge_iv = itemView.findViewById(R.id.new_badge_iv);
             badge_layout = itemView.findViewById(R.id.badge_layout);
        }
    }
}
