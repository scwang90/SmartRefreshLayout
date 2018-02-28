package com.scwang.smartrefresh.layout.internal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshInternal;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.util.DensityUtil;
import com.scwang.smartrefresh.layout.util.DesignUtil;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * 经典组件
 * Created by SCWANG on 2017/5/28.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract class InternalClassics<T extends InternalClassics> extends InternalAbstract implements RefreshInternal {

    public static final byte ID_TEXT_TITLE = 1;
    public static final byte ID_IMAGE_ARROW = 2;
    public static final byte ID_IMAGE_PROGRESS = 3;

    protected TextView mTitleText;
    protected ImageView mArrowView;
    protected ImageView mProgressView;
    protected LinearLayout mCenterLayout;
    protected RefreshKernel mRefreshKernel;
    protected ArrowDrawable mArrowDrawable;
    protected ProgressDrawable mProgressDrawable;
//    protected SpinnerStyle mSpinnerStyle = SpinnerStyle.Translate;
    protected Integer mAccentColor;
    protected Integer mPrimaryColor;
    protected int mBackgroundColor;
    protected int mFinishDuration = 500;
    protected int mPaddingTop = 20;
    protected int mPaddingBottom = 20;

    //<editor-fold desc="RelativeLayout">

    public InternalClassics(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        DensityUtil density = new DensityUtil();

        mSpinnerStyle = SpinnerStyle.Translate;
        mCenterLayout = new LinearLayout(context);
        mCenterLayout.setId(android.R.id.widget_frame);
        mCenterLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        mCenterLayout.setOrientation(LinearLayout.VERTICAL);
        mTitleText = new TextView(context);
        mTitleText.setId(ID_TEXT_TITLE);

        LinearLayout.LayoutParams lpHeaderText = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        mCenterLayout.addView(mTitleText, lpHeaderText);

        LayoutParams lpHeaderLayout = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lpHeaderLayout.addRule(CENTER_IN_PARENT);
        super.addView(mCenterLayout,lpHeaderLayout);

        LayoutParams lpArrow = new LayoutParams(density.dip2px(20), density.dip2px(20));
        lpArrow.addRule(CENTER_VERTICAL);
        lpArrow.addRule(LEFT_OF, android.R.id.widget_frame);
        mArrowView = new ImageView(context);
        mArrowView.setId(ID_IMAGE_ARROW);
        super.addView(mArrowView, lpArrow);

        LayoutParams lpProgress = new LayoutParams((ViewGroup.LayoutParams)lpArrow);
        lpProgress.addRule(CENTER_VERTICAL);
        lpProgress.addRule(LEFT_OF, android.R.id.widget_frame);
        mProgressView = new ImageView(context);
        mProgressView.setId(ID_IMAGE_PROGRESS);
        mProgressView.animate().setInterpolator(new LinearInterpolator());
        super.addView(mProgressView, lpProgress);

        if (super.getPaddingTop() == 0) {
            if (super.getPaddingBottom() == 0) {
                super.setPadding(super.getPaddingLeft(), mPaddingTop = density.dip2px(20), super.getPaddingRight(), mPaddingBottom = density.dip2px(20));
            } else {
                super.setPadding(super.getPaddingLeft(), mPaddingTop = density.dip2px(20), super.getPaddingRight(), mPaddingBottom = super.getPaddingBottom());
            }
        } else {
            if (super.getPaddingBottom() == 0) {
                super.setPadding(super.getPaddingLeft(), mPaddingTop = super.getPaddingTop(), super.getPaddingRight(), mPaddingBottom = density.dip2px(20));
            } else {
                mPaddingTop = super.getPaddingTop();
                mPaddingBottom = super.getPaddingBottom();
            }
        }

        if (super.isInEditMode()) {
            mArrowView.setVisibility(GONE);
        } else {
            mProgressView.setVisibility(GONE);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            super.setPadding(super.getPaddingLeft(), 0, super.getPaddingRight(), 0);
        } else {
            super.setPadding(super.getPaddingLeft(), mPaddingTop, super.getPaddingRight(), mPaddingBottom);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            mArrowView.animate().cancel();
            mProgressView.animate().cancel();
        }
        Drawable drawable = mProgressView.getDrawable();
        if (drawable instanceof Animatable) {
            if (((Animatable) drawable).isRunning()) {
                ((Animatable) drawable).stop();
            }
        }
    }

    protected T self() {
        //noinspection unchecked
        return (T) this;
    }

    //</editor-fold>

    //<editor-fold desc="RefreshHeader">
    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int extendHeight) {
        mRefreshKernel = kernel;
        mRefreshKernel.requestDrawBackgroundFor(this, mBackgroundColor);
//        if (this instanceof RefreshHeader) {
//            mRefreshKernel.requestDrawBackgroundForHeader(mBackgroundColor);
//        } else if (this instanceof RefreshFooter) {
//            mRefreshKernel.requestDrawBackgroundForFooter(mBackgroundColor);
//        }
    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int extendHeight) {
        if (mProgressView.getVisibility() != VISIBLE) {
            mProgressView.setVisibility(VISIBLE);
            Drawable drawable = mProgressView.getDrawable();
            if (drawable instanceof Animatable) {
                ((Animatable) drawable).start();
            } else {
                mProgressView.animate().rotation(36000).setDuration(100000);
            }
        }
    }

    @Override
    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int extendHeight) {
        onStartAnimator(refreshLayout, height, extendHeight);
    }

    @Override
    public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
        Drawable drawable = mProgressView.getDrawable();
        if (drawable instanceof Animatable) {
            if (((Animatable) drawable).isRunning()) {
                ((Animatable) drawable).stop();
            }
        } else {
            mProgressView.animate().rotation(0).setDuration(0);
        }
        mProgressView.setVisibility(GONE);
        return mFinishDuration;//延迟500毫秒之后再弹回
    }

    @Override@Deprecated
    public void setPrimaryColors(@ColorInt int ... colors) {
        if (colors.length > 0) {
            if (!(super.getBackground() instanceof BitmapDrawable) && mPrimaryColor == null) {
                setPrimaryColor(colors[0]);
                mPrimaryColor = null;
            }
            if (mAccentColor == null) {
                if (colors.length > 1) {
                    setAccentColor(colors[1]);
                } else {
                    setAccentColor(colors[0] == 0xffffffff ? 0xff666666 : 0xffffffff);
                }
                mAccentColor = null;
            }
        }
    }

//    @NonNull
//    @Override
//    public SpinnerStyle getSpinnerStyle() {
//        return mSpinnerStyle;
//    }

    //</editor-fold>

    //<editor-fold desc="API">

//    public T setProgressBitmap(Bitmap bitmap) {
//        mProgressDrawable = null;
//        mProgressView.setImageBitmap(bitmap);
//        return self();
//    }

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
//    public T setArrowBitmap(Bitmap bitmap) {
//        mArrowDrawable = null;
//        mArrowView.setImageBitmap(bitmap);
//        return self();
//    }
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
        mBackgroundColor = mPrimaryColor = primaryColor;
        if (mRefreshKernel != null) {
            mRefreshKernel.requestDrawBackgroundFor(this, mPrimaryColor);
//            if (this instanceof RefreshHeader) {
//                mRefreshKernel.requestDrawBackgroundForHeader(mPrimaryColor);
//            } else if (this instanceof RefreshFooter) {
//                mRefreshKernel.requestDrawBackgroundForFooter(mPrimaryColor);
//            }
        }
        return self();
    }

    public T setAccentColor(@ColorInt int accentColor) {
        mAccentColor = accentColor;
        mTitleText.setTextColor(accentColor);
        if (mArrowDrawable != null) {
            mArrowDrawable.setColor(accentColor);
        }
        if (mProgressDrawable != null) {
            mProgressDrawable.setColor(accentColor);
        }
        return self();
    }

    public T setPrimaryColorId(@ColorRes int colorId) {
        setPrimaryColor(DesignUtil.getColor(super.getContext(), colorId));
        return self();
    }

    public T setAccentColorId(@ColorRes int colorId) {
        setAccentColor(DesignUtil.getColor(super.getContext(), colorId));
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
//            if (this instanceof RefreshHeader) {
//                mRefreshKernel.requestRemeasureHeightForHeader();
//            } else if (this instanceof RefreshFooter) {
//                mRefreshKernel.requestRemeasureHeightForFooter();
//            }
        }
        return self();
    }

//    public T setTextSizeTitle(int unit, float size) {
//        mTitleText.setTextSize(unit, size);
//        if (mRefreshKernel != null) {
//            if (this instanceof RefreshHeader) {
//                mRefreshKernel.requestRemeasureHeightForHeader();
//            } else if (this instanceof RefreshFooter) {
//                mRefreshKernel.requestRemeasureHeightForFooter();
//            }
//        }
//        return self();
//    }

    public T setDrawableMarginRight(float dp) {
        MarginLayoutParams lpArrow = (MarginLayoutParams)mArrowView.getLayoutParams();
        MarginLayoutParams lpProgress = (MarginLayoutParams)mProgressView.getLayoutParams();
        lpArrow.rightMargin = lpProgress.rightMargin = DensityUtil.dp2px(dp);
        mArrowView.setLayoutParams(lpArrow);
        mProgressView.setLayoutParams(lpProgress);
        return self();
    }

//    public T setDrawableMarginRightPx(int px) {
//        MarginLayoutParams lpArrow = (MarginLayoutParams)mArrowView.getLayoutParams();
//        MarginLayoutParams lpProgress = (MarginLayoutParams)mProgressView.getLayoutParams();
//        lpArrow.rightMargin = lpProgress.rightMargin = px;
//        mArrowView.setLayoutParams(lpArrow);
//        mProgressView.setLayoutParams(lpProgress);
//        return self();
//    }

    public T setDrawableSize(float dp) {
        ViewGroup.LayoutParams lpArrow = mArrowView.getLayoutParams();
        ViewGroup.LayoutParams lpProgress = mProgressView.getLayoutParams();
        lpArrow.width = lpProgress.width = DensityUtil.dp2px(dp);
        lpArrow.height = lpProgress.height = DensityUtil.dp2px(dp);
        mArrowView.setLayoutParams(lpArrow);
        mProgressView.setLayoutParams(lpProgress);
        return self();
    }

//    public T setDrawableSizePx(int px) {
//        ViewGroup.LayoutParams lpArrow = mArrowView.getLayoutParams();
//        ViewGroup.LayoutParams lpProgress = mProgressView.getLayoutParams();
//        lpArrow.width = lpProgress.width = px;
//        lpArrow.height = lpProgress.height = px;
//        mArrowView.setLayoutParams(lpArrow);
//        mProgressView.setLayoutParams(lpProgress);
//        return self();
//    }

    public T setDrawableArrowSize(float dp) {
        ViewGroup.LayoutParams lpArrow = mArrowView.getLayoutParams();
        lpArrow.height = lpArrow.width = DensityUtil.dp2px(dp);
        mArrowView.setLayoutParams(lpArrow);
        return self();
    }

//    public T setDrawableArrowSizePx(int px) {
//        ViewGroup.LayoutParams lpArrow = mArrowView.getLayoutParams();
//        lpArrow.width = px;
//        lpArrow.height = px;
//        mArrowView.setLayoutParams(lpArrow);
//        return self();
//    }

    public T setDrawableProgressSize(float dp) {
        ViewGroup.LayoutParams lpProgress = mProgressView.getLayoutParams();
        lpProgress.height = lpProgress.width = DensityUtil.dp2px(dp);
        mProgressView.setLayoutParams(lpProgress);
        return self();
    }

//    public T setDrawableProgressSizePx(int px) {
//        ViewGroup.LayoutParams lpProgress = mProgressView.getLayoutParams();
//        lpProgress.width = px;
//        lpProgress.height = px;
//        mProgressView.setLayoutParams(lpProgress);
//        return self();
//    }

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
