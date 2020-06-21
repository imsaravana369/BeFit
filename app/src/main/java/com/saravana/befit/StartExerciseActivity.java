package com.saravana.befit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.saravana.befit.Adapters.DataBaseAdapter;
import com.saravana.befit.Fragment.ExerciseFragment;
import com.saravana.befit.Fragment.IntervalFragment;
import com.saravana.befit.Fragment.RecordFragment;
import com.saravana.befit.Interfaces.FinishExercise;
import com.saravana.befit.Interfaces.FinishInterval;
import com.saravana.befit.Utils.Utils;
import com.saravana.befit.model.Exercise;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StartExerciseActivity extends AppCompatActivity implements AudioManager.OnAudioFocusChangeListener {
    public static final String TAG="IWANT";
    private ArrayList<Exercise> selectedExercises;
    private  int TOTAL_SETS, MINUTES_PER_SET;
    private int daily_exercise_day;

    private TextView setsText,exerCountText;
    private FrameLayout frameLayout;
    private ExerciseFragment exerciseFragment;
    private IntervalFragment intervalFragment;
    private RecordFragment recordFragment;
    private FragmentManager fragmentManager;
    private Map<String,Object> map;
    private int CURRENT_SET=1,CURRENT_EXERCISE=1;
    private int startCount=3;
    private MediaPlayer mediaPlayer;
    private ArrayList<Integer> musicIds;
    private  int musicSize;
    private int currRunningSongCtr;
    private AudioManager audioManager;
    private AudioFocusRequest audioFocusRequest;
    private boolean isExerciseRunning;
    final Object focusLock = new Object();  // a dummy object used for syncronization for audio playing
    //private boolean playbackDelayed ;
    private boolean playbackNowAuthorized ;
    //private boolean resumeOnFocusGain;


    private DataBaseAdapter db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_exercise);
        if(getIntent()!=null){
            Intent i = getIntent();
            Bundle bundle = i.getExtras();
            selectedExercises = bundle.getParcelableArrayList("EXERCISES");
            TOTAL_SETS = bundle.getInt("SETS",1);
            MINUTES_PER_SET = bundle.getInt("MINUTES_SET",TOTAL_SETS*1);
            daily_exercise_day = bundle.getInt("DAILY",-1);

        }
        initWidgets();
        setHeaders();
        if(isMusicOn()) {
            initMusic();
            initAudioFocus();
        }

        db= DataBaseAdapter.getDbAdapter(StartExerciseActivity.this);
        //db.delete_tables();
        map=new HashMap<>();
        //java.sql.Date.valueOf(date) ==> returns java.sql.Date
        // insert data as String
        map.put(Utils.DATE, (Utils.getDateString(new Date(),false)));

        exerciseFragment = new ExerciseFragment();
        exerciseFragment.setCurrentExercise(getCurrentExercise());
        exerciseFragment.setFinishExerciseInterface(new FinishExercise() {
            @Override
            public void startInterval() {
                stopMediaPlayer();
                addExerciseToMap();  // add done exercise to map
                CURRENT_SET += (CURRENT_EXERCISE/ selectedExercises.size());
                CURRENT_EXERCISE = (CURRENT_EXERCISE%selectedExercises.size())+1;

                if(CURRENT_SET>TOTAL_SETS){
                    showRecordFragment();
                }else{
                    showIntervalFragment();
                    intervalFragment.setNextExercise(getCurrentExercise());
                }
            }
        });
        intervalFragment = new IntervalFragment();
        intervalFragment.setFinishInterval(new FinishInterval() {
            @Override
            public void startExercise(int interval_sec) {
                showExerciseFragment();
                exerciseFragment.setCurrentExercise(getCurrentExercise());
                setHeaders();
                startMediaPlayer();
                int timeSpentOnInterval = getorDefaultInt(Utils.INTERVAL_MIN_KEYS,0); // return TimeSpentOnInterval if exists else 0
                map.put(Utils.INTERVAL_MIN_KEYS, timeSpentOnInterval+interval_sec); // updating interval time

            }
        });

        recordFragment = new RecordFragment();
        fragmentManager=getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.start_exer_container,exerciseFragment).commit();
        Log.d(TAG, "ADDED EXERCISE FRAGMENT");
        fragmentManager.beginTransaction().add(R.id.start_exer_container,intervalFragment).commit();
        Log.d(TAG, "ADDED INTERVAL FRAGMENT");

        fragmentManager.beginTransaction().hide(exerciseFragment).commit();
        Log.d(TAG, "SHOW EXERCISE");
        fragmentManager.beginTransaction().hide(intervalFragment).commit();
        Log.d(TAG, "HIDE INTERVAL ");

        showCountDownDialog();  //first thing thats shown, a countdown timer that counts 3-2-1

    }

    private void initAudioFocus() {
        int res;
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                                          .setUsage(AudioAttributes.USAGE_MEDIA)
                                          .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                           .build();

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O) {
             audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(audioAttributes)
                    .setAcceptsDelayedFocusGain(true)
                     .setOnAudioFocusChangeListener(StartExerciseActivity.this)
                    .build();
             res = audioManager.requestAudioFocus(audioFocusRequest);
        }
        else{
            res = audioManager.requestAudioFocus(StartExerciseActivity.this,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
        }

        switch (res){
            case AudioManager.AUDIOFOCUS_REQUEST_GRANTED:
                playbackNowAuthorized=true;
                //playbackDelayed=false;
                break;
            case AudioManager.AUDIOFOCUS_REQUEST_FAILED:
                playbackNowAuthorized=false;
                //playbackDelayed=false;
                break;
            case AudioManager.AUDIOFOCUS_REQUEST_DELAYED:
                //playbackDelayed=true;
                playbackNowAuthorized=false;
                break;
        }


    }
    private void initMusic() {
        int[] musicIdArr = {R.raw.one,R.raw.two,R.raw.three,R.raw.four};
        musicIds=new ArrayList<>();
        for(int id : musicIdArr)
            musicIds.add(id);
        musicSize = musicIds.size();
        currRunningSongCtr=-1;
    }


    private int getorDefaultInt(String objName,int def){
        int retVal = def;
        try {
            if (map.containsKey(objName)) {
                retVal = (Integer) map.get(objName);
            }
        }
        catch (ClassCastException ex){
            Log.e(TAG, "getorDefaultInt: ClassCastException :"+ex.getMessage());
        }

        return retVal;
    }

    private void addExerciseToMap() {
        Exercise exercise = getCurrentExercise();
        int prevtimeSpentOnExercise = getorDefaultInt(exercise.getName(),0);
        map.put(exercise.getName(),prevtimeSpentOnExercise+exercise.getMinute());

    }

    private void showIntervalFragment(){
        fragmentManager.beginTransaction().hide(exerciseFragment).commit();
        fragmentManager.beginTransaction().show(intervalFragment).commit();
        isExerciseRunning=false;
    }
    private void showExerciseFragment(){
        fragmentManager.beginTransaction().hide(intervalFragment).commit();
        fragmentManager.beginTransaction().show(exerciseFragment).commit();
        isExerciseRunning=true;
    }
    private void showRecordFragment(){
        fragmentManager.beginTransaction().remove(exerciseFragment).commit();
        fragmentManager.beginTransaction().remove(intervalFragment).commit();
        Log.d("WHATIWANT", "showRecordFragment: GONNA ADD TO DB ");
        addToDB(); //adding the exercise the user has done to the data base
        Log.d("WHATIWANT", "showRecordFragment: GONNA ADD TO PREF ");
        addToSharedPref();
        Log.d("WHATIWANT", "showRecordFragment: GONNA SET EXERCISE");
        recordFragment.setExerciseMap(map);
        Log.d("WHATIWANT", "showRecordFragment: gonna begin transaction ");
        fragmentManager.beginTransaction().add(R.id.start_exer_container,recordFragment).commitAllowingStateLoss();
        // if you use commit() it will throw illegalStateException
        Log.d("WHATIWANT", "showRecordFragment: begin transaction committed");
    }

    private void addToDB() {
        boolean isDailyExercise=false;
        if(daily_exercise_day>0) {
            map.put(Utils.DAY, daily_exercise_day);  //user can redo the day-exercise that he has already done
            isDailyExercise=true;
        }
        //  db.Insert(Map<String,Integer> map,boolean isDailyExercise,String[] toAvoidMapKeys)
        Log.d(TAG, "isInserted: "+db.Insert(map,isDailyExercise,new String[]{Utils.INTERVAL_MIN_KEYS})); //we dont need to insert Interval_Minutes in db
    }
    private void addToSharedPref(){
        if(daily_exercise_day>0){
            SharedPreferences sharedPreferences = getSharedPreferences(getPackageName()+"."+DailyActivity.TAG,MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong(Utils.SP_LAST_DONE, new Date().getTime());
            editor.commit();
        }
    }

    private Exercise getCurrentExercise(){
        if(CURRENT_EXERCISE <= selectedExercises.size())  // for safety
            return selectedExercises.get(CURRENT_EXERCISE-1);
        return null;
    }
    private void showCountDownDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(StartExerciseActivity.this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View view = LayoutInflater.from(this).inflate(R.layout.start_exer_pop_up_layout,viewGroup,false);

        builder.setCancelable(false);
        builder.setView(view);
        final TextView countText = view.findViewById(R.id.start_exer_popup_count);

        final AlertDialog dialog = builder.create();
        new CountDownTimer(3000,1000){

            @Override
            public void onTick(long millisUntilFinished) {
                countText.setText(String.valueOf(startCount--));
            }

            @Override
            public void onFinish() {
                dialog.cancel();
                fragmentManager.beginTransaction().show(exerciseFragment).commit();
                if(playbackNowAuthorized)
                     startMediaPlayer();
                Log.d(TAG, "SHOW EXERCISE");
                fragmentManager.beginTransaction().hide(intervalFragment).commit();
                Log.d(TAG, "HIDE INTERVAL ");
            }
        }.start();
        dialog.show();
    }

    private void initWidgets() {
        setsText = findViewById(R.id.start_exer_sets_count);
        exerCountText = findViewById(R.id.start_exer_exer_count);
        frameLayout = findViewById(R.id.start_exer_container);
    }
    private void setHeaders(){
        setsText.setText("Sets "+CURRENT_SET+"/"+TOTAL_SETS);
        exerCountText.setText("Exercises "+CURRENT_EXERCISE+"/"+selectedExercises.size());
    }

    @Override
    public void onBackPressed() {
        stopFragments(true);
        pauseMediaPlayer();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you want to Quit?");
        builder.setMessage("If you feel like quitting, then think why you have started ?");
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    stopFragments(false);
                    resumeMediaPlayer();
            }
        }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //StartExerciseActivity.super.onBackPressed();
                stopMediaPlayer();
                showRecordFragment();
            }
        });
        builder.create().show();
    }
    private void stopFragments(boolean stop){
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.start_exer_container);
        if(currentFragment!=null){
            if(stop) {
                if (currentFragment instanceof ExerciseFragment)
                    ((ExerciseFragment) currentFragment).onBackPressed();
                else if (currentFragment instanceof IntervalFragment)
                    ((IntervalFragment) currentFragment).onBackPressed();
            }else{
                if (currentFragment instanceof ExerciseFragment)
                    ((ExerciseFragment) currentFragment).resumeTimer();
                else if (currentFragment instanceof IntervalFragment)
                    ((IntervalFragment) currentFragment).resumeTimer();
            }


        }
    }
    private void startMediaPlayer(){
        if(isMusicOn()) {
            if (mediaPlayer == null) {
                currRunningSongCtr += 1;
                int currSongId = (currRunningSongCtr) % musicSize;
                mediaPlayer = MediaPlayer.create(StartExerciseActivity.this, musicIds.get(currSongId));
            }
            playbackNowAuthorized = true;
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
    }
    private void resumeMediaPlayer(){
        if(isExerciseRunning) {
            if (mediaPlayer == null) {
                startMediaPlayer();
            } else {
                mediaPlayer.start();
            }
        }
    }
    private void stopMediaPlayer(){
        if(mediaPlayer!=null) {
            playbackNowAuthorized=false;
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }
    private void pauseMediaPlayer(){
        if(mediaPlayer!=null) {
//            if(!resumeOnFocusGain) {
//                Log.d(TAG, "pauseMediaPlayer: ResumeOnFocusGain = False");
//                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
//                      audioManager.abandonAudioFocusRequest(audioFocusRequest);
//            }
            mediaPlayer.pause();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mediaPlayer!=null) {
            stopMediaPlayer();
            abandonAudioFocus();
        }

    }
    private void abandonAudioFocus(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
            audioManager.abandonAudioFocusRequest(audioFocusRequest);
        else
            audioManager.abandonAudioFocus(StartExerciseActivity.this);

    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch(focusChange){
            case AudioManager.AUDIOFOCUS_GAIN:
                Log.d(TAG, "onAudioFocusChange: AUDIOFOCUS_GAIN");
//               if( resumeOnFocusGain || playbackDelayed ){
//                    Log.d(TAG, "onAudioFocusChange: AudioFocusGained: resumeOnFocusGain:"+ (resumeOnFocusGain)+",PlayBackDelayed:"+playbackDelayed);
//                    synchronized (focusLock){
//                        playbackDelayed=false;
//                        resumeOnFocusGain=false;
//                    }
                    startMediaPlayer();
//                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                Log.d(TAG, "onAudioFocusChange: AUDIOFOCUS_LOSS");

//                synchronized (focusLock){
//                    playbackDelayed=false;
//                    resumeOnFocusGain=false;
//                }
                //requestForAudioRequest(); // because once audioFocus is lost, the system lose audio focus
                pauseMediaPlayer();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                Log.d(TAG, "onAudioFocusChange: AUDIOFOCUS_LOSS_TRANSIENT");

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                Log.d(TAG, "onAudioFocusChange: AUDIOOCUS_LOSS_TRANSIENT_CAN_DUCK");

//                synchronized (focusLock){
//                    resumeOnFocusGain=true;
//                    playbackDelayed=false;
//                }
                pauseMediaPlayer();
                break;
        }
    }
       private boolean isMusicOn(){
        return getSharedPreferences(Utils.NOTIF_SP,MODE_PRIVATE).getBoolean(Utils.SETTINGS_MUSIC_ON,true);

    }
}
