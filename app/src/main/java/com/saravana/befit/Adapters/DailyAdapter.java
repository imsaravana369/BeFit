package com.saravana.befit.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.saravana.befit.Manual.ManualCheckActivity;
import com.saravana.befit.R;
import com.saravana.befit.StartExerciseActivity;

public class DailyAdapter extends RecyclerView.Adapter<DailyAdapter.ViewHolder> {
    private static final String TAG = "DailyAdapter";
    private int days_completed;
    private Context context;
    private boolean isTodayOver;
    private int INCREMENT_COUNT=10;
    private ViewHolder lastExerciseHolder;
    public DailyAdapter(Context context, int days_completed,boolean isTodayOver){
        this.context=context;
        this.days_completed=days_completed;
        this.isTodayOver = isTodayOver;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.daily_item,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
          if(position<days_completed){
              Log.d(TAG, "onBindViewHolder: IF");
              holder.textView.setText(""+(position+1));
              holder.cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.white,null));
              holder.imageView.setImageResource(android.R.color.transparent);
              holder.imageView.setVisibility(View.GONE);
              if(! isTodayOver) {
                  holder.itemView.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          goToManualCheckActivity(position + 1);
                      }
                  });
              }
              else{
                  holder.itemView.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          Toast.makeText(context,"It's enough for today",Toast.LENGTH_SHORT).show();
                      }
                  });
              }

              //to keep track of the last exercise day
              /*
              if(position==days_completed-1)
                  holder.cardView.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_light,null));
              else
                  holder.cardView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent,null));

               */
          }
         else{
              Log.d(TAG, "onBindViewHolder: Else");
              holder.textView.setVisibility(View.GONE);
              holder.imageView.setVisibility(View.VISIBLE);

          }
    }

    @Override
    public int getItemCount() {
        return days_completed+ INCREMENT_COUNT;
    }


    private void goToManualCheckActivity(int day){
        Intent checkIntent = new Intent(context, ManualCheckActivity.class);
        checkIntent.putParcelableArrayListExtra("EXERCISES",DataBaseAdapter.getDbAdapter(context).getExercisesAtDay(day));
        checkIntent.putExtra("SETS",1);
        checkIntent.putExtra("DAILY",day);
        context.startActivity(checkIntent);
    }

    protected class ViewHolder extends RecyclerView.ViewHolder{
        private View itemView;
        private TextView textView;
        private CardView cardView;
        private ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView=itemView;
            textView = itemView.findViewById(R.id.dailyItem_day_id);
            imageView = itemView.findViewById(R.id.dailyItem_lock);
            cardView=itemView.findViewById(R.id.dailyItem_card);
        }
    }
}
