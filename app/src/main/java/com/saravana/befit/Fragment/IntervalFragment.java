package com.saravana.befit.Fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.saravana.befit.Interfaces.FinishInterval;
import com.saravana.befit.Manual.onBackPressed;
import com.saravana.befit.R;
import com.saravana.befit.model.Exercise;


/**
 * A simple {@link Fragment} subclass.
 */
public class IntervalFragment extends Fragment implements onBackPressed {
    public static final String TAG="IWANT";
    private View view;
    private FinishInterval mFinishInterval;
    private Button skipBtn;
    private Exercise nextExercise;
    private TextView counterText,nextExerciseText,minText;
    private CountDownTimer timer;
    private long TOTAL_INTERVAL=40*1000;
    private long INTERVAL_MILLIS_LEFT;
    public IntervalFragment() {
        // Required empty public constructor
    }
    public void setFinishInterval(FinishInterval mFinishInterval){
        this.mFinishInterval=mFinishInterval;
    }
    public void setNextExercise(Exercise exercise){
        this.nextExercise=exercise;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "INTERVAL: onCreateView");
        view= inflater.inflate(R.layout.fragment_interval, container, false);
        counterText = view.findViewById(R.id.interval_fragment_counter);
        nextExerciseText = view.findViewById(R.id.interval_fragment_next_exercise_text);
        minText= view.findViewById(R.id.interval_fragment_next_exercise_min_text);
        skipBtn = view.findViewById(R.id.interval_fragment_skipBtn);

        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFinishInterval.startExercise(toSeconds(TOTAL_INTERVAL-INTERVAL_MILLIS_LEFT));
            }
        });

        INTERVAL_MILLIS_LEFT=TOTAL_INTERVAL;
        return view;
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            Log.d(TAG, "Interval : not hidden");
            nextExerciseText.setText(nextExercise.getName());
            minText.setText(nextExercise.getMinute()+" min");
            startTimer(INTERVAL_MILLIS_LEFT);
        }else{
            Log.d(TAG, "INTERVAL :  hidden");
        }

    }
    private int toSeconds(long milliseconds){
        return (int) (milliseconds/1000);
    }
    private void startTimer(long totalmilli){
        timer=new CountDownTimer(totalmilli, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                INTERVAL_MILLIS_LEFT = millisUntilFinished-1000;
                counterText.setText(String.format("%02d",toSeconds(INTERVAL_MILLIS_LEFT)));
            }

            @Override
            public void onFinish() {
                mFinishInterval.startExercise(toSeconds(TOTAL_INTERVAL));
            }
        };
        timer.start();
    }
    private void stopTimer(){
        if(timer!=null)
           timer.cancel();
    }
    public void resumeTimer(){
        startTimer(INTERVAL_MILLIS_LEFT);
    }

    @Override
    public void onBackPressed() {
        stopTimer();
    }
}
