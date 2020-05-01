package com.example.aneto;

import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

public class BottomMenu {
    private static final String LOG_TAG = "Aneto";
    private CoupleMeasure[] list;
    private int current;
    private int size;
    private String name;
    private int color;
    private TextView description;

    public static BottomMenu newBottomMenu(String mName, int mColor){
        BottomMenu result = new BottomMenu();
        result.current = 0;
        result.size = 0;
        result.name = mName;
        result.color = mColor;
        return result;
    }

    public void addToBottomMenu(Measure measureA, Measure measureB){

        CoupleMeasure[] newlist = new CoupleMeasure[size+1];

        for(int i= 0; i < size; i++){
            newlist[i] = list[i];
        }

        newlist[size] = CoupleMeasure.newCouple(measureA,measureB);

        list = newlist;

        size = size + 1;
    }

    public CoupleMeasure getCurrentCouple(){
        return list[current];
    }

    public void nextCouple(){
        CoupleMeasure oldCouple = list[current];
        current = (current + 1) % size;
        CoupleMeasure newCouple = list[current];
        newCouple.changeViews(oldCouple);
    }

    public String getName(){
        return name;
    }

    public void addToDatabase(MeasureDatabase measureDatabase){
        for(int i= 0; i < size ; i++){
            measureDatabase.addMeasure(list[i].getTop());
            measureDatabase.addMeasure(list[i].getBottom());
        }
    }

    public void addViews(TextView mText11, TextView mText12, TextView mText13, TextView mText14,
                         TextView mText21, TextView mText22, TextView mText23, TextView mText24
                        ,TextView mMenu){

        list[current].addViews(mText11,mText12,mText13,mText14,
                mText21,mText22,mText23, mText24);
        description = mMenu;
        updateView();
    }

    public void changeViews(BottomMenu mBottomMenu){
        //Log.d(LOG_TAG, "change views in BottomView:\nnewView\n"+toString() + "\noldView\n" + mBottomMenu.toString());
        list[current].changeViews(mBottomMenu.list[mBottomMenu.current]);

        description = mBottomMenu.description;
        mBottomMenu.description = null;
        updateView();
    }

    public String toString(){
        String result = "Name: "+ name +"\tsize: " + size + "\tcurrent: " + current+ "\n" ;

        for(int i=0; i< size; i++){
            result = result + list[i].toString();
        }

        return  result;
    }

    public void updateView(){
        description.setText(name);
        description.setBackgroundColor(color);
        list[current].getTop().updateViews();
        list[current].getBottom().updateViews();
    }

}
