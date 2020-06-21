package com.saravana.befit.Manual;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.saravana.befit.Adapters.ManualSelectRecyclerAdapter;
import com.saravana.befit.PractiseEntryActivity;
import com.saravana.befit.R;
import com.saravana.befit.Utils.Exercises;
import com.saravana.befit.model.Exercise;

import java.util.ArrayList;

public class ManualChooseActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView exerciseCountText,exerciseMinText;
    private Spinner exerciseSetSpinner;
    private RecyclerView recyclerView;
    private ManualSelectRecyclerAdapter adapter;
    private Button startBtn;

    private ArrayList<Exercise> selectedExercises;
    private int totalMin;
    private int no_of_sets;
    private final int MAX_EXERCISE_MINUTES= 45;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_choose);
        initWidgets();

        selectedExercises = new ArrayList<>();
        totalMin=0;
        adapter = new ManualSelectRecyclerAdapter(this, Exercises.getAllExercises(), new ManualCardViewInterface() {
            @Override
            public boolean incrementExerciseCount(Exercise exercise) {
                if(incrementMinutes(exercise.getMinute())) {
                    selectedExercises.add(exercise);
                    exerciseCountText.setText("Number of Exercises: "+selectedExercises.size());
                    return true;
                }
                else
                    return false;
            }

            @Override
            public boolean decreaseExerciseCount(Exercise exercise) {
               if(decrementMinutes(exercise.getMinute())) {
                   selectedExercises.remove(exercise);
                   exerciseCountText.setText("Number of Exercises: "+selectedExercises.size());
                   return true;
               }
               else
                   return false;
            }

            @Override
            public boolean increaseMinutes() {
                if(incrementMinutes(1))
                    return true;
                else
                    return false;
            }

            @Override
            public boolean decreaseMinutes() {
               if(decrementMinutes(1))
                   return true;
               else
                   return false;
            }
        });

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                no_of_sets = Integer.parseInt(exerciseSetSpinner.getSelectedItem().toString());
                goToManualCheckActivity();

            }
        });


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

    }

    private void goToManualCheckActivity() {
        Intent manualChooseIntent = new Intent(ManualChooseActivity.this, ManualCheckActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("EXERCISES",selectedExercises);
        bundle.putInt("SETS",no_of_sets);

        manualChooseIntent.putExtras(bundle);
        startActivity(manualChooseIntent);
    }

    private void initWidgets() {
        toolbar = findViewById(R.id.manual_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Choose Today Excerises");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        exerciseCountText = findViewById(R.id.manual_exercise_count);
        exerciseMinText = findViewById(R.id.manual_exercise_min);
        exerciseSetSpinner = findViewById(R.id.manual_exercise_set_spinner);
        recyclerView = findViewById(R.id.manual_recycler_view);
        startBtn = findViewById(R.id.manual_start_btn);
        startBtn.setVisibility(View.GONE);

        ArrayAdapter spinnerAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,new String[]{"1","2","3"});
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exerciseSetSpinner.setAdapter(spinnerAdapter);

    }
    private boolean incrementMinutes(int min){
        if(totalMin==0){
            startBtn.setVisibility(View.VISIBLE);
        }
        if(totalMin+min<=MAX_EXERCISE_MINUTES){
            totalMin+=min;
            exerciseMinText.setText("Total Minutes per Set: "+ totalMin);
            Log.d("IWANT", "incrementMinutes: Total:"+totalMin+" Added:"+min);
            return true;
        }else{
            Toast.makeText(ManualChooseActivity.this,"Maximum Minutes/Exercise:"+MAX_EXERCISE_MINUTES+" Min",Toast.LENGTH_LONG).show();
        }
        Log.d("IWANT", "incrementMin(rejected): Total:"+totalMin+" Added:"+min);

        return false;

    }
    private boolean decrementMinutes(int min){
          if(totalMin-min>=0){
              totalMin-=min;
              exerciseMinText.setText("Total Minutes per Set: "+ totalMin);
              Log.d("IWANT", "decrementMin: Total:"+totalMin+" Added:"+min);
              if(totalMin==0){
                  startBtn.setVisibility(View.GONE);
              }
              return true;
          }
        Log.d("IWANT", "decrementMin(rejected): Total:"+totalMin+" Added:"+min);


        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
