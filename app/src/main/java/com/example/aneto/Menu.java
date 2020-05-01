package com.example.aneto;

import android.widget.TextView;

import org.w3c.dom.Text;

public class Menu {
    
    private BottomMenu[] list;
    private int current;
    private int size;
    
    public static Menu newMenu(){
        Menu monMenu = new Menu();
        monMenu.size = 0;
        monMenu.current = 0;
        return monMenu;
    }

    public void addMenu(BottomMenu mBottom){
        BottomMenu[] newlist = new BottomMenu[size+1];

        for(int i= 0; i < size; i++){
            newlist[i] = list[i];
        }

        newlist[size] = mBottom;

        list = newlist;

        size = size + 1;
        
    }


    public BottomMenu getCurrent(){
        return list[current];
    }

    public void nextMenu(){
        BottomMenu oldBottomMenu = list[current];
        current = (current + 1) % size;
        BottomMenu newBottomMenu = list[current];

        newBottomMenu.changeViews(oldBottomMenu);
    }

    public void addToDatabase(MeasureDatabase measureDatabase){
        for (int i = 0; i < size; i++){
            list[i].addToDatabase(measureDatabase);
        }
    }

    public void addViews(TextView mText11, TextView mText12, TextView mText13, TextView mText14,
                         TextView mText21, TextView mText22, TextView mText23, TextView mText24, TextView mMenu){
        list[current].addViews(mText11,mText12,mText13,mText14,
                mText21,mText22,mText23, mText24, mMenu);
    }

    public String toString(){
        String result = "Size: " + size + "\tCurrent: " + current+ "\n" ;

        for(int i=0; i< size; i++){
            result = result + list[i].toString();
        }

        return  result;
    }

    public void updateView(){
        list[current].updateView();
    }

}
