package com.saravana.befit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.saravana.befit.Manual.ManualChooseActivity;

public class PractiseEntryActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Button manualBtn,randomBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practise_entry);

        toolbar = findViewById(R.id.practise_entry_toolbar);
        manualBtn = findViewById(R.id.practise_entry_manually);
        randomBtn = findViewById(R.id.practise_entry_randomly);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Practise");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        manualBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent manualIntent = new Intent(PractiseEntryActivity.this, ManualChooseActivity.class);
                startActivity(manualIntent);

            }
        });

        randomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent randomIntent = new Intent(PractiseEntryActivity.this, RandomChooseActivity.class);
                startActivity(randomIntent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
