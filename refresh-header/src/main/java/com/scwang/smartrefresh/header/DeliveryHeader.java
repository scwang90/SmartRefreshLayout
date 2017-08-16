package com.scwang.smartrefresh.header;

import android.support.annotation.RequiresApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.internal.pathview.PathsDrawable;
import com.scwang.smartrefresh.layout.util.DensityUtil;

/**
 * Refresh your delivery!
 * https://dribbble.com/shots/2753803-Refresh-your-delivery
 * Created by SCWANG on 2017/6/25.
 */

public class DeliveryHeader extends View implements RefreshHeader {


    //<editor-fold desc="Field">
    private Paint mPaint;
    private int mCloudX1;
    private int mCloudX2;
    private int mCloudX3;
    private int mHeaderHeight;
    private float mAppreciation;
    private RefreshState mState;
    private PathsDrawable mCloudDrawable;
    private PathsDrawable mUmbrellaDrawable;
    private PathsDrawable mBoxDrawable;
    //</editor-fold>

    //<editor-fold desc="View">
    public DeliveryHeader(Context context) {
        super(context);
        this.initView(context, null);
    }

    public DeliveryHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initView(context, attrs);
    }

    public DeliveryHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView(context, attrs);
    }

    @SuppressWarnings("unused")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public DeliveryHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initView(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    private void initView(Context context, AttributeSet attrs) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
//        setBackgroundColor(0xff283645);
        setMinimumHeight(DensityUtil.dp2px(150));

        mCloudDrawable = new PathsDrawable();
        mCloudDrawable.parserPaths("M63,0.1A22.6,22.4 0,0 0,42.1 14.2,17.3 17.3,0 0,0 30.9,10.2 17.3,17.3 0,0 0,13.7 25.8,8.8 8.8,0 0,0 8.7,24.2 8.8,8.8 0,0 0,0 32h99a7.9,7.9 0,0 0,0 -0.6,7.9 7.9,0 0,0 -7.9,-7.9 7.9,7.9 0,0 0,-5.8 2.6,22.6 22.4,0 0,0 0.3,-3.6A22.6,22.4 0,0 0,63 0.1Z");
        mCloudDrawable.parserColors(0xffffffff);
        mCloudDrawable.setGeometricHeight(DensityUtil.dp2px(20));
        mUmbrellaDrawable = new PathsDrawable();
        mUmbrellaDrawable.parserPaths(
            "M113.91,328.86 L119.02,331.02 134.86,359.02 133.99,359.02ZM2.18,144.52c-3.67,-76.84 49.96,-122.23 96.3,-134.98 6.03,0.21 7.57,0.59 13.23,3.9 0.19,1.7 0.25,2.17 -0.41,3.98 -47.88,37.64 -55.13,79.65 -68.07,137.22C37.56,194.8 16.18,191.4 2.18,144.52Z",
            "m133.99,359.02 l-0.71,-26.66 2.6,0.26 -1.02,26.4zM119.02,331.02c-3.39,-0.99 -8.53,-3.03 -8.72,-6.61 0,-0.81 -2.02,-3.63 -4.49,-6.27C88.05,299.71 7.29,218.46 2.18,144.52c17.67,43.57 33.35,45.33 41.05,10.12 0.13,-70.78 33.78,-125.48 68.07,-137.22 2.34,3.33 4.11,4.81 8.14,7.8 -22.02,65.69 -23.25,84.11 -24.14,150.23 -8.68,29.57 -37.44,32.81 -52.07,-20.81 14.12,64.06 31.66,101.57 60.64,147.13 6.2,8.38 14.74,18.4 15.15,29.25zM98.48,9.54c4.59,-1.5 17.8,-4.6 33.87,-5.07 0.95,0.95 1.38,1.91 1.14,2.91 -8.81,1.34 -16.36,3.1 -21.78,6.06 -2.53,-1.27 -7.82,-3.26 -13.23,-3.9z",
            "m119.02,331.02c-1.29,-7.57 -4.22,-12.31 -6.54,-15.79 -36.86,-54.89 -63.48,-98.79 -69.25,-160.59 19.89,45.9 41.27,48.65 52.07,20.81 -1.95,-52.55 -8.04,-91.2 24.14,-150.23 10.47,-0.28 16.85,0.17 30.66,-0.34 40.19,60.54 24.92,135.95 22.16,149.57 -13.9,53.18 -66.91,34.12 -76.96,1 11.54,50.55 20.28,89.27 30,135.97 4.12,10.03 5.37,10.37 5.06,21.35 -2.82,-0.22 -8.22,-1.01 -11.35,-1.75z",
            "m172.27,174.45c4.91,-51.6 -1.8,-105.99 -22.16,-149.57 2.51,-3.42 3.25,-4.36 6.59,-6.04 47.91,22.5 77.5,62.66 68.9,139.94 -16.53,49.7 -45.39,52.78 -53.33,15.68zM154.36,13.39c-6.65,-2.73 -11.65,-4.27 -20.87,-6.01 -0.25,-1.02 -0.71,-2.21 -1.14,-2.91 16.31,-0.22 27.58,2.29 37.27,4.82 -5.49,0.42 -11.39,1.87 -15.26,4.11z",
            "m133.99,359.02 l14.98,-28.13 2.24,-0.75 -16.34,28.88zM141.15,332.65c0.01,-11.71 2.3,-14.29 4.13,-21.52 11.82,-46.68 16.09,-77.45 26.98,-136.68 12.18,38.55 37.7,23.31 53.33,-15.68 -4.01,53.72 -43.68,121.28 -68.8,155.25 -6.17,9.5 -8.27,16.22 -5.59,16.12 -3.69,1.47 -6.24,2.05 -10.05,2.51z",
            "m225.59,158.77c1.61,-52.44 -22.26,-117.1 -68.9,-139.94 -1.48,-2.24 -1.63,-2.16 -2.34,-5.44 3.7,-3.42 9.42,-4.82 15.26,-4.11 48.59,9.81 103.07,66.75 96.62,132.26 -9.7,45.68 -35.45,51.78 -40.64,17.24z",
            "m156.48,313.99c32.9,-59.38 53.82,-87.19 69.12,-155.22 12.23,38.4 28.73,22.32 40.64,-17.24 -2.11,50.59 -43.12,112.84 -99.62,171.38 -4.57,4.73 -8.31,9.42 -8.31,10.4 -0,2.28 -3.52,5.43 -7.1,6.82 -4.65,0.73 2.08,-12.86 5.27,-16.15z",
            "M130.37,332.77C129.51,321.51 128.56,320.77 125.3,311.42 113.97,281.37 101.34,222.24 95.3,175.45c16.48,38.98 60.02,33.39 76.96,-1 -5.91,58.92 -10.85,88.45 -27.42,138.74 -1.67,6.75 -2.67,11.63 -3.7,19.46 -2.94,0.45 -6.48,0.45 -10.78,0.12zM119.44,25.22c-3.52,-1.25 -6.98,-3.72 -8.14,-7.8 -0.44,-1.53 -0.24,-2.79 0.41,-3.98 2.48,-4.55 14.53,-6.26 21.78,-6.06 5.29,0.15 14.87,0.72 20.87,6.01 1.82,1.61 2.74,3.95 2.34,5.44 -0.76,2.83 -4.21,5.19 -6.59,6.04 -7.49,2.68 -22.62,3.2 -30.66,0.34z"
        );
        mUmbrellaDrawable.parserColors(
            0xff92dfeb,
            0xff6dd0e9,
            0xff4fc3e7,
            0xff2fb6e6,
            0xff25a9de,
            0xff11abe4,
            0xff0e9bd8,
            0xff40b7e1
        );
        mUmbrellaDrawable.setGeometricWidth(DensityUtil.dp2px(200));
        mBoxDrawable = new PathsDrawable();
        mBoxDrawable.parserPaths(
                "M0,17.5 L3.1,29.8 2.9,76.4 47.5,93 92.8,76.2V29.9L94.9,18.1 47.4,0.5Z",
                "M3.1,29.8 L47.8,46.4 47.5,93 2.9,76.4ZM0,17.5 L47.9,35.4 47.8,46.4 0.2,28.8Z",
                "m56.5,17.8c0,2.1 -3.8,3.8 -8.5,3.8 -4.7,0 -8.5,-1.7 -8.5,-3.8 0,-2.1 3.8,-3.8 8.5,-3.8 4.7,0 8.5,1.7 8.5,3.8zM3.1,29.8 L3.1,34.7l44.7,16.9 0,-5.1z",
                "M47.9,35.4 L47.5,93 92.8,76.2V29.9l2.2,-0.8 0,-10.9z",
                "M82.6,80 L92.8,62.4 92.8,76.2ZM47.6,79.8 L59.8,88.4 47.5,93ZM47.8,46.4 L92.8,29.9 92.8,34.2 47.8,51.6Z"
        );
        mBoxDrawable.parserColors(
                0xfff8b147,
                0xfff2973c,
                0xffed8030,
                0xfffec051,
                0xfff7ad49
        );
        mBoxDrawable.setGeometricWidth(DensityUtil.dp2px(50));
//        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DeliveryHeader);
//        ta.recycle();

        if (isInEditMode()) {
            mState = RefreshState.Refreshing;
            mAppreciation = 100;
            mCloudX1 = (int)(mCloudDrawable.width()*3.5f);
            mCloudX2 = (int)(mCloudDrawable.width()*0.5f);
            mCloudX3 = (int)(mCloudDrawable.width()*2.0f);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int width = getWidth();
        final int height = getHeight();

        final int saveCount = canvas.getSaveCount();
        canvas.save();

        int shake = (int) (mHeaderHeight / 13 * Math.sin(mAppreciation));

        if (mState == RefreshState.Refreshing
                || mState == RefreshState.RefreshFinish) {
            mCloudDrawable.getBounds().offsetTo(mCloudX1, mHeaderHeight / 3);
            mCloudDrawable.draw(canvas);
            mCloudDrawable.getBounds().offsetTo(mCloudX2, mHeaderHeight / 2);
            mCloudDrawable.draw(canvas);
            mCloudDrawable.getBounds().offsetTo(mCloudX3, mHeaderHeight * 2 / 3);
            mCloudDrawable.draw(canvas);
            canvas.rotate(5 * (float) Math.sin(mAppreciation / 2), width / 2 , mHeaderHeight / 2 - mUmbrellaDrawable.height());
            calculateFrame(width);
        }

        final int centerY = height - mHeaderHeight / 2 + shake;
        final int centerYBox = centerY + (mHeaderHeight / 2 - mBoxDrawable.height())
                - Math.min(mHeaderHeight / 2 - mBoxDrawable.height(), DensityUtil.dp2px(mAppreciation * 100));
        mBoxDrawable.getBounds().offsetTo(width / 2 - mBoxDrawable.width() / 2, centerYBox - mBoxDrawable.height() / 4);
        mBoxDrawable.draw(canvas);

        if (mState == RefreshState.Refreshing
                || mState == RefreshState.RefreshFinish) {
            Rect bounds = mUmbrellaDrawable.getBounds();
            final int centerYUmbrella = centerY - mHeaderHeight + Math.min(mHeaderHeight, DensityUtil.dp2px(mAppreciation * 100));
            mUmbrellaDrawable.getBounds().offsetTo(width / 2 - bounds.width() / 2, centerYUmbrella - bounds.height());
            mUmbrellaDrawable.draw(canvas);
        }

        canvas.restoreToCount(saveCount);
    }

    private void calculateFrame(int width) {
        mCloudX1 += DensityUtil.dp2px(9);
        mCloudX2 += DensityUtil.dp2px(5);
        mCloudX3 += DensityUtil.dp2px(12);
        int cloudWidth = mCloudDrawable.getBounds().width();
        if (mCloudX1 > width + cloudWidth) {
            mCloudX1 = -cloudWidth;
        }
        if (mCloudX2 > width + cloudWidth) {
            mCloudX2 = -cloudWidth;
        }
        if (mCloudX3 > width + cloudWidth) {
            mCloudX3 = -cloudWidth;
        }

        mAppreciation += 0.1f;
        invalidate();
    }

    //</editor-fold>


    //<editor-fold desc="RefreshHeader">

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {
    }

    @Override
    public void onPullingDown(float percent, int offset, int headerHeight, int extendHeight) {
        if (mState != RefreshState.Refreshing) {
            mBoxDrawable.getPaint().setAlpha((int) (255 * (1f - Math.max(0, percent - 1))));
        }
    }

    @Override
    public void onReleasing(float percent, int offset, int headerHeight, int extendHeight) {
        if (mState != RefreshState.Refreshing) {
            mBoxDrawable.getPaint().setAlpha((int) (255 * (1f - Math.max(0, percent - 1))));
        }
    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        mState = newState;
        if (newState == RefreshState.None) {
            mAppreciation = 0;
            mCloudX1 = mCloudX2 = mCloudX3 = -mCloudDrawable.getBounds().width();
        }
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Scale;
    }

    @Override@Deprecated
    public void setPrimaryColors(int... colors) {
        if (colors.length > 0) {
            setBackgroundColor(colors[0]);
            if (colors.length > 1) {
                mCloudDrawable.parserColors(colors[1]);
            }
        }
    }

    @Override
    public void onInitialized(RefreshKernel kernel, int height, int extendHeight) {
        mHeaderHeight = height;
    }

    @Override
    public void onStartAnimator(RefreshLayout layout, int height, int extendHeight) {
        mState = RefreshState.Refreshing;
        invalidate();
    }

    @Override
    public int onFinish(RefreshLayout layout, boolean success) {
        return 0;
    }
    //</editor-fold>
}
