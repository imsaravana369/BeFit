package com.saravana.befit.Utils;

import android.content.Context;
import android.util.Log;

import com.saravana.befit.R;
import com.saravana.befit.model.Exercise;

import java.util.ArrayList;

public class Exercises {

    private static ArrayList<Exercise> allExercises;

    static{
        allExercises = new ArrayList<>();

        Exercise pushup = new Exercise("Pushups", R.drawable.pushup ,1);
        allExercises.add(pushup);
        Exercise situp = new Exercise("Situps",R.drawable.situp,1);
        allExercises.add(situp);
        Exercise barbellCurl = new Exercise("Barbell Curl",R.drawable.barbell_curl,1);
        allExercises.add(barbellCurl);
        Exercise barbellPress = new Exercise("Barbell Press", R.drawable.barbell_press,1);
        allExercises.add(barbellPress);
        Exercise declineSitUps = new Exercise("Decline Situp",R.drawable.decline_situps,1);
        allExercises.add(declineSitUps);
        Exercise inclineBarbellPress = new Exercise("Incline Barbell Press",R.drawable.incline_barbell_press,1);
        allExercises.add(inclineBarbellPress);
        Exercise indoorRowing = new Exercise("Indoor Rowing",R.drawable.indoor_rowing,1);
        allExercises.add(indoorRowing);
        Exercise renegadeRow = new Exercise("Renegade Row",R.drawable.renegade_row,1);
        allExercises.add(renegadeRow);
        Exercise touchToe = new Exercise("TouchToe", R.drawable.touch_toe,1);
        allExercises.add(touchToe);
        Exercise vrikasana = new Exercise("Vrikasana",R.drawable.vriksasana,1);
        allExercises.add(vrikasana);
        Exercise sideStretch = new Exercise("Side Stretch",R.drawable.stretch,1);
        allExercises.add(sideStretch);

    }

    public static ArrayList<Exercise> getAllExercises(){
        return  allExercises;
    }
    public static ArrayList<String> getAllExercisesName(){
        ArrayList<String> exerNames=new ArrayList<>();
        for(Exercise exer: allExercises) {
            String name=(exer.getName()).replaceAll("\\s", "");
            Log.d("IIIWANT", ""+name);
            exerNames.add(name);
        }

        return exerNames;
    }


}
