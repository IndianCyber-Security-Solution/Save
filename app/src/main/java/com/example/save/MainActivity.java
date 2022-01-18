package com.example.save;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import static android.content.ContentValues.TAG;
import static java.lang.Integer.parseInt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Random;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    TextView ra,rb,bp,bt,sa,sb,na,nb;
    private final OkHttpClient nclient = new OkHttpClient();
    private long startTime;
    private long endTime;
    private long fileSize;
    // Bandwidth range in kbps copied from FBConnect Class
    private int POOR_BANDWIDTH = 20;
    private int AVERAGE_BANDWIDTH = 1024;
    private int GOOD_BANDWIDTH = 2000;
    boolean userDataMustDelete = false;
    private static final String TAG = "";
    IntentFilter intentfilter;
    float batteryTemp;
    String currentBatterytemp="Temperature :";
    int batteryLevel;
    int deviceStatus;
    String currentBatteryStatus="Battery ";
    private static final Random RANDOM = new Random();
    private LineGraphSeries<DataPoint> series,seriessp,seriesbt,seriesbp;
    private int lastX = 0;
    private int lastspX = 0;
    private int lastbtX = 0;
    private int lastbpX = 0;
    long ramperc;
    int spper;
    int btlev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ra=findViewById(R.id.ramu);
        rb=findViewById(R.id.ramt);
        bp=findViewById(R.id.storeu);
        bt=findViewById(R.id.storev);
        sa=findViewById(R.id.speedu);
        sb=findViewById(R.id.speedt);
       // na=findViewById(R.id.netwou);
        //nb=findViewById(R.id.netwot);
        ra.setText(getMemoryInfo());
        intentfilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        MainActivity.this.registerReceiver(broadcastreceiver,intentfilter);
        MainActivity.this.registerReceiver(broadcastreceiverp,intentfilter);

        if(isConnected(this)) {
            sa.setText("Connected");
            sa.setTextColor(getResources().getColor(R.color.success));
            downloadInfo();
        }
        else{
            sa.setText("No active internet,\n Please connect to mobile network or WiFI");
            sa.setTextColor(getResources().getColor(R.color.error));
            sb.setText("0");
        }
        GraphView graph = (GraphView) findViewById(R.id.graph);
        // data
        series = new LineGraphSeries<DataPoint>();
        graph.addSeries(series);

        series.setBackgroundColor(R.color.success);
        series.setColor(Color.GREEN);
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(5);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0.5);
        graph.getViewport().setMaxX(3.5);
        // customize a little bit viewport
        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(100);
        viewport.setScrollable(true);
        GraphView graphsp = (GraphView) findViewById(R.id.graphsp);
        // data
        seriessp = new LineGraphSeries<DataPoint>();
        graphsp.addSeries(seriessp);
        seriessp.setBackgroundColor(R.color.success);
        seriessp.setColor(Color.GREEN);
        seriessp.setDrawDataPoints(true);
        seriessp.setDataPointsRadius(5);
        graphsp.getViewport().setXAxisBoundsManual(true);
        graphsp.getViewport().setMinX(0.5);
        graphsp.getViewport().setMaxX(3.5);
        // customize a little bit viewport
        Viewport viewportsp = graphsp.getViewport();
        viewportsp.setYAxisBoundsManual(true);
        viewportsp.setMinY(0);
        viewportsp.setMaxY(10000);
        viewportsp.setScrollable(true);
        GraphView graphbt = (GraphView) findViewById(R.id.graphbt);
        // data
        seriesbt = new LineGraphSeries<DataPoint>();
        graphbt.addSeries(seriesbt);
        seriesbt.setBackgroundColor(R.color.success);
        seriesbt.setColor(Color.GREEN);
        seriesbt.setDrawDataPoints(true);
        seriesbt.setDataPointsRadius(5);
        graphbt.getViewport().setXAxisBoundsManual(true);
        graphbt.getViewport().setMinX(0.5);
        graphbt.getViewport().setMaxX(3.5);
        // customize a little bit viewport
        Viewport viewportbt = graphbt.getViewport();
        viewportbt.setYAxisBoundsManual(true);
        viewportbt.setMinY(0);
        viewportbt.setMaxY(50);
        viewportbt.setScrollable(true);
        GraphView graphbp = (GraphView) findViewById(R.id.graphbp);
        // data
        seriesbp = new LineGraphSeries<DataPoint>();
        graphbp.addSeries(seriesbp);
        seriesbp.setBackgroundColor(R.color.success);
        seriesbp.setColor(Color.GREEN);
        seriesbp.setDrawDataPoints(true);
        seriesbp.setDataPointsRadius(5);
        graphbp.getViewport().setXAxisBoundsManual(true);
        graphbp.getViewport().setMinX(0.5);
        graphbp.getViewport().setMaxX(3.5);
        // customize a little bit viewport
        Viewport viewportbp = graphbp.getViewport();
        viewportbp.setYAxisBoundsManual(true);
        viewportbp.setMinY(0);
        viewportbp.setMaxY(100);
        viewportbp.setScrollable(true);
//        WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
//        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
//        na.setText("" + wifiInfo.getLinkSpeed());
    }



    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    protected void onResume() {
        super.onResume();
        // we're going to simulate real time with thread that append data to the graph
        new Thread(new Runnable() {

            @Override
            public void run() {
                // we add 100 new entries
                for (int i = 0; i < 100; i++) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            getMemoryInfo();
                            addEntry();
                            downloadInfo();
                            addEntrysp();
                            MainActivity.this.registerReceiver(broadcastreceiverp,intentfilter);
                            addEntrybp();
                            MainActivity.this.registerReceiver(broadcastreceiver,intentfilter);

                            addEntrybt();
                        }
                    });

                    // sleep to slow down the add of entries
                    try {
                        Thread.sleep(600);
                    } catch (InterruptedException e) {
                        // manage error ...
                    }
                }
            }
        }).start();
    }

    // add random data to graph
    private void addEntry() {
        // here, we choose to display max 10 points on the viewport and we scroll to end
        series.appendData(new DataPoint(lastX++, ramperc), true, 10);
    }
    private void addEntrysp() {
        // here, we choose to display max 10 points on the viewport and we scroll to end
        seriessp.appendData(new DataPoint(lastspX++, spper), true, 500);
    }
    private void addEntrybt() {
        // here, we choose to display max 10 points on the viewport and we scroll to end
        seriesbt.appendData(new DataPoint(lastbtX++, batteryTemp), true, 20);
    }
    private void addEntrybp() {
        // here, we choose to display max 10 points on the viewport and we scroll to end
        seriesbp.appendData(new DataPoint(lastbpX++, btlev), true, 20);
    }

    private String getMemoryInfo() {
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(memoryInfo);
        Runtime runtime = Runtime.getRuntime();
        long avil=memoryInfo.availMem/(1024*1024);
        long tot=memoryInfo.totalMem/(1024*1024);
        double fina=(double)parseInt(String.valueOf(avil)) / (double)parseInt(String.valueOf(1024));
        double finf=(double)parseInt(String.valueOf(tot)) / (double)parseInt(String.valueOf(1024));

        DecimalFormat df_obj = new DecimalFormat("#.##");

        // round number to the next lowest value
        df_obj.setRoundingMode(RoundingMode.FLOOR);
        //rb.setText(fin+"");
        ramperc=((avil*100)/tot);
        //na.setText(avil+"aval"+tot+"tot"+ramperc+"");
       // Toast.makeText(MainActivity.this, String.valueOf(ramperc),Toast.LENGTH_SHORT).show();
        StringBuilder builder = new StringBuilder();
        builder.append("Available Memory: ").append(df_obj.format(fina)).append(" GB ").
                append(" (").append(ramperc).append("%) \n").
                append("Total Memory: ").append(df_obj.format(finf)).append("GB\n");
                /*append("Runtime Maximum Memory: ").append(runtime.maxMemory()/(1024*1024)).append("\n").
                append("Runtime Total Memory: ").append(runtime.totalMemory()/(1024*1024)).append("\n").
                append("Runtime Free Memory: ").append(runtime.freeMemory()/(1024*1024)).append("\n")*/;
                ra.setText(builder.toString());
        return builder.toString();
    }


    private void downloadInfo() {
        Log.d(TAG, "downloadInfo: iN downloadInfo");
        Request request = new Request.Builder()
                .url("https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png") // replace image url
                .build();
        startTime = System.currentTimeMillis();
        nclient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "downloadInfo: iN failure");
                //check when device is connected to Router but there is no internet
                sa.setText("Device connected to internet/ Router but no Internet detected");
                sa.setTextColor(getResources().getColor(R.color.error));
                sb.setText("0");
                spper=0;
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "downloadInfo: iN success");
                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);
                Headers responseHeaders = response.headers();
                for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                    Log.d(TAG, responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }
                InputStream input = response.body().byteStream();
                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    while (input.read(buffer) != -1) {
                        bos.write(buffer);
                    }
                    byte[] docBuffer = bos.toByteArray();
                    fileSize = bos.size();
                } finally {
                    input.close();
                }
                endTime = System.currentTimeMillis();
                // calculate how long it took by subtracting endtime from starttime
                final double timeTakenMills = Math.floor(endTime - startTime);  // time taken in milliseconds
                final double timeTakenInSecs = timeTakenMills / 1000;  // divide by 1000 to get time in seconds
                final int kilobytePerSec = (int) Math.round(1024 / timeTakenInSecs);
                final double speed = Math.round(fileSize / timeTakenMills);
                Log.d(TAG, "Time taken in secs: " + timeTakenInSecs);
                Log.d(TAG, "Kb per sec: " + kilobytePerSec);
                Log.d(TAG, "Download Speed: " + speed);
                Log.d(TAG, "File size in kb: " + fileSize);
                DecimalFormat df_obj = new DecimalFormat("#.##");

                // round number to the next lowest value
                df_obj.setRoundingMode(RoundingMode.FLOOR);
                spper=kilobytePerSec;
                double net;
                if(kilobytePerSec>1024){
                   net=(double)parseInt(String.valueOf(kilobytePerSec)) / (double)parseInt(String.valueOf(1024));

                }else{
                    net=kilobytePerSec;
                }


                // Toast.makeText(MainActivity.this,String.valueOf(kilobytePerSec),Toast.LENGTH_SHORT).show();
                // update the UI with the speed test results
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       // sb.setText(" \nYour speed  is "  + df_obj.format(net)+" mbps" +kilobytePerSec);
                        //sb.setTextColor(getResources().getColor(R.color.success));
                        if (kilobytePerSec <= POOR_BANDWIDTH) {
                            // slow connection
                            sa.setText("Connected");
                            sb.setText("Slow Connection: \nyour speed  is "  + df_obj.format(net)+" mbps" );
                            sa.setTextColor(getResources().getColor(R.color.success));
                            sb.setTextColor(getResources().getColor(R.color.success));
                        } else if (kilobytePerSec <= AVERAGE_BANDWIDTH) {
                            // Average connection
                            sa.setText("Connected");
                            sb.setText("Average Connection: \nyour speed  is "  + df_obj.format(net)+" mbps" );
                            sa.setTextColor(getResources().getColor(R.color.success));
                            sb.setTextColor(getResources().getColor(R.color.success));
                        }
                        else if (kilobytePerSec > GOOD_BANDWIDTH){
                            //Good connection
                            sa.setText("Connected");
                            sb.setText("Good Connection: \nyour speed  is "  + df_obj.format(net)+" mbps" );
                            sa.setTextColor(getResources().getColor(R.color.success));
                            sb.setTextColor(getResources().getColor(R.color.success));
                        }
                    }
                });

            }
        });
    }
    private BroadcastReceiver broadcastreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            batteryTemp = (float)(intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0))/10;

            bt.setText(currentBatterytemp +" "+batteryTemp +" "+ (char) 0x00B0 +"C");

        }
    };
    private BroadcastReceiver broadcastreceiverp = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            deviceStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS,-1);
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int batteryLevel=(int)(((float)level / (float)scale) * 100.0f);
            btlev=batteryLevel;

            if(deviceStatus == BatteryManager.BATTERY_STATUS_CHARGING){

                bp.setText(currentBatteryStatus+"  Charging at "+batteryLevel+" %");

            }

            if(deviceStatus == BatteryManager.BATTERY_STATUS_DISCHARGING){

                bp.setText(currentBatteryStatus+"  Discharging at "+batteryLevel+" %");

            }

            if (deviceStatus == BatteryManager.BATTERY_STATUS_FULL){

                bp.setText(currentBatteryStatus+"= Battery Full at "+batteryLevel+" %");

            }

            if(deviceStatus == BatteryManager.BATTERY_STATUS_UNKNOWN){

                bp.setText(currentBatteryStatus+" = Unknown at "+batteryLevel+" %");
            }

            if (deviceStatus == BatteryManager.BATTERY_STATUS_NOT_CHARGING){

                bp.setText(currentBatteryStatus+" = Not Charging at "+batteryLevel+" %");

            }

        }
    };
    public static boolean isConnected(Context context) {
        if (context != null) {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connMgr != null) {
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                return (networkInfo != null && networkInfo.isConnected());
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}