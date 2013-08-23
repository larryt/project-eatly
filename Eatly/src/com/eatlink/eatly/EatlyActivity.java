package com.eatlink.eatly;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class EatlyActivity extends Activity implements SensorEventListener {
    private final String TAG = getClass().getSimpleName();
    private static final int MAX_INTERVAL_BETHWEEN_SAMPLINGS = 200;
    private static final int MIN_MOVEMENT = 10;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private TextView title, tv, tv1, tv2;
    private float last_x = 0, last_y = 0, last_z = 0;
    private long m_curTime, m_lastUpdate = -1, m_cooldowntime = MAX_INTERVAL_BETHWEEN_SAMPLINGS;
    private EatlyPickUp m_pickup;
    private boolean isShakeDetectionEnabled = false;
    private EatlyDataBase m_db = EatlyDataBase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eatly);
        title = (TextView) findViewById(R.id.name);
        tv = (TextView) findViewById(R.id.xval);
        tv1 = (TextView) findViewById(R.id.yval);
        tv2 = (TextView) findViewById(R.id.zval);
        // get the sensor service
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // get the accelerometer sensor
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        m_pickup = new EatlyPickUp();
        m_lastUpdate = System.currentTimeMillis();
        new downloadDBTask().execute();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.eatly, menu);
        return true;
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSensorChanged(SensorEvent arg0) {
        // TODO Auto-generated method stub

        float x = arg0.values[0];
        float y = arg0.values[1];
        float z = arg0.values[2];
        // display values using TextView
        title.setText(R.string.app_name);
        tv.setText("X axis" + "\t\t" + x);
        tv1.setText("Y axis" + "\t\t" + y);
        tv2.setText("Z axis" + "\t\t" + z);

        if (!isShakeDetectionEnabled) {
            last_x = x;
            last_y = y;
            last_z = z;
            isShakeDetectionEnabled = true;
        }

        m_curTime = System.currentTimeMillis();

        float totalMovement = Math.abs(x + y + z - last_x - last_y - last_z);
        if (totalMovement > MIN_MOVEMENT) {
            if (/* isShakeDetectionEnabled && */((m_curTime - m_lastUpdate) > m_cooldowntime)) {

                final String mylaunch = m_pickup.select();
                if (mylaunch != null) {
                    // isShakeDetectionEnabled = false;

                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(EatlyActivity.this, mylaunch, Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                m_cooldowntime = 7 * MAX_INTERVAL_BETHWEEN_SAMPLINGS;
                printDebug(" curTime " + m_curTime + " lastUpdate " + m_lastUpdate + " cooldowntime " + m_cooldowntime);

            } else {
                if (m_cooldowntime != MAX_INTERVAL_BETHWEEN_SAMPLINGS)
                    m_cooldowntime = MAX_INTERVAL_BETHWEEN_SAMPLINGS;
            }

            m_lastUpdate = m_curTime;

        }
        last_x = x;
        last_y = y;
        last_z = z;

    }

    private class downloadDBTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            if (m_db != null) {
                m_db.genDB();
                m_db.dumpDB();
            }
            return null;
        }

    }

    private void printDebug(String s) {
        Log.d(TAG, s);
    }
}
