package com.scwang.smartrefreshlayout.header;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.scwang.smartrefreshlayout.R;
import com.scwang.smartrefreshlayout.api.RefreshHeader;
import com.scwang.smartrefreshlayout.api.RefreshLayout;
import com.scwang.smartrefreshlayout.constant.RefreshState;
import com.scwang.smartrefreshlayout.constant.SpinnerStyle;
import com.scwang.smartrefreshlayout.header.bezier.RippleView;
import com.scwang.smartrefreshlayout.header.bezier.RoundDotView;
import com.scwang.smartrefreshlayout.header.bezier.RoundProgressView;
import com.scwang.smartrefreshlayout.header.bezier.WaveView;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * 贝塞尔曲线风格刷新组件
 * Created by lcodecore on 2016/10/2.
 */

public class BezierHeader extends FrameLayout implements RefreshHeader {

    private WaveView mWaveView;
    private RippleView mRippleView;
    private RoundDotView mDotView;
    private RoundProgressView mProgressView;

    public BezierHeader(Context context) {
        this(context,null);
    }

    public BezierHeader(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BezierHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getTag() instanceof String) {
            String tag = getTag().toString();
            String[] colors = tag.split("#");
            for (String color : colors) {
                if (color.matches("[0-9a-fA-F]{6,8}")) {
                    if (color.equals(colors[colors.length - 1])) {
                        setAccentColor(Color.parseColor("#"+color));
                    } else {
                        setPrimary(Color.parseColor("#"+color));
                    }
                }
            }
        }
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        /**
         * 初始化headView
         */
        mWaveView = new WaveView(getContext());
        mRippleView = new RippleView(getContext());
        mDotView = new RoundDotView(getContext());
        mProgressView = new RoundProgressView(getContext());
        if (isInEditMode()) {
            this.addView(mWaveView, MATCH_PARENT, MATCH_PARENT);
            this.addView(mProgressView, MATCH_PARENT, MATCH_PARENT);
            mWaveView.setHeadHeight(1000);
        } else {
            this.addView(mWaveView, MATCH_PARENT, WRAP_CONTENT);
            this.addView(mDotView, MATCH_PARENT, WRAP_CONTENT);
            this.addView(mProgressView, MATCH_PARENT, WRAP_CONTENT);
            this.addView(mRippleView, MATCH_PARENT, WRAP_CONTENT);
            //mProgressView.setVisibility(View.INVISIBLE);
            mProgressView.setScaleX(0);
            mProgressView.setScaleY(0);
        }


        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BezierHeader);

        int primaryColor = ta.getColor(R.styleable.BezierHeader_srlPrimaryColor, 0);
        int accentColor = ta.getColor(R.styleable.BezierHeader_srlAccentColor, 0);
        if (primaryColor != 0) {
            setPrimary(primaryColor);
        }
        if (accentColor != 0) {
            setAccentColor(primaryColor);
        }

        ta.recycle();
    }

    public void setPrimary(int color) {
        mWaveView.setWaveColor(color);
        mProgressView.setBackColor(color);
    }

    public void setAccentColor(int color) {
        mDotView.setDotColor(color);
        mRippleView.setFrontColor(color);
        mProgressView.setFrontColor(color);
    }

    public void setBackColorId(int colorId) {
        setPrimary(ContextCompat.getColor(getContext(), colorId));
    }

    public void setFrontColorId(int colorId) {
        setAccentColor(ContextCompat.getColor(getContext(), colorId));
    }

    @Override
    public void setPrimaryColors(int... colors) {
        if (colors.length > 0) {
            setPrimary(colors[0]);
        }
        if (colors.length > 1) {
            setAccentColor(colors[1]);
        }
    }

    @NonNull
    public View getView() {
        return this;
    }

    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Scale;
    }

    @Override
    public void onPullingDown(float percent, int offset, int headHeight, int extendHeight) {
        mWaveView.setHeadHeight(Math.min(headHeight, offset));
        mWaveView.setWaveHeight((int)(1.9f*Math.max(0, offset - headHeight)));
        mDotView.setFraction(percent);
    }

    @Override
    public void onReleasing(float percent, int offset, int headHeight, int extendHeight) {
        mWaveView.setHeadHeight(Math.min(headHeight, offset));
        mWaveView.setWaveHeight((int)(1.9f*Math.max(0, offset - headHeight)));
        mDotView.setFraction(percent);
    }

    @Override
    public void startAnimator(RefreshLayout layout, int headHeight, int extendHeight) {
        mWaveView.setHeadHeight(headHeight);
        ValueAnimator animator = ValueAnimator.ofInt(
                mWaveView.getWaveHeight(), 0,
                -(int)(mWaveView.getWaveHeight()*0.8),0,
                -(int)(mWaveView.getWaveHeight()*0.4),0);
        animator.addUpdateListener(animation -> {
            mWaveView.setWaveHeight((int) animation.getAnimatedValue()/2);
            mWaveView.invalidate();
        });
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(800);
        animator.start();
        /*处理圈圈进度条**/
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1, 0);
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mDotView.setVisibility(INVISIBLE);
                //mProgressView.setVisibility(View.VISIBLE);
                //mProgressView.animate().setDuration(300);
                mProgressView.animate().scaleX((float) 1.0);
                mProgressView.animate().scaleY((float) 1.0);
                mProgressView.postDelayed(() -> {
                    mProgressView.startAnim();
                },200);
            }
        });

        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(300);
        valueAnimator.addUpdateListener(animation -> mDotView.setAlpha((Float) animation.getAnimatedValue()));
        valueAnimator.start();
    }

    @Override
    public void onFinish(RefreshLayout layout) {
        mProgressView.stopAnim();
        mProgressView.animate().scaleX(0f);
        mProgressView.animate().scaleY(0f);
        mRippleView.startReveal();
    }

    @Override
    public void onStateChanged(RefreshState state) {
        switch (state) {
            case None:
                break;
            case PullDownToRefresh:
                mDotView.setAlpha(1);
                mDotView.setVisibility(View.VISIBLE);
                mProgressView.setScaleX(0);
                mProgressView.setScaleY(0);
                //mProgressView.setVisibility(View.INVISIBLE);
                break;
            case PullToUpLoad:
                break;
            case Refreshing:
                break;
            case Loading:
                break;
        }
    }
}
