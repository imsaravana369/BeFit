package com.saravana.befit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.saravana.befit.Adapters.DailyAdapter;
import com.saravana.befit.Adapters.DataBaseAdapter;
import com.saravana.befit.Manual.ManualCheckActivity;
import com.saravana.befit.Utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DailyActivity extends AppCompatActivity {
    public static final String TAG = "DailyActivity";
    private Toolbar toolbar;
    private TextView textView;
    private RecyclerView recyclerView;
    private Button  startBtn;
    private SharedPreferences sharedPreferences;
    private DailyAdapter adapter;
    private boolean isTodaySessionOver=false;
    private int day;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily);
        initWidgets();
        Log.d(TAG, "onCreate: One");
        day = (DataBaseAdapter.getDbAdapter(DailyActivity.this).getHighestDay())+1;
        Log.d(TAG, "onCreate: Two");
        adapter = new DailyAdapter(DailyActivity.this, day,isTodaySessionOver);
        recyclerView.setLayoutManager(new GridLayoutManager(this,5));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToManualCheckActivity(day);
            }
        });
    }

    private void initWidgets() {
        toolbar = findViewById(R.id.daily_toolbar);
        textView = findViewById(R.id.daily_textview);
        recyclerView= findViewById(R.id.daily_recyclerview);
        startBtn=findViewById(R.id.daily_startBtn);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Daily Exercise");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharedPreferences(getPackageName()+"."+TAG,MODE_PRIVATE);

        long last_done_milli = getSharedPreferences();
        isTodaySessionOver=checkIfTodaySessionOver(last_done_milli);
        Log.d(TAG, "initWidgets: isTodaySessionOver:"+isTodaySessionOver);
    }

    private long getSharedPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        long last_done = sharedPreferences.getLong(Utils.SP_LAST_DONE,0);
        if(last_done!=0){ // the user has already started daily exercises
            textView.setText("Last Visited:"+ toStringDate(last_done));
        }
        else{
            //the user hasn't started daily exercises
            textView.setText("Getting Started Today!!");
        }
        return last_done;
    }
    private String toStringDate(long date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return simpleDateFormat.format(new Date(date));
    }
    private boolean checkIfTodaySessionOver(long dateMilli){
        Calendar calendarToday = Calendar.getInstance() ;
        Calendar calendarLastDone = Calendar.getInstance();
        calendarToday.setTime(new Date()); //today
        calendarLastDone.setTimeInMillis(dateMilli); //last done data

        // neglecting the hours,minutes,seconds,milliseconds
        calendarToday = getOnlyDayMonthYearCalendar(calendarToday);
        calendarLastDone = getOnlyDayMonthYearCalendar(calendarLastDone);

        Date d1 = calendarToday.getTime();
        Date d2 = calendarLastDone.getTime();

        return d1.compareTo(d2)==0;

    }
    private Calendar getOnlyDayMonthYearCalendar(Calendar c){
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c;

    }
    private void goToManualCheckActivity(int day){
        Intent checkIntent = new Intent(DailyActivity.this, ManualCheckActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("EXERCISES",DataBaseAdapter.getDbAdapter(DailyActivity.this).getExercisesAtDay(day));
        bundle.putInt("SETS",1);
        checkIntent.putExtras(bundle);
        startActivity(checkIntent);
    }
}
