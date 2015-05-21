package com.example.chris.sensorsandcontext;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;

/**
 * Created by chris on 5/20/15.
 */
public class View1 extends View{

    private final float MAX_VALUE = 50;

    private SensorHandler mSensorHandler;
    private Paint mPaint;
    private Matrix mMatrix;
    private Path[][] mPaths;
    private int mCounter = 0;
    private int mActivePath = 0;

    public View1(Context context, AttributeSet attrs){
        super(context, attrs);
        mSensorHandler = ((MainActivity)getContext()).mSensorHandler;
        mSensorHandler.setView1(this);
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(5);
        mPaint.setStyle(Paint.Style.STROKE);
        mMatrix = new Matrix();
        mMatrix.preTranslate(-5, 0);
        mPaths = new Path[2][4];
        for (int j=0; j<2; j++) {
            for (int i = 0; i < 4; i++) {
                mPaths[j][i] = new Path();
            }
        }
    }

    private float normalize(float value) {
        return (float) (value / MAX_VALUE + 0.5);
    }


    public void addValues(float[] values) {
        if (mCounter++ == 300) {
            resetPaths(); // Too large paths cannot be rendered
        }
        double magnitude = Math.sqrt(values[0]*values[0] + values[1]*values[1] + values[2]*values[2]);
        for (int j=0; j<2; j++) {

            for (int i = 0; i < 3; i++) {
                mPaths[j][i].lineTo(getWidth(), normalize(values[i]) * ((float) getHeight() - 1));
                mPaths[j][i].transform(mMatrix);
            }
            mPaths[j][3].lineTo(getWidth(), (1 - (float) (magnitude / MAX_VALUE)) * getHeight());
            mPaths[j][3].transform(mMatrix);
        }
    }

    private void resetPaths() {
        for (int i = 0; i < 3; i++) {
            mPaths[mActivePath][i].reset();
            mPaths[mActivePath][i].moveTo(getWidth(), getHeight() / 2);
        }
        mPaths[mActivePath][3].reset();
        mPaths[mActivePath][3].moveTo(getWidth(), getHeight());
        mCounter = 0;
        mActivePath = (mActivePath + 1) % 2;
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        resetPaths();
     }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(Color.RED);
        canvas.drawPath(mPaths[mActivePath][0], mPaint);
        mPaint.setColor(Color.GREEN);
        canvas.drawPath(mPaths[mActivePath][1], mPaint);
        mPaint.setColor(Color.BLUE);
        canvas.drawPath(mPaths[mActivePath][2], mPaint);
        mPaint.setColor(Color.WHITE);
        canvas.drawPath(mPaths[mActivePath][3], mPaint);
    }
}
