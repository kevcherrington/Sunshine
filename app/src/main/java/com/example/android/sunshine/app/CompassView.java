package com.example.android.sunshine.app;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

/**
 * (C) Copyright 2015 Kevin Cherrington (kevcherrington@gmail.com).
 * <p/>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * <p/>
 * Contributors:
 * Kevin Cherrington
 */
public class CompassView extends View {
    private static final String LOG_TAG = CompassView.class.getSimpleName();

    private Context mContext;
    private ShapeDrawable mOuterCirc;
    private ShapeDrawable mInnerCirc;
    private Path mNeedle;
    private Paint mNeedlePaint;
    private Paint mCardinalPaint;
    private Paint mNorthCardinalPaint;
    private Point[] mCardinalPoints; //going in circle starting from north moving to east etc.
    private Point[] mNeedlePoints; // starting with point then to the bottom right and around.
    private float mWindSpeed;
    private float mDirection;
    private float mPadding = 10f;
    private float mMinHeight = 300f;
    private float mMinWidth = 300f;
    private float mHeight;
    private float mWidth;

    public CompassView(Context context) {
        super(context);
        mContext = context;
        init(null);
    }

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }

    public CompassView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CompassView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a;
        if (attrs != null) {
            // get the width and height from the xml
            a = getContext().getTheme().obtainStyledAttributes(attrs,
                    R.styleable.CompassView,
                    0, 0);
            try {
                if (a != null) {
                    mWidth = a.getDimension(R.styleable.CompassView_compassWidth, mMinWidth);
                    mHeight = a.getDimension(R.styleable.CompassView_compassHeight, mMinHeight);
                }
            } finally {
                a.recycle();
            }
        }

        if (mWidth == 0.0f) {
            mWidth = mMinWidth;
        }
        if (mHeight == 0.0f) {
            mHeight = mMinHeight;
        }

        mOuterCirc = new ShapeDrawable(new OvalShape());
        mOuterCirc.getPaint().setColor(0xff000000);
        mOuterCirc.getPaint().setStyle(Paint.Style.STROKE);
        mOuterCirc.getPaint().setStrokeWidth(0f);

        mOuterCirc.setBounds(1, 1,
                (int) mWidth - 1,
                (int) mHeight - 1);

        mInnerCirc = new ShapeDrawable(new OvalShape());
        mInnerCirc.getPaint().setColor(0xff000000);
        mInnerCirc.getPaint().setStyle(Paint.Style.STROKE);
        mInnerCirc.getPaint().setStrokeWidth(0f);

        mInnerCirc.setBounds((int) mPadding * 3, (int) mPadding * 3,
                (int) (mWidth - mPadding * 3),
                (int) (mHeight - mPadding * 3));

        calculateNeedlePoints();

        updateNeedle();

        mNeedlePaint = new Paint();
        mNeedlePaint.setStyle(Paint.Style.FILL);
        mNeedlePaint.setColor(Color.RED);

        mCardinalPaint = new Paint();
        mCardinalPaint.setStyle(Paint.Style.STROKE);
        mCardinalPaint.setColor(0xff000000);
        mCardinalPaint.setTextSize(40);
        mCardinalPaint.setTextAlign(Paint.Align.CENTER);

        mNorthCardinalPaint = new Paint();
        mNorthCardinalPaint.setStyle(Paint.Style.STROKE);
        mNorthCardinalPaint.setColor(0xff000000);
        mNorthCardinalPaint.setTextSize(40);
        mNorthCardinalPaint.setTextAlign(Paint.Align.CENTER);
        mNorthCardinalPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        CalculateCardinalPoints();
    }

    private void updateNeedle() {
        mNeedle = new Path();
        mNeedle.moveTo(mNeedlePoints[0].x, mNeedlePoints[0].y);
        mNeedle.lineTo(mNeedlePoints[1].x, mNeedlePoints[1].y);
        mNeedle.lineTo(mNeedlePoints[2].x, mNeedlePoints[2].y);
        mNeedle.close();
    }

    private void calculateNeedlePoints() {
        mNeedlePoints = new Point[3];
        Point center = new Point((int) mWidth / 2, (int) mHeight / 2);
        float radius = (mWidth /2) - (mPadding * 2);
        float angleDeg = (mDirection + 270f) % 360f;

        mNeedlePoints[0] = new Point((int) (radius * (float) Math.cos(Math.toRadians(angleDeg)) + center.x),
                (int) (radius * (float) Math.sin(Math.toRadians(angleDeg)) + center.y));
        mNeedlePoints[1] = new Point((int) (20 * (float) Math.cos(Math.toRadians((angleDeg + 90F) % 360F)) + center.x),
                (int) (20 * (float) Math.sin(Math.toRadians((angleDeg + 90F) % 360F)) + center.y));
        mNeedlePoints[2] = new Point((int) (20 * (float) Math.cos(Math.toRadians((angleDeg + 270F) % 360F)) + center.x),
                (int) (20 * (float) Math.sin(Math.toRadians((angleDeg + 270F) % 360F)) + center.y));
    }

    private void CalculateCardinalPoints() {
        mCardinalPoints = new Point[8];
        Point center = new Point((int) mWidth / 2, (int) (mHeight / 2) + 15);// + 20 is offset for font size.
        float radius = (mWidth / 2) - (mPadding * 2);
        float angleDeg = 270f;

        for (int i = 0; i < 8; i ++) {
            Point point = new Point((int) (radius * (float) Math.cos(Math.toRadians(angleDeg)) + center.x),
                    (int) (radius * (float) Math.sin(Math.toRadians(angleDeg)) + center.y));
            mCardinalPoints[i] = point;
            angleDeg = angleDeg + 45F % 360F;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int hSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int myHeight = hSpecSize < (int) getMinHeight() ? (int) getMinHeight() : hSpecSize;

        if (hSpecMode == MeasureSpec.EXACTLY) {
            if (myHeight < getMinHeight()) {
                myHeight = (int) getMinHeight();
            }
        } else if (hSpecMode == MeasureSpec.AT_MOST) {
            // Wrap Content
        }

        int wSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int myWidth = wSpecSize < (int) getMinWidth() ? (int) getMinWidth() : wSpecSize;

        if (wSpecMode == MeasureSpec.EXACTLY) {
            if (myWidth < getMinWidth()) {
                myWidth = (int) getMinWidth();
            }
        } else if (wSpecMode == MeasureSpec.AT_MOST) {
            // Wrap Content
        }

        setMeasuredDimension(myWidth, myHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mOuterCirc.draw(canvas);
        mInnerCirc.draw(canvas);
        canvas.drawText("N", mCardinalPoints[0].x, mCardinalPoints[0].y, mNorthCardinalPaint);
        canvas.drawText("NE", mCardinalPoints[1].x, mCardinalPoints[1].y, mCardinalPaint);
        canvas.drawText("E", mCardinalPoints[2].x, mCardinalPoints[2].y, mCardinalPaint);
        canvas.drawText("SE", mCardinalPoints[3].x, mCardinalPoints[3].y, mCardinalPaint);
        canvas.drawText("S", mCardinalPoints[4].x, mCardinalPoints[4].y, mCardinalPaint);
        canvas.drawText("SW", mCardinalPoints[5].x, mCardinalPoints[5].y, mCardinalPaint);
        canvas.drawText("W", mCardinalPoints[6].x, mCardinalPoints[6].y, mCardinalPaint);
        canvas.drawText("NW", mCardinalPoints[7].x, mCardinalPoints[7].y, mCardinalPaint);
        canvas.drawPath(mNeedle, mNeedlePaint);
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent ev) {
        ev.getText().add(Utility.getFormattedWind(mContext, mWindSpeed, mDirection));
        return true;
    }

    private float getMinHeight() {
        return mMinHeight;
    }

    private float getMinWidth() {
        return mMinWidth;
    }

    public void setWindSpeedDir(float windSpeed, float windDir) {
        this.mWindSpeed = windSpeed;
        this.mDirection = windDir;

        calculateNeedlePoints();
        updateNeedle();

        AccessibilityManager am = (AccessibilityManager) mContext.getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (am.isEnabled()) {
            sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED);
        }
    }
}
