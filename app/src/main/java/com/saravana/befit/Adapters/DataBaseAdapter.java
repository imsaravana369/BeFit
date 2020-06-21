package com.saravana.befit.Adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.renderscript.Element;
import android.util.Log;
import android.widget.Toast;

import com.saravana.befit.Utils.Exercises;
import com.saravana.befit.Utils.Utils;
import com.saravana.befit.model.Exercise;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DataBaseAdapter {

    private static final String TAG = "DataBaseAdapter";
    private static final String DB_NAME="BEFIT_DB";
    private static String CASAUL_EXERCISE_TABLE = "CASUAL_EXER_TABLE";
    private static String DAILY_EXERCISE_TABLE="DAILY_EXERCISE_TABLE";
    private static int DB_VERSION=1;


    private  static ArrayList<String> EXERCISES = Exercises.getAllExercisesName();

    private Context context;
    private SQLiteDatabase db;
    private static DataBaseAdapter instance;

    private DataBaseAdapter(Context context){
        this.context=context;
        db= new DataBaseHelper(context,DB_NAME,null,DB_VERSION).getWritableDatabase();
    }
    public static DataBaseAdapter getDbAdapter(Context context){
        if(instance==null)
            instance= new DataBaseAdapter(context);
        return instance;
    }

    public boolean Insert(Map<String,Object> map,boolean isDailyExercise,String[] toAvoidMapKeys){
        ContentValues contentValues = new ContentValues();
        int total_minutes=0;
        for(Map.Entry<String,Object> entry:map.entrySet()){
            if(! Utils.contains(toAvoidMapKeys,entry.getKey())) {
                String KEY = stripString(entry.getKey());
                Object obj = entry.getValue();
                if(obj instanceof Integer) {
                    int VALUE = (Integer) entry.getValue();
                    total_minutes+=VALUE;  //assuming the interval minutes is not considered
                    Log.d(TAG, "Insert: instance of Int: Value inserted :"+VALUE);
                    contentValues.put(KEY,VALUE );
                }
                else  if(obj instanceof  String) {
                    String VALUE = (String) entry.getValue();
                    Log.d(TAG, "Insert: instance of String: Value inserted :"+VALUE);
                    contentValues.put(KEY, VALUE);
                }
            }
        }
        //adding total minutes
        contentValues.put(Utils.TOTAL_MIN,total_minutes);
        Log.d(TAG, "Inserted Values");
        if(isDailyExercise) {
            return db.insert(DAILY_EXERCISE_TABLE, null, contentValues) > 0;
        }else{
            return db.insert(CASAUL_EXERCISE_TABLE, null, contentValues) > 0;
        }
    }

    public int getMaxDailyExerciseLevel(){
       String fetchDay = String.format("SELECT MAX(%s) FROM %s", Utils.DAY, DAILY_EXERCISE_TABLE);
       Cursor cursor = db.rawQuery(fetchDay,null);
       if(cursor!=null & cursor.getCount()>0){
           return cursor.getInt(cursor.getColumnIndex(Utils.DAY));
       }else
           return 0;

    }
    public void delete_tables(){
        Log.d(TAG, "delete_tables: ");
        String del_casual_table = String.format("DROP TABLE IF EXISTS %s", CASAUL_EXERCISE_TABLE);
        String del_exer_table = String.format("DROP TABLE IF EXISTS %s", DAILY_EXERCISE_TABLE);
        db.execSQL(del_casual_table);
        db.execSQL(del_exer_table);

    }
    public ArrayList<Exercise> getExercisesAtDay(int day){
        ArrayList<Exercise> allExercises = Exercises.getAllExercises();
        ArrayList<Exercise> selectedExercises = new ArrayList<>();
        String dayStr = String.valueOf(day);
        Cursor cursor = db.rawQuery(String.format("SELECT * FROM %s WHERE %s=%s LIMIT 1 ",DAILY_EXERCISE_TABLE,Utils.DAY,dayStr),null); // check where and limit semantics
        if(cursor!=null && cursor.getCount()>0){
            if(cursor.moveToNext()) {
                for (int i = 0; i < EXERCISES.size(); i++) {
                    String strippedKey = stripString(EXERCISES.get(i));
                    int min = cursor.getInt(cursor.getColumnIndex(strippedKey));
                    if (min > 0) {
                        Exercise exercise = allExercises.get(i);
                        exercise.setMinute(min);
                        selectedExercises.add(exercise);
                    }
                }
            }
        }
        //If the day is new, create today's exercises using an algorithm
        else{
            Log.d(TAG, "getExercisesAtDay: New Day Allocation");
            int min =((int) Math.ceil(day/allExercises.size()))+1;
            //assume allExercises.size() = 13
            day=day-1; // days are converted to index , at day 13, day=12
            int startExerciseIndex = day%(allExercises.size()); // at day 13, startExerciseIndex=12
            int endExerciseIndex = ((startExerciseIndex+5)%(allExercises.size())); //at day 13, endExerciseIndex= (17%13)= 4
            int count=0;
            while(count<5){
                Exercise exercise = allExercises.get((startExerciseIndex+count++)%13);
                exercise.setMinute(min);
                selectedExercises.add(exercise);
                Log.d(TAG, "getExercisesAtDay: "+exercise.getName());
            }

        }
        return selectedExercises;
    }
    public Map<Integer,Integer> getExerciseAtMonth(int month,int year){
        Map<Integer,Integer> exerciseMap = new HashMap<>();
        // return map ==> (day_of_month , exercised_minutes )

        String startDayofMonth = getMilliStartorEnd(month,year,true);
        String endDayofMonth = getMilliStartorEnd(month,year,false);

        Cursor casualCursor = db.rawQuery(String.format("select * from %s where %s BETWEEN '%s' AND '%s' ",
                CASAUL_EXERCISE_TABLE,Utils.DATE,startDayofMonth,endDayofMonth),null);
        Cursor dailyCursor = db.rawQuery(String.format("select * from %s where %s BETWEEN '%s' AND '%s' ",
                DAILY_EXERCISE_TABLE,Utils.DATE,startDayofMonth,endDayofMonth),null);

        if(casualCursor!=null){
            while(casualCursor.moveToNext()) {
                int day_of_month = Utils.getDay(null,casualCursor.getString(casualCursor.getColumnIndex(Utils.DATE)),"yyyy-MM-dd");
                int count = casualCursor.getInt(casualCursor.getColumnIndex(Utils.TOTAL_MIN));

                if(exerciseMap.containsKey(day_of_month)){
                    int casualExerciseCount = exerciseMap.get(day_of_month);
                    count +=casualExerciseCount;
                }
                exerciseMap.put(day_of_month,count);
                Log.d(TAG, "getExerciseAtMonth: KEY="+day_of_month+" VALUE:"+count);
            }
        }
        if(dailyCursor!=null){
            while(dailyCursor.moveToNext()) {
                int day_of_month = Utils.getDay(null,dailyCursor.getString(dailyCursor.getColumnIndex(Utils.DATE)),"yyyy-MM-dd");
                int count = dailyCursor.getInt(dailyCursor.getColumnIndex(Utils.TOTAL_MIN));
                if(exerciseMap.containsKey(day_of_month)){
                    int casualExerciseCount = exerciseMap.get(day_of_month);
                    count +=casualExerciseCount;
                }
                exerciseMap.put(day_of_month,count);
                Log.d(TAG, "getExerciseAtMonth: KEY="+day_of_month+" VALUE:"+count);
            }
            }
        /*
        select *
          from MyTable
          where mydate >= Datetime('2000-01-01 00:00:00')
          and mydate <= Datetime('2050-01-01 23:00:59')
          (or)
             column_date BETWEEN '2000-01-01 00:00:00' AND '2050-01-01 23:00:59'

          */

        return  exerciseMap;
    }

    private String getMilliStartorEnd(int month,int year,boolean returnFirstDay){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.YEAR,year);

        if(returnFirstDay){
            calendar.set(Calendar.DAY_OF_MONTH,1);
            calendar = getBeginningOfDay(calendar);

        }else{
            int lastDay =calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            calendar.set(Calendar.DAY_OF_MONTH,lastDay);
            calendar = getEndingofDay(calendar);
        }
        Date date =  calendar.getTime();
        return Utils.getDateString(date,true);  // returns the first day or last day of the passed month and year

    }
    private Calendar getBeginningOfDay(Calendar c){
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c;
    }
    private  Calendar getEndingofDay(Calendar c){
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c;
    }
    public int getHighestDay(){
        int day= 0 ;
        Log.d(TAG, "getHighestDay: Called");
        Cursor cursor = db.rawQuery(String.format("SELECT MAX(%s) FROM %s",Utils.DAY,DAILY_EXERCISE_TABLE),null);
        if(cursor!=null ){
            if(cursor.moveToNext()) {
                Log.d(TAG, "getHighestDay: Inside");
                day = cursor.getInt(0);
            }
        }
        Log.d(TAG, "getHighestDay: Day="+day);
        return day;
    }

    private String stripString(String string){
        return string.replaceAll("\\s","");
    }

    public class DataBaseHelper extends SQLiteOpenHelper{
        public DataBaseHelper(Context context, String DB_NAME, SQLiteDatabase.CursorFactory factory,int VERSION){
            super(context,DB_NAME,factory,VERSION);
            Log.d(TAG, "DataBaseHelper: Constructor()");
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(TAG, "onCreate: "+CASAUL_EXERCISE_TABLE+" created");
            StringBuilder casual_createTable = new StringBuilder("CREATE TABLE "+CASAUL_EXERCISE_TABLE+" ( "+Utils.ID+" INTEGER PRIMARY KEY," +
                    Utils.DATE+ " INTEGER,"+ Utils.TOTAL_MIN+" INTEGER,");
            StringBuilder daily_createTable = new StringBuilder("CREATE TABLE "+DAILY_EXERCISE_TABLE+" ( "+Utils.ID+" INTEGER PRIMARY KEY," +
                    Utils.DAY+" INTEGER,"+
                    Utils.DATE+ " DATE,"+ Utils.TOTAL_MIN+" INTEGER,");
            for(int i=0;i<EXERCISES.size()-1;i++) {
                casual_createTable.append(stripString(EXERCISES.get(i)) + " INTEGER, ");
                daily_createTable.append(stripString(EXERCISES.get(i)) + " INTEGER, ");
            }
            casual_createTable.append(stripString(EXERCISES.get(EXERCISES.size()-1))+ " INTEGER)");
            daily_createTable.append(stripString(EXERCISES.get(EXERCISES.size()-1))+ " INTEGER)");
            Log.d(TAG, "onCreate: Casual Table "+casual_createTable);
            Log.d(TAG, "onCreate: Daily Table"+daily_createTable);

            db.execSQL(new String(casual_createTable));
            db.execSQL(new String(daily_createTable));
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }



}
