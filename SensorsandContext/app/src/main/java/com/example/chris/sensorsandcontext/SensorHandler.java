package com.example.chris.sensorsandcontext;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

/**
 * Created by chris on 5/20/15.
 */
public class SensorHandler implements SensorEventListener {

    private MainActivity mActivity;
    private View1 mView1;

    public SensorHandler(MainActivity activity) {
        mActivity = activity;
    }

    public void setView1(View1 view1) {
        mView1 = view1;
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


    @Override
    public final void onSensorChanged(SensorEvent event) {
        //red.lineTo(position, (int) (normalize(event.values[0]) * ((float) height - 1)));

        if (mView1 != null) {
            mView1.addValues(event.values);
            mView1.invalidate();
        }
    }

}
