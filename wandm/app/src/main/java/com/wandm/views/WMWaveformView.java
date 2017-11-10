package com.wandm.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class WMWaveformView extends View {
    private static final int DENSITY = 2;
    private static final int WAVE_COUNT = 6;
    private static final float FREQUENCY = 0.1875f;
    private static final float PRIMARY_WIDTH = 1.0f;
    private static final float SECONDARY_WIDTH = 0.5f;
    private static final float MIN_AMPLITUDE = 0.125f;
    private static int WAVE_COLOR = Color.LTGRAY;

    private float mAmplitude = MIN_AMPLITUDE;
    private float mPhaseShift = -0.1875f;
    private float mPhase = mPhaseShift;

    private Paint mPrimaryPaint;
    private Paint mSecondaryPaint;

    private Path mPath;

    private float mLastX;
    private float mLastY;

    public WMWaveformView(Context context) {
        this(context, null);
    }

    public WMWaveformView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WMWaveformView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        WAVE_COLOR = Color.LTGRAY;
        initialize();
    }

    public void reset() {
        for (int i = 0; i < 5; i++) {
            updateAmplitude(-2);
        }
    }

    private void initialize() {
        mPrimaryPaint = new Paint();
        mPrimaryPaint.setStrokeWidth(PRIMARY_WIDTH);
        mPrimaryPaint.setAntiAlias(true);
        mPrimaryPaint.setStyle(Paint.Style.STROKE);
        mPrimaryPaint.setColor(WAVE_COLOR);

        mSecondaryPaint = new Paint();
        mSecondaryPaint.setStrokeWidth(SECONDARY_WIDTH);
        mSecondaryPaint.setAntiAlias(true);
        mSecondaryPaint.setStyle(Paint.Style.STROKE);
        mSecondaryPaint.setColor(WAVE_COLOR);

        mPath = new Path();
    }

    public void updateAmplitude(float amplitude) {
        mAmplitude = Math.max(amplitude, MIN_AMPLITUDE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        for (int l = 0; l < WAVE_COUNT; ++l) {
            float midH = height / 2.0f;
            float midW = width / 2.0f;

            float maxAmplitude = midH / 1.0f;
            float progress = 1.0f - l * 1.0f / WAVE_COUNT;
            float normalAmplitude = (1.5f * progress - 0.5f) * mAmplitude;

            float multiplier = (float) Math.min(1.0, (progress / 3.0f * 2.0f) + (1.0f / 3.0f));

            if (l != 0) {
                mSecondaryPaint.setAlpha((int) (multiplier * 255));
            }

            mPath.reset();
            for (int x = 0; x < width + DENSITY; x += DENSITY) {
                float scaling = 1f - (float) Math.pow(1 / midW * (x - midW), 2);
                float y = scaling * maxAmplitude * normalAmplitude * (float) Math.sin(
                        180 * x * FREQUENCY / (width * Math.PI) + mPhase) + midH;

//                canvas.drawPoint(x, y, l == 0 ? mPrimaryPaint : mSecondaryPaint);
//                canvas.drawLine(x, y, x, 2*midH - y, mSecondaryPaint);
                if (x == 0) {
                    mPath.moveTo(x, y);
                } else {
                    mPath.lineTo(x, y);
//                    final float x2 = (x + mLastX) / 2;
//                    final float y2 = (y + mLastY) / 2;
//                    mPath.quadTo(x2, y2, x, y);
                }

                mLastX = x;
                mLastY = y;
            }

            if (l == 0) {
                canvas.drawPath(mPath, mPrimaryPaint);
            } else {
                canvas.drawPath(mPath, mSecondaryPaint);
            }
        }

        mPhase += mPhaseShift;
        invalidate();
    }
}
