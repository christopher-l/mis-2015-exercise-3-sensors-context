package com.example.chris.sensorsandcontext;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

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
    private long mLastAccess;
    private double mCurrentSamplingFrequency;
    private double[] mFFTValues;
    private TextView mActivityView;


    public View2(Context context, AttributeSet attrs) {
        super(context, attrs);
        mSensorHandler = ((MainActivity)getContext()).mSensorHandler;
        mSensorHandler.setView2(this);
        mValues = new double[mWindowSize];
        mFFT = new FFT(mWindowSize);
        mFFTValues = new double[mWindowSize/2];
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mLastAccess = System.currentTimeMillis();
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
        if (mActivityView == null) {
            mActivityView = (TextView) ((Activity)getContext()).findViewById(R.id.activity);
        }

    }


    private void drawValue(Canvas canvas, int pos, double value) {
        double width = (double) getWidth() / (double) (mWindowSize/2);
        int left = (int) (width * (double) pos);
        int height = (int) ((value / MAX_VALUE) * getHeight());
        canvas.drawRect(left, getHeight() - height, (int) (left + width), getHeight(), mPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mCurrentSamplingFrequency = 1000.0 / (double) (System.currentTimeMillis() - mLastAccess);
        mLastAccess = System.currentTimeMillis();
        double[] x = mValues.clone();
        double[] y = new double[mWindowSize];
        mFFT.fft(x, y);
        for (int i=1; i<=mWindowSize/2; i++) {
            double value = Math.sqrt(x[i]*x[i] + y[i]*y[i]);
            mFFTValues[i-1] = value;
            drawValue(canvas, i-1, value);
        }
        if (mActivityView != null) {
            mActivityView.setText(recognizeActivity());
        }
    }

    private double posToFrequency(int pos) {
        // from https://stackoverflow.com/questions/6740545/need-help-understanding-fft-output/6741403#6741403
        return ((pos + 1) * mCurrentSamplingFrequency / 2) / (mWindowSize / 2);
    }

    private double getHighestFrequency() {
        int highestPos = 0;
        for (int i=0; i<mWindowSize/2; i++) {
            if (mFFTValues[i] > highestPos) {
                highestPos = i;
            }
        }
        return posToFrequency(highestPos);
    }

    private double getAverageMagnitude() {
        double sum = 0;
        for (int i=0; i<mWindowSize/2; i++) {
            sum += mFFTValues[i];
        }
        return sum / (double) (mWindowSize/2);
    }

    private String recognizeActivity() {
        double freq = getHighestFrequency();
        double mag = getAverageMagnitude();
        if (mag < 3) {
            return "lying on couch/bed"; // very little movement overall
        } else if (mag < 10 && freq < 10) {
            return "walking";
            // moderate movement, low frequency
            // (compensate for artifacts in frequency -- actual walking frequency should be lower)
        } else if (freq > 30) {
            return "driving in bus / car"; // sudden concussions
        } else {
            return "running"; // movement exceeds previous thresholds
        }

    }

    public class SeekBarListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            mWindowSize = (int) Math.pow(2, progress);
            mValues = new double[mWindowSize];
            mFFT = new FFT(mWindowSize);
            mFFTValues = new double[mWindowSize/2];
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }
}
