package com.scwang.smart.refresh.header;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;

import com.scwang.smart.drawable.PathsDrawable;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshKernel;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.simple.SimpleComponent;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;
import com.scwang.smart.refresh.layout.util.SmartUtil;
import com.scwang.smartrefresh.header.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Taurus
 * Created by scwang on 2017/5/31.
 * from <a href="https://github.com/Yalantis/Taurus">...</a>
 */
public class TaurusHeader extends SimpleComponent implements RefreshHeader {

    //<editor-fold desc="static">
    protected static String[] airplanePaths = new String[]{
            "m23 81c0 0 0 -1 0 -1 0 -0.5 0 -1 1.5 -1 2 -1 2.6 -2 2 -2.5 -0.5 -1 -2 -1 -11.6 -2.5 -5 -1 -10 -1 -11 -1.5l-1 0 1 -1c1 -1 1 -1 2 -1 0.6 0 6 0 13 1 6 0 12 1 12.6 0.6l1 0 -1 -2C30 67 16 42 15 40.6l-0.5 -1 4 -1c2 -0.6 4 -1 4 -1 0 0 6 4 13 8.5 14.6 10 17 11 20 12 4.6 2 6 1.6 13 -0.6 13 -5 25 -9 26 -9 0.6 0 3.6 1 -24 -14L51 23 47 16 43 10 43.6 9c1 -1 1 -1 1 -0.5 0 0 4 3 7.5 6 4 3 7 6 7.5 6 0 0 13.6 3 29.5 6 16 3 32 6 35 7l6 1 3 -1c41.6 -14.6 68 -23 85 -28 15 -4 24 -5 32 -2.5 7 2 10 5 8 8 -1.6 2.5 -4.6 4.6 -10.6 7.5 -6 3 -10 4 -25 9 -8 2.6 -16.6 6 -39 14 -67 25 -88 31 -121.6 36 -14.5 2 -24 3 -34 3 -5 0 -5.5 0 -6 -0.5z"
    };
    protected static int[] airplaneColors = new int[]{
            0xffffffff
    };

    protected static String[] cloudPaths = new String[]{
            "M552 1A65 65 0 0 0 504 22A51 51 0 0 0 492 20A51 51 0 0 0 442 71A51 51 0 0 0 492 121A51 51 0 0 0 511 118A65 65 0 0 0 517 122L586 122A65 65 0 0 0 600 111A60 60 0 0 0 608 122L696 122A60 60 0 0 0 712 82A60 60 0 0 0 652 22A60 60 0 0 0 611 39A65 65 0 0 0 552 1zM246 2A55 55 0 0 0 195 37A47 47 0 0 0 168 28A47 47 0 0 0 121 75A47 47 0 0 0 168 121A47 47 0 0 0 209 97A55 55 0 0 0 246 111A55 55 0 0 0 269 107A39 39 0 0 0 281 122L328 122A39 39 0 0 0 343 91A39 39 0 0 0 304 52A39 39 0 0 0 301 52A55 55 0 0 0 246 2z",
            "m507 31a53 53 0 0 0 -53 53 53 53 0 0 0 16 38h75a53 53 0 0 0 2 -2 28 28 0 0 0 1 2h213a97 97 0 0 0 -87 -54.8 97 97 0 0 0 -73 34 28 28 0 0 0 -27 -19 28 28 0 0 0 -13 3 53 53 0 0 0 0 -1 53 53 0 0 0 -53 -53zM206 32a54 54 0 0 0 -50 34 74.9 74.9 0 0 0 -47 -17 74.9 74.9 0 0 0 -74 61 31 31 0 0 0 -10 -2 31 31 0 0 0 -26 14L301 122a38 38 0 0 0 0 -4 38 38 0 0 0 -38 -38 38 38 0 0 0 -4 0 54 54 0 0 0 -54 -49z",
            "m424 37a53 53 0 0 0 -41 19 53 53 0 0 0 -1 2 63 63 0 0 0 -5 0 63 63 0 0 0 -61 50 63 63 0 0 0 -1 4 16 16 0 0 0 -10 -4 16 16 0 0 0 -8 2 21 21 0 0 0 -18 -11 21 21 0 0 0 -19 13 22 22 0 0 0 -7 -1 22 22 0 0 0 -19 11L523 122a44 44 0 0 0 -43 -37 44 44 0 0 0 -3 0 53 53 0 0 0 -53 -48zM129 38a50 50 0 0 0 -50 50 50 50 0 0 0 2 15 15 16 0 0 0 -6 2 15 16 0 0 0 -1 1 17 16 0 0 0 -12 -5 17 16 0 0 0 -16 14 20 16 0 0 0 -15 7L224 122a43 43 0 0 0 1 -10 43 43 0 0 0 -43 -43 43 43 0 0 0 -7 1 50 50 0 0 0 -47 -32zM632 83a64 64 0 0 0 -45 18 27 27 0 0 0 -11 -2 27 27 0 0 0 -23 13 17 17 0 0 0 -7 -1 17 17 0 0 0 -16 12h160a64 64 0 0 0 -59 -39z",
    };
    protected static int[] cloudColors = new int[]{
            0xaac7dcf1,0xdde8f3fd,0xfffdfdfd
    };
    //</editor-fold>

    //<editor-fold desc="Field">
    protected static final float SCALE_START_PERCENT = 0.5f;
    protected static final int ANIMATION_DURATION = 1000;

    protected static final float SIDE_CLOUDS_INITIAL_SCALE = 0.6f;//1.05f;
    protected static final float SIDE_CLOUDS_FINAL_SCALE = 1f;//1.55f;

    protected static final float CENTER_CLOUDS_INITIAL_SCALE = 0.8f;//0.8f;
    protected static final float CENTER_CLOUDS_FINAL_SCALE = 1f;//1.30f;

    protected static final Interpolator ACCELERATE_DECELERATE_INTERPOLATOR = new AccelerateDecelerateInterpolator();

    // Multiply with this animation interpolator time
    protected static final int LOADING_ANIMATION_COEFFICIENT = 80;
    protected static final int SLOW_DOWN_ANIMATION_COEFFICIENT = 6;
    // Amount of lines when is going lading animation
    protected static final int WIND_SET_AMOUNT = 10;
    protected static final int Y_SIDE_CLOUDS_SLOW_DOWN_COF = 4;
    protected static final int X_SIDE_CLOUDS_SLOW_DOWN_COF = 2;
    protected static final int MIN_WIND_LINE_WIDTH = 50;
    protected static final int MAX_WIND_LINE_WIDTH = 300;
    protected static final int MIN_WIND_X_OFFSET = 1000;
    protected static final int MAX_WIND_X_OFFSET = 2000;
    protected static final int RANDOM_Y_COEFFICIENT = 5;

    protected Drawable mAirplane;
    protected Drawable mCloudCenter;
    protected Matrix mMatrix;
    protected float mPercent;
    protected int mHeight;
    protected int mHeaderHeight;
    protected Animation mAnimation;

    protected boolean isRefreshing = false;
    protected float mLoadingAnimationTime;
    protected float mLastAnimationTime;

    protected Random mRandom;
//    protected boolean mEndOfRefreshing;

    //KEY: Y position, Value: X offset of wind
    protected Map<Float, Float> mWinds;
    protected Paint mWindPaint;
    protected float mWindLineWidth;
    protected boolean mNewWindSet;
    protected boolean mInverseDirection;
    protected float mFinishTransformation;
    protected int mBackgroundColor;
    protected RefreshKernel mKernel;

    protected enum AnimationPart {
        FIRST,
        SECOND,
        THIRD,
        FOURTH
    }
    //</editor-fold>

    //<editor-fold desc="View">
    public TaurusHeader(Context context) {
        this(context, null);
    }

    public TaurusHeader(Context context, AttributeSet attrs) {
        super(context, attrs, 0);

        final View thisView = this;

        thisView.setMinimumHeight(SmartUtil.dp2px(100));

        mMatrix = new Matrix();
        mWinds = new HashMap<>();
        mRandom = new Random();

        mWindPaint = new Paint();
        mWindPaint.setColor(0xffffffff);
        mWindPaint.setStrokeWidth(SmartUtil.dp2px(3));
        mWindPaint.setAlpha(50);

        mSpinnerStyle = SpinnerStyle.FixedBehind;

        //<editor-fold desc="setupAnimations">
        mAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, @NonNull Transformation t) {
                /*SLOW DOWN ANIMATION IN {@link #SLOW_DOWN_ANIMATION_COEFFICIENT} time */
                mLoadingAnimationTime = LOADING_ANIMATION_COEFFICIENT * (interpolatedTime / SLOW_DOWN_ANIMATION_COEFFICIENT);
                thisView.invalidate();
            }
        };
        mAnimation.setRepeatCount(Animation.INFINITE);
        mAnimation.setRepeatMode(Animation.REVERSE);
        mAnimation.setInterpolator(ACCELERATE_DECELERATE_INTERPOLATOR);
        mAnimation.setDuration(ANIMATION_DURATION);
    //</editor-fold>

        //<editor-fold desc="setupPathDrawable">
        PathsDrawable airplane = new PathsDrawable();
        if (!airplane.parserPaths(airplanePaths)) {
            airplane.declareOriginal(3, 3, 257, 79);
        }
//        airplane.printOriginal("airplane");
        airplane.parserColors(airplaneColors);

        PathsDrawable cloudCenter = new PathsDrawable();
        if(!cloudCenter.parserPaths(cloudPaths)) {
            cloudCenter.declareOriginal(-1, 1, 761, 121);
        }
//        cloudCenter.printOriginal("cloudCenter");
        cloudCenter.parserColors(cloudColors);

        mAirplane = airplane;
        mCloudCenter = cloudCenter;
        mAirplane.setBounds(0, 0, SmartUtil.dp2px(65), SmartUtil.dp2px(20));
        mCloudCenter.setBounds(0, 0, SmartUtil.dp2px(260), SmartUtil.dp2px(45));
    //</editor-fold>

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TaurusHeader);

        int primaryColor = ta.getColor(R.styleable.TaurusHeader_thPrimaryColor, 0);
        if (primaryColor != 0) {
            mBackgroundColor = primaryColor;
//            thisView.setBackgroundColor(primaryColor);
        } else {
            mBackgroundColor = 0xff11bbff;
//            thisView.setBackgroundColor(0xff11bbff);
        }

        ta.recycle();

    }
    //</editor-fold>

    //<editor-fold desc="RefreshHeader">
    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {
        mKernel = kernel;
        kernel.requestDrawBackgroundFor(this, mBackgroundColor);
    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
        mHeight = offset;
        mPercent = percent;
        mHeaderHeight = height;
        if (isDragging) {
            mFinishTransformation = 0;
        }
        this.invalidate();
    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout layout, int height, int maxDragHeight) {
        isRefreshing = true;
        mFinishTransformation = 0;
        final View thisView = this;
        thisView.startAnimation(mAnimation);
    }

    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        final View thisView = this;
        thisView.clearAnimation();
        if (success) {
            thisView.startAnimation(new Animation() {{
                super.setDuration(100);
                super.setInterpolator(new AccelerateInterpolator());
            }
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    if (interpolatedTime == 1) {
                        isRefreshing = false;
                    }
                    mFinishTransformation = interpolatedTime;
                    thisView.invalidate();
                }
            });
            return 200;
        } else {
            isRefreshing = false;
            return 0;
        }
    }

    /**
     * @param colors 对应Xml中配置的 srlPrimaryColor srlAccentColor
     * @deprecated 只由框架调用
     * 使用者使用 {@link RefreshLayout#setPrimaryColorsId(int...)}
     */
    @Override
    @Deprecated
    public void setPrimaryColors(@ColorInt int ... colors) {
//        final View thisView = this;
//        thisView.setBackgroundColor(colors[0]);
        mBackgroundColor = colors[0];
        if (mKernel != null) {
            mKernel.requestDrawBackgroundFor(this, mBackgroundColor);
        }
    }
    //</editor-fold>

    //<editor-fold desc="draw">
    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) {

        final View thisView = this;
        final int width = thisView.getWidth();
        final int height = mHeight;//thisView.getHeight();
        //noinspection EqualsBetweenInconvertibleTypes
        final boolean footer = mKernel != null && (this.equals(mKernel.getRefreshLayout().getRefreshFooter()));

        if (footer) {
            canvas.save();
            canvas.translate(0, thisView.getHeight() - mHeight);
        }

        drawWinds(canvas, width);
        drawAirplane(canvas, width, height);
        drawSideClouds(canvas, width, height);
        drawCenterClouds(canvas, width, height);

        if (footer) {
            canvas.restore();
        }

        super.dispatchDraw(canvas);
    }

    protected void drawWinds(Canvas canvas, int width) {
        if (isRefreshing) {
            // Set up new set of wind
            while (mWinds.size() < WIND_SET_AMOUNT) {
                float y = (float) (mHeaderHeight / (Math.random() * RANDOM_Y_COEFFICIENT));
                float x = random(MIN_WIND_X_OFFSET, MAX_WIND_X_OFFSET);

                // Magic with checking interval between winds
                if (mWinds.size() > 1) {
                    y = 0;
                    while (y == 0) {
                        float tmp = (float) (mHeaderHeight / (Math.random() * RANDOM_Y_COEFFICIENT));

                        for (Map.Entry<Float, Float> wind : mWinds.entrySet()) {
                            // We want that interval will be greater than fifth part of draggable distance
                            if (Math.abs(wind.getKey() - tmp) > 1f * mHeaderHeight / RANDOM_Y_COEFFICIENT) {
                                y = tmp;
                            } else {
                                y = 0;
                                break;
                            }
                        }
                    }
                }

                mWinds.put(y, x);
                drawWind(canvas, y, x, width);
            }

            // Draw current set of wind
            //noinspection ConstantConditions
            if (mWinds.size() >= WIND_SET_AMOUNT) {
                for (Map.Entry<Float, Float> wind : mWinds.entrySet()) {
                    drawWind(canvas, wind.getKey(), wind.getValue(), width);
                }
            }

            // We should to create new set of winds
            if (mInverseDirection && mNewWindSet) {
                mWinds.clear();
                mNewWindSet = false;
                mWindLineWidth = random(MIN_WIND_LINE_WIDTH, MAX_WIND_LINE_WIDTH);
            }

            // needed for checking direction
            mLastAnimationTime = mLoadingAnimationTime;
        }
    }

    /**
     * Draw wind on loading animation
     *
     * @param canvas  - area where we will draw
     * @param y       - y position fot one of lines
     * @param xOffset - x offset for on of lines
     */
    protected void drawWind(Canvas canvas, float y, float xOffset, int width) {
        /* We should multiply current animation time with this coefficient for taking all screen width in time
        Removing slowing of animation with dividing on {@LINK #SLOW_DOWN_ANIMATION_COEFFICIENT}
        And we should don't forget about distance that should "fly" line that depend on screen of device and x offset
        */
        float cof = (width + xOffset) / (1f * LOADING_ANIMATION_COEFFICIENT / SLOW_DOWN_ANIMATION_COEFFICIENT);
        float time = mLoadingAnimationTime;

        // HORRIBLE HACK FOR REVERS ANIMATION THAT SHOULD WORK LIKE RESTART ANIMATION
        if (mLastAnimationTime - mLoadingAnimationTime > 0) {
            mInverseDirection = true;
            // take time from 0 to end of animation time
            time = (1f * LOADING_ANIMATION_COEFFICIENT / SLOW_DOWN_ANIMATION_COEFFICIENT) - mLoadingAnimationTime;
        } else {
            mNewWindSet = true;
            mInverseDirection = false;
        }

        // Taking current x position of drawing wind
        // For fully disappearing of line we should subtract wind line width
        float x = (width - (time * cof)) + xOffset - mWindLineWidth;
        float xEnd = x + mWindLineWidth;

        canvas.drawLine(x, y, xEnd, y, mWindPaint);
    }

    protected void drawSideClouds(Canvas canvas, int width, int height) {
        Matrix matrix = mMatrix;
        matrix.reset();

        Drawable mCloudLeft = mCloudCenter;
        Drawable mCloudRight = mCloudCenter;

        // Drag percent will newer get more then 1 here
        float dragPercent = Math.min(1f, Math.abs(mPercent));

        final View thisView = this;
        if (thisView.isInEditMode()) {
            dragPercent = 1;
            mHeaderHeight = height;
        }

        float scale;
        float scalePercentDelta = dragPercent - SCALE_START_PERCENT;
        if (scalePercentDelta > 0) {
            float scalePercent = scalePercentDelta / (1.0f - SCALE_START_PERCENT);
            scale = SIDE_CLOUDS_INITIAL_SCALE + (SIDE_CLOUDS_FINAL_SCALE - SIDE_CLOUDS_INITIAL_SCALE) * scalePercent;
        } else {
            scale = SIDE_CLOUDS_INITIAL_SCALE;
        }

        // Current y position of clouds
        float dragYOffset = mHeaderHeight * (1.0f - dragPercent);

        // Position where clouds fully visible on screen and we should drag them with content of listView
//        int cloudsVisiblePosition = mHeaderHeight / 2 - mCloudCenter.height() / 2;

//        boolean needMoveCloudsWithContent = false;
//        if (dragYOffset < cloudsVisiblePosition) {
//            needMoveCloudsWithContent = true;
//        }

        float offsetLeftX = 0 - mCloudLeft.getBounds().width() / 2f;
        float offsetLeftY = (//needMoveCloudsWithContent
                //? mHeaderHeight * dragPercent - mCloudLeftgetBounds().height() :
                dragYOffset);

        float offsetRightX = width - mCloudRight.getBounds().width() / 2f;
        float offsetRightY = (//needMoveCloudsWithContent
                //? mHeaderHeight * dragPercent - mCloudRightgetBounds().height() :
                dragYOffset);

        // Magic with animation on loading process
        if (isRefreshing) {
            if (checkCurrentAnimationPart(AnimationPart.FIRST)) {
                offsetLeftX -= 2*getAnimationPartValue(AnimationPart.FIRST) / Y_SIDE_CLOUDS_SLOW_DOWN_COF;
                offsetRightX += getAnimationPartValue(AnimationPart.FIRST) / X_SIDE_CLOUDS_SLOW_DOWN_COF;
            } else if (checkCurrentAnimationPart(AnimationPart.SECOND)) {
                offsetLeftX -= 2*getAnimationPartValue(AnimationPart.SECOND) / Y_SIDE_CLOUDS_SLOW_DOWN_COF;
                offsetRightX += getAnimationPartValue(AnimationPart.SECOND) / X_SIDE_CLOUDS_SLOW_DOWN_COF;
            } else if (checkCurrentAnimationPart(AnimationPart.THIRD)) {
                offsetLeftX -= getAnimationPartValue(AnimationPart.THIRD) / Y_SIDE_CLOUDS_SLOW_DOWN_COF;
                offsetRightX += 2*getAnimationPartValue(AnimationPart.THIRD) / X_SIDE_CLOUDS_SLOW_DOWN_COF;
            } else if (checkCurrentAnimationPart(AnimationPart.FOURTH)) {
                offsetLeftX -= getAnimationPartValue(AnimationPart.FOURTH) / X_SIDE_CLOUDS_SLOW_DOWN_COF;
                offsetRightX += 2*getAnimationPartValue(AnimationPart.FOURTH) / Y_SIDE_CLOUDS_SLOW_DOWN_COF;
            }
        }

        if (offsetLeftY + scale * mCloudLeft.getBounds().height() < height + 2) {
            offsetLeftY = height + 2 - scale * mCloudLeft.getBounds().height();
        }
        if (offsetRightY + scale * mCloudRight.getBounds().height() < height + 2) {
            offsetRightY = height + 2 - scale * mCloudRight.getBounds().height();
        }

        final int saveCount = canvas.getSaveCount();
        canvas.save();
        canvas.translate(offsetLeftX, offsetLeftY);
        matrix.postScale(scale, scale, mCloudLeft.getBounds().width() * 3 / 4f, mCloudLeft.getBounds().height());
        canvas.concat(matrix);
        mCloudLeft.setAlpha(100);
        mCloudLeft.draw(canvas);
        mCloudLeft.setAlpha(255);
        canvas.restoreToCount(saveCount);
        canvas.save();
        canvas.translate(offsetRightX, offsetRightY);
        matrix.postScale(scale, scale, 0, mCloudRight.getBounds().height());
        canvas.concat(matrix);
        mCloudRight.setAlpha(100);
        mCloudRight.draw(canvas);
        mCloudRight.setAlpha(255);
        canvas.restoreToCount(saveCount);
    }

    protected void drawCenterClouds(Canvas canvas, int width, int height) {
        Matrix matrix = mMatrix;
        matrix.reset();
        float dragPercent = Math.min(1f, Math.abs(mPercent));

        final View thisView = this;
        if (thisView.isInEditMode()) {
            dragPercent = 1;
            mHeaderHeight = height;
        }

        float scale;
        float overDragPercent = 0;
        boolean overDrag = false;

        if (mPercent > 1.0f) {
            overDrag = true;
            // Here we want know about how mach percent of over drag we done
            overDragPercent = Math.abs(1.0f - mPercent);
        }

        float scalePercentDelta = dragPercent - SCALE_START_PERCENT;
        if (scalePercentDelta > 0) {
            float scalePercent = scalePercentDelta / (1.0f - SCALE_START_PERCENT);
            scale = CENTER_CLOUDS_INITIAL_SCALE + (CENTER_CLOUDS_FINAL_SCALE - CENTER_CLOUDS_INITIAL_SCALE) * scalePercent;
        } else {
            scale = CENTER_CLOUDS_INITIAL_SCALE;
        }

        float parallaxPercent = 0;
        boolean parallax = false;
        // Current y position of clouds
        float dragYOffset = mHeaderHeight * dragPercent;
        // Position when should start parallax scrolling
        int startParallaxHeight = mHeaderHeight - mCloudCenter.getBounds().height()/2;

        if (dragYOffset > startParallaxHeight) {
            parallax = true;
            parallaxPercent = dragYOffset - startParallaxHeight;
        }

        float offsetX = (width / 2f) - mCloudCenter.getBounds().width() / 2f;
        float offsetY = dragYOffset
                - (parallax ? mCloudCenter.getBounds().height()/2f + parallaxPercent : mCloudCenter.getBounds().height()/2f);

        float sx = overDrag ? scale + overDragPercent / 4 : scale;
        float sy = overDrag ? scale + overDragPercent / 2 : scale;

        if (isRefreshing && !overDrag) {
            if (checkCurrentAnimationPart(AnimationPart.FIRST)) {
                sx = scale - (getAnimationPartValue(AnimationPart.FIRST) / LOADING_ANIMATION_COEFFICIENT) / 8;
            } else if (checkCurrentAnimationPart(AnimationPart.SECOND)) {
                sx = scale - (getAnimationPartValue(AnimationPart.SECOND) / LOADING_ANIMATION_COEFFICIENT) / 8;
            } else if (checkCurrentAnimationPart(AnimationPart.THIRD)) {
                sx = scale + (getAnimationPartValue(AnimationPart.THIRD) / LOADING_ANIMATION_COEFFICIENT) / 6;
            } else if (checkCurrentAnimationPart(AnimationPart.FOURTH)) {
                sx = scale + (getAnimationPartValue(AnimationPart.FOURTH) / LOADING_ANIMATION_COEFFICIENT) / 6;
            }
            sy = sx;
        }


        matrix.postScale(sx, sy, mCloudCenter.getBounds().width() / 2f, 0);

        if (offsetY + sy * mCloudCenter.getBounds().height() < height + 2) {
            offsetY = height + 2 - sy * mCloudCenter.getBounds().height();
        }

        final int saveCount = canvas.getSaveCount();
        canvas.save();
        canvas.translate(offsetX, offsetY);
        canvas.concat(matrix);
        mCloudCenter.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    protected void drawAirplane(Canvas canvas, int width, int height) {
        Matrix matrix = mMatrix;
        matrix.reset();

        float dragPercent = mPercent;
        float rotateAngle = 0;

        final View thisView = this;
        if (thisView.isInEditMode()) {
            dragPercent = 1;
            mHeaderHeight = height;
        }

        // Check overDrag
        if (dragPercent > 1.0f) {
            rotateAngle = 20 * (float) (1 - Math.pow(100, -(dragPercent - 1) / 2));
            dragPercent = 1.0f;
        }

        float offsetX = ((width * dragPercent) / 2) - mAirplane.getBounds().width() / 2f;
        float offsetY = mHeaderHeight * (1 - dragPercent/2) - mAirplane.getBounds().height() / 2f;

        if (mFinishTransformation > 0) {
            offsetY += (0 - offsetY) * mFinishTransformation;
            offsetX += (width + mAirplane.getBounds().width() - offsetX) * mFinishTransformation;
        }

        if (isRefreshing) {
            if (checkCurrentAnimationPart(AnimationPart.FIRST)) {
                offsetY -= getAnimationPartValue(AnimationPart.FIRST);
            } else if (checkCurrentAnimationPart(AnimationPart.SECOND)) {
                offsetY -= getAnimationPartValue(AnimationPart.SECOND);
            } else if (checkCurrentAnimationPart(AnimationPart.THIRD)) {
                offsetY += getAnimationPartValue(AnimationPart.THIRD);
            } else if (checkCurrentAnimationPart(AnimationPart.FOURTH)) {
                offsetY += getAnimationPartValue(AnimationPart.FOURTH);
            }
        }

        if (rotateAngle > 0) {
            matrix.postRotate(rotateAngle,
                    mAirplane.getBounds().width() / 2f,
                    mAirplane.getBounds().height() / 2f);
        }

        final int saveCount = canvas.getSaveCount();
        canvas.save();
        canvas.translate(offsetX, offsetY);
        canvas.concat(matrix);
        mAirplane.draw(canvas);
        canvas.restoreToCount(saveCount);
    }
    //</editor-fold>

    //<editor-fold desc="protected">
    protected float random(int min, int max) {
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        return mRandom.nextInt((max - min) + 1) + min;
    }

    /**
     * We need a special value for different part of animation
     *
     * @param part - needed part
     * @return - value for needed part
     */
    protected float getAnimationPartValue(AnimationPart part) {
        switch (part) {
            case FIRST: {
                return mLoadingAnimationTime;
            }
            case SECOND: {
                return getAnimationTimePart(AnimationPart.FOURTH) - (mLoadingAnimationTime - getAnimationTimePart(AnimationPart.FOURTH));
            }
            case THIRD: {
                return mLoadingAnimationTime - getAnimationTimePart(AnimationPart.SECOND);
            }
            case FOURTH: {
                return getAnimationTimePart(AnimationPart.THIRD) - (mLoadingAnimationTime - getAnimationTimePart(AnimationPart.FOURTH));
            }
            default:
                return 0;
        }
    }

    /**
     * On drawing we should check current part of animation
     *
     * @param part - needed part of animation
     * @return - return true if current part
     */
    protected boolean checkCurrentAnimationPart(AnimationPart part) {
        switch (part) {
            case FIRST: {
                return mLoadingAnimationTime < getAnimationTimePart(AnimationPart.FOURTH);
            }
            case SECOND:
            case THIRD: {
                return mLoadingAnimationTime < getAnimationTimePart(part);
            }
            case FOURTH: {
                return mLoadingAnimationTime > getAnimationTimePart(AnimationPart.THIRD);
            }
            default:
                return false;
        }
    }

    /**
     * Get part of animation duration
     *
     * @param part - needed part of time
     * @return - interval of time
     */
    protected int getAnimationTimePart(AnimationPart part) {
        switch (part) {
            case SECOND: {
                return LOADING_ANIMATION_COEFFICIENT / 2;
            }
            case THIRD: {
                return getAnimationTimePart(AnimationPart.FOURTH) * 3;
            }
            case FOURTH: {
                return LOADING_ANIMATION_COEFFICIENT / 4;
            }
            default:
                return 0;
        }
    }
    //</editor-fold>
}
