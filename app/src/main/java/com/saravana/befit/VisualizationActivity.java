package com.saravana.befit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.saravana.befit.Adapters.DataBaseAdapter;
import com.saravana.befit.Utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class VisualizationActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "VisualizationActivity";
    private Toolbar toolbar;
    private TextView dateText;
    private ImageButton slideBackBtn,slideFrontBtn;
    private Date chartDate;
    private CombinedChart combinedChart;
    int XLabelCount,YLabelCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualization);
        chartDate = new Date();
        initWidgets();

        slideBackBtn.setOnClickListener(this);
        slideFrontBtn.setOnClickListener(this);


    }

    private void initWidgets() {
        Log.d(TAG, "initWidgets: Initialization started");
        toolbar = findViewById(R.id.visu_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Your Records");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dateText = findViewById(R.id.visu_date);
        slideBackBtn = findViewById(R.id.visu_back);
        slideFrontBtn = findViewById(R.id.visu_front);
        combinedChart = findViewById(R.id.visu_combichart);
        initBarchart();
        Log.d(TAG, "initWidgets: Initialization Ended");

    }
    private void initBarchart(){
        Log.d(TAG, "initBarchart: started");

        combinedChart.setDrawBarShadow(false);
        combinedChart.setDescription(null);
        combinedChart.setDrawValueAboveBar(true);
        combinedChart.setMaxVisibleValueCount(50);
        combinedChart.setPinchZoom(false);
        combinedChart.setDrawGridBackground(true);
        combinedChart.setVisibleXRangeMaximum(31);

        combinedChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR,  CombinedChart.DrawOrder.LINE
        });

        YAxis yRightAxis = combinedChart.getAxisRight();
        yRightAxis.setEnabled(false);
        setLegendAlignment();
        setChartData();
        Log.d(TAG, "initBarchart: Ended");

    }
    private void setChartData(){
        XLabelCount=0;
        YLabelCount=0;
        dateText.setText(Utils.getDateString(chartDate,false));

        Log.d(TAG, "setBarChartData: Started");
        int month = Utils.getMonth(chartDate);
        int year = Utils.getYear(chartDate);
        XLabelCount = Utils.getMaximumDayInMonth(month,year);

        Map<Integer,Integer> map = DataBaseAdapter.getDbAdapter(VisualizationActivity.this).getExerciseAtMonth(month,year);

        BarData barData= getBarData(map);

        LineData lineData= getLineData(map);

//        barDataSet.setColors(getResources().getColor(R.color.font_color,null));
//        barDataSet.setDrawValues(true); //control writing values over the bar
//        barDataSet.setValueFormatter(new MyBarFormatter()); //to use this, should use barDataset.setDrawValue(true)

        CombinedData combinedData = new CombinedData();
        combinedData.setData(barData);
        combinedData.setData(lineData);

        combinedChart.setData(combinedData);
        combinedChart.setBackgroundColor(Color.TRANSPARENT);
        combinedChart.setDrawGridBackground(false);
        setXAxis();
        setYAxis();
        disableTouchEvents();

        combinedChart.animateY(1000, Easing.EaseInOutBounce);
        combinedChart.invalidate();

    }

    private void disableTouchEvents() {
        combinedChart.setTouchEnabled(false);
        combinedChart.setScaleEnabled(true);
        combinedChart.setDragEnabled(true);
        combinedChart.setPinchZoom(true);
    }

    private void setXAxis(){
        int xLabelSplit = 5;
        XAxis xAxis = combinedChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(true);
        xAxis.setTextColor(Color.WHITE);
        Log.d(TAG, "setXAxis: XLABELCOUNT:"+XLabelCount);
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(XLabelCount);
        xAxis.setLabelCount((XLabelCount/2)+1,true);
        xAxis.setValueFormatter(new MyXFormatter()); //
        //xAxis.setValueFormatter(new IndexAxisValueFormatter(getXLabels(XLabelCount,xLabelSplit)));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setGranularity(1f);
    }
    private void setYAxis(){
        YAxis yAxis = combinedChart.getAxisLeft();
        yAxis.setTextColor(Color.WHITE);
        yAxis.setLabelCount(10,true);
        yAxis.setAxisMaximum(YLabelCount);
        yAxis.setAxisMinimum(0f);
    }

    private BarData getBarData(Map<Integer,Integer> map){
        int maxExerciseMinutes=0;
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        for(Map.Entry<Integer,Integer> entry : map.entrySet()){
                int day = entry.getKey();
                int count =  entry.getValue();
                Log.d(TAG, "setBarChartData: KEY="+day+" COUNT="+map.get(day));
                barEntries.add(new BarEntry(day,count));
                if(count>maxExerciseMinutes)
                    maxExerciseMinutes=count;
            }

        setYLabelCount(maxExerciseMinutes);

        BarDataSet barDataSet = new BarDataSet(barEntries, "Bar");
        barDataSet.setColor(getResources().getColor(R.color.bar_color,null));
        barDataSet.setDrawValues(false); //control writing values over the bar
        //barDataSet.setValueFormatter(new MyBarFormatter()); //to use this, should use barDataset.setDrawValue(true)

        BarData barData = new BarData(barDataSet);

        return barData;

    }
    private LineData getLineData(Map<Integer,Integer> map){
        ArrayList<Entry> lineEntries = new ArrayList<>();
        for(Map.Entry<Integer,Integer> entry : map.entrySet()){
            int day = entry.getKey();
            int count =  entry.getValue();
            Log.d(TAG, "setBarChartData: KEY="+day+" COUNT="+map.get(day));
            lineEntries.add(new Entry(day,count));
        }
        LineDataSet lineDatset = new LineDataSet(lineEntries, "Line");
        //set.setColor(Color.rgb(240, 238, 70));
        lineDatset.setColor(getResources().getColor(R.color.font_color,null)); //line color
        lineDatset.setLineWidth(3f);

        lineDatset.setCircleColor(getResources().getColor(R.color.font_color,null));
        lineDatset.setCircleRadius(3.5f);
        lineDatset.setDrawCircleHole(false);
        //lineDatset.setCircleHoleRadius(2f);
        //lineDatset.setFillColor(Color.rgb(240, 238, 70));

        //lineDatset.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDatset.setDrawValues(true);
        lineDatset.setValueTextColor(Color.WHITE);
        lineDatset.setValueFormatter(new MyBarFormatter());
        //lineDatset.setValueTextSize(10f);

        lineDatset.setAxisDependency(YAxis.AxisDependency.LEFT);

        LineData lineData = new LineData(lineDatset);
        return lineData;
    }

    private void setLegendAlignment(){
        Legend l = combinedChart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setEnabled(false);

    }


    private void setYLabelCount(int maxExerciseMinutes) {
        //Todo : implement this method
        float yMax = maxExerciseMinutes/50;
        if(yMax<1){
            yMax = 50f;
        }else{
            yMax = (float) Math.ceil(yMax) * 10;  // To adjust the ylabel count
        }
        YLabelCount = (int) yMax;
    }


    private void slideBack(){
        chartDate = Utils.addToDate(chartDate, Calendar.MONTH, -1);
        //update the chart

        setChartData();
    }
    private void slideFront(){
        chartDate = Utils.addToDate(chartDate,Calendar.MONTH,1);
        setChartData();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.visu_back:
                slideBack();
                break;
            case R.id.visu_front:
                slideFront();
                break;
        }
    }

    public class MyXFormatter extends IndexAxisValueFormatter {
        @Override
        public String getFormattedValue(float value) {
             return String.valueOf((int)value);
            //return  String.valueOf(((int)Math.ceil(value)));

        }

    }

    public class MyBarFormatter extends IndexAxisValueFormatter{

        @Override
        public String getFormattedValue(float value) {
            Log.d(TAG, "getFormattedValue: "+value);
            if(value>0)
              return String.valueOf((int)value);

            return "";
        }
    }
}
