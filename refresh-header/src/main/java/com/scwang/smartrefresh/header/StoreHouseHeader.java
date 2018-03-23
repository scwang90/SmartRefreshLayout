package com.scwang.smartrefresh.header;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.scwang.smartrefresh.header.storehouse.StoreHouseBarItem;
import com.scwang.smartrefresh.header.storehouse.StoreHousePath;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.internal.InternalAbstract;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * StoreHouseHeader
 * Created by SCWANG on 2017/5/31.
 * from https://github.com/liaohuqiu/android-Ultra-Pull-To-Refresh
 */
@SuppressWarnings({"unused", "UnusedReturnValue", "SameParameterValue"})
public class StoreHouseHeader extends InternalAbstract implements RefreshHeader {

    //<editor-fold desc="Field">
    public List<StoreHouseBarItem> mItemList = new ArrayList<>();

    protected int mLineWidth = -1;
    protected float mScale = 1;
    protected int mDropHeight = -1;
    protected static final float mInternalAnimationFactor = 0.7f;
    protected int mHorizontalRandomness = -1;

    protected float mProgress = 0;

    protected int mDrawZoneWidth = 0;
    protected int mDrawZoneHeight = 0;
    protected int mOffsetX = 0;
    protected int mOffsetY = 0;
    protected static final float mBarDarkAlpha = 0.4f;
    protected static final float mFromAlpha = 1.0f;
    protected static final float mToAlpha = 0.4f;

    protected int mLoadingAniDuration = 1000;
    protected int mLoadingAniSegDuration = 1000;
    protected static final int mLoadingAniItemDuration = 400;

    protected int mTextColor = Color.WHITE;
    protected int mBackgroundColor = 0;
    protected boolean mIsInLoading = false;
    protected boolean mEnableFadeAnimation = false;
    protected Matrix mMatrix = new Matrix();
    protected RefreshKernel mRefreshKernel;
    protected AniController mAniController = new AniController();
    protected Transformation mTransformation = new Transformation();
    //</editor-fold>

    //<editor-fold desc="View">
    public StoreHouseHeader(Context context) {
        this(context, null);
    }

    public StoreHouseHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StoreHouseHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        DensityUtil density = new DensityUtil();
        mLineWidth = density.dip2px(1);
        mDropHeight = density.dip2px(40);
        mHorizontalRandomness = Resources.getSystem().getDisplayMetrics().widthPixels / 2;
        mBackgroundColor = 0xff333333;
        setTextColor(0xffcccccc);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.StoreHouseHeader);
        mLineWidth = ta.getDimensionPixelOffset(R.styleable.StoreHouseHeader_shhLineWidth, mLineWidth);
        mDropHeight = ta.getDimensionPixelOffset(R.styleable.StoreHouseHeader_shhDropHeight, mDropHeight);
        mEnableFadeAnimation = ta.getBoolean(R.styleable.StoreHouseHeader_shhEnableFadeAnimation, mEnableFadeAnimation);
        if (ta.hasValue(R.styleable.StoreHouseHeader_shhText)) {
            initWithString(ta.getString(R.styleable.StoreHouseHeader_shhText));
        } else {
            initWithString("StoreHouse");
        }
        ta.recycle();

        final View thisView = this;
        thisView.setMinimumHeight(mDrawZoneHeight + DensityUtil.dp2px(40));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final View thisView = this;
//        int height = getTopOffset() + mDrawZoneHeight + getBottomOffset();
//        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.setMeasuredDimension(
                View.resolveSize(super.getSuggestedMinimumWidth(), widthMeasureSpec),
                View.resolveSize(super.getSuggestedMinimumHeight(), heightMeasureSpec));

        mOffsetX = (thisView.getMeasuredWidth() - mDrawZoneWidth) / 2;
        mOffsetY = (thisView.getMeasuredHeight() - mDrawZoneHeight) / 2;//getTopOffset();
        mDropHeight = thisView.getMeasuredHeight() / 2;//getTopOffset();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        final View thisView = this;
        final int c1 = canvas.save();
        final int len = mItemList.size();
        final float progress = thisView.isInEditMode() ? 1 : mProgress;

        for (int i = 0; i < len; i++) {

            canvas.save();
            StoreHouseBarItem storeHouseBarItem = mItemList.get(i);
            float offsetX = mOffsetX + storeHouseBarItem.midPoint.x;
            float offsetY = mOffsetY + storeHouseBarItem.midPoint.y;

            if (mIsInLoading) {
                storeHouseBarItem.getTransformation(thisView.getDrawingTime(), mTransformation);
                canvas.translate(offsetX, offsetY);
            } else {

                if (progress == 0) {
                    storeHouseBarItem.resetPosition(mHorizontalRandomness);
                    continue;
                }

                float startPadding = (1 - mInternalAnimationFactor) * i / len;
                float endPadding = 1 - mInternalAnimationFactor - startPadding;

                // done
                if (progress == 1 || progress >= 1 - endPadding) {
                    canvas.translate(offsetX, offsetY);
                    storeHouseBarItem.setAlpha(mBarDarkAlpha);
                } else {
                    float realProgress;
                    if (progress <= startPadding) {
                        realProgress = 0;
                    } else {
                        realProgress = Math.min(1, (progress - startPadding) / mInternalAnimationFactor);
                    }
                    offsetX += storeHouseBarItem.translationX * (1 - realProgress);
                    offsetY += -mDropHeight * (1 - realProgress);
                    mMatrix.reset();
                    mMatrix.postRotate(360 * realProgress);
                    mMatrix.postScale(realProgress, realProgress);
                    mMatrix.postTranslate(offsetX, offsetY);
                    storeHouseBarItem.setAlpha(mBarDarkAlpha * realProgress);
                    canvas.concat(mMatrix);
                }
            }
            storeHouseBarItem.draw(canvas);
            canvas.restore();
        }
        if (mIsInLoading) {
            thisView.invalidate();
        }
        canvas.restoreToCount(c1);

        super.dispatchDraw(canvas);
    }

    //</editor-fold>

    //<editor-fold desc="API">

    public StoreHouseHeader setLoadingAniDuration(int duration) {
        mLoadingAniDuration = duration;
        mLoadingAniSegDuration = duration;
        return this;
    }

    public StoreHouseHeader setLineWidth(int width) {
        mLineWidth = width;
        for (int i = 0; i < mItemList.size(); i++) {
            mItemList.get(i).setLineWidth(width);
        }
        return this;
    }

    public StoreHouseHeader setTextColor(@ColorInt int color) {
        mTextColor = color;
        for (int i = 0; i < mItemList.size(); i++) {
            mItemList.get(i).setColor(color);
        }
        return this;
    }

    public StoreHouseHeader setDropHeight(int height) {
        mDropHeight = height;
        return this;
    }

    public StoreHouseHeader initWithString(String str) {
        initWithString(str, 25);
        return this;
    }

    public StoreHouseHeader initWithString(String str, int fontSize) {
        List<float[]> pointList = StoreHousePath.getPath(str, fontSize * 0.01f, 14);
        initWithPointList(pointList);
        return this;
    }

    public StoreHouseHeader initWithStringArray(int id) {
        final View thisView = this;
        String[] points = thisView.getResources().getStringArray(id);
        List<float[]> pointList = new ArrayList<>();
        for (String point : points) {
            String[] x = point.split(",");
            float[] f = new float[4];
            for (int j = 0; j < 4; j++) {
                f[j] = Float.parseFloat(x[j]);
            }
            pointList.add(f);
        }
        initWithPointList(pointList);
        return this;
    }

    public StoreHouseHeader setScale(float scale) {
        mScale = scale;
        return this;
    }

    public StoreHouseHeader initWithPointList(List<float[]> pointList) {

        float drawWidth = 0;
        float drawHeight = 0;
        boolean shouldLayout = mItemList.size() > 0;
        mItemList.clear();
        DensityUtil density = new DensityUtil();
        for (int i = 0; i < pointList.size(); i++) {
            float[] line = pointList.get(i);
            PointF startPoint = new PointF(density.dip2px(line[0]) * mScale, density.dip2px(line[1]) * mScale);
            PointF endPoint = new PointF(density.dip2px(line[2]) * mScale, density.dip2px(line[3]) * mScale);

            drawWidth = Math.max(drawWidth, startPoint.x);
            drawWidth = Math.max(drawWidth, endPoint.x);

            drawHeight = Math.max(drawHeight, startPoint.y);
            drawHeight = Math.max(drawHeight, endPoint.y);

            StoreHouseBarItem item = new StoreHouseBarItem(i, startPoint, endPoint, mTextColor, mLineWidth);
            item.resetPosition(mHorizontalRandomness);
            mItemList.add(item);
        }
        mDrawZoneWidth = (int) Math.ceil(drawWidth);
        mDrawZoneHeight = (int) Math.ceil(drawHeight);
        if (shouldLayout) {
            final View thisView = this;
            thisView.requestLayout();
        }
        return this;
    }
    //</editor-fold>

    //<editor-fold desc="RefreshHeader">

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {
//        kernel.requestDrawBackgroundForHeader(mBackgroundColor);
        mRefreshKernel = kernel;
        mRefreshKernel.requestDrawBackgroundFor(this, mBackgroundColor);
    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
        mProgress = (percent * .8f);
        final View thisView = this;
        thisView.invalidate();
    }

//    @Override
//    public void onPulling(float percent, int offset, int height, int maxDragHeight) {
//        mProgress = (percent * .8f);
//        invalidate();
//    }
//
//    @Override
//    public void onReleasing(float percent, int offset, int height, int maxDragHeight) {
//        onPulling(percent, offset, height, maxDragHeight);
//    }

    @Override
    public void onReleased(@NonNull RefreshLayout layout, int height, int maxDragHeight) {
        mIsInLoading = true;
        mAniController.start();
        final View thisView = this;
        thisView.invalidate();
    }

    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        mIsInLoading = false;
        mAniController.stop();
        if (success && mEnableFadeAnimation) {
            final View thisView = this;
            thisView.startAnimation(new Animation() {{
                super.setDuration(250);
                super.setInterpolator(new AccelerateInterpolator());
            }
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    final View thisView = StoreHouseHeader.this;
                    mProgress = (1 - interpolatedTime);
                    thisView.invalidate();
                    if (interpolatedTime == 1) {
                        for (int i = 0; i < mItemList.size(); i++) {
                            mItemList.get(i).resetPosition(mHorizontalRandomness);
                        }
                    }
                }
            });
            return 250;
        } else {
            for (int i = 0; i < mItemList.size(); i++) {
                mItemList.get(i).resetPosition(mHorizontalRandomness);
            }
        }
        return 0;
    }

    /**
     * @param colors 对应Xml中配置的 srlPrimaryColor srlAccentColor
     * @deprecated 请使用 {@link RefreshLayout#setPrimaryColorsId(int...)}
     */
    @Override@Deprecated
    public void setPrimaryColors(@ColorInt int ... colors) {
        if (colors.length > 0) {
            mBackgroundColor = colors[0];
            if (mRefreshKernel != null) {
//                mRefreshKernel.requestDrawBackgroundForHeader(colors[0]);
                mRefreshKernel.requestDrawBackgroundFor(this, mBackgroundColor);
            }
            if (colors.length > 1) {
                setTextColor(colors[1]);
            }
        }
    }

    //</editor-fold>

    private class AniController implements Runnable {

        int mTick = 0;
        int mCountPerSeg = 0;
        int mSegCount = 0;
        int mInterval = 0;
        boolean mRunning = true;

        private void start() {
            mRunning = true;
            mTick = 0;

            mInterval = mLoadingAniDuration / mItemList.size();
            mCountPerSeg = mLoadingAniSegDuration / mInterval;
            mSegCount = mItemList.size() / mCountPerSeg + 1;
            run();
        }

        @Override
        public void run() {

            int pos = mTick % mCountPerSeg;
            for (int i = 0; i < mSegCount; i++) {

                int index = i * mCountPerSeg + pos;
                if (index > mTick) {
                    continue;
                }

                index = index % mItemList.size();
                StoreHouseBarItem item = mItemList.get(index);

                item.setFillAfter(false);
                item.setFillEnabled(true);
                item.setFillBefore(false);
                item.setDuration(mLoadingAniItemDuration);
                item.start(mFromAlpha, mToAlpha);
            }

            mTick++;
            if (mRunning && mRefreshKernel != null) {
                final View refreshView = mRefreshKernel.getRefreshLayout().getLayout();
                refreshView.postDelayed(this, mInterval);
            }
        }

        private void stop() {
            mRunning = false;
            final View thisView = StoreHouseHeader.this;
            thisView.removeCallbacks(this);
        }
    }
}