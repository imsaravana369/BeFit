package com.saravana.befit.Fragment;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.saravana.befit.MainActivity;
import com.saravana.befit.R;
import com.saravana.befit.Utils.Exercises;
import com.saravana.befit.Utils.Utils;

import java.util.ArrayList;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends Fragment {
    private static final String TAG = "RecordFragment";
    private PieChart pieChart;
    private Button exitBtn;
    private Map<String,Object> map;
    private View view;
    private ArrayList<PieEntry> pieEntries;
    private PieDataSet pieDataSet;
    private PieData pieData;

    public RecordFragment() {
        // Required empty public constructor
    }
    public void setExerciseMap(Map<String,Object> map){
        Log.d("RecordFragment", "showRecordFragment: map setted ");
        this.map=map;

        Log.d("RecordFragment", "showRecordFragment: is map null:"+ (this.map==null));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView: Record Fragment");
        view = inflater.inflate(R.layout.fragment_record, container, false);
        pieChart = view.findViewById(R.id.record_fragment_piechart);
        exitBtn = view.findViewById(R.id.record_fragment_exitbtn);

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFragments();
                goToHome();
            }
        });
        //pieChart.setUsePercentValues(true);
        Log.d(TAG, "onCreateView: ispiechart null:"+(pieChart==null));
        Log.d(TAG, "onCreateView: HERE1");

        pieChart.setDragDecelerationFrictionCoef(0.5f);  // higher the value, higher it will rotate if dragged

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(30f);
        pieChart.getLegend().setWordWrapEnabled(true);
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.animateY(1000, Easing.EaseInOutCubic);
        Log.d(TAG, "onCreateView: HERE2");


        pieEntries = new ArrayList<>();
        if(map!=null) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                Log.d(TAG, "onCreateView: " + entry.getKey() + "==>" + entry.getValue());
                if (Exercises.getAllExercisesName().contains(stripstring(entry.getKey()))) {
                    //Exercise
                    int val = (Integer) entry.getValue();
                    PieEntry pieEntry = new PieEntry(val, entry.getKey());
                    pieEntries.add(pieEntry);
                } else if (entry.getKey().equals(Utils.INTERVAL_MIN_KEYS)) {
                    // Interval
                    int val = (Integer) entry.getValue();
                    float min = val / 60;
                    if(min>1) {
                        PieEntry pieEntry = new PieEntry(min, entry.getKey());
                        pieEntries.add(pieEntry);
                    }
                }
            }
        }

        Log.d(TAG, "onCreateView: HERE3");
        pieDataSet = new PieDataSet(pieEntries,"Exercise");
        pieDataSet.setSelectionShift(30f);  // Distnce an etry is lifted on Clicking it
        pieDataSet.setSliceSpace(3f);
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        Log.d(TAG, "onCreateView: HERE4");

        pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.BLACK);
        Log.d(TAG, "onCreateView: HERE5");
        pieChart.setData(pieData);
        Log.d(TAG, "onCreateView: HERE6");

        return view;
    }

    private void goToHome() {
        Intent homeActivity = new Intent(getActivity(), MainActivity.class);
        startActivity(homeActivity);
        if(getActivity()!=null  )
            getActivity().finish();
    }
    public void clearFragments() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    private String stripstring(String string){
        return string.replaceAll("\\s","");
    }

    @Override
    public void onStop() {
        clearFragments();
        super.onStop();
    }
}
