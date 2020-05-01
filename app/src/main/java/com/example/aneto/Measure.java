package com.example.aneto;

import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;

public class Measure {
    private String type;
    private double value;
    private String source;
    private Date timeStamp;
    private double interval;
    private double mean;

    private double[] actuals;
    private Date timeStampMean;

    TextView caption;
    TextView measurementScreen;
    TextView unitView;
    TextView trendView;

    private static String LOG_TAG = "Aneto";
    private static double MEAN_AVERAGE = 30000;
    private static double VALUE = 0.0;
    private static String UNKNOWN = "unknown";
    private static double ACTUALS_INTERVAL = 30000;

    public String description(){
        switch(type)
        {
            case "apparentWindDirection":
                return "apparent wind direction";
            case "apparentWindSpeed":
                return "apparent wind speed";
            case "currentCourseMagnetic":
                return "current direction (magnetic north)";
            case "currentCourseTrue":
                return "current direction (true north)";
            case "currentCourseRelative":
                return "current relative direction";
            case "currentSpeed":
                return "current speed";
            case "windDirection":
                return "true wind direction (true north)";
            case "windDirectionMagnetic":
                return "true wind direction (magnetic north)";
            case "windSpeed":
                return "true wind speed";
            case "waterDepth":
                return "water depth";
            case "courseOverGround":
                return "course over the ground (true north)";
            case "courseOverGroundMagnetic":
                return "course over the ground (magnetic north)";
            case "courseOverWater":
                return "course over the water (true north)";
            case "courseOverWaterMagnetic":
                return "course over the water (magnetic north)";
            case "heading":
                return "heading (true north)";
            case "headingMagnetic":
                return "heading (magnetic north)";
            case "heartRate":
                return "heart rate";
            case "speedOverWater":
                return "speed over the water";
            case "speedOverGround":
                return "speed over the ground";
            case "speedParallelToWind":
                return "speed parallel to the wind (VMG)";
            default:
                return "no description";
        }
    }

    public String toCaption(){

        /*
        switch(type)
        {
            case "apparentWindDirection":
                return "app.\nwind";
            case "apparentWindSpeed":
                return "app.\nwind";
            case "currentCourseMagnetic":
                return "curr.\nhead.";
            case "currentCourseTrue":
                return "curr.\nhead.";
            case "currentCourseRelative":
                return "app.\ncurr.";
            case "currentSpeed":
                return "curr.\nspeed";
            case "windDirection":
                return "true\nwind";
            case "windDirectionMagnetic":
                return "true\nwind";
            case "windSpeed":
                return "true\nwind";
            case "waterDepth":
                return "water\ndepth";
            case "courseOverGround":
                return "course\nground";
            case "courseOverGroundMagnetic":
                return "course\nground";
            case "courseOverWater":
                return "course\nwater";
            case "courseOverWaterMagnetic":
                return "course\nwater";
            case "heading":
                return "boat\nhead.";
            case "headingMagnetic":
                return "boat\nhead.";
            case "speedOverWater":
                return "speed\nwater";
            case "speedOverGround":
                return "speed\nground";
            case "speedParallelToWind":
                return "speed\nwind";
            case "heartRate":
                return "heart\nrate";
            default:
                return "no\ndescr.";
        }
         */
        return description();
    }

    public String toAppendix(){
        switch(type)
        {
            case "apparentWindDirection":
                return "dg";
            case "apparentWindSpeed":
                return "kn";
            case "currentCourseMagnetic":
                return "dg";
            case "currentCourseTrue":
                return "dg";
            case "currentCourseRelative":
                return "dg";
            case "currentSpeed":
                return "kn";
            case "windDirection":
                return "dg";
            case "windDirectionMagnetic":
                return "dg";
            case "windSpeed":
                return "kn";
            case "waterDepth":
                return "m";
            case "courseOverGround":
                return "dg";
            case "courseOverGroundMagnetic":
                return "dg";
            case "courseOverWater":
                return "dg";
            case "courseOverWaterMagnetic":
                return "dg";
            case "heading":
                return "dg";
            case "headingMagnetic":
                return "dg";
            case "speedOverWater":
                return "kn";
            case "speedOverGround":
                return "kn";
            case "speedParallelToWind":
                return "kn";
            case "heartRate":
                return "bpm";
            default:
                return "?";
        }
    }

    public static Measure newMeasure(){
        Measure result = new Measure();
        result.timeStamp = Calendar.getInstance().getTime();
        result.type = UNKNOWN;
        result.source =UNKNOWN;
        result.value = VALUE;
        result.interval = 0;
        return result;
    }

    public static Measure newMeasure(String type, String source, Double value){
        Measure result = new Measure();
        result.timeStamp = Calendar.getInstance().getTime();
        result.timeStampMean = result.timeStamp;
        result.type = type;
        result.source = source;
        result.value = value;
        result.mean = value;
        result.interval = 0;
        result.actuals = new double[1];
        result.actuals[0] = value;
        return result;
    }

    public static Measure emptyMeasure(String type){
        return newMeasure(type, "--",VALUE);
    }

    public static String toStringHeader(){
        return "Source\ttype\tvalue\tinterval\ttimestamp\tdescription\tmean\tactuals";
    }

    public String toString() {
        String aux = "";
        for (int i=0; i < actuals.length; i++){
            aux = aux + String.valueOf(actuals[i]) + "\t";
        }
        return source + "\t" + type + "\t" + String.valueOf(value) + "\t" + String.valueOf(interval) +"\t"+ timeStamp.toString()+"\t" + description() + "\t" + String.valueOf(mean) + "\t" + aux;
    }

    public void updateMeasure(String newSource, Double newValue){
        Date newTimeStamp = Calendar.getInstance().getTime();

        //update average
        interval = newTimeStamp.getTime() - timeStamp.getTime();
        double weight = Double.min(interval/MEAN_AVERAGE, 1.00);
        mean = value*weight + mean * (1.00 - weight);

        //update actuals
        if ((newTimeStamp.getTime()- timeStampMean.getTime()) > ACTUALS_INTERVAL){
            double[] aux = new double[actuals.length+1];
            for(int i=0;i<actuals.length;i++){
                aux[i] = actuals[i];
                Log.d(LOG_TAG, "new actual:\t"+aux[i]+"\n");
            }
            aux[actuals.length] = mean;
            Log.d(LOG_TAG, "new actual:\t"+mean+"\n");
            actuals = aux;
            Log.d(LOG_TAG, "vibration");
            timeStampMean = newTimeStamp;
        }

        timeStamp = newTimeStamp;
        source = newSource;
        value = newValue;

        updateViews();
    }

    public void updateMeasure(Measure newMeasure){
                updateMeasure(newMeasure.source,newMeasure.value);
    }

    public void addMeasureListeners(TextView myCaption, TextView myMeasurementScreen, TextView myUnitView, TextView myTrendView){
        caption = myCaption;
        measurementScreen = myMeasurementScreen;
        unitView = myUnitView;
        trendView = myTrendView;
        updateViews();
    }

    public void updateViews(){
        if (caption != null){
            caption.setText(toCaption());
            caption.setAutoSizeTextTypeUniformWithConfiguration(1,100,1,1);
        }
        if (measurementScreen != null){
            if (value < 1){measurementScreen.setText(String.format("%.2f", value));};
            if (value >= 1 && value < 10){measurementScreen.setText(String.format("%.1f", value));};
            if (value > 10){measurementScreen.setText(String.format("%d", (int) value));};
            measurementScreen.setAutoSizeTextTypeUniformWithConfiguration(1,100,1,1);
        }
        if (unitView != null){
            unitView.setText(toAppendix());
            unitView.setAutoSizeTextTypeUniformWithConfiguration(1,100,1,1);
        }
        if (trendView != null){
            if (mean < value){
                trendView.setText("∧");
                trendView.setTextColor(Color.GREEN);
            }
            if (mean > value){
                trendView.setText("∨");
                trendView.setTextColor(Color.RED);
            }
            if (mean == value){
                trendView.setText("-");
                trendView.setTextColor(Color.WHITE);
            }
        }

    }

    public boolean sameType(Measure myMeasure){
        return (type.equals(myMeasure.type));
    }

    public boolean isEmpty(){
        return (type.equals(UNKNOWN));
    }

    public void changeViews(Measure oldMeasure){
        //Log.d(LOG_TAG, "change views in Measure:\nnewView\n"+toString() + "\noldView\n" + oldMeasure.toString());

        caption = oldMeasure.caption;
        measurementScreen = oldMeasure.measurementScreen;
        unitView = oldMeasure.unitView;
        trendView = oldMeasure.trendView;

        oldMeasure.caption = null;
        oldMeasure.measurementScreen = null;
        oldMeasure.unitView = null;
        oldMeasure.trendView = null;
    }

}
