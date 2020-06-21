package com.saravana.befit.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.saravana.befit.Callback.ItemMoveCallback;
import com.saravana.befit.R;
import com.saravana.befit.Interfaces.StartDragListener;
import com.saravana.befit.model.Exercise;

import java.util.ArrayList;
import java.util.Collections;

public class ExerciseListAdapter extends RecyclerView.Adapter<ExerciseListAdapter.ViewHolder> implements ItemMoveCallback.ItemTouchHelperContract {
    private Context mContext;
    private ArrayList<Exercise> exercises;
    private ArrayList<ViewHolder> holders;
    private StartDragListener mStartDragListener;
    public ExerciseListAdapter(Context mContext,ArrayList<Exercise> exercises){
        this.mContext = mContext;
        this.exercises = exercises;
        this.holders = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exercise_item,null);
        Log.d("MYENTRY", "Created");
        ViewHolder holder = new ViewHolder(view);
        holders.add(holder);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
       Exercise exercise = this.exercises.get(position);
       holder.countText.setText(""+(position+1));
       holder.exerImg.setImageResource(exercise.getImgId());
       holder.nameText.setText(""+exercise.getName());
       holder.minText.setText(""+exercise.getMinute()+" min");
        holder.swipeBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() ==
                        MotionEvent.ACTION_DOWN) {
                    mStartDragListener.requestDrag(holder);
                }
                return false;
            }
        });
    }
    public void setStartDragListener(StartDragListener mStartDragListener){
        this.mStartDragListener=mStartDragListener;
    }

    @Override
    public int getItemCount() {
        return this.exercises.size();
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(exercises, i, i + 1);
                Collections.swap(holders,i,i+1);
                holders.get(i).countText.setText(""+(i+1));
                holders.get(i+1).countText.setText(""+(i+2));

            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(exercises, i, i - 1);
                Collections.swap(holders,i,i-1);
                holders.get(i-1).countText.setText(""+i);
                holders.get(i).countText.setText(""+(i+1));

            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onRowSelected(ViewHolder myViewHolder) {
        myViewHolder.cardView.setBackgroundColor(Color.GRAY);
    }

    @Override
    public void onRowClear(ViewHolder myViewHolder) {
        myViewHolder.cardView.setBackgroundColor(Color.WHITE);
    }





    public class ViewHolder extends RecyclerView.ViewHolder{
        private CardView cardView;
        private ImageView exerImg;
        private ImageButton swipeBtn;
        private TextView countText,nameText,minText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.cardView= itemView.findViewById(R.id.exercise_item_cardview);
            swipeBtn = itemView.findViewById(R.id.exercise_item_swipe_btn);
            countText = itemView.findViewById(R.id.exercise_item_count);
            exerImg = itemView.findViewById(R.id.exercise_item_img);
            nameText = itemView.findViewById(R.id.exercise_item_name);
            minText = itemView.findViewById(R.id.exercise_item_min);
        }
    }
}
