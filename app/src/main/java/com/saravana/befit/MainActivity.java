package com.saravana.befit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.saravana.befit.Adapters.DataBaseAdapter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView bellIcon,dailyIcon,practiseIcon,recordIcon;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWigets();
        bellIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSettingsActivity();
            }
        });
    }

    private void goToSettingsActivity() {
        Intent settingsIntent = new Intent(MainActivity.this,SettingsActivity.class);
        startActivity(settingsIntent);

    }


    private void initWigets() {
        toolbar = findViewById(R.id.main_toolbar);
        bellIcon = findViewById(R.id.main_bell);
        dailyIcon = findViewById(R.id.main_daily);
        practiseIcon = findViewById(R.id.main_practise);
        recordIcon = findViewById(R.id.main_record);

        bellIcon.setOnClickListener(this);
        dailyIcon.setOnClickListener(this);
        practiseIcon.setOnClickListener(this);
        recordIcon.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.main_bell:
                break;
            case R.id.main_daily:
                goToDailyActivity();
                break;
            case R.id.main_practise:
                goToPractiseEntryActivity();
                break;
            case R.id.main_record:
                goToRecordActivity();
                break;
        }
    }

    private void goToDailyActivity() {
       Intent dailyActivityIntent = new Intent(MainActivity.this,DailyActivity.class);
       startActivity(dailyActivityIntent);
    }

    private void goToPractiseEntryActivity() {
        Intent practiseEntryActvity = new Intent(MainActivity.this,PractiseEntryActivity.class);
        startActivity(practiseEntryActvity);

    }
    private void goToRecordActivity() {
        Intent recordActivity = new Intent(MainActivity.this,VisualizationActivity.class);
        startActivity(recordActivity);

    }


}
