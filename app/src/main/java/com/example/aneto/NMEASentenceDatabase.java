package com.example.aneto;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;

public class NMEASentenceDatabase {
    private NMEASentence database[];
    private int size;
    private Date timeStamp;

    private static final String LOG_TAG = "Aneto";

    public static NMEASentenceDatabase newDatabase(){
        NMEASentenceDatabase result = new NMEASentenceDatabase();
        result.size = 0;
        result.timeStamp = Calendar.getInstance().getTime();
        return result;
    }

    public void addNMEASentence(NMEASentence sentence){
        NMEASentence aux;
        boolean found = false;

        for(int i=0; i<size;i++){

            aux = database[i];
            if (aux.sameNmeaKey(sentence)){
                aux.updateNMEA(sentence);
                found = true;
            }
        }

        if (found || sentence.isEmpty()){

        }else {

            size = size + 1;
            NMEASentence[] aux2 = database;

            database = new NMEASentence[size];

            for(int i=0; i < size-1; i++){
                database[i] = aux2[i];
            }
            database[size-1] = sentence;
        }

    }

    public String toString(){
        String result = "timeStamp: " + timeStamp.toString() + "\n";
        result = result + "size: " + size + "\n";

        for(int i=0;i<size; i++){
            result = result + "key: " + i + " , sentence: " + database[i].toString() + "\n";
        }
        return result;
    }

    public String setIndicator(String nmeaK){
        String result = new String();

        for(int i = 0; i < size; i++){
            if (database[i].sameNmeaKeyString(nmeaK)){result = database[i].toView();}
        }

        return result;
    }

}
