/**
 * @file XListViewHeader.java
 * @create Apr 18, 2012 5:22:27 PM
 * @author Maxwin
 * @description XListView's header
 */
package com.scwang.smartrefresh.header;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.scwang.smartrefresh.header.internal.MaterialProgressDrawable;
import com.scwang.smartrefresh.header.waterdrop.WaterDropView;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.internal.InternalAbstract;
import com.scwang.smartrefresh.layout.internal.ProgressDrawable;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import static android.view.View.MeasureSpec.AT_MOST;
import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.getSize;
import static android.view.View.MeasureSpec.makeMeasureSpec;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * WaterDropHeader
 * Created by SCWANG on 2017/5/31.
 * from https://github.com/THEONE10211024/WaterDropListView
 */
public class WaterDropHeader extends InternalAbstract implements RefreshHeader {

    //<editor-fold desc="Field">
    private static final float MAX_PROGRESS_ANGLE = 0.8f;

    private RefreshState mState;
    private ImageView mImageView;
    private WaterDropView mWaterDropView;
    private ProgressDrawable mProgressDrawable;
    private MaterialProgressDrawable mProgress;
    //</editor-fold>

    //<editor-fold desc="ViewGroup">
    public WaterDropHeader(Context context) {
        this(context, null);
    }

    public WaterDropHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaterDropHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        DensityUtil density = new DensityUtil();
        mWaterDropView = new WaterDropView(context);
        addView(mWaterDropView, MATCH_PARENT, MATCH_PARENT);
        mWaterDropView.updateCompleteState(0);

        mProgressDrawable = new ProgressDrawable();
        mProgressDrawable.setBounds(0, 0, density.dip2px(20), density.dip2px(20));
        mProgressDrawable.setCallback(this);

        mImageView = new ImageView(context);
        mProgress = new MaterialProgressDrawable(context, mImageView);
        mProgress.setBackgroundColor(0xffffffff);
        mProgress.setAlpha(255);
        mProgress.setColorSchemeColors(0xffffffff,0xff0099cc,0xffff4444,0xff669900,0xffaa66cc,0xffff8800);
        mImageView.setImageDrawable(mProgress);
        addView(mImageView, density.dip2px(30), density.dip2px(30));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        LayoutParams lpImage = (LayoutParams) mImageView.getLayoutParams();
        mImageView.measure(
                makeMeasureSpec(lpImage.width, EXACTLY),
                makeMeasureSpec(lpImage.height, EXACTLY)
        );
        mWaterDropView.measure(
                makeMeasureSpec(getSize(widthMeasureSpec), AT_MOST),
                heightMeasureSpec
        );
        int maxWidth = Math.max(mImageView.getMeasuredWidth(), mWaterDropView.getMeasuredHeight());
        int maxHeight = Math.max(mImageView.getMeasuredHeight(), mWaterDropView.getMeasuredHeight());
        setMeasuredDimension(resolveSize(maxWidth, widthMeasureSpec), resolveSize(maxHeight, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int measuredWidth = getMeasuredWidth();

        final int widthWaterDrop = mWaterDropView.getMeasuredWidth();
        final int heightWaterDrop = mWaterDropView.getMeasuredHeight();
        final int leftWaterDrop = measuredWidth / 2 - widthWaterDrop / 2;
        final int topWaterDrop = 0;
        mWaterDropView.layout(leftWaterDrop, topWaterDrop, leftWaterDrop + widthWaterDrop, topWaterDrop + heightWaterDrop);

        final int widthImage = mImageView.getMeasuredWidth();
        final int heightImage = mImageView.getMeasuredHeight();
        final int leftImage = measuredWidth / 2 - widthImage / 2;
        int topImage = widthWaterDrop / 2 - widthImage / 2;
        if (topImage + heightImage > mWaterDropView.getBottom() - (widthWaterDrop - widthImage) / 2) {
            topImage = mWaterDropView.getBottom() - (widthWaterDrop - widthImage) / 2 - heightImage;
        }
        mImageView.layout(leftImage, topImage, leftImage + widthImage, topImage + heightImage);
    }
    //</editor-fold>

    //<editor-fold desc="Draw">
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mState == RefreshState.Refreshing) {
            canvas.save();
            canvas.translate(
                    getWidth()/2-mProgressDrawable.getBounds().width()/2,
                    mWaterDropView.getMaxCircleRadius()
                            +mWaterDropView.getPaddingTop()
                            -mProgressDrawable.getBounds().height()/2
            );
            mProgressDrawable.draw(canvas);
            canvas.restore();
        }
    }

    @Override
    public void invalidateDrawable(@NonNull Drawable drawable) {
        if (drawable == mProgressDrawable) {
            invalidate();
        } else {
            super.invalidateDrawable(drawable);
        }
    }

    //</editor-fold>

    //<editor-fold desc="RefreshHeader">

    @Override
    public void onPulling(float percent, int offset, int height, int extendHeight) {
        mWaterDropView.updateCompleteState((offset), height + extendHeight);
        mWaterDropView.postInvalidate();

        float originalDragPercent = 1f * offset / height;

        float dragPercent = Math.min(1f, Math.abs(originalDragPercent));
        float adjustedPercent = (float) Math.max(dragPercent - .4, 0) * 5 / 3;
        float extraOS = Math.abs(offset) - height;
        float tensionSlingshotPercent = Math.max(0, Math.min(extraOS, (float) height * 2)
                / (float) height);
        float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow(
                (tensionSlingshotPercent / 4), 2)) * 2f;
        float strokeStart = adjustedPercent * .8f;
        float rotation = (-0.25f + .4f * adjustedPercent + tensionPercent * 2) * .5f;
        mProgress.showArrow(true);
        mProgress.setStartEndTrim(0f, Math.min(MAX_PROGRESS_ANGLE, strokeStart));
        mProgress.setArrowScale(Math.min(1f, adjustedPercent));
        mProgress.setProgressRotation(rotation);
    }

    @Override
    public void onReleasing(float percent, int offset, int height, int extendHeight) {
        if (mState != RefreshState.Refreshing && mState != RefreshState.RefreshReleased) {
            mWaterDropView.updateCompleteState(Math.max(offset, 0), height + extendHeight);
            mWaterDropView.postInvalidate();
        }
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        mState = newState;
        switch (newState) {
            case None:
                mWaterDropView.setVisibility(View.VISIBLE);
                break;
            case PullDownToRefresh:
                mWaterDropView.setVisibility(View.VISIBLE);
                break;
            case PullDownCanceled:
                break;
            case ReleaseToRefresh:
                mWaterDropView.setVisibility(View.VISIBLE);
                break;
            case Refreshing:
                break;
            case RefreshFinish:
                mWaterDropView.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onReleased(@NonNull final RefreshLayout layout, int height, int extendHeight) {
        mProgressDrawable.start();
        mWaterDropView.createAnimator().start();//开始回弹
        mWaterDropView.animate().setDuration(150).alpha(0).setListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                mWaterDropView.setVisibility(GONE);
                mWaterDropView.setAlpha(1);
            }
        });
    }

    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        mProgressDrawable.stop();
        return 0;
    }

    /**
     * @param colors 对应Xml中配置的 srlPrimaryColor srlAccentColor
     * @deprecated 请使用 {@link RefreshLayout#setPrimaryColorsId(int...)}
     */
    @Override@Deprecated
    public void setPrimaryColors(@ColorInt int ... colors) {
        if (colors.length > 0) {
            mWaterDropView.setIndicatorColor(colors[0]);
        }
    }

    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Scale;
    }
    //</editor-fold>
}