package com.example.chris.sensorsandcontext;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;

/**
 * Created by chris on 5/22/15.
 */
public class View2 extends View{

    private SensorHandler mSensorHandler;
    private double[] mValues;
    private int mPos = 0;
    private int mWindowSize = 16;
    private FFT mFFT;
    private Paint mPaint;
    private final double MAX_VALUE = 50;
    private SeekBar mSeekBar;




    public View2(Context context, AttributeSet attrs) {
        super(context, attrs);
        mSensorHandler = ((MainActivity)getContext()).mSensorHandler;
        mSensorHandler.setView2(this);
        mValues = new double[mWindowSize];
        mFFT = new FFT(mWindowSize);
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);

    }

    public void addValues(float[] values) {
        double magnitude = Math.sqrt(values[0]*values[0] + values[1]*values[1] + values[2]*values[2]);
        mPos = (mPos + 1) % mWindowSize;
        mValues[mPos] = magnitude;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mSeekBar == null) {
            mSeekBar = (SeekBar) ((Activity)getContext()).findViewById(R.id.seekbar2);
            if (mSeekBar != null) {
                SeekBarListener listener = new SeekBarListener();
                mSeekBar.setOnSeekBarChangeListener(listener);
            }
        }

    }


    private void drawValue(Canvas canvas, int pos, double value) {
        double width = (double) getWidth() / (double) mWindowSize;
        int left = (int) (width * (double) pos);
        int height = (int) ((value / MAX_VALUE) * getHeight());
        canvas.drawRect(left, getHeight() - height, (int) (left + width), getHeight(), mPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        double[] x = mValues.clone();
        double[] y = new double[mWindowSize];
        mFFT.fft(x, y);
        for (int i=0; i<mWindowSize; i++) {
            drawValue(canvas, i, Math.sqrt(x[i]*x[i] + y[i]*y[i]));
        }
    }


    public class SeekBarListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            mWindowSize = (int) Math.pow(2, progress);
            mValues = new double[mWindowSize];
            mFFT = new FFT(mWindowSize);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }
}
