/*
* Copyright (C) 2015 Mert Şimşek
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.wandm.views;

import android.animation.AnimatorSet;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.squareup.picasso.Picasso;
import com.wandm.R;
import com.wandm.utils.Utils;

import java.io.IOException;

public class WMPlayerView extends View {


    /**
     * Modified OnClickListener. We do not want all view click.
     * notify onClick() only button area touched.
     */
    private OnClickListener onClickListener;


    /**
     * Paint to draw cover photo to canvas
     */
    private static Paint mPaintCover;

    /**
     * Bitmap for shader.
     */
    private Bitmap mBitmapCover;

    /**
     * Shader for make drawable circle
     */
    private BitmapShader mShader;

    /**
     * Scale image to view width/height
     */
    private float mCoverScale;

    /**
     * Image Height and Width values.
     */
    private int mHeight;
    private int mWidth;

    /**
     * Center values for cover image.
     */
    private float mCenterX;
    private float mCenterY;

    /**
     * Cover image is rotating. That is why we hold that value.
     */
    private int mRotateDegrees;

    /**
     * Handler for posting runnable object
     */
    private Handler mHandlerRotate;

    /**
     * Runnable for turning image (default velocity is 10)
     */
    private final Runnable mRunnableRotate = new Runnable() {
        @Override
        public void run() {
            if (isRotating) {
                updateCoverRotate();
                mHandlerRotate.postDelayed(mRunnableRotate, ROTATE_DELAY);
            }
        }
    };

    /**
     * Handler for posting runnable object
     */
    private Handler mHandlerProgress;

    /**
     * Runnable for turning image (default velocity is 10)
     */
    private Runnable mRunnableProgress = new Runnable() {
        @Override
        public void run() {
            if (isRotating) {
                mHandlerProgress.postDelayed(mRunnableProgress, PROGRESS_SECOND_MS);
            }
        }
    };

    /**
     * isRotating
     */
    private boolean isRotating;

    /**
     * Handler will post runnable object every @ROTATE_DELAY seconds.
     */
    private static int ROTATE_DELAY = 25;

    /**
     * 1 sn = 1000 ms
     */
    private static int PROGRESS_SECOND_MS = 1000;

    /**
     * mRotateDegrees count increase 1 by 1 default.
     * I used that parameter as velocity.
     */
    private static int VELOCITY = 1;

    /**
     * Default color code for cover
     */
    private int mCoverColor = Color.GRAY;


    /**
     * Animator set for play pause toggle
     */
    private AnimatorSet mAnimatorSet;

    private boolean mFirstDraw = true;

    /**
     * Constructor
     */
    public WMPlayerView(Context context) {
        super(context);
        init(context, null);
    }

    /**
     * Constructor
     */
    public WMPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * Constructor
     */
    public WMPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * Constructor
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WMPlayerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    /**
     * Initializes resource values, create objects which we need them later.
     * Object creation must not called onDraw() method, otherwise it won't be
     * smooth.
     */
    private void init(Context context, AttributeSet attrs) {

        setWillNotDraw(false);


        //Get Image resource from xml
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.playerview);
        Drawable mDrawableCover = a.getDrawable(R.styleable.playerview_cover);
        if (mDrawableCover != null) mBitmapCover = drawableToBitmap(mDrawableCover);

        a.recycle();

        mRotateDegrees = 0;

        //Handler and Runnable object for turn cover image by updating rotation degrees
        mHandlerRotate = new Handler();


    }

    /**
     * Calculate mWidth, mHeight, mCenterX, mCenterY values and
     * scale resource bitmap. Create shader. This is not called multiple times.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);

        int minSide = Math.min(mWidth, mHeight);
        mWidth = minSide;
        mHeight = minSide;

        this.setMeasuredDimension(mWidth, mHeight);

        mCenterX = mWidth / 2f;
        mCenterY = mHeight / 2f;

        createShader();

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * This is where magic happens as you know.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mShader == null) return;

        //Draw cover image
        float radius = mCenterX <= mCenterY ? mCenterX - 25.0f : mCenterY - 25.0f;
        canvas.rotate(mRotateDegrees, mCenterX, mCenterY);
        canvas.drawCircle(mCenterX, mCenterY, radius, mPaintCover);

        //Rotate back to make play/pause button stable(No turn)
        canvas.rotate(-mRotateDegrees, mCenterX, mCenterY);

        if (mFirstDraw) {
            mFirstDraw = false;
        }

    }

    /**
     * We need to convert drawable (which we get from attributes) to bitmap
     * to prepare if for BitmapShader
     */
    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    /**
     * Create shader and set shader to mPaintCover
     */
    private void createShader() {

        if (mWidth == 0) return;

        //if mBitmapCover is null then create default colored cover
        if (mBitmapCover == null) {
            mBitmapCover = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
            mBitmapCover.eraseColor(mCoverColor);
        }

        mCoverScale = ((float) mWidth) / (float) mBitmapCover.getWidth();

        mBitmapCover =
                Bitmap.createScaledBitmap(mBitmapCover, (int) (mBitmapCover.getWidth() * mCoverScale),
                        (int) (mBitmapCover.getHeight() * mCoverScale), true);

        mShader = new BitmapShader(mBitmapCover, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mPaintCover = new Paint();
        mPaintCover.setAntiAlias(true);
        mPaintCover.setShader(mShader);
    }

    /**
     * Update rotate degree of cover and invalide onDraw();
     */
    public void updateCoverRotate() {
        mRotateDegrees += VELOCITY;
        mRotateDegrees = mRotateDegrees % 360;
        postInvalidate();
    }

    /**
     * Checks is rotating
     */
    public boolean isRotating() {
        return isRotating;
    }

    /**
     * Start turning image
     */
    public void start() {

        isRotating = true;
        mHandlerRotate.removeCallbacksAndMessages(null);
        mHandlerRotate.postDelayed(mRunnableRotate, ROTATE_DELAY);
        postInvalidate();
    }

    /**
     * Stop turning image
     */
    public void stop() {
        isRotating = false;
        postInvalidate();
    }

    /**
     * Set velocity.When updateCoverRotate() method called,
     * increase degree by velocity value.
     */
    public void setVelocity(int velocity) {
        if (velocity > 0) VELOCITY = velocity;
    }

    /**
     * set cover image resource
     */
    public void setCoverDrawable(int coverDrawable) {
        Drawable drawable = getContext().getResources().getDrawable(coverDrawable);
        mBitmapCover = drawableToBitmap(drawable);
        createShader();
        postInvalidate();
    }

    /**
     * sets cover image
     *
     * @param drawable
     */
    public void setCoverDrawable(Drawable drawable) {
        mBitmapCover = drawableToBitmap(drawable);
        createShader();
        postInvalidate();
    }

    /**
     * gets image URL and load it to cover image.It uses Picasso Library.
     */
    public void setCoverURL(String imageUrl) {
        try {
            mBitmapCover = Picasso.with(getContext())
                    .load(imageUrl).get();
        } catch (IllegalArgumentException | IOException e) {
            mBitmapCover = Utils.INSTANCE.convertDrawableToBitmap(
                    getResources().getDrawable(R.drawable.ic_music));
        }

        createShader();
        postInvalidate();
    }

    /**
     * This is detect when mButtonRegion is clicked. Which means
     * play/pause action happened.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                return true;
            }
            case MotionEvent.ACTION_UP: {

            }
            break;

            default:
                break;
        }

        return super.onTouchEvent(event);
    }

    /**
     * onClickListener.onClick will be called when button clicked.
     * We dont want all view click. We only want button area click.
     * That is why we override it.
     */
    @Override
    public void setOnClickListener(OnClickListener l) {
        onClickListener = l;
    }

    /**
     * Resize bitmap with @newHeight and @newWidth parameters
     */
    private Bitmap getResizedBitmap(Bitmap bm, float newHeight, float newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }
}
