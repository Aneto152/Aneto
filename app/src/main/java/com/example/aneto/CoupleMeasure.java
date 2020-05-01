package com.example.aneto;

import android.util.Log;
import android.widget.TextView;

public class CoupleMeasure {
    private static final String LOG_TAG = "Aneto";
    private Measure top;
    private Measure bottom;

    public Measure getTop(){
        return top;
    }

    public Measure getBottom(){
        return bottom;
    }

    public static CoupleMeasure newCouple(Measure mtop, Measure mbottom){
        CoupleMeasure result = new CoupleMeasure();
        result.top = mtop;
        result.bottom = mbottom;

        return result;
    }

    public String toString(){
        return "top:\n"+top.toString()+"\nbottom:\n"+bottom.toString()+"\n";
    }

    public void addViews(TextView mText11, TextView mText12, TextView mText13, TextView mText14,
                         TextView mText21, TextView mText22, TextView mText23, TextView mText24){

        top.addMeasureListeners(mText11,mText12,mText13,mText14);
        bottom.addMeasureListeners(mText21,mText22,mText23, mText24);
    }

    public void changeViews(CoupleMeasure mCouple){
        //Log.d(LOG_TAG, "change views in Couple:\nnewView\n"+toString() + "\noldView\n" + mCouple.toString());
        top.changeViews(mCouple.top);
        bottom.changeViews(mCouple.bottom);
        top.updateViews();
        bottom.updateViews();
    }

}
