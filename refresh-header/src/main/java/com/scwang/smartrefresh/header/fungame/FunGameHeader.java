package com.scwang.smartrefresh.header.fungame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.header.R;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * 游戏 header
 * Created by SCWANG on 2017/6/17.
 * from https://github.com/Hitomis/FunGameRefresh
 */
@SuppressWarnings({"unused", "SameParameterValue"})
public abstract class FunGameHeader extends FunGameBase implements RefreshHeader {

    //<editor-fold desc="Field - Curtain">
    /**
     * 分割线默认宽度大小
     */
    protected float DIVIDING_LINE_SIZE = 1.f;

    private View mShadowView;
    private TextView mMaskViewTop;
    private TextView mMaskViewBottom;
    private RelativeLayout mCurtainLayout;

    private int mHalfHeaderHeight;

    //</editor-fold>

    //<editor-fold desc="View">
    public FunGameHeader(Context context) {
        this(context, null);
    }

    public FunGameHeader(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FunGameHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FunGameHeader);

        String maskTextBottom = context.getResources().getString(R.string.fgh_mask_bottom);//"拖动控制游戏";//"Scroll to move handle";
        String maskTextTopPull = context.getResources().getString(R.string.fgh_mask_top_pull);//"下拉即将展开";//"Pull To Break Out!";
        String maskTextTopRelease = context.getResources().getString(R.string.fgh_mask_top_release);//"放手即将展开";//"Release To Break Out!";
        if (ta.hasValue(R.styleable.FunGameHeader_fghMaskTextTop)) {
            maskTextTopPull = maskTextTopRelease = ta.getString(R.styleable.FunGameHeader_fghMaskTextTop);
        }
        if (ta.hasValue(R.styleable.FunGameHeader_fghMaskTextTopPull)) {
            maskTextTopPull = ta.getString(R.styleable.FunGameHeader_fghMaskTextTopPull);
        }
        if (ta.hasValue(R.styleable.FunGameHeader_fghMaskTextTopRelease)) {
            maskTextTopRelease = ta.getString(R.styleable.FunGameHeader_fghMaskTextTopRelease);
        }
        if (ta.hasValue(R.styleable.FunGameHeader_fghMaskTextBottom)) {
            maskTextBottom = ta.getString(R.styleable.FunGameHeader_fghMaskTextBottom);
        }

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int maskTextSizeTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, metrics);
        int maskTextSizeBottom = maskTextSizeTop * 14 / 16;

        maskTextSizeTop = ta.getDimensionPixelSize(R.styleable.FunGameHeader_fghMaskTextSizeTop, maskTextSizeTop);
        maskTextSizeBottom = ta.getDimensionPixelSize(R.styleable.FunGameHeader_fghMaskTextSizeBottom, maskTextSizeBottom);

        ta.recycle();

        mCurtainLayout = new RelativeLayout(context);
        mShadowView = new RelativeLayout(context);
        mShadowView.setBackgroundColor(0xFF3A3A3A);

        mMaskViewTop = createMaskView(context,maskTextTopPull, maskTextSizeTop, Gravity.BOTTOM);
        mMaskViewBottom = createMaskView(context,maskTextBottom, maskTextSizeBottom, Gravity.TOP);
        mMaskViewTop.setHint(maskTextTopPull);
        mMaskViewBottom.setHint(maskTextTopRelease);

        DIVIDING_LINE_SIZE = Math.max(1, DensityUtil.dp2px(0.5f));
    }


    private TextView createMaskView(Context context, String text, int textSize, int gravity) {
        TextView maskView = new TextView(context);
        maskView.setTextColor(Color.BLACK);
        maskView.setBackgroundColor(Color.WHITE);
        maskView.setGravity(gravity | Gravity.CENTER_HORIZONTAL);
        maskView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        maskView.setText(text);
        return maskView;
    }

    protected abstract void onGameStart();

    //<editor-fold desc="RefreshHeader">

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int extendHeight) {
        super.onInitialized(kernel, height, extendHeight);
        if (getChildCount() < 2 && !isInEditMode()) {
            LayoutParams maskLp = new LayoutParams(MATCH_PARENT,mHeaderHeight);
//            maskLp.topMargin = (int) FunGameView.DIVIDING_LINE_SIZE;
//            maskLp.bottomMargin = (int) FunGameView.DIVIDING_LINE_SIZE;

            addView(mShadowView, maskLp);
            addView(mCurtainLayout, maskLp);

            mHalfHeaderHeight = (int) ((mHeaderHeight/* - 2 * DIVIDING_LINE_SIZE*/) * .5f);
            RelativeLayout.LayoutParams topRelayLayoutParams = new RelativeLayout.LayoutParams(MATCH_PARENT, mHalfHeaderHeight);
            RelativeLayout.LayoutParams bottomRelayLayoutParams = new RelativeLayout.LayoutParams(MATCH_PARENT, mHalfHeaderHeight);
            bottomRelayLayoutParams.topMargin = mHeaderHeight - mHalfHeaderHeight;
            mCurtainLayout.addView(mMaskViewTop, topRelayLayoutParams);
            mCurtainLayout.addView(mMaskViewBottom, bottomRelayLayoutParams);
        }
    }

    @Override@Deprecated
    public void setPrimaryColors(@ColorInt int ... colors) {
        super.setPrimaryColors(colors);
        if (colors.length > 0) {
            mMaskViewTop.setTextColor(colors[0]);
            mMaskViewBottom.setTextColor(colors[0]);
            if (colors.length > 1) {
                mShadowView.setBackgroundColor(colors[1]);
                mMaskViewTop.setBackgroundColor(colors[1]);
                mMaskViewBottom.setBackgroundColor(colors[1]);
            }
        }
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        super.onStateChanged(refreshLayout, oldState, newState);
        switch (newState) {
            case None:
                break;
            case PullDownToRefresh:
                mMaskViewTop.setText(mMaskViewTop.getHint());
                break;
            case PullDownCanceled:
                break;
            case ReleaseToRefresh:
                mMaskViewTop.setText(mMaskViewBottom.getHint());
                break;
            case RefreshReleased:
                break;
            case Refreshing:
                break;
            case RefreshFinish:
                break;
        }
    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout layout, int height, int extendHeight) {
        super.onStartAnimator(layout, height, extendHeight);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(ObjectAnimator.ofFloat(mMaskViewTop, "translationY", mMaskViewTop.getTranslationY(), -mHalfHeaderHeight))
                .with(ObjectAnimator.ofFloat(mMaskViewBottom, "translationY", mMaskViewBottom.getTranslationY(), mHalfHeaderHeight))
                .with(ObjectAnimator.ofFloat(mShadowView, "alpha", mShadowView.getAlpha(), 0));
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mMaskViewTop.setVisibility(View.GONE);
                mMaskViewBottom.setVisibility(View.GONE);
                mShadowView.setVisibility(View.GONE);
                onGameStart();
            }
        });
        animatorSet.setDuration(800);
        animatorSet.setStartDelay(200);
        animatorSet.start();
    }

    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        if (!mManualOperation) {
            mMaskViewTop.setTranslationY(mMaskViewTop.getTranslationY() + mHalfHeaderHeight);
            mMaskViewBottom.setTranslationY(mMaskViewBottom.getTranslationY() - mHalfHeaderHeight);
            mShadowView.setAlpha(1.f);

            mMaskViewTop.setVisibility(View.VISIBLE);
            mMaskViewBottom.setVisibility(View.VISIBLE);
            mShadowView.setVisibility(View.VISIBLE);
        }
        return super.onFinish(layout, success);
    }

    //</editor-fold>
}
