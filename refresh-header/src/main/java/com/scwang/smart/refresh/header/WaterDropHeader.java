package com.scwang.smart.refresh.header;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.scwang.smart.drawable.ProgressDrawable;
import com.scwang.smart.refresh.header.material.MaterialProgressDrawable;
import com.scwang.smart.refresh.header.waterdrop.WaterDropView;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;
import com.scwang.smart.refresh.layout.simple.SimpleComponent;
import com.scwang.smart.refresh.layout.util.SmartUtil;

import static android.view.View.MeasureSpec.AT_MOST;
import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.getSize;
import static android.view.View.MeasureSpec.makeMeasureSpec;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * WaterDropHeader
 * Created by scwang on 2017/5/31.
 * from https://github.com/THEONE10211024/WaterDropListView
 */
public class WaterDropHeader extends SimpleComponent implements RefreshHeader {

    //<editor-fold desc="Field">
    protected static final float MAX_PROGRESS_ANGLE = 0.8f;

    protected RefreshState mState;
    protected ImageView mImageView;
    protected WaterDropView mWaterDropView;
    protected ProgressDrawable mProgressDrawable;
    protected MaterialProgressDrawable mProgress;
    //</editor-fold>

    //<editor-fold desc="ViewGroup">
    public WaterDropHeader(Context context) {
        this(context, null);
    }

    public WaterDropHeader(Context context, AttributeSet attrs) {
        super(context, attrs, 0);

        final ViewGroup thisGroup = this;

        for (SpinnerStyle style : SpinnerStyle.values) {
            if (style.scale) {
                mSpinnerStyle = style;
                break;
            }
        }
        mWaterDropView = new WaterDropView(context);
        mWaterDropView.updateCompleteState(0);
        thisGroup.addView(mWaterDropView, MATCH_PARENT, MATCH_PARENT);

        mProgressDrawable = new ProgressDrawable();
        final Drawable progressDrawable = mProgressDrawable;
        progressDrawable.setCallback(this);
        progressDrawable.setBounds(0, 0, SmartUtil.dp2px(20), SmartUtil.dp2px(20));

        mImageView = new ImageView(context);
        mProgress = new MaterialProgressDrawable(mImageView);
//        mProgress.setBackgroundColor(0xffffffff);
//        mProgress.setAlpha(255);
        mProgress.setColorSchemeColors(0xffffffff,0xff0099cc,0xffff4444,0xff669900,0xffaa66cc,0xffff8800);
        mImageView.setImageDrawable(mProgress);
        thisGroup.addView(mImageView, SmartUtil.dp2px(30), SmartUtil.dp2px(30));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final View imageView = mImageView;
        final View dropView = mWaterDropView;
        LayoutParams lpImage = (LayoutParams) imageView.getLayoutParams();
        imageView.measure(
                makeMeasureSpec(lpImage.width, EXACTLY),
                makeMeasureSpec(lpImage.height, EXACTLY)
        );
        dropView.measure(
                makeMeasureSpec(getSize(widthMeasureSpec), AT_MOST),
                heightMeasureSpec
        );
        int maxWidth = Math.max(imageView.getMeasuredWidth(), dropView.getMeasuredWidth());
        int maxHeight = Math.max(imageView.getMeasuredHeight(), dropView.getMeasuredHeight());
        super.setMeasuredDimension(View.resolveSize(maxWidth, widthMeasureSpec), View.resolveSize(maxHeight, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final View thisView = this;
        final View imageView = mImageView;
        final View dropView = mWaterDropView;
        final int measuredWidth = thisView.getMeasuredWidth();

        final int widthWaterDrop = dropView.getMeasuredWidth();
        final int heightWaterDrop = dropView.getMeasuredHeight();
        final int leftWaterDrop = measuredWidth / 2 - widthWaterDrop / 2;
        final int topWaterDrop = 0;
        dropView.layout(leftWaterDrop, topWaterDrop, leftWaterDrop + widthWaterDrop, topWaterDrop + heightWaterDrop);

        final int widthImage = imageView.getMeasuredWidth();
        final int heightImage = imageView.getMeasuredHeight();
        final int leftImage = measuredWidth / 2 - widthImage / 2;
        int topImage = widthWaterDrop / 2 - widthImage / 2;
        if (topImage + heightImage > dropView.getBottom() - (widthWaterDrop - widthImage) / 2) {
            topImage = dropView.getBottom() - (widthWaterDrop - widthImage) / 2 - heightImage;
        }
        imageView.layout(leftImage, topImage, leftImage + widthImage, topImage + heightImage);
    }
    //</editor-fold>

    //<editor-fold desc="Draw">
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        final View thisView = this;
        final View dropView = mWaterDropView;
        final Drawable progressDrawable = mProgressDrawable;
        if (mState == RefreshState.Refreshing) {
            canvas.save();
            canvas.translate(
                    thisView.getWidth()/2f-progressDrawable.getBounds().width()/2f,
                    mWaterDropView.getMaxCircleRadius()
                            +dropView.getPaddingTop()
                            -progressDrawable.getBounds().height()/2f
            );
            progressDrawable.draw(canvas);
            canvas.restore();
        }
    }

    @Override
    public void invalidateDrawable(@NonNull Drawable drawable) {
        final View thisView = this;
        thisView.invalidate();
//        if (drawable == mProgressDrawable) {
//            super.invalidate();
//        } else {
//            super.invalidateDrawable(drawable);
//        }
    }
    //</editor-fold>

    //<editor-fold desc="RefreshHeader">
    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
        if (isDragging || (mState != RefreshState.Refreshing && mState != RefreshState.RefreshReleased)) {
            final View dropView = mWaterDropView;
            mWaterDropView.updateCompleteState(Math.max(offset, 0), height + maxDragHeight);
            dropView.postInvalidate();
        }
        if (isDragging) {

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
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        final View dropView = mWaterDropView;
        final View imageView = mImageView;
        mState = newState;
        switch (newState) {
            case None:
                dropView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
                break;
            case PullDownToRefresh:
                dropView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
                break;
            case PullDownCanceled:
                break;
            case ReleaseToRefresh:
                dropView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
                break;
            case Refreshing:
                break;
            case RefreshFinish:
                dropView.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onReleased(@NonNull final RefreshLayout layout, int height, int maxDragHeight) {
        final View imageView = mImageView;
        final View dropView = mWaterDropView;
        mProgressDrawable.start();
        imageView.setVisibility(GONE);
        mWaterDropView.createAnimator().start();//开始回弹
        dropView.animate().setDuration(150).alpha(0).setListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                dropView.setVisibility(GONE);
                dropView.setAlpha(1);
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
    //</editor-fold>
}