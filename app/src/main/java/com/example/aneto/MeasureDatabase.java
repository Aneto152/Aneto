package com.example.aneto;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MeasureDatabase {
    private ArrayList<MyMenuItem> menuItems;
    private Measure database[];
    private int size;
    private Date timeStamp;

    private static final String LOG_TAG = "Database";

    public static MeasureDatabase newDatabase() {
        MeasureDatabase result = new MeasureDatabase();
        result.size = 0;
        result.timeStamp = Calendar.getInstance().getTime();
        return result;
    }

    public Measure emptyMeasure(String type){
        Measure result = Measure.emptyMeasure(type);
        this.addMeasure(result);
        return result;
    }

    public void addMeasure(Measure measure) {

        Measure aux;
        boolean found = false;

        for (int i = 0; i < size; i++) {
            aux = database[i];
            if (aux.sameType(measure)) {
                aux.updateMeasure(measure);
                found = true;
            }
        }

        if (found || measure.isEmpty()) {
        } else {
            size = size + 1;
            Measure[] aux2 = database;

            database = new Measure[size];

            for (int i = 0; i < size - 1; i++) {
                database[i] = aux2[i];
            }
            database[size - 1] = measure;
        }

    }

    public String toString() {
        String result = "timeStamp: " + timeStamp.toString() + "\n";
        result = result + "size: " + size + "\n";

        result = result + "key\t" + Measure.toStringHeader() + "\n";
        for (int i = 0; i < size; i++) {
            result = result + i + "\t" + database[i].toString() + "\n";
        }
        return result;
    }

    public void addNMEASentence(String message) {
        addNMEASentence(NMEASentence.convertMessage(message));
    }

    public void addNMEASentence(NMEASentence message) {

        if (message.isEmpty()) {
            Log.d(LOG_TAG, "empty message\t" + message);
        } else {

            switch (message.getNmeaKey()) {
                case "AAM":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "ABK":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "ABM":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "ACA":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "ACK":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "ACS":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "AIR":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "ALM":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "ALR":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "APB":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "BBM":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "BEC":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "BOD":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "BWC":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "BWR":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "BWW":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "CUR":
                    if (message.getCount() > 9) {
                        if (message.getArguments()[5].equals("T")) {
                            if (message.getArguments()[9].equals("T")) {
                                try {
                                    Measure courseOverGround = Measure.newMeasure("currentCourseTrue", message.getNmeaSource(), Double.parseDouble(message.getArguments()[4]));
                                    addMeasure(courseOverGround);
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (message.getArguments()[9].equals("M")) {
                                try {
                                    Measure courseOverGround = Measure.newMeasure("currentCourseMagnetic", message.getNmeaSource(), Double.parseDouble(message.getArguments()[4]));
                                    addMeasure(courseOverGround);
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        if (message.getArguments()[5].equals("R")) {
                            try {
                                Measure courseOverGround = Measure.newMeasure("currentCourseRelative", message.getNmeaSource(), Double.parseDouble(message.getArguments()[4]));
                                addMeasure(courseOverGround);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            Measure speedOverGround = Measure.newMeasure("currentSpeed", message.getNmeaSource(), Double.parseDouble(message.getArguments()[6]));
                            addMeasure(speedOverGround);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Log.d(LOG_TAG, "error\t" + message.getNmeaKey());
                    }
                    break;
                case "DBT":
                    if (message.getCount() > 3) {
                        try {
                            Measure depth = Measure.newMeasure("waterDepth", message.getNmeaSource(), Double.parseDouble(message.getArguments()[3]));
                            addMeasure(depth);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.d(LOG_TAG, "error\t" + message.getNmeaKey());
                    }
                    break;
                case "DCN":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "DPT":
                    if (message.getCount() > 2) {
                        try {
                            Measure depth = Measure.newMeasure("waterDepth", message.getNmeaSource(), Double.parseDouble(message.getArguments()[0]) + Double.parseDouble(message.getArguments()[1]));
                            addMeasure(depth);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.d(LOG_TAG, "error\t" + message.getNmeaKey());
                    }
                    break;
                case "DSC":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "DSE":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "DSI":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "DSR":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "DTM":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "FSI":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "GBS":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "GGA":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "GLC":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "GLL":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "GMP":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "GNS":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "GRS":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "GSA":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "GST":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "GSV":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "HDG":
                    if (message.getCount() > 2) {
                        if (message.getArguments()[2].equals("E")) {
                            try {
                                Measure heading = Measure.newMeasure("headingMagnetic", message.getNmeaSource(), Double.parseDouble(message.getArguments()[0]) + Double.parseDouble(message.getArguments()[1]));
                                addMeasure(heading);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                        if (message.getArguments()[2].equals("W")) {
                            try {
                                Measure heading = Measure.newMeasure("headingMagnetic", message.getNmeaSource(), Double.parseDouble(message.getArguments()[0]) - Double.parseDouble(message.getArguments()[1]));
                                addMeasure(heading);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }

                    } else {
                        Log.d(LOG_TAG, "error\t" + message.getNmeaKey());
                    }
                    break;
                case "HDT":
                    if (message.getCount() > 2) {
                        try {
                            Measure trueHeading = Measure.newMeasure("heading", message.getNmeaSource(), Double.parseDouble(message.getArguments()[1]));
                            addMeasure(trueHeading);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.d(LOG_TAG, "error\t" + message.getNmeaKey());
                    }
                    break;
                case "HMR":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "HMS":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "HSC":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "HTC":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "HTD":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "LCD":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "LRF":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "LRI":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "LR1":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "LR2":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "LR3":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "MLA":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "MSK":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "MSS":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "MTW":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "MWD":
                    if (message.getCount() > 5) {
                        try {
                            Measure trueWind = Measure.newMeasure("windDirection", message.getNmeaSource(), Double.parseDouble(message.getArguments()[0]));
                            addMeasure(trueWind);
                            Measure trueWindMagnetic = Measure.newMeasure("windDirectionMagnetic", message.getNmeaSource(), Double.parseDouble(message.getArguments()[2]));
                            addMeasure(trueWindMagnetic);
                            Measure windSpeed = Measure.newMeasure("windSpeed", message.getNmeaSource(), Double.parseDouble(message.getArguments()[4]));
                            addMeasure(windSpeed);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.d(LOG_TAG, "error\t" + message.getNmeaKey());
                    }
                    break;
                case "MWV":
                    if (message.getCount() > 2) {
                        try {
                            Measure apparentWind = Measure.newMeasure("apparentWindDirection", message.getNmeaSource(), Double.parseDouble(message.getArguments()[0]));
                            addMeasure(apparentWind);
                            Measure apparentSpeed = Measure.newMeasure("apparentWindSpeed", message.getNmeaSource(), Double.parseDouble(message.getArguments()[2]));
                            addMeasure(apparentSpeed);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.d(LOG_TAG, "error\t" + message.getNmeaKey());
                    }
                    break;
                case "OSD":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "RMA":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "RMB":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "RMC":
                    if (message.getCount() >= 8) {
                        try {
                            Measure speedOverGround = Measure.newMeasure("speedOverGround", message.getNmeaSource(), Double.parseDouble(message.getArguments()[6]));
                            addMeasure(speedOverGround);

                            Measure courseOverGround = Measure.newMeasure("courseOverGround", message.getNmeaSource(), Double.parseDouble(message.getArguments()[7]));
                            addMeasure(courseOverGround);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Log.d(LOG_TAG, "error\t" + message.getNmeaKey());
                    }
                    break;
                case "ROT":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "RPM":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "RSA":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "RSD":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "RTE":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "SFI":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "SSD":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "STN":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "TLB":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "TLL":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "TTM":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "TUT":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "TXT":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "VBW":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "VDM":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "VDO":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "VDR":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "VHW":
                    if (message.getCount() >= 4) {
                        try {
                            Measure courseOverWater = Measure.newMeasure("courseOverWater", message.getNmeaSource(), Double.parseDouble(message.getArguments()[0]));
                            addMeasure(courseOverWater);

                            Measure courseOverWaterMagnetic = Measure.newMeasure("courseOverWaterMagnetic", message.getNmeaSource(), Double.parseDouble(message.getArguments()[2]));
                            addMeasure(courseOverWaterMagnetic);

                            Measure speedOverWater = Measure.newMeasure("speedOverWater", message.getNmeaSource(), Double.parseDouble(message.getArguments()[4]));
                            addMeasure(speedOverWater);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Log.d(LOG_TAG, "error\t" + message.getNmeaKey());
                    }
                    break;
                case "VLW":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "VPW":
                    if (message.getCount() >= 0) {
                        try {
                            Measure speedParallelWind = Measure.newMeasure("speedParallelToWind", message.getNmeaSource(), Double.parseDouble(message.getArguments()[0]));
                            addMeasure(speedParallelWind);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Log.d(LOG_TAG, "error\t" + message.getNmeaKey());
                    }
                    break;
                case "VSD":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "VTG":
                    if (message.getCount() >= 5) {
                        try {
                            Measure speedOverGround = Measure.newMeasure("speedOverGround", message.getNmeaSource(), Double.parseDouble(message.getArguments()[4]));
                            addMeasure(speedOverGround);

                            Measure courseOverGroundTrue = Measure.newMeasure("courseOverGround", message.getNmeaSource(), Double.parseDouble(message.getArguments()[0]));
                            addMeasure(courseOverGroundTrue);

                            Measure courseOverGroundMagnetic = Measure.newMeasure("courseOverGroundMagnetic", message.getNmeaSource(), Double.parseDouble(message.getArguments()[2]));
                            addMeasure(courseOverGroundMagnetic);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Log.d(LOG_TAG, "error\t" + message.getNmeaKey());
                    }
                    break;
                case "WCV":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "WNC":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "WPL":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "XDR":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "XTE":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "XTR":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "ZDA":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "ZDL":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "ZFO":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                case "ZTG":
                    Log.d(LOG_TAG, "not yet implemented\t" + message.getNmeaKey());
                    break;
                default:
                    Log.d(LOG_TAG, "no NMEA message match\t" + message);
            }
        }
    }
}
