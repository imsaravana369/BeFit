package com.saravana.befit.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.saravana.befit.Manual.ManualCardViewInterface;
import com.saravana.befit.R;
import com.saravana.befit.model.Exercise;

import java.util.ArrayList;

public class ManualSelectRecyclerAdapter extends RecyclerView.Adapter<ManualSelectRecyclerAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<Exercise> excerciseList;
    private ArrayList<ViewHolder> holders;
    private static int MAX_MINUTES= 15;
    private ManualCardViewInterface mInterface;
    private boolean isCboxEnabled;
    public ManualSelectRecyclerAdapter(Context context,ArrayList<Exercise> exercises,ManualCardViewInterface mInterface){
        this.mContext = context;
        this.excerciseList = exercises;
        this.mInterface= mInterface;
        this.holders = new ArrayList<>();
        this.isCboxEnabled=true;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType<holders.size()){
            return holders.get(viewType);
        }else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manual_exercise_item, null);
            ViewHolder viewHolder = new ViewHolder(view);
            if(!isCboxEnabled) {
                viewHolder.cBox.setEnabled(false);
                viewHolder.cBox.setVisibility(View.GONE);
                viewHolder.enablePlusBtn();
                viewHolder.enableMinusBtn();
            }
            holders.add(viewHolder);
            viewHolder.setIsRecyclable(false);
            return viewHolder;
        }
    }
    public void disableCBox(){
        this.isCboxEnabled=false;

    }
    public void setExerciseList(ArrayList<Exercise> excerciseList){
        this.excerciseList=excerciseList;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull final  ViewHolder holder, int position) {
        final Exercise exercise = excerciseList.get(position);
        holder.setImgView(exercise.getImgId());
        holder.cBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    holder.enablePlusBtn();
                    holder.enableMinusBtn();
                    if(mInterface.incrementExerciseCount(exercise)){
                        if(Integer.parseInt(holder.exerCountText.getText().toString())==0) {
                            if (mInterface.increaseMinutes()) {
                                exercise.incrementMinutes();
                                holder.incrementCount();
                                holder.cardView.setBackgroundColor(mContext.getResources().getColor(R.color.selected_box_color,null));
                            }
                        }else {
                            holder.cardView.setBackgroundColor(mContext.getResources().getColor(R.color.selected_box_color, null));
                        }

                    }else{
                        holder.cBox.setChecked(false);
                        holder.disableMinusBtn();
                        holder.disablePlusBtn();
                    }
                }
                else {
                    holder.disablePlusBtn();
                    holder.disableMinusBtn();
                    if(mInterface.decreaseExerciseCount(exercise)){
                        exercise.clearMinutes();
                        holder.clearCount();
                        holder.cardView.setBackgroundColor(mContext.getResources().getColor(R.color.unselected_box_color,null));
                    }else{
                        holder.cBox.setChecked(true);
                        holder.enablePlusBtn();
                        holder.enableMinusBtn();
                    }
                }
            }
        });
        holder.setExerNameText(exercise.getName());

        holder.plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.cBox.isChecked() || ! isCboxEnabled){
                    if(mInterface.increaseMinutes()){
                        exercise.incrementMinutes();
                        holder.incrementCount();
                    }
                }
            }
        });

        holder.minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((holder.cBox.isChecked() || !isCboxEnabled) && Integer.parseInt(holder.exerCountText.getText().toString())>1){
                    if(mInterface.decreaseMinutes()){
                        exercise.decrementMinutes();
                        holder.decrementCount();
                    }
                }
            }
        });



    }

    @Override
    public int getItemCount() {
        return this.excerciseList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private CheckBox cBox;
        private ImageView imgView;
        private TextView exerNameText, exerCountText;
        private ImageButton plusBtn,minusBtn;
        private CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.manual_exercise_card);
            cBox = itemView.findViewById(R.id.manual_item_checkbox);
            imgView = itemView.findViewById(R.id.manual_item_img);
            exerNameText = itemView.findViewById(R.id.manual_item_exercise_name);
            exerCountText = itemView.findViewById(R.id.manual_item_count);
            plusBtn = itemView.findViewById(R.id.manual_item_plus);
            minusBtn = itemView.findViewById(R.id.manual_item_minus);

                    }
        public void setImgView(int imgId){
            imgView.setImageResource(imgId);
        }
        public void setExerNameText(String exerName){
            exerNameText.setText(exerName);
        }

        public void clearCount(){
            exerCountText.setText(""+0);
        }
        public void incrementCount(){
            Log.d("NOW", "incrementCount");
            int val = Integer.parseInt(exerCountText.getText().toString());
            if(val<MAX_MINUTES) {
                val++;
                exerCountText.setText("" + val);
                if(val==1)
                    enableMinusBtn();
                else if(val == MAX_MINUTES)
                    disablePlusBtn();
            }

        }
        public void decrementCount(){
            Log.d("NOW", "decrementCount");

            int val = Integer.parseInt(exerCountText.getText().toString());
            if(val>0) {
                val--;
                exerCountText.setText("" + val);
                if(val==0)
                    disableMinusBtn();
                else if(val== MAX_MINUTES-1)
                    enablePlusBtn();

            }
        }

        private void enablePlusBtn(){
            plusBtn.setEnabled(true);
            plusBtn.setClickable(true);
            plusBtn.setImageResource(R.drawable.plus);

        }
        private void disablePlusBtn(){
            plusBtn.setEnabled(false);
            plusBtn.setClickable(false);
            plusBtn.setImageResource(R.drawable.plus_disabled);
        }

        private void enableMinusBtn(){
            minusBtn.setEnabled(true);
            minusBtn.setClickable(true);
            minusBtn.setImageResource(R.drawable.minus);
        }
        private void disableMinusBtn(){
            minusBtn.setEnabled(false);
            minusBtn.setClickable(false);
            minusBtn.setImageResource(R.drawable.minus_disabled);

        }

    }
}
