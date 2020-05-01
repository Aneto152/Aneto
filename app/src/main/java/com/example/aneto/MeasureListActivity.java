package com.example.aneto;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MeasureListActivity extends AppCompatActivity {

    private static final String TAG = "MeasureListActivity";
    private LinearLayout myContent;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.measure_chart_main);
        myContent = findViewById(R.id.content);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        final int position =  intent.getIntExtra(MainActivity.EXTRA_MESSAGE,-1);



        // Capture the layout's TextView and set the string as its text
        //TextView textView = findViewById(R.id.text);
        //textView.setText(message);
        load(position);

    }


    public void load(int position){
        ArrayList<MyMenuItem> aux = MainActivity.getMenuItems();
        Log.d(TAG, "timestamp: " + aux.get(position));

        LayoutInflater inflater = getLayoutInflater();


        ArrayList<Measure> myMeasureToDisplay = aux.get(position).getMenuMeasures();

        TextView title;
        ImageView icon;
        TextView caption;
        TextView value;
        TextView unit;
        TextView trend;
        View rootView;

        rootView = inflater.inflate(R.layout.submenu_title, myContent, false);
        title = rootView.findViewById(R.id.title);
        title.setText(aux.get(position).getText());
        icon = rootView.findViewById(R.id.menu_icon);
        icon.setImageResource(aux.get(position).getImage());
        myContent.addView(rootView);

        for(int i=0; i<myMeasureToDisplay.size(); i++){
            rootView = inflater.inflate(R.layout.measure_layout, myContent, false);
            caption = rootView.findViewById(R.id.caption);
            value = rootView.findViewById(R.id.value);
            unit = rootView.findViewById(R.id.unit);
            trend = rootView.findViewById(R.id.trend);
            myMeasureToDisplay.get(i).addMeasureListeners(caption,value,unit,trend);
            Log.d(TAG, "measure: " + myMeasureToDisplay.get(i).toString());
            if (i % 2 == 0){
                rootView.setBackgroundColor(Color.DKGRAY);
            }
            myContent.addView(rootView);
        }

        rootView = inflater.inflate(R.layout.submenu_footer, myContent, false);
        title = rootView.findViewById(R.id.title);
        title.setText("Aneto 152");
        icon = rootView.findViewById(R.id.menu_icon);
        icon.setImageResource(R.drawable.muscadet_icon);
        myContent.addView(rootView);

    }


}