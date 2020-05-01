package com.example.aneto;


import android.content.Context;
import android.content.Intent;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.os.VibrationEffect;
import android.os.Vibrator;

import android.support.wearable.activity.WearableActivity;

import android.util.Log;

import android.view.View;
import android.view.View.OnLongClickListener;


import java.lang.ref.WeakReference;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.Calendar;
import java.util.Date;


import android.graphics.Color;


import androidx.annotation.RequiresApi;

import androidx.wear.ambient.AmbientModeSupport;

import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;


public class MainActivity<connectivityManager> extends WearableActivity implements AmbientModeSupport.AmbientCallbackProvider, SensorEventListener {

    public static final String EXTRA_MESSAGE = "com.example.aneto.MESSAGE";
    final static int taille = 1024;
    final static byte buffer[] = new byte[taille];


    private static final String LOG_TAG = "Aneto";


    // Intent action for sending the user directly to the add Wi-Fi network activity.
    private static final String ACTION_ADD_NETWORK_SETTINGS =
            "com.google.android.clockwork.settings.connectivity.wifi.ADD_NETWORK_SETTINGS";

    // Message to notify the network request timout handler that too much time has passed.
    private static final int MESSAGE_CONNECTIVITY_TIMEOUT = 1;

    // How long the app should wait trying to connect to a sufficient high-bandwidth network before
    // asking the user to add a new Wi-Fi network.
    private static final long NETWORK_CONNECTIVITY_TIMEOUT_MS = TimeUnit.SECONDS.toMillis(10);

    // The minimum network bandwidth required by the app for high-bandwidth operations.
    private static final int MIN_NETWORK_BANDWIDTH_KBPS = 10000;
    // Handler for dealing with network connection timeouts.
    private final TimeOutHandler mTimeOutHandler = new TimeOutHandler(this);

    private ConnectivityManager.NetworkCallback mNetworkCallback;

    //support to ambient mode
    private AmbientModeSupport.AmbientController ambientController;

    private Handler timerHandler;
    private long startTime;

    //private ImageView splash;

    private Measure heartRateMeasure;

    private int messageCount;

    public static ArrayList<MyMenuItem> getMenuItems(){
        return menuItems;
    }


    Runnable timerRunnable = new Runnable() {

        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            String aux = String.format("%d:%02d", minutes, seconds);

            if (seconds == 20){

                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

                long[] vibration = new long[3];
                vibration[0] = 100;
                vibration[1] = 500;
                vibration[2] = 200;

                VibrationEffect vibratorEffect = VibrationEffect.createWaveform(vibration,VibrationEffect.DEFAULT_AMPLITUDE);

                //vibratorEffect = VibrationEffect.createOneShot(100,VibrationEffect.DEFAULT_AMPLITUDE);
                //vibrator.vibrate(vibratorEffect);
            }
            timerHandler.postDelayed(this, 1000);

        }
    };


    private static final MeasureDatabase database = MeasureDatabase.newDatabase();

    private static final ArrayList<MyMenuItem> menuItems = new ArrayList<>();


    private Menu monMenu;

    private ConnectivityManager connectivityManager;
    private View.OnClickListener updateBottomMenu = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public void onClick(View v) {
            monMenu.getCurrent().nextCouple();
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            VibrationEffect vibratorEffect = VibrationEffect.createOneShot(25,VibrationEffect.DEFAULT_AMPLITUDE);
            vibrator.vibrate(vibratorEffect);
        }
    };
    private View.OnClickListener updateMenu = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            monMenu.nextMenu();
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            VibrationEffect vibratorEffect = VibrationEffect.createOneShot(25,VibrationEffect.DEFAULT_AMPLITUDE);
            vibrator.vibrate(vibratorEffect);
        }
    };
    private OnLongClickListener updateMenu2 = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            startTime = System.currentTimeMillis() - 15000;
            timerHandler.postDelayed(timerRunnable, 0);
            //setContentView(R.layout.timer_main);

            //android:theme="@style/Theme.Wearable.Modal"

            return true;
        }
    };

    public void newMessageReceived(NMEASentence message){
        //mText1.setText(message);
        messageCount++;
        database.addNMEASentence(message);

    }

    @Override
    public void onStop() {
        releaseHighBandwidthNetwork();
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isNetworkHighBandwidth()) {
            //setUiState(UI_STATE_NETWORK_CONNECTED);
        } else {
            //setUiState(UI_STATE_REQUEST_NETWORK);
        }
    }

    private void unregisterNetworkCallback() {
        if (mNetworkCallback != null) {
            Log.d(LOG_TAG, "Unregistering network callback");
            connectivityManager.unregisterNetworkCallback(mNetworkCallback);
            mNetworkCallback = null;
        }
    }

    // Determine if there is a high-bandwidth network exists. Checks both the active
    // and bound networks. Returns false if no network is available (low or high-bandwidth).
    private boolean isNetworkHighBandwidth() {
        Network network = connectivityManager.getBoundNetworkForProcess();
        network = network == null ? connectivityManager.getActiveNetwork() : network;
        if (network == null) {
            return false;
        }

        // requires android.permission.ACCESS_NETWORK_STATE
        int bandwidth = connectivityManager
                .getNetworkCapabilities(network).getLinkDownstreamBandwidthKbps();

        return bandwidth >= MIN_NETWORK_BANDWIDTH_KBPS;

    }

    private void requestHighBandwidthNetwork() {
        // Before requesting a high-bandwidth network, ensure prior requests are invalidated.
        unregisterNetworkCallback();

        Log.d(LOG_TAG, "Requesting high-bandwidth network");

        // Requesting an unmetered network may prevent you from connecting to the cellular
        // network on the user's watch or phone; however, unless you explicitly ask for permission
        // to a access the user's cellular network, you should request an unmetered network.
        NetworkRequest request = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build();

        mNetworkCallback = new ConnectivityManager.NetworkCallback() {

            @Override
            public void onAvailable(final Network network) {
                mTimeOutHandler.removeMessages(MESSAGE_CONNECTIVITY_TIMEOUT);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // requires android.permission.INTERNET
                        if (!connectivityManager.bindProcessToNetwork(network)) {
                            Log.e(LOG_TAG, "ConnectivityManager.bindProcessToNetwork()"
                                    + " requires android.permission.INTERNET");
                            //setUiState(UI_STATE_REQUEST_NETWORK);
                        } else {
                            Log.d(LOG_TAG, "Network available");
                            //setUiState(UI_STATE_NETWORK_CONNECTED);
                        }
                    }
                });
            }

            @Override
            public void onCapabilitiesChanged(final Network network,
                                              NetworkCapabilities networkCapabilities) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(LOG_TAG, "Network capabilities changed:\t"+network.toString());
                    }
                });
            }

            @Override
            public void onLost(Network network) {
                Log.d(LOG_TAG, "Network lost");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //setUiState(UI_STATE_REQUEST_NETWORK);
                    }
                });
            }
        };
        Log.d(LOG_TAG, connectivityManager.toString());
        // requires android.permission.CHANGE_NETWORK_STATE
        connectivityManager.requestNetwork(request, mNetworkCallback);
        Log.d(LOG_TAG, "test 3");
        mTimeOutHandler.sendMessageDelayed(
                mTimeOutHandler.obtainMessage(MESSAGE_CONNECTIVITY_TIMEOUT),
                NETWORK_CONNECTIVITY_TIMEOUT_MS);
        Log.d(LOG_TAG, "test 4");
    }

    private void releaseHighBandwidthNetwork() {
        connectivityManager.bindProcessToNetwork(null);
        unregisterNetworkCallback();
    }

    private void addWifiNetwork() {
        // requires android.permission.CHANGE_WIFI_STATE
        startActivity(new Intent(ACTION_ADD_NETWORK_SETTINGS));
    }

    private void updateView(String message){

    }

    private void loadApp(){
        //initialisation de la database
        //database = MeasureDatabase.newDatabase();

        timerHandler = new Handler();
        startTime = 0;

        /* initialiser les indicateurs
        monMenu = Menu.newMenu();

        //Menu départ
        BottomMenu aux = BottomMenu.newBottomMenu("Départ", Color.YELLOW);
        aux.addToBottomMenu(
                Measure.emptyMeasure("windSpeed"),
                Measure.emptyMeasure("windDirection"));
        aux.addToBottomMenu(
                Measure.emptyMeasure("currentSpeed"),
                Measure.emptyMeasure("currentCourseTrue"));
        monMenu.addMenu(aux);

        //Menu Vent
        aux = BottomMenu.newBottomMenu("Vent", Color.GREEN);
        aux.addToBottomMenu(
                Measure.emptyMeasure("windSpeed"),
                Measure.emptyMeasure("windDirection"));
        aux.addToBottomMenu(
                Measure.emptyMeasure("apparentWindSpeed"),
                Measure.emptyMeasure("apparentWindDirection"));
        monMenu.addMenu(aux);

        //Menu Courant
        aux = BottomMenu.newBottomMenu("Courant",Color.MAGENTA);
        aux.addToBottomMenu(
                Measure.emptyMeasure("currentSpeed"),
                Measure.emptyMeasure("currentCourseTrue"));
        aux.addToBottomMenu(
                Measure.emptyMeasure("currentSpeed"),
                Measure.emptyMeasure("currentCourseRelative"));
        monMenu.addMenu(aux);

        //Menu Performance
        aux = BottomMenu.newBottomMenu("Performance", Color.CYAN);
        heartRateMeasure = Measure.emptyMeasure("heartRate");
        aux.addToBottomMenu(
                heartRateMeasure,
                Measure.emptyMeasure("test")
        );
        aux.addToBottomMenu(
                Measure.emptyMeasure("speedOverWater"),
                Measure.emptyMeasure("heading"));
        aux.addToBottomMenu(
                Measure.emptyMeasure("speedParallelToWind"),
                Measure.emptyMeasure("windSpeed"));
        aux.addToBottomMenu(
                Measure.emptyMeasure("speedOverGround"),
                Measure.emptyMeasure("courseOverGround"));
        monMenu.addMenu(aux);

        monMenu.addToDatabase(database);
        */

        // initialisation base connectivité
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        requestHighBandwidthNetwork();

        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);

        loadTest();

        messageCount = 0;

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.top_menu);

        loadApp();

        final WearableRecyclerView recyclerView = findViewById(R.id.main_menu_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setEdgeItemsCenteringEnabled(true);
        //recyclerView.setLayoutManager(new WearableLinearLayoutManager(this));

        CustomScrollingLayoutCallback customScrollingLayoutCallback =
                new CustomScrollingLayoutCallback();
        recyclerView.setLayoutManager(
                new WearableLinearLayoutManager(this, customScrollingLayoutCallback));



        //Start
        ArrayList<Measure> aux = new ArrayList<>();
        aux.add(database.emptyMeasure("start"));
        menuItems.add(new MyMenuItem(R.drawable.start, "Start", aux));

        //Wind
        aux = new ArrayList<>();
        aux.add(database.emptyMeasure("windSpeed"));
        aux.add(database.emptyMeasure("windDirection"));
        aux.add(database.emptyMeasure("apparentWindSpeed"));
        aux.add(database.emptyMeasure("apparentWindDirection"));
        aux.add(database.emptyMeasure("speedParallelToWind"));
        menuItems.add(new MyMenuItem(R.drawable.wind, "Vent", aux));

        //Current
        aux = new ArrayList<>();
        aux.add(database.emptyMeasure("currentSpeed"));
        aux.add(database.emptyMeasure("currentCourseTrue"));
        aux.add(database.emptyMeasure("currentCourseRelative"));
        menuItems.add(new MyMenuItem(R.drawable.wave, "Courant", aux));

        //Performance
        aux = new ArrayList<>();
        aux.add(database.emptyMeasure("speedOverWater"));
        aux.add(database.emptyMeasure("heading"));
        aux.add(database.emptyMeasure("speedOverGround"));
        aux.add(database.emptyMeasure("courseOverGround"));
        menuItems.add(new MyMenuItem(R.drawable.performance, "Performance", aux));

        //Tactics
        aux = new ArrayList<>();
        aux.add(database.emptyMeasure("speedOverWater"));
        aux.add(database.emptyMeasure("heading"));
        aux.add(database.emptyMeasure("speedOverGround"));
        aux.add(database.emptyMeasure("courseOverGround"));
        menuItems.add(new MyMenuItem(R.drawable.buoy, "Tactique", aux));

        //Forecast
        aux = new ArrayList<>();
        aux.add(database.emptyMeasure("speedOverWater"));
        aux.add(database.emptyMeasure("heading"));
        aux.add(database.emptyMeasure("speedOverGround"));
        aux.add(database.emptyMeasure("courseOverGround"));
        menuItems.add(new MyMenuItem(R.drawable.forecast, "Prévision", aux));

        //Pulse
        aux = new ArrayList<>();
        heartRateMeasure = database.emptyMeasure("heartRate");
        aux.add(heartRateMeasure);
        menuItems.add(new MyMenuItem(R.drawable.pulse, "Pulse", aux));

        //Settigs
        aux = new ArrayList<>();
        aux.add(database.emptyMeasure("settings"));
        menuItems.add(new MyMenuItem(R.drawable.settings, "Réglage", aux));

        recyclerView.setAdapter(new MainMenuAdapter(this, menuItems, new MainMenuAdapter.AdapterCallback() {
            @Override
            public void onItemClicked(final Integer menuPosition) {

                Log.d(LOG_TAG, "Action "+menuPosition);
                launchMenu(menuPosition);

                switch (menuPosition) {
                    case 0:
                        Log.d(LOG_TAG, "Action 1: "+menuItems.get(menuPosition));
                        break;
                    case 1:
                        Log.d(LOG_TAG, "Action 2");
                        break;
                    case 2:
                        Log.d(LOG_TAG, "Action 3");
                        break;
                    case 3:
                        Log.d(LOG_TAG, "Action 4");
                        break;
                    default:
                        return; //cancelMenu();
                }
            }
        }));

        Date currentTime = Calendar.getInstance().getTime();

        //setContentView(R.layout.measure_chart_main);


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {

                    InetAddress serveur = null;

                    try {
                        Log.d(LOG_TAG, "Connection au serveur");
                        serveur = InetAddress.getByName("10.10.10.255");
                        Log.d(LOG_TAG, "Serveur connecté: " + serveur.toString());
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }

                    DatagramSocket socket = new DatagramSocket(2000, serveur);

                    while(true)
                    {
                        DatagramPacket data = new DatagramPacket(buffer,buffer.length);
                        //Log.d(LOG_TAG, "prêt à recevoir des données...");
                        socket.receive(data);
                        String auxMessage = new String(data.getData());
                        final NMEASentence message;
                        int length = Math.max(auxMessage.indexOf("\n"), auxMessage.indexOf("*"));
                        if (length > 0){message = NMEASentence.convertMessage(auxMessage.substring(0,length-1));}
                        else{message = NMEASentence.convertMessage(auxMessage);}

                        //Log.d(LOG_TAG, "data received: " + message);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                newMessageReceived(message);
                            }
                        });
                        //Log.d(LOG_TAG, "from: " + data.getAddress() + ":" + data.getPort());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        // Enables Always-on
        setAmbientEnabled();
    }

    public void launchMenu(int position){
        Intent intent = new Intent(this, MeasureListActivity.class);
        intent.putExtra(EXTRA_MESSAGE,position);
        startActivity(intent);
        Log.d(LOG_TAG, "timestamp: " + System.currentTimeMillis());
    }

    @Override
    public AmbientModeSupport.AmbientCallback getAmbientCallback() {
        return new MyAmbientCallback();
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);

        Log.d(LOG_TAG, "onEnter");

        //splash.setVisibility(View.VISIBLE);

    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();

        Log.d(LOG_TAG, "onExit");

        //splash.setVisibility(View.INVISIBLE);



    }

    private static class TimeOutHandler extends Handler {
        private final WeakReference<MainActivity> mMainActivityWeakReference;

        TimeOutHandler(MainActivity mainActivity) {
            mMainActivityWeakReference = new WeakReference<>(mainActivity);
        }
        @Override
        public void handleMessage(Message msg) {
            MainActivity mainActivity = mMainActivityWeakReference.get();

            if (mainActivity != null) {
                switch (msg.what) {
                    case MESSAGE_CONNECTIVITY_TIMEOUT:
                        Log.d(LOG_TAG, "Network connection timeout");
                        //mainActivity.setUiState(UI_STATE_CONNECTION_TIMEOUT);
                        mainActivity.unregisterNetworkCallback();
                        break;
                }
            }
        }
    }

    private class MyAmbientCallback extends AmbientModeSupport.AmbientCallback {
        @Override
        public void onEnterAmbient(Bundle ambientDetails) {
            // Handle entering ambient mode
        }

        @Override
        public void onExitAmbient() {
            // Handle exiting ambient mode
        }

        @Override
        public void onUpdateAmbient() {
            // Update the content
        }
    }

    private void loadTest() {
        SensorManager mSensorManager = ((SensorManager)getSystemService(SENSOR_SERVICE));
        Sensor mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        Sensor mStepCountSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        Sensor mStepDetectSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mStepCountSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mStepDetectSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }


    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(LOG_TAG, "onAccuracyChanged - accuracy: " + accuracy);
    }

    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            String msg = "" + (int)event.values[0];
            Log.d(LOG_TAG, msg);
            heartRateMeasure.updateMeasure("AS",(double) event.values[0]);
        }
        else if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            String msg = "Count: " + (int)event.values[0];
            //mMenu.setText(msg);
            Log.d(LOG_TAG, msg);
        }
        else if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            String msg = "Detected at " + (Calendar.getInstance().getTime().toString());
            //mMenu.setText(msg);
            Log.d(LOG_TAG, msg);
        }
        else
            Log.d(LOG_TAG, "Unknown sensor type");
    }

}
