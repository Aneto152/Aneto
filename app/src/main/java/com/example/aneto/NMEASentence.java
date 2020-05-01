package com.example.aneto;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;

public class NMEASentence {
    private String nmeaKey;
    private String nmeaSource;
    private int count;
    private String arguments[];
    private Date timeStamp;

    private static final String LOG_TAG = "Aneto";

    public static NMEASentence newNMEASentence(){

        NMEASentence result = new NMEASentence();
        result.timeStamp = Calendar.getInstance().getTime();
        result.count = 0;
        result.nmeaKey ="";
        result.nmeaSource="";

        return result;
    }

    public void updateNMEA(NMEASentence sentence){
        nmeaKey = sentence.nmeaKey;
        count = sentence.count;
        arguments = sentence.arguments;
        timeStamp = sentence.timeStamp;
    }

    public static NMEASentence convertMessage(String message){

        NMEASentence result = newNMEASentence();

        int i = message.indexOf(",");

        if (i != 6 || message.charAt(0) != '$'){
            Log.d(LOG_TAG, "message is not NMEA0183\t" + message);
        }else
        {
            result.nmeaSource = message.substring(1,3);
            result.nmeaKey = message.substring(3,6);

            result.count = 1;

            String aux = message.substring(i+1);
            i = aux.indexOf(",");

            while (i >= 0 ){
                result.count = result.count + 1;

                i = aux.indexOf(",");
                aux = aux.substring(i+1);
            }


            result.arguments = new String[result.count];

            i = message.indexOf(",");
            aux = message.substring(i+1);


            for(int j=0;j<result.count;j++){
                i = aux.indexOf(",");

                if (i != -1){
                    result.arguments[j] = aux.substring(0,i);
                                    }
                else
                {
                    result.arguments[j]=aux;
                }

                aux = aux.substring(i+1);
            }
        }
        return result;
    }

    public String toString(){
        return ("TimeStamp: " + timeStamp + " , nmeaKey: " + nmeaKey + " , nb arguments: " + count);
    }

    public boolean sameNmeaKey(NMEASentence sentence){
        return (nmeaKey.equals(sentence.nmeaKey));
    }

    public boolean sameNmeaKeyString(String nmeak){
        return (nmeaKey.equals(nmeak));
    }

    public boolean isEmpty(){
        return nmeaKey.isEmpty();
    }

    public String getNmeaKey(){
        return nmeaKey;
    }

    public int getCount(){
        return count;
    }

    public String[] getArguments(){
        return arguments;
    }


    public String getNmeaSource(){
        return nmeaSource;
    }

    public String toView(){
        if (nmeaKey.equals("VMG")) {return arguments[0];}
        return arguments[0];
    }

}
