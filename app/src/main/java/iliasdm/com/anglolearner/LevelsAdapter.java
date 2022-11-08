package iliasdm.com.anglolearner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class LevelsAdapter  extends RecyclerView.Adapter<LevelsAdapter.LevelsViewHolder> {

    private ArrayList<String> LevelNumList;
    private ArrayList<String> PercentageList;
    private ArrayList<String> ChapterList;
    private String Username;
    private Context Context;

    public LevelsAdapter(ArrayList<String> LevelNumList, ArrayList<String> PercentageList, ArrayList<String> ChapterList,String Username, Context Context) {
        this.LevelNumList = LevelNumList;
        this.PercentageList = PercentageList;
        this.ChapterList = ChapterList;
        this.Context = Context;
        this.Username = Username;
    }

    @NonNull
    @Override
    public LevelsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tree_level_item, parent,false);
        LevelsViewHolder holder = new LevelsViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final LevelsViewHolder holder, int position) {
        final String lvlNum = LevelNumList.get(position);
        final String lvlName = ChapterList.get(position);
        holder.levelnum_tv.setText("LEVEL: " + LevelNumList.get(position));
        holder.percentage_tv.setText(PercentageList.get(position) + "%");
        holder.chapter_tv.setText(ChapterList.get(position));

        holder.chapter_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open Chapter to be studied
                Intent intent = new Intent(Context, Exercise.class);
                //We pass the level number on
                intent.putExtra("LEVELNUM",lvlNum);
                //We pass the username on
                intent.putExtra("USERNAME", Username);
                //We pass the level name on
                intent.putExtra("LEVELNAME", lvlName);
                Context.startActivity(intent);
                //We finish the Tree Screen
                ((Activity)Context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return LevelNumList.size();
    }

    public class LevelsViewHolder extends RecyclerView.ViewHolder {

        TextView levelnum_tv, percentage_tv, chapter_tv;
        ConstraintLayout chapter_layout;

        public LevelsViewHolder(View itemView) {
            super(itemView);

            levelnum_tv = itemView.findViewById(R.id.levelnum_tv);
            percentage_tv = itemView.findViewById(R.id.percentage_tv);
            chapter_tv = itemView.findViewById(R.id.chapter_tv);
            chapter_layout = itemView.findViewById(R.id.chapter_layout);
        }
    }
}
