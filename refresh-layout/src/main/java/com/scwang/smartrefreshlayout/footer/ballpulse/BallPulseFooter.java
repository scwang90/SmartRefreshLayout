package com.scwang.smartrefreshlayout.footer.ballpulse;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.scwang.smartrefreshlayout.R;
import com.scwang.smartrefreshlayout.api.RefreshFooter;
import com.scwang.smartrefreshlayout.constant.RefreshState;
import com.scwang.smartrefreshlayout.constant.SpinnerStyle;
import com.scwang.smartrefreshlayout.util.DensityUtil;

import static android.view.View.MeasureSpec.AT_MOST;
import static android.view.View.MeasureSpec.getSize;
import static android.view.View.MeasureSpec.makeMeasureSpec;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * 球脉冲底部加载组件
 * Created by SCWANG on 2017/5/30.
 */

public class BallPulseFooter extends ViewGroup implements RefreshFooter {

    private BallPulseView mBallPulseView;

    public BallPulseFooter(@NonNull Context context) {
        super(context);
        init(context, null, 0);
    }

    public BallPulseFooter(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public BallPulseFooter(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mBallPulseView = new BallPulseView(context);
        addView(mBallPulseView, WRAP_CONTENT, WRAP_CONTENT);
        setMinimumHeight(DensityUtil.dp2px(60));

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BallPulseFooter);

        int primaryColor = ta.getColor(R.styleable.BallPulseFooter_srlPrimaryColor, 0);
        int accentColor = ta.getColor(R.styleable.BallPulseFooter_srlAccentColor, 0);
        if (primaryColor != 0) {
            mBallPulseView.setAnimatingColor(primaryColor);
        }
        if (accentColor != 0) {
            mBallPulseView.setNormalColor(primaryColor);
        }

        ta.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mBallPulseView != null) {
            int heightSpec = makeMeasureSpec(getSize(heightMeasureSpec), AT_MOST);
            int widthSpec = makeMeasureSpec(getSize(widthMeasureSpec), AT_MOST);
            mBallPulseView.measure(widthSpec, heightSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mBallPulseView != null) {
            int pwidth = getMeasuredWidth();
            int pheight = getMeasuredHeight();
            int cwidth = mBallPulseView.getMeasuredWidth();
            int cheight = mBallPulseView.getMeasuredHeight();
            int left = pwidth / 2 - cwidth / 2;
            int top = pheight / 2 - cheight / 2;
            mBallPulseView.layout(left, top, left + cwidth, top + cheight);
        }
    }

    @Override
    public void onPullingUp(int offset, int bottomHeight, int extendHeight) {
        mBallPulseView.onPullingUp(offset,bottomHeight,extendHeight);
    }

    @Override
    public void onPullReleasing(int offset, int bottomHeight, int extendHeight) {
        mBallPulseView.onPullReleasing(offset,bottomHeight,extendHeight);
    }

    @Override
    public void startAnimator(int bottomHeight, int extendHeight) {
        mBallPulseView.startAnimator(bottomHeight, extendHeight);
    }

    @Override
    public void onStateChanged(RefreshState state) {
        mBallPulseView.onStateChanged(state);
    }

    @Override
    public void onFinish() {
        mBallPulseView.onFinish();
    }

    @Override
    public void setPrimaryColors(int... colors) {
        mBallPulseView.setPrimaryColors(colors);
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
}
