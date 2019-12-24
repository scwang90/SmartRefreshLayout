package com.scwang.smart.refresh.classics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.scwang.smart.drawable.PaintDrawable;
import com.scwang.smart.refresh.footer.classics.R;
import com.scwang.smart.refresh.layout.api.RefreshComponent;
import com.scwang.smart.refresh.layout.api.RefreshKernel;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;
import com.scwang.smart.refresh.layout.simple.SimpleComponent;
import com.scwang.smart.refresh.layout.util.SmartUtil;

import static android.view.View.MeasureSpec.EXACTLY;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract class ClassicsAbstract<T extends ClassicsAbstract> extends SimpleComponent implements RefreshComponent {

    public static final int ID_TEXT_TITLE = R.id.srl_classics_title;
    public static final int ID_IMAGE_ARROW = R.id.srl_classics_arrow;
    public static final int ID_IMAGE_PROGRESS = R.id.srl_classics_progress;

    protected TextView mTitleText;
    protected ImageView mArrowView;
    protected ImageView mProgressView;

    protected RefreshKernel mRefreshKernel;
    protected PaintDrawable mArrowDrawable;
    protected PaintDrawable mProgressDrawable;

    protected boolean mSetAccentColor;
    protected boolean mSetPrimaryColor;
    protected int mBackgroundColor;
    protected int mFinishDuration = 500;
    protected int mPaddingTop = 20;
    protected int mPaddingBottom = 20;
    protected int mMinHeightOfContent = 0;

    //<editor-fold desc="RelativeLayout">
    public ClassicsAbstract(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mSpinnerStyle = SpinnerStyle.Translate;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final View thisView = this;
        if (mMinHeightOfContent == 0) {
            mPaddingTop = thisView.getPaddingTop();
            mPaddingBottom = thisView.getPaddingBottom();
            if (mPaddingTop == 0 || mPaddingBottom == 0) {
                int paddingLeft = thisView.getPaddingLeft();
                int paddingRight = thisView.getPaddingRight();
                mPaddingTop = mPaddingTop == 0 ? SmartUtil.dp2px(20) : mPaddingTop;
                mPaddingBottom = mPaddingBottom == 0 ? SmartUtil.dp2px(20) : mPaddingBottom;
                thisView.setPadding(paddingLeft, mPaddingTop, paddingRight, mPaddingBottom);
            }
            final ViewGroup thisGroup = this;
            thisGroup.setClipToPadding(false);
        }
        if (MeasureSpec.getMode(heightMeasureSpec) == EXACTLY) {
            final int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
            if (parentHeight < mMinHeightOfContent) {
                final int padding = (parentHeight - mMinHeightOfContent) / 2;
                thisView.setPadding(thisView.getPaddingLeft(), padding, thisView.getPaddingRight(), padding);
            } else {
                thisView.setPadding(thisView.getPaddingLeft(), 0, thisView.getPaddingRight(), 0);
            }
        } else {
            thisView.setPadding(thisView.getPaddingLeft(), mPaddingTop, thisView.getPaddingRight(), mPaddingBottom);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mMinHeightOfContent == 0) {
            final ViewGroup thisGroup = this;
            for (int i = 0; i < thisGroup.getChildCount(); i++) {
                final int height = thisGroup.getChildAt(i).getMeasuredHeight();
                if (mMinHeightOfContent < height) {
                    mMinHeightOfContent = height;
                }
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        final View arrowView = mArrowView;
        final View progressView = mProgressView;
        arrowView.animate().cancel();
        progressView.animate().cancel();
        final Drawable drawable = mProgressView.getDrawable();
        if (drawable instanceof Animatable) {
            if (((Animatable) drawable).isRunning()) {
                ((Animatable) drawable).stop();
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

    //</editor-fold>

    //<editor-fold desc="RefreshHeader">
    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {
        mRefreshKernel = kernel;
        mRefreshKernel.requestDrawBackgroundFor(this, mBackgroundColor);
    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
        final View progressView = mProgressView;
        if (progressView.getVisibility() != VISIBLE) {
            progressView.setVisibility(VISIBLE);
            Drawable drawable = mProgressView.getDrawable();
            if (drawable instanceof Animatable) {
                ((Animatable) drawable).start();
            } else {
                progressView.animate().rotation(36000).setDuration(100000);
            }
        }
    }

    @Override
    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
        onStartAnimator(refreshLayout, height, maxDragHeight);
    }

    @Override
    public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
        final View progressView = mProgressView;
        Drawable drawable = mProgressView.getDrawable();
        if (drawable instanceof Animatable) {
            if (((Animatable) drawable).isRunning()) {
                ((Animatable) drawable).stop();
            }
        } else {
            progressView.animate().rotation(0).setDuration(0);
        }
        progressView.setVisibility(GONE);
        return mFinishDuration;//延迟500毫秒之后再弹回
    }

    @Override
    public void setPrimaryColors(@ColorInt int... colors) {
        if (colors.length > 0) {
            final View thisView = this;
            if (!(thisView.getBackground() instanceof BitmapDrawable) && !mSetPrimaryColor) {
                setPrimaryColor(colors[0]);
                mSetPrimaryColor = false;
            }
            if (!mSetAccentColor) {
                if (colors.length > 1) {
                    setAccentColor(colors[1]);
                }
                mSetAccentColor = false;
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="API">
    public T setProgressBitmap(Bitmap bitmap) {
        mProgressDrawable = null;
        mProgressView.setImageBitmap(bitmap);
        return self();
    }

    public T setProgressDrawable(Drawable drawable) {
        mProgressDrawable = null;
        mProgressView.setImageDrawable(drawable);
        return self();
    }

    public T setProgressResource(@DrawableRes int resId) {
        mProgressDrawable = null;
        mProgressView.setImageResource(resId);
        return self();
    }

    public T setArrowBitmap(Bitmap bitmap) {
        mArrowDrawable = null;
        mArrowView.setImageBitmap(bitmap);
        return self();
    }

    public T setArrowDrawable(Drawable drawable) {
        mArrowDrawable = null;
        mArrowView.setImageDrawable(drawable);
        return self();
    }

    public T setArrowResource(@DrawableRes int resId) {
        mArrowDrawable = null;
        mArrowView.setImageResource(resId);
        return self();
    }

    public T setSpinnerStyle(SpinnerStyle style) {
        this.mSpinnerStyle = style;
        return self();
    }

    public T setPrimaryColor(@ColorInt int primaryColor) {
        mSetPrimaryColor = true;
        mBackgroundColor = primaryColor;
        if (mRefreshKernel != null) {
            mRefreshKernel.requestDrawBackgroundFor(this, primaryColor);
        }
        return self();
    }

    public T setAccentColor(@ColorInt int accentColor) {
        mSetAccentColor = true;
        mTitleText.setTextColor(accentColor);
        if (mArrowDrawable != null) {
            mArrowDrawable.setColor(accentColor);
            mArrowView.invalidateDrawable(mArrowDrawable);
        }
        if (mProgressDrawable != null) {
            mProgressDrawable.setColor(accentColor);
            mProgressView.invalidateDrawable(mProgressDrawable);
        }
        return self();
    }

    public T setPrimaryColorId(@ColorRes int colorId) {
        final View thisView = this;
        setPrimaryColor(ContextCompat.getColor(thisView.getContext(), colorId));
        return self();
    }

    public T setAccentColorId(@ColorRes int colorId) {
        final View thisView = this;
        setAccentColor(ContextCompat.getColor(thisView.getContext(), colorId));
        return self();
    }

    public T setFinishDuration(int delay) {
        mFinishDuration = delay;
        return self();
    }

    public T setTextSizeTitle(float size) {
        mTitleText.setTextSize(size);
        if (mRefreshKernel != null) {
            mRefreshKernel.requestRemeasureHeightFor(this);
        }
        return self();
    }

    public T setTextSizeTitle(int unit, float size) {
        mTitleText.setTextSize(unit, size);
        if (mRefreshKernel != null) {
            mRefreshKernel.requestRemeasureHeightFor(this);
        }
        return self();
    }

    public T setDrawableMarginRight(float dp) {
        final View arrowView = mArrowView;
        final View progressView = mProgressView;
        MarginLayoutParams lpArrow = (MarginLayoutParams) arrowView.getLayoutParams();
        MarginLayoutParams lpProgress = (MarginLayoutParams) progressView.getLayoutParams();
        lpArrow.rightMargin = lpProgress.rightMargin = SmartUtil.dp2px(dp);
        arrowView.setLayoutParams(lpArrow);
        progressView.setLayoutParams(lpProgress);
        return self();
    }

    public T setDrawableMarginRightPx(int px) {
        MarginLayoutParams lpArrow = (MarginLayoutParams)mArrowView.getLayoutParams();
        MarginLayoutParams lpProgress = (MarginLayoutParams)mProgressView.getLayoutParams();
        lpArrow.rightMargin = lpProgress.rightMargin = px;
        mArrowView.setLayoutParams(lpArrow);
        mProgressView.setLayoutParams(lpProgress);
        return self();
    }

    public T setDrawableSize(float dp) {
        final View arrowView = mArrowView;
        final View progressView = mProgressView;
        ViewGroup.LayoutParams lpArrow = arrowView.getLayoutParams();
        ViewGroup.LayoutParams lpProgress = progressView.getLayoutParams();
        lpArrow.width = lpProgress.width = SmartUtil.dp2px(dp);
        lpArrow.height = lpProgress.height = SmartUtil.dp2px(dp);
        arrowView.setLayoutParams(lpArrow);
        progressView.setLayoutParams(lpProgress);
        return self();
    }

    public T setDrawableSizePx(int px) {
        ViewGroup.LayoutParams lpArrow = mArrowView.getLayoutParams();
        ViewGroup.LayoutParams lpProgress = mProgressView.getLayoutParams();
        lpArrow.width = lpProgress.width = px;
        lpArrow.height = lpProgress.height = px;
        mArrowView.setLayoutParams(lpArrow);
        mProgressView.setLayoutParams(lpProgress);
        return self();
    }

    public T setDrawableArrowSize(float dp) {
        final View arrowView = mArrowView;
        ViewGroup.LayoutParams lpArrow = arrowView.getLayoutParams();
        lpArrow.height = lpArrow.width = SmartUtil.dp2px(dp);
        arrowView.setLayoutParams(lpArrow);
        return self();
    }

    public T setDrawableArrowSizePx(int px) {
        ViewGroup.LayoutParams lpArrow = mArrowView.getLayoutParams();
        lpArrow.width = px;
        lpArrow.height = px;
        mArrowView.setLayoutParams(lpArrow);
        return self();
    }

    public T setDrawableProgressSize(float dp) {
        final View progressView = mProgressView;
        ViewGroup.LayoutParams lpProgress = progressView.getLayoutParams();
        lpProgress.height = lpProgress.width = SmartUtil.dp2px(dp);
        progressView.setLayoutParams(lpProgress);
        return self();
    }

    public T setDrawableProgressSizePx(int px) {
        ViewGroup.LayoutParams lpProgress = mProgressView.getLayoutParams();
        lpProgress.width = px;
        lpProgress.height = px;
        mProgressView.setLayoutParams(lpProgress);
        return self();
    }

//    /**
//     * @deprecated 使用 findViewById(ID_IMAGE_ARROW) 代替
//     */
//    @Deprecated
//    public ImageView getArrowView() {
//        return mArrowView;
//    }
//
//    /**
//     * @deprecated 使用 findViewById(ID_IMAGE_PROGRESS) 代替
//     */
//    @Deprecated
//    public ImageView getProgressView() {
//        return mProgressView;
//    }
//
//    /**
//     * @deprecated 使用 findViewById(ID_TEXT_TITLE) 代替
//     */
//    @Deprecated
//    public TextView getTitleText() {
//        return mTitleText;
//    }
    //</editor-fold>
}
