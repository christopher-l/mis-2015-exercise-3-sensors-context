package com.example.chris.sensorsandcontext;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.View;

/**
 * Created by chris on 5/20/15.
 */
public class SensorHandler implements SensorEventListener {

    private final float MAX_VALUE = 50;
    private MainActivity mActivity;
    private View1 mView1;
    private Bitmap mBitmap1;
    private int position = 0;

    public SensorHandler(MainActivity activity) {
        mActivity = activity;
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private float normalize(float value) {
        float result =  (float) (value / MAX_VALUE + 0.5);
        if (result < 0) {
            return 0;
        } else if (result > 1) {
            return 1;
        } else {
            return result;
        }
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        if (mView1 == null) {
            mView1 = (View1)mActivity.findViewById(R.id.view1);
        }
        if (mBitmap1 == null) {
            mBitmap1 = mView1.mBitmap;
        }
        if (mBitmap1 != null) {
            int height = mBitmap1.getHeight();
            int width = mBitmap1.getWidth();

            System.out.println(normalize(event.values[0]) * ((float) height - 1));


            mBitmap1.setPixel(position, (int) (normalize(event.values[0]) * ((float) height - 1)), Color.RED);


            position = (position + 1) % width;
        }
        mView1.invalidate();
    }

}
