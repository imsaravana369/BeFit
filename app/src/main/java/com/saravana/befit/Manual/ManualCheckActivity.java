package com.saravana.befit.Manual;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.saravana.befit.Adapters.ExerciseListAdapter;
import com.saravana.befit.Callback.ItemMoveCallback;
import com.saravana.befit.R;
import com.saravana.befit.Interfaces.StartDragListener;
import com.saravana.befit.StartExerciseActivity;
import com.saravana.befit.model.Exercise;

import java.util.ArrayList;

public class ManualCheckActivity extends AppCompatActivity{
    private TextView setCountText, perMinText, totalMinText;
    private RecyclerView recyclerView;
    private ExerciseListAdapter adapter;
    private Button startBtn;
    private ArrayList<Exercise> seleectedExercises;
    private ItemTouchHelper touchHelper;
    private int totalSets;
    private int minutes_per_set=0;
    private int daily_day;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_check);
        if(getIntent()!=null){
            Bundle bundle = getIntent().getExtras();
            seleectedExercises = bundle.getParcelableArrayList("EXERCISES");
            totalSets = bundle.getInt("SETS");
            daily_day = bundle.getInt("DAILY",-1);
            for(Exercise e: seleectedExercises)
                minutes_per_set+= e.getMinute();
        }
        initWidgets();
        for(Exercise e: seleectedExercises)
         Log.d("MYENTRY", ""+e);

        adapter = new ExerciseListAdapter(this, seleectedExercises);
        adapter.setStartDragListener(new StartDragListener() {
            @Override
            public void requestDrag(RecyclerView.ViewHolder viewHolder) {
                touchHelper.startDrag(viewHolder);
            }
        });
        ItemTouchHelper.Callback callback = new ItemMoveCallback(adapter);
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(ManualCheckActivity.this));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);


        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent startActivityIntent = new Intent(ManualCheckActivity.this, StartExerciseActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("EXERCISES",seleectedExercises);
                bundle.putInt("SETS",totalSets);
                bundle.putInt("MINUTES_SET",minutes_per_set);
                bundle.putInt("DAILY",daily_day);
                startActivityIntent.putExtras(bundle);
                startActivity(startActivityIntent);
                finish();

            }
        });
    }

    private void initWidgets() {
        setCountText = findViewById(R.id.m_practise_set_count);
        setCountText.setText("No.of.Sets: "+totalSets);
        perMinText = findViewById(R.id.m_practise_min_per_set);
        perMinText.setText("Minutes/Set: "+minutes_per_set);
        totalMinText = findViewById(R.id.m_practise_total_min);
        totalMinText.setText("Total Minutes: "+ minutes_per_set*totalSets);
        recyclerView = findViewById(R.id.m_practise_recyclerview);
        startBtn = findViewById(R.id.m_practise_startbtn);
    }

}
