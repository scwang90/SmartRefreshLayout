package com.scwang.smartrefresh.layout.footer;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.ColorUtils;
import android.util.AttributeSet;

import com.scwang.smartrefresh.layout.R;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.internal.InternalAbstract;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 球脉冲底部加载组件
 * Created by SCWANG on 2017/5/30.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class BallPulseFooter extends InternalAbstract implements RefreshFooter {

    //<editor-fold desc="属性变量">
    public static final int DEFAULT_SIZE = 50; //dp

    private boolean mManualNormalColor;
    private boolean mManualAnimationColor;
    private SpinnerStyle mSpinnerStyle = SpinnerStyle.Translate;

    private Paint mPaint;

    private int mNormalColor = 0xffeeeeee;
    private int mAnimatingColor = 0xffe75946;

    private float mCircleSpacing;
    private float[] mScaleFloats = new float[]{1f, 1f, 1f};


    private boolean mIsStarted = false;
    private ArrayList<ValueAnimator> mAnimators;
    private Map<ValueAnimator, ValueAnimator.AnimatorUpdateListener> mUpdateListeners = new HashMap<>();;
    //</editor-fold>

    //<editor-fold desc="构造方法">
    public BallPulseFooter(@NonNull Context context) {
        this(context, null);
    }

    public BallPulseFooter(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BallPulseFooter(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setMinimumHeight(DensityUtil.dp2px(60));

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BallPulseFooter);

        if (ta.hasValue(R.styleable.BallPulseFooter_srlNormalColor)) {
            setNormalColor(ta.getColor(R.styleable.BallPulseFooter_srlNormalColor, 0));
        }
        if (ta.hasValue(R.styleable.BallPulseFooter_srlAnimatingColor)) {
            setAnimatingColor(ta.getColor(R.styleable.BallPulseFooter_srlAnimatingColor, 0));
        }

        mSpinnerStyle = SpinnerStyle.values()[ta.getInt(R.styleable.BallPulseFooter_srlClassicsSpinnerStyle, mSpinnerStyle.ordinal())];

        ta.recycle();


        mCircleSpacing = DensityUtil.dp2px(4);

        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

        mAnimators = new ArrayList<>();
        final int[] delays = new int[]{120, 240, 360};
        for (int i = 0; i < 3; i++) {
            final int index = i;

            ValueAnimator animator = ValueAnimator.ofFloat(1, 0.3f, 1);

            animator.setDuration(750);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setStartDelay(delays[i]);

            mUpdateListeners.put(animator, new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mScaleFloats[index] = (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
            mAnimators.add(animator);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAnimators != null) for (int i = 0; i < mAnimators.size(); i++) {
            mAnimators.get(i).cancel();
            mAnimators.get(i).removeAllListeners();
            mAnimators.get(i).removeAllUpdateListeners();
        }
    }

    //</editor-fold>

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int width = getWidth(), height = getHeight();
        float radius = (Math.min(width, height) - mCircleSpacing * 2) / 6;
        float x = width / 2 - (radius * 2 + mCircleSpacing);
        float y = height / 2;
        for (int i = 0; i < 3; i++) {
            canvas.save();
            float translateX = x + (radius * 2) * i + mCircleSpacing * i;
            canvas.translate(translateX, y);
            canvas.scale(mScaleFloats[i], mScaleFloats[i]);
            canvas.drawCircle(0, 0, radius, mPaint);
            canvas.restore();
        }
        super.dispatchDraw(canvas);
    }


    //<editor-fold desc="刷新方法 - RefreshFooter">

    @Override
    public void onStartAnimator(@NonNull RefreshLayout layout, int height, int extendHeight) {
        if (mIsStarted) return;

        for (int i = 0; i < mAnimators.size(); i++) {
            ValueAnimator animator = mAnimators.get(i);

            //when the animator restart , add the updateListener again because they was removed by animator stop .
            ValueAnimator.AnimatorUpdateListener updateListener = mUpdateListeners.get(animator);
            if (updateListener != null) {
                animator.addUpdateListener(updateListener);
            }
            animator.start();
        }
        mIsStarted = true;
        mPaint.setColor(mAnimatingColor);
    }

    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        if (mAnimators != null && mIsStarted) {
            mIsStarted = false;
            mScaleFloats = new float[]{1f, 1f, 1f};
            for (ValueAnimator animator : mAnimators) {
                if (animator != null) {
                    animator.removeAllUpdateListeners();
                    animator.end();
                }
            }
        }
        mPaint.setColor(mNormalColor);
        return 0;
    }

    @Override
    public boolean setNoMoreData(boolean noMoreData) {
        return false;
    }

    @Override@Deprecated
    public void setPrimaryColors(@ColorInt int... colors) {
        if (!mManualAnimationColor && colors.length > 1) {
            setAnimatingColor(colors[0]);
            mManualAnimationColor = false;
        }
        if (!mManualNormalColor) {
            if (colors.length > 1) {
                setNormalColor(colors[1]);
            } else if (colors.length > 0) {
                setNormalColor(ColorUtils.compositeColors(0x99ffffff,colors[0]));
            }
            mManualNormalColor = false;
        }
    }

    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return mSpinnerStyle;
    }

    //</editor-fold>

    //<editor-fold desc="开放接口 - API">

    public BallPulseFooter setSpinnerStyle(SpinnerStyle mSpinnerStyle) {
        this.mSpinnerStyle = mSpinnerStyle;
        return this;
    }

    public BallPulseFooter setNormalColor(@ColorInt int color) {
        mNormalColor = color;
        mManualNormalColor = true;
        if (!mIsStarted) {
            mPaint.setColor(color);
        }
        return this;
    }

    public BallPulseFooter setAnimatingColor(@ColorInt int color) {
        mAnimatingColor = color;
        mManualAnimationColor = true;
        if (mIsStarted) {
            mPaint.setColor(color);
        }
        return this;
    }

    //</editor-fold>
}
