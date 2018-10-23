package com.dpass.android.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by AndyL on 2018/2/1.
 *
 */

public class CircleIndicator extends View {

    public static final int defaultCount = 2;

    private int activeColor = Color.parseColor("#01C75E");
    private int inactiveColor = Color.parseColor("#FFFFFF");

    private float radius;
    private float gapDistance;
    private float startX;
    private float centerY;

    private Paint mPaint;

    private int   position = 0;

    private int count = defaultCount;

    private float dycOffset = 0;

    public CircleIndicator(Context context) {
        this(context, null);
    }

    public CircleIndicator(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CircleIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        radius = height * 1.0f / 2f;
        gapDistance = radius * 2f / 5f * 6f;
        float totalWidth = radius * 2 * count + gapDistance * (count - 1);
        startX = (getMeasuredWidth() - getPaddingStart() - getPaddingEnd()) / 2 - totalWidth / 2 + getPaddingStart();
        centerY = (getMeasuredHeight() - getPaddingTop() - getPaddingBottom()) / 2 + getPaddingTop();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        dycOffset = startX;
        for (int i = 0; i < count; i++) {
            if (i == position){
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(activeColor);
                canvas.drawCircle(dycOffset + radius, centerY, radius, mPaint);
            }else{
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(inactiveColor);
                canvas.drawCircle(dycOffset + radius, centerY, radius, mPaint);
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeWidth(2f);
                mPaint.setColor(activeColor);
                canvas.drawCircle(dycOffset + radius, centerY, radius, mPaint);
            }
            dycOffset = dycOffset + radius * 2  + gapDistance;
        }
    }

    public void transfer(int position){
        this.position = position;
    }

    public void setCount(int count){
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
