package com.saravana.befit.Fragment;


import android.os.Bundle;


import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.saravana.befit.Interfaces.FinishExercise;
import com.saravana.befit.Manual.onBackPressed;
import com.saravana.befit.R;
import com.saravana.befit.model.Exercise;

import java.text.MessageFormat;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ExerciseFragment extends Fragment implements onBackPressed {
    public static final String TAG="IWANT";
    private View view;
    private Exercise currentExercise;
    private FinishExercise mFinishExercise;
    private TextView nameText,timerText;
    private ImageView img;
    private CountDownTimer timer;
    private long MILLIS_LEFT;
    public ExerciseFragment() {
        // Required empty public constructor
    }

    public void setFinishExerciseInterface(FinishExercise mFinishExercise){
        this.mFinishExercise=mFinishExercise;
    }
    public void setCurrentExercise(Exercise exercise){
        this.currentExercise=exercise;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "EXERCISE : onCreateView ");
        view = inflater.inflate(R.layout.fragment_exercise, container, false);

        nameText = view.findViewById(R.id.fragment_exer_name);
        timerText = view.findViewById(R.id.fragment_exer_timer);
        img= view.findViewById(R.id.fragment_exer_imgview);
        nameText.setText(currentExercise.getName());
        img.setImageResource(currentExercise.getImgId());

        return view;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            Log.d(TAG, "EXERCISE :not hidden");
            nameText.setText(currentExercise.getName());
            img.setImageResource(currentExercise.getImgId());
            MILLIS_LEFT = currentExercise.getMinute()*60*1000;
            startTimer(MILLIS_LEFT);

        }else{
            Log.d(TAG, "EXERCISE : hidden");
        }
    }
    private void startTimer(long totalmilli){
        //testing purpose
        timer=new CountDownTimer(totalmilli, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int minutes = ((int) millisUntilFinished)/(60*1000);
                int left_min= (int) millisUntilFinished - (minutes*60*1000);
                int seconds =  left_min/1000;
                timerText.setText(String.format("%02d:%02d",minutes,seconds));
                MILLIS_LEFT-=1000;
            }

            @Override
            public void onFinish() {
                mFinishExercise.startInterval();
            }
        };
        timer.start();
    }
    private void stopTimer(){
        if(timer!=null)
            timer.cancel();
    }
    public void resumeTimer(){
        startTimer(MILLIS_LEFT);
    }

    @Override
    public void onBackPressed() {
        stopTimer();
    }
}
