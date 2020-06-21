package com.saravana.befit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.saravana.befit.Adapters.ManualSelectRecyclerAdapter;
import com.saravana.befit.Manual.ManualCardViewInterface;
import com.saravana.befit.Manual.ManualCheckActivity;
import com.saravana.befit.Manual.ManualChooseActivity;
import com.saravana.befit.Utils.Exercises;
import com.saravana.befit.model.Exercise;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomChooseActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Spinner exerciseSpinner,setsSpinner;
    private TextView minutesText;
    private RecyclerView recyclerView;
    private Button startBtn,rechooseBtn;

    private ArrayAdapter exerciseAdapter,setAdapter;
    private ManualSelectRecyclerAdapter adapter;

    private ArrayList<Exercise> selectedExercises;
    private int totalMin;
    private int no_of_sets;
    private final int MAX_EXERCISE_MINUTES= 45;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_choose);
        initWidgets();
        selectedExercises=new ArrayList<>();
        adapter = new ManualSelectRecyclerAdapter(RandomChooseActivity.this,null,new ManualCardViewInterface() {
            @Override
            public boolean incrementExerciseCount(Exercise exercise) {
                return true;
            }

            @Override
            public boolean decreaseExerciseCount(Exercise exercise) {
                return true;
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
        adapter.disableCBox();
        rechoose();
        recyclerView.setLayoutManager(new LinearLayoutManager(RandomChooseActivity.this));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        rechooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rechoose();
            }
        });
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToManualCheckActivity();
            }
        });

    }

    private void goToManualCheckActivity() {
        no_of_sets = Integer.parseInt((String)setsSpinner.getSelectedItem());
        Intent manualChooseIntent = new Intent(RandomChooseActivity.this, ManualCheckActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("EXERCISES",selectedExercises);
        bundle.putInt("SETS",no_of_sets);

        manualChooseIntent.putExtras(bundle);
        startActivity(manualChooseIntent);

    }

    private void rechoose() {
        totalMin=0;
        int selectedItem = exerciseSpinner.getSelectedItemPosition()+1;
        selectedExercises=getRandomExercises(selectedItem);
        for(Exercise e:selectedExercises)
            totalMin+=e.getMinute();
        minutesText.setText("Total Minutes per Set: "+ totalMin);

        adapter.setExerciseList(selectedExercises);
        adapter.notifyDataSetChanged();
    }

    private void initWidgets() {
        toolbar= findViewById(R.id.random_toolbar);
        toolbar.setTitle("Random Chooser");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        exerciseSpinner= findViewById(R.id.random_choose_exercise_spinner);
        setsSpinner = findViewById(R.id.random_choose_set_spinner);
        minutesText=findViewById(R.id.random_choose_min_text);
        recyclerView=findViewById(R.id.random_choose_recyclerview);
        startBtn=findViewById(R.id.random_choose_start_btn);
        rechooseBtn=findViewById(R.id.random_choose_rechoose_btn);

        String[] MAX_EXER_COUNT=new String[Exercises.getAllExercises().size()/2];
        for(int i=0;i<Exercises.getAllExercises().size()/2;i++){
            MAX_EXER_COUNT[i] = String.valueOf(i+1);
        }
        exerciseAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,MAX_EXER_COUNT);
        exerciseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exerciseSpinner.setAdapter(exerciseAdapter);
        exerciseSpinner.setSelection(MAX_EXER_COUNT.length-1);
        exerciseAdapter.setNotifyOnChange(true);
        exerciseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rechoose();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        setAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,new String[]{"1","2","3"});
        setAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setsSpinner.setAdapter(setAdapter);

    }
    private ArrayList<Exercise> getRandomExercises(int total_numbers){
        List<Exercise> list = new ArrayList<>(Exercises.getAllExercises());
        Log.d("NOW", "getRandomExercises: Size"+list.size());
        ArrayList<Exercise> random_list=new ArrayList<>();
        Random r=new Random();
        for(int i=0;i<total_numbers;i++){
            int index = r.nextInt(list.size());
            Exercise exercise = list.get(index);
            random_list.add(exercise);
            list.remove(index);
        }
        return random_list;
    }
    private boolean incrementMinutes(int min){
        if(totalMin+min<=MAX_EXERCISE_MINUTES){
            totalMin+=min;
            minutesText.setText("Total Minutes per Set: "+ totalMin);
            Log.d("IWANT", "incrementMinutes: Total:"+totalMin+" Added:"+min);
            return true;
        }else{
            Toast.makeText(RandomChooseActivity.this,"Maximum Minutes/Exercise:"+MAX_EXERCISE_MINUTES+" Min",Toast.LENGTH_LONG).show();
        }
        Log.d("IWANT", "incrementMin(rejected): Total:"+totalMin+" Added:"+min);

        return false;

    }
    private boolean decrementMinutes(int min){
        if(totalMin-min>=0){
            totalMin-=min;
            minutesText.setText("Total Minutes per Set: "+ totalMin);
            Log.d("IWANT", "decrementMin: Total:"+totalMin+" Added:"+min);
            return true;
        }
        Log.d("IWANT", "decrementMin(rejected): Total:"+totalMin+" Added:"+min);


        return false;
    }


}
