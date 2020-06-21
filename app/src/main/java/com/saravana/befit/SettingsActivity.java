package com.saravana.befit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.saravana.befit.BroadCastReceiver.NotificationReceiver;
import com.saravana.befit.Utils.Utils;

import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";
    private Toolbar toolbar;
    private TextView notificationDayRangeText,notificationTimeText,notificationTimeSubText;
    private SwitchCompat switchBtn;
    private ImageButton datepickerBtn;
    private View alertDialogView;
    private ImageButton addBtn,minusBtn;
    private TextView dayCountText;
    private CheckBox musicCBox;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sharedPreferences = getSharedPreferences(Utils.NOTIF_SP,MODE_PRIVATE);
        initWidgets();
        switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    showText();
                }else{
                    hideText();
                    setNotificationAlarm(false);
                }
            }
        });
        musicCBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setMusicOn(isChecked);
            }
        });
        notificationDayRangeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });
        datepickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timerPicker = new TimePickerDialog(SettingsActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Log.d(TAG, "onTimeSet: hour:"+hourOfDay+" Minutes:"+minute);
                        setTimeStatus(hourOfDay,minute,true);
                        setNotificationAlarm(true);
                    }
                },6,0,false);
                timerPicker.show();

            }
        });

    }
    private void setTimeStatus(int hour,int minute,boolean saveTosp){
        if(saveTosp) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(Utils.NOTIF_SP_TIME_HOUR, hour);
            editor.putInt(Utils.NOTIF_SP_TIME_MINUTE, minute);
            editor.commit();
        }

        String ampm;
        if(hour>=12){
            hour = (hour%12) + 12;
            ampm= "PM";
        }
        else if(hour==0){
            hour=12;
            ampm="AM";
        }
        else{
            ampm= "AM";
        }
        notificationTimeSubText.setText(String.format("%02d:%02d %s",hour,minute,ampm));
        notificationTimeSubText.setAlpha(0.7f);
    }
    private void setDayRangeStatus(Integer day,boolean saveToSp){
        if(day==null)
            day =Integer.parseInt(dayCountText.getText().toString());

        if(saveToSp) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(Utils.NOTIF_SP_DAY_RANGE, day);
            editor.commit();
        }

        String str = String.format("Notify me every %d day(s)",day);
        notificationDayRangeText.setText(str);

    }
    private void showAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        if(alertDialogView.getParent()!=null){
            ((ViewGroup)alertDialogView.getParent()).removeView(alertDialogView);
        }
        builder.setView(alertDialogView);
        builder.setTitle("");

        builder.setPositiveButton("SET", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int dayCount = Integer.parseInt(dayCountText.getText().toString());
                setDayRangeStatus(dayCount,true);
                setNotificationAlarm(true);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void initAlertDialogView(){
        alertDialogView = getLayoutInflater().inflate(R.layout.counter_alert_dialog,null,false);
        addBtn= alertDialogView.findViewById(R.id.counter_plus_btn);
        minusBtn=alertDialogView.findViewById(R.id.counter_minus_btn);
        dayCountText = alertDialogView.findViewById(R.id.counter_text);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = Integer.parseInt(dayCountText.getText().toString());
                if(count<7)
                    dayCountText.setText(""+(count+1));
                else
                    Toast.makeText(SettingsActivity.this,"You should workout atleast Once a Week",Toast.LENGTH_SHORT);
            }
        });
        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = Integer.parseInt(dayCountText.getText().toString());
                if(count>1)
                    dayCountText.setText(""+(count-1));

            }
        });
    }
    private void initWidgets() {
        toolbar = findViewById(R.id.sett_toolbar);
        notificationDayRangeText = findViewById(R.id.sett_notii_everyday_text);
        notificationTimeText = findViewById(R.id.sett_noti_time_text);
        notificationTimeSubText=findViewById(R.id.sett_time_sub);
        switchBtn = findViewById(R.id.sett_noti_switch);
        datepickerBtn = findViewById(R.id.sett_date_btn);
        musicCBox = findViewById(R.id.sett_music_checkbox);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Notification Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initAlertDialogView();
        setDefaultValue();
    }

    private void setDefaultValue() {
        Map<String,?> map= sharedPreferences.getAll();
        Integer day_range=null,hour=null,minutes=null;
        Boolean isNotifOn=null,isMusicOn=false;

        if(map!=null) {
            day_range = (Integer) map.get(Utils.NOTIF_SP_DAY_RANGE);
            hour = (Integer) map.get(Utils.NOTIF_SP_TIME_HOUR);
            minutes = (Integer) map.get(Utils.NOTIF_SP_TIME_MINUTE);
            isNotifOn = (Boolean) map.get(Utils.NOTIF_SP_ON);
            isMusicOn = (Boolean) map.get(Utils.SETTINGS_MUSIC_ON);
        }
        if(day_range!=null){
                setDayRangeStatus( day_range,false);
                dayCountText.setText(""+day_range);
        }else{
            setDayRangeStatus(1,false);
            dayCountText.setText(1+"");
        }
        if(hour!=null && minutes!=null){
            setTimeStatus(hour,minutes,false);
        }
        if(isNotifOn==null || isNotifOn==false ){
            switchBtn.setChecked(false);
            hideText();
        }else{
            switchBtn.setChecked(true);
            showText();
        }
        if(isMusicOn!=null )
             musicCBox.setChecked(isMusicOn);
        else
            musicCBox.setChecked(true);  //default music is on
    }


    private void showText(){
        notificationDayRangeText.setAlpha(1f);
        notificationTimeText.setAlpha(1f);
        notificationTimeSubText.setAlpha(1f);
        notificationDayRangeText.setClickable(true);
        datepickerBtn.setEnabled(true);
        onNotification(true);

    }
    private void hideText(){
        float alpha =0.5f;
        notificationDayRangeText.setAlpha(alpha);
        notificationTimeText.setAlpha(alpha);
        notificationTimeSubText.setAlpha(alpha);
        notificationDayRangeText.setClickable(false);
        datepickerBtn.setEnabled(false);
        onNotification(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void onNotification(boolean isOn){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Utils.NOTIF_SP_ON,isOn);
        editor.commit();
    }
    private void setMusicOn(boolean isMusicOn){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Utils.SETTINGS_MUSIC_ON,isMusicOn);
        editor.commit();

    }
    private void setNotificationAlarm(boolean isOn){
        Calendar c=Calendar.getInstance();
        int dayRange;
        int[] hoursSeconds;

        dayRange = sharedPreferences.getInt(Utils.NOTIF_SP_DAY_RANGE,1);

        int hrs = sharedPreferences.getInt(Utils.NOTIF_SP_TIME_HOUR,c.get(Calendar.HOUR_OF_DAY));
        int min = sharedPreferences.getInt(Utils.NOTIF_SP_TIME_MINUTE, (c.get(Calendar.MINUTE)>=30 ?30 : 0));
        setTimeStatus(hrs,min,true);

        hoursSeconds = new int[]{hrs,min};

        c.set(Calendar.HOUR_OF_DAY,hoursSeconds[0]);
        c.set(Calendar.MINUTE,hoursSeconds[1]);
        c.set(Calendar.SECOND,0);

        if(Calendar.getInstance().after(c)){
            c.add(Calendar.DAY_OF_MONTH,1);
        }

        Log.d(TAG, "setNotificationAlarm: "+ Utils.getDateString(c.getTime(),false));

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent receiverIntent = new Intent(SettingsActivity.this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(SettingsActivity.this,0,receiverIntent,PendingIntent.FLAG_CANCEL_CURRENT);

        if(isOn) {
            Log.d(TAG, "setNotificationAlarm: Alarm On");
            //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), dayRange * (AlarmManager.INTERVAL_DAY), pendingIntent);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), TimeUnit.MILLISECONDS.convert(dayRange,TimeUnit.DAYS), pendingIntent);

        }
        else {
            Log.d(TAG, "setNotificationAlarm: Alarm off");
            alarmManager.cancel(pendingIntent);
        }

    }
}
