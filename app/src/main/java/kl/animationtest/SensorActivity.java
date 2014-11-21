package kl.animationtest;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * Created by Administrator on 2014/11/18.
 */
public class SensorActivity extends Activity {

    private SensorManager mSensorManager;
    //private Sensor mOrientation;
    private float[] gravity = new float[3];
    private float[] geomagnetic = new float[3];
    float[] MR = new float[9];
    float[] MI = new float[9];
    float[] orientation = new float[3];
    Sensor mSensor_ACCELEROMETER;
    Sensor mSensor_MAGNETIC;

    SensorEventListener listener_ac;
    SensorEventListener listener_ma;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
       // mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        mSensor_ACCELEROMETER = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensor_MAGNETIC = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        for(Sensor s : mSensorManager.getSensorList(Sensor.TYPE_ALL)){
            Log.i("sensor",s.getName());
        }
        listener_ac = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                gravity[0] = sensorEvent.values[0];
                gravity[1] = sensorEvent.values[1];
                gravity[2] = sensorEvent.values[2];
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        listener_ma = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                geomagnetic[0] = sensorEvent.values[0];
                geomagnetic[1] = sensorEvent.values[1];
                geomagnetic[2] = sensorEvent.values[2];
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SensorManager.getRotationMatrix(MR,MI,gravity,geomagnetic);
//                String sr = "R:",si = "I:";
//                for(int i = 0; i != MR.length;i++){
//                    sr += (i+":" + MR[i] + ", ");
//                    si += (i+":" + MI[i] + ", ");
//                }
                SensorManager.getOrientation(MR,orientation);
                String s = "orientation";
                for(float f : orientation){
                    s += f + ",";
                }
                Log.i("sensor",s);
//                Log.i("sensor",sr);
//                Log.i("sensor",si);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mSensor_ACCELEROMETER != null){
            Log.i("sensor","support ACCELEROMETER");
            mSensorManager.registerListener(listener_ac,mSensor_ACCELEROMETER,SensorManager.SENSOR_DELAY_FASTEST );
        }
        if(mSensor_MAGNETIC != null){
            Log.i("sensor","support MAGNETIC");
            mSensorManager.registerListener(listener_ma,mSensor_MAGNETIC,SensorManager.SENSOR_DELAY_FASTEST );
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mSensor_ACCELEROMETER != null){
            mSensorManager.unregisterListener(listener_ac);
        }
        if(mSensor_MAGNETIC != null){
            mSensorManager.unregisterListener(listener_ma);
        }
    }
}
