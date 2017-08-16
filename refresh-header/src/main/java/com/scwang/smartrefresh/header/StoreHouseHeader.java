package com.scwang.smartrefresh.header;

import android.support.annotation.RequiresApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Transformation;

import com.scwang.smartrefresh.header.storehouse.StoreHouseBarItem;
import com.scwang.smartrefresh.header.storehouse.StoreHousePath;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import java.util.ArrayList;

public class  StoreHouseHeader extends View implements RefreshHeader {

    //<editor-fold desc="Field">
    public ArrayList<StoreHouseBarItem> mItemList = new ArrayList<StoreHouseBarItem>();

    private int mLineWidth = -1;
    private float mScale = 1;
    private int mDropHeight = -1;
    private static final float mInternalAnimationFactor = 0.7f;
    private int mHorizontalRandomness = -1;

    private float mProgress = 0;

    private int mDrawZoneWidth = 0;
    private int mDrawZoneHeight = 0;
    private int mOffsetX = 0;
    private int mOffsetY = 0;
    private static final float mBarDarkAlpha = 0.4f;
    private static final float mFromAlpha = 1.0f;
    private static final float mToAlpha = 0.4f;

    private int mLoadingAniDuration = 1000;
    private int mLoadingAniSegDuration = 1000;
    private static final int mLoadingAniItemDuration = 400;

    private Transformation mTransformation = new Transformation();
    private boolean mIsInLoading = false;
    private AniController mAniController = new AniController();
    private int mTextColor = Color.WHITE;
    private int mBackgroundColor = 0;
    private Matrix mMatrix = new Matrix();
    private RefreshKernel mRefreshKernel;
    //</editor-fold>

    //<editor-fold desc="View">
    public StoreHouseHeader(Context context) {
        super(context);
        this.initView(context, null);
    }

    public StoreHouseHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initView(context, attrs);
    }

    public StoreHouseHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView(context, attrs);
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public StoreHouseHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        DensityUtil density = new DensityUtil();
        mLineWidth = density.dip2px(1);
        mDropHeight = density.dip2px(40);
        mHorizontalRandomness = Resources.getSystem().getDisplayMetrics().widthPixels / 2;
//        setBackgroundColor(0xff333333);
        mBackgroundColor = 0xff333333;
        setTextColor(0xffcccccc);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.StoreHouseHeader);
        mLineWidth = ta.getDimensionPixelOffset(R.styleable.StoreHouseHeader_shhLineWidth, mLineWidth);
        mDropHeight = ta.getDimensionPixelOffset(R.styleable.StoreHouseHeader_shhDropHeight, mDropHeight);
        if (ta.hasValue(R.styleable.StoreHouseHeader_shhText)) {
            initWithString(ta.getString(R.styleable.StoreHouseHeader_shhText));
        } else {
            initWithString("StoreHouse");
        }
        ta.recycle();

        setMinimumHeight(mDrawZoneHeight + DensityUtil.dp2px(40));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int height = getTopOffset() + mDrawZoneHeight + getBottomOffset();
//        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        setMeasuredDimension(resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec));

        mOffsetX = (getMeasuredWidth() - mDrawZoneWidth) / 2;
        mOffsetY = (getMeasuredHeight() - mDrawZoneHeight) / 2;//getTopOffset();
        mDropHeight = getMeasuredHeight() / 2;//getTopOffset();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float progress = mProgress;
        int c1 = canvas.save();
        int len = mItemList.size();

        if (isInEditMode()) {
            progress = 1;
        }

        for (int i = 0; i < len; i++) {

            canvas.save();
            StoreHouseBarItem storeHouseBarItem = mItemList.get(i);
            float offsetX = mOffsetX + storeHouseBarItem.midPoint.x;
            float offsetY = mOffsetY + storeHouseBarItem.midPoint.y;

            if (mIsInLoading) {
                storeHouseBarItem.getTransformation(getDrawingTime(), mTransformation);
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
            invalidate();
        }
        canvas.restoreToCount(c1);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mRefreshKernel = null;
    }

    //</editor-fold>

    //<editor-fold desc="API">
    public int getLoadingAniDuration() {
        return mLoadingAniDuration;
    }

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

    public StoreHouseHeader setTextColor(int color) {
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
        ArrayList<float[]> pointList = StoreHousePath.getPath(str, fontSize * 0.01f, 14);
        initWithPointList(pointList);
        return this;
    }

    public StoreHouseHeader initWithStringArray(int id) {
        String[] points = getResources().getStringArray(id);
        ArrayList<float[]> pointList = new ArrayList<float[]>();
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

    public float getScale() {
        return mScale;
    }

    public StoreHouseHeader setScale(float scale) {
        mScale = scale;
        return this;
    }

    public StoreHouseHeader initWithPointList(ArrayList<float[]> pointList) {

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
            requestLayout();
        }
        return this;
    }
    //</editor-fold>

    //<editor-fold desc="background">
    private Runnable restoreRunable;
    private void restoreRefreshLayoutBackground() {
        if (restoreRunable != null) {
            restoreRunable.run();
            restoreRunable = null;
        }
    }
    private void replaceRefreshLayoutBackground(RefreshLayout refreshLayout) {
//        if (restoreRunable == null) {
//            restoreRunable = new Runnable() {
//                Drawable drawable = refreshLayout.getLayout().getBackground();
//                @Override
//                public void run() {
//                    refreshLayout.getLayout().setBackgroundDrawable(drawable);
//                }
//            };
//            refreshLayout.getLayout().setBackgroundDrawable(getBackground());
//        }
    }
    //</editor-fold>

    //<editor-fold desc="private">
    private void setProgress(float progress) {
        mProgress = progress;
    }

    private int getTopOffset() {
        return getPaddingTop() + DensityUtil.dp2px(10);
    }

    private int getBottomOffset() {
        return getPaddingBottom() + DensityUtil.dp2px(10);
    }

    private void beginLoading() {
        mIsInLoading = true;
        mAniController.start();
        invalidate();
    }

    private void loadFinish() {
        mIsInLoading = false;
        mAniController.stop();
    }
    //</editor-fold>

    //<editor-fold desc="RefreshHeader">

    @Override
    public void onInitialized(RefreshKernel kernel, int height, int extendHeight) {
        if (mBackgroundColor != 0) {
            kernel.requestDrawBackgoundForHeader(mBackgroundColor);
        }
        mRefreshKernel = kernel;
    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {
    }

    @Override
    public void onPullingDown(float percent, int offset, int headHeight, int extendHeight) {
        setProgress(percent * .8f);
        invalidate();
    }

    @Override
    public void onReleasing(float percent, int offset, int headHeight, int extendHeight) {
        setProgress(percent * .8f);
        invalidate();
    }

    @Override
    public void onStartAnimator(RefreshLayout layout, int headHeight, int extendHeight) {
        beginLoading();
    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        if (newState == RefreshState.ReleaseToRefresh) {
            replaceRefreshLayoutBackground(refreshLayout);
        } else if (newState == RefreshState.None) {
            restoreRefreshLayoutBackground();
        }
    }

    @Override
    public int onFinish(RefreshLayout layout, boolean success) {
        loadFinish();
        for (int i = 0; i < mItemList.size(); i++) {
            mItemList.get(i).resetPosition(mHorizontalRandomness);
        }
        return 0;
    }

    @Override@Deprecated
    public void setPrimaryColors(int... colors) {
        if (colors.length > 0) {
            mBackgroundColor = colors[0];
            if (mRefreshKernel != null) {
                mRefreshKernel.requestDrawBackgoundForHeader(colors[0]);
            }
            if (colors.length > 1) {
                setTextColor(colors[1]);
            }
        }
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;
    }
    //</editor-fold>

    private class AniController implements Runnable {

        private int mTick = 0;
        private int mCountPerSeg = 0;
        private int mSegCount = 0;
        private int mInterval = 0;
        private boolean mRunning = true;

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
                mRefreshKernel.getRefreshLayout().getLayout().postDelayed(this, mInterval);
            }
        }

        private void stop() {
            mRunning = false;
            removeCallbacks(this);
        }
    }
}