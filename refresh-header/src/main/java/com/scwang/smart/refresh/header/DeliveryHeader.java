package com.scwang.smart.refresh.header;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.scwang.smart.drawable.PathsDrawable;
import com.scwang.smart.refresh.layout.api.RefreshKernel;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.simple.SimpleComponent;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;
import com.scwang.smart.refresh.layout.util.SmartUtil;

/**
 * Refresh your delivery!
 * Created by scwang on 2017/6/25.
 * design https://dribbble.com/shots/2753803-Refresh-your-delivery
 */
public class DeliveryHeader extends SimpleComponent implements RefreshHeader {

    //<editor-fold desc="static">

    protected static String[] umbrellaPaths = new String[]{
        "m 114,329 5,2 16,28 h -1 z M 2,144.5 c -4,-77 50,-122 96,-135 6,0 7.1,0.2 13,3.5 v 4.5 C 63,55.1 56,97.1 43,154.5 37.6,195 16,191 2,144.5 Z",
        "m 134,359 -1,-27 h 2.6 l -1,26 z m -24,-34.6 c 0,-1 -2,-3.6 -4.5,-6 C 88,300 7,218.5 2,144.5 c 18,43.6 33,45 41,10 0,-71 34,-125.5 68,-137 2,3 4,4.5 8,7.5 C 97,91 96.5,109.4 95.5,175.4 86.5,205 58,208.5 43,154.5 c 14,64 32,101.6 60.6,147 6,8 15.4,18.5 15.4,29.5 -3.8,-1.3 -8.27988,-2.8 -9,-6.6 z M 98.5,9.5 c 4.6,-1.5 18,-4.6 34,-5 1,1 1,2 1,3 -9,1 -16,3 -22,6 -2.5,-1 -8,-3 -13,-4 z",
        "m 119,331 c -1,-7.6 -4,-12 -6.5,-16 -37,-55 -64,-98.9 -69.5,-160.5 20,46 41.5,48.5 52.5,20.9 C 93.5,122.9 87,84 119,25 l 31,-0.1 c 40,60.5 25.2,136.5 22.2,150.1 -14,53 -66.7,33.4 -76.7,0.4 11.5,50.5 19.7,89.1 29.7,136.1 4,10 4.2,10.1 5,21.5 -3,0 -8,-1 -11,-2 z",
        "m 172,174.5 c 5,-51.6 -2,-106 -22,-149.6 2.5,-3 3,-4 6.6,-6 48,22.5 77.5,63 69,140 -24.8,55.8 -48.1,39.2703 -53.6,15.6 z M 154.6,14 C 148,11 142.4,9 133,7 c 0,-1 -0.5,-1.5 -0.5,-2.5 16,0 31.5,3.5 40.9,6.5 C 167.9,11 158.6,12 154.6,14 Z",
        "m 134,359 15,-28 2,-1 -16,29 z m 7,-26 c 0,-12 2,-14.4 4,-21.9 12,-47 16,-77.5 27,-137 12,38.5 37.1,22.9 53.6,-15.2 -4,54 -44.6,120.2 -69.6,154.2 -6,9.5 -7.4,16.9 -5,16.9 -2.4,1.4 -6.5,2.4 -10,3 z",
        "m225.6,159c1.6,-52 -22,-117 -69,-140 -1.5,-2 -1.6,-2 -2,-5 4,-3 9,-5 15,-4 48.6,10 103,67 96.6,132 -10,46 -35.5,52 -40.6,17z",
        "m 156,313.1 c 33,-59 54.6,-86.2 69.6,-154.2 12,38 28.9,22.1 40.5,-16.9 -2,50.6 -43,113 -99.6,171 -4.6,5 -8,9 -8,10 0,2 -3.5,5 -7,7 -4.6,1 1.5,-13.9 4.5,-16.9 z",
        "m 130,333 c -0.5,-11.5 -1.4,-12 -5,-22 -11,-30 -23.5,-89.1 -29.5,-135.6 16.5,39 59.5,33.1 76.5,-0.9 -6,59 -11,88.5 -27,139 -2,7 -3,11.6 -4,19.5 -3,0.5 -6.5,0.5 -11,0 z M 119,25 c -3.5,-1 -7,-3.5 -8,-7.5 V 13 c 2.5,-4.5 14.5,-6 22,-6 5,0 15,1 21,6 2,1.6 3.2,3.9 2.6,5.9 -1,3 -4,5 -6.6,6 -14.8,4.2 -31.0,0.1 -31,0.1 z",
    };

    protected static int[] umbrellaColors = new int[]{
            0xff92dfeb,
            0xff6dd0e9,
            0xff4fc3e7,
            0xff2fb6e6,
            0xff25a9de,
            0xff11abe4,
            0xff0e9bd8,
            0xff40b7e1
    };

    protected static String[] cloudPaths = new String[]{
            "M63,0A22.6,22 0,0 0,42 14,17 17,0 0,0 30.9,10 17,17 0,0 0,13.7 26,9 9,0 0,0 9,24 9,9 0,0 0,0 32h99a8,8 0,0 0,0 -0.6,8 8,0 0,0 -8,-8 8,8 0,0 0,-6 2.6,22.6 22,0 0,0 0,-3.6A22.6,22 0,0 0,63 0Z"
    };

    protected static int[] cloudColors = new int[]{
            0xffffffff
    };

    protected static String[] boxPaths = new String[]{
            "M0,17.5 L3,30 2.9,76 47.5,93 92.8,76V30L95,18 47,0.5Z",
            "M3,30 L48,46 47.5,93 2.9,76ZM0,17.5 L48,35 48,46 0,29Z",
            "m56.5,18c0,2 -3.8,3.8 -8.5,3.8 -4.7,0 -8.5,-1.7 -8.5,-3.8 0,-2 3.8,-3.8 8.5,-3.8 4.7,0 8.5,1.7 8.5,3.8zM3,30 L3,34.7l44.7,17 0,-5z",
            "M48,35 L47.5,93 92.8,76V30l2,-0.8 0,-10.9z",
            "M82.6,80 L92.8,62 92.8,76ZM47.6,80 L60,88 47.5,93ZM48,46 L92.8,30 92.8,34 48,51.6Z"
    };

    protected static int[] boxColors = new int[]{
            0xfff8b147,
            0xfff2973c,
            0xffed8030,
            0xfffec051,
            0xfff7ad49
    };
    //</editor-fold>

    //<editor-fold desc="Field">
    protected int mCloudX1;
    protected int mCloudX2;
    protected int mCloudX3;
    protected int mHeight;
    protected int mHeaderHeight;
    protected int mBackgroundColor;
    protected float mAppreciation;
    protected RefreshState mState;
    protected Drawable mCloudDrawable;
    protected Drawable mUmbrellaDrawable;
    protected Drawable mBoxDrawable;
    protected RefreshKernel mKernel;
    //</editor-fold>

    //<editor-fold desc="View">
    public DeliveryHeader(Context context) {
        this(context, null);
    }

    public DeliveryHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);

        mSpinnerStyle = SpinnerStyle.FixedBehind;

        final View thisView = this;
        thisView.setMinimumHeight(SmartUtil.dp2px(150));

        PathsDrawable cloudDrawable = new PathsDrawable();
        if (!cloudDrawable.parserPaths(cloudPaths)) {
            cloudDrawable.declareOriginal(0, 0, 99, 32);
        }
//        cloudDrawable.printOriginal("cloudDrawable");
        cloudDrawable.parserColors(cloudColors);
        cloudDrawable.setGeometricHeight(SmartUtil.dp2px(20));
        PathsDrawable umbrellaDrawable = new PathsDrawable();
        if (!umbrellaDrawable.parserPaths(umbrellaPaths)) {
            umbrellaDrawable.declareOriginal(2, 4, 265, 355);
        }
//        umbrellaDrawable.printOriginal("umbrellaDrawable");
        umbrellaDrawable.parserColors(umbrellaColors);
        umbrellaDrawable.setGeometricWidth(SmartUtil.dp2px(200));
        PathsDrawable boxDrawable = new PathsDrawable();
        if (!boxDrawable.parserPaths(boxPaths)) {
            boxDrawable.declareOriginal(0, 1, 95, 92);
        }
//        boxDrawable.printOriginal("boxDrawable");
        boxDrawable.parserColors(boxColors);
        boxDrawable.setGeometricWidth(SmartUtil.dp2px(50));
        mBoxDrawable = boxDrawable;
        mCloudDrawable = cloudDrawable;
        mUmbrellaDrawable = umbrellaDrawable;

        if (thisView.isInEditMode()) {
            mState = RefreshState.Refreshing;
            mAppreciation = 100;
            mCloudX1 = (int)(mCloudDrawable.getBounds().width()*3.5f);
            mCloudX2 = (int)(mCloudDrawable.getBounds().width()*0.5f);
            mCloudX3 = (int)(mCloudDrawable.getBounds().width()*2.0f);
        }
    }
    //</editor-fold>

    //<editor-fold desc="draw">
    @Override
    protected void dispatchDraw(Canvas canvas) {
        final View thisView = this;
        final int width = thisView.getWidth();
        final int height = mHeight;//thisView.getHeight();
        final int saveCount = canvas.getSaveCount();
        //noinspection EqualsBetweenInconvertibleTypes
        final boolean footer = mKernel != null && (this.equals(mKernel.getRefreshLayout().getRefreshFooter()));

        canvas.save();

        if (footer) {
            canvas.translate(0, thisView.getHeight() - mHeight);
        }

        int shake = (int) (mHeaderHeight / 13 * Math.sin(mAppreciation));

        drawCloud(canvas, width);
        drawBox(canvas, width, height, shake);
        drawUmbrella(canvas, width, height, shake);

        canvas.restoreToCount(saveCount);

        super.dispatchDraw(canvas);
    }

    protected void drawBox(Canvas canvas, int width, int height, int shake) {
        final int centerY = height - mHeaderHeight / 2 + shake;
        final int centerYBox = centerY + (mHeaderHeight / 2 - mBoxDrawable.getBounds().height())
                - Math.min(mHeaderHeight / 2 - mBoxDrawable.getBounds().height(), SmartUtil.dp2px(mAppreciation * 100));
        mBoxDrawable.getBounds().offsetTo(width / 2 - mBoxDrawable.getBounds().width() / 2, centerYBox - mBoxDrawable.getBounds().height() / 4);
        mBoxDrawable.draw(canvas);
    }

    protected void drawUmbrella(Canvas canvas, int width, int height, int shake) {
        if (mState == RefreshState.Refreshing
                || mState == RefreshState.RefreshFinish) {
            Rect bounds = mUmbrellaDrawable.getBounds();
            final int centerY = height - mHeaderHeight / 2 + shake;
            final int centerYUmbrella = centerY - mHeaderHeight + Math.min(mHeaderHeight, SmartUtil.dp2px(mAppreciation * 100));
            mUmbrellaDrawable.getBounds().offsetTo(width / 2 - bounds.width() / 2, centerYUmbrella - bounds.height());
            mUmbrellaDrawable.draw(canvas);
        }
    }

    protected void drawCloud(Canvas canvas, int width) {
        if (mState == RefreshState.Refreshing
                || mState == RefreshState.RefreshFinish) {
            mCloudDrawable.getBounds().offsetTo(mCloudX1, mHeaderHeight / 3);
            mCloudDrawable.draw(canvas);
            mCloudDrawable.getBounds().offsetTo(mCloudX2, mHeaderHeight / 2);
            mCloudDrawable.draw(canvas);
            mCloudDrawable.getBounds().offsetTo(mCloudX3, mHeaderHeight * 2 / 3);
            mCloudDrawable.draw(canvas);
            canvas.rotate(5 * (float) Math.sin(mAppreciation / 2), width / 2f , mHeaderHeight / 2f - mUmbrellaDrawable.getBounds().height());
            calculateFrame(width);
        }
    }

    protected void calculateFrame(int width) {
        mCloudX1 += SmartUtil.dp2px(9);
        mCloudX2 += SmartUtil.dp2px(5);
        mCloudX3 += SmartUtil.dp2px(12);
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
        final View thisView = this;
        thisView.invalidate();
    }
    //</editor-fold>

    //<editor-fold desc="RefreshHeader">
    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {
        mKernel = kernel;
        mHeaderHeight = height;
        if (mBackgroundColor != 0) {
            mKernel.requestDrawBackgroundFor(this, mBackgroundColor);
        }
    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
        mHeight = offset;
        if (mState != RefreshState.Refreshing) {
            mBoxDrawable.setAlpha((int) (255 * (1f - Math.max(0, percent - 1))));
        }
        this.invalidate();
    }

    @Override
    public void onReleased(@NonNull RefreshLayout layout, int height, int maxDragHeight) {
        onStartAnimator(layout, height, maxDragHeight);
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        mState = newState;
        if (newState == RefreshState.None) {
            mAppreciation = 0;
            mCloudX1 = mCloudX2 = mCloudX3 = -mCloudDrawable.getBounds().width();
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
        if (colors.length > 0) {
            mBackgroundColor = colors[0];
            if (mKernel != null) {
                mKernel.requestDrawBackgroundFor(this, mBackgroundColor);
            }
            if (colors.length > 1 && mCloudDrawable instanceof PathsDrawable) {
                ((PathsDrawable) mCloudDrawable).parserColors(colors[1]);
            }
        }
    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout layout, int height, int maxDragHeight) {
        mState = RefreshState.Refreshing;
        mBoxDrawable.setAlpha(255);
        final View thisView = this;
        thisView.invalidate();
    }
    //</editor-fold>
}
