package com.project.sensorlog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends Activity implements SensorEventListener {

    private static final String TAG = ".MainActivity";
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mGyroscope;
    private EditText name;
    RandomAccessFile raf;
    File root;
    private TextView xA,yA,zA,xG,yG,zG;
    String fileName;
    Calendar cal;
    private Button Startb, Stopb;
    SensorEvent sensorEvent;
    long MillisecondTime, Updatetime = 0L, StartTime;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        xA = (TextView) findViewById(R.id.xValue);
        yA = (TextView) findViewById(R.id.yValue);
        zA = (TextView) findViewById(R.id.zValue);

        xG = (TextView) findViewById(R.id.xGyroValue);
        yG = (TextView) findViewById(R.id.yGyroValue);
        zG = (TextView) findViewById(R.id.zGyroValue);

        name = (EditText) findViewById(R.id.name);

        Startb = (Button) findViewById(R.id.onStartClick);
        Stopb = (Button) findViewById(R.id.onStopClick);
        cal=Calendar.getInstance();


        Log.d(TAG, "onCreate: Initializing Sensor Services");

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Startb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fileName = name.getText().toString();

                mSensorManager.registerListener(MainActivity.this,mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_GAME);

                mSensorManager.registerListener(MainActivity.this,mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),SensorManager.SENSOR_DELAY_GAME);

                StartTime = SystemClock.uptimeMillis();

                //String mark=String.valueOf(cal.getTime());
                root=new File(Environment.getExternalStorageDirectory()+"/SensorRecorder/");
                if(!root.exists()){
                    root.mkdirs();
                }
                try {
                    raf=new RandomAccessFile(root+"/"+ fileName + " " + SystemClock.uptimeMillis() +"_100Hz.csv","rw");
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Stopb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSensorManager.unregisterListener(MainActivity.this);
                try {
                    raf.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                name.setText("");
                fileName = "";
            }
        });

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /*public static String currentDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy ss:ms");
        // get current date time with Date()
        Date date = new Date();
        // System.out.println(dateFormat.format(date));SensorEventListener)
        // don't print it, but save it!
        return dateFormat.format(date);
    }*/


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //String timestamp=String.valueOf(System.currentTimeMillis());
        Sensor sensor = sensorEvent.sensor;
        if(sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            xA.setText("" + sensorEvent.values[0]);
            yA.setText("" + sensorEvent.values[1]);
            zA.setText("" + sensorEvent.values[2]);
        }
        else if(sensor.getType() == Sensor.TYPE_GYROSCOPE) {

            xG.setText("" + sensorEvent.values[0]);
            yG.setText("" + sensorEvent.values[1]);
            zG.setText("" + sensorEvent.values[2]);
        }

        MillisecondTime = SystemClock.uptimeMillis()-StartTime;


        try {
            raf.seek(raf.length());
            raf.write((xA.getText()+","+yA.getText()+","+zA.getText()+","+xG.getText()+","+yG.getText()+","+zG.getText()+","+ MillisecondTime +"\n").getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}