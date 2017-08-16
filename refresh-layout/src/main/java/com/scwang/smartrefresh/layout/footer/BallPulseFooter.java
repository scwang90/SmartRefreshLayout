package com.scwang.smartrefresh.layout.footer;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.ColorUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.scwang.smartrefresh.layout.R;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ballpulse.BallPulseView;
import com.scwang.smartrefresh.layout.util.DensityUtil;

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
    private SpinnerStyle mSpinnerStyle = SpinnerStyle.Translate;

    //<editor-fold desc="ViewGroup">
    public BallPulseFooter(@NonNull Context context) {
        super(context);
        initView(context, null, 0);
    }

    public BallPulseFooter(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0);
    }

    public BallPulseFooter(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        mBallPulseView = new BallPulseView(context);
        addView(mBallPulseView, WRAP_CONTENT, WRAP_CONTENT);
        setMinimumHeight(DensityUtil.dp2px(60));

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BallPulseFooter);

        int primaryColor = ta.getColor(R.styleable.BallPulseFooter_srlPrimaryColor, 0);
        int accentColor = ta.getColor(R.styleable.BallPulseFooter_srlAccentColor, 0);
        if (primaryColor != 0) {
            mBallPulseView.setNormalColor(primaryColor);
        }
        if (accentColor != 0) {
            mBallPulseView.setAnimatingColor(accentColor);
        }

        mSpinnerStyle = SpinnerStyle.values()[ta.getInt(R.styleable.BallPulseFooter_srlClassicsSpinnerStyle, mSpinnerStyle.ordinal())];

        ta.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpec = makeMeasureSpec(getSize(widthMeasureSpec), AT_MOST);
        int heightSpec = makeMeasureSpec(getSize(heightMeasureSpec), AT_MOST);
        mBallPulseView.measure(widthSpec, heightSpec);
        setMeasuredDimension(
                resolveSize(mBallPulseView.getMeasuredWidth(), widthMeasureSpec),
                resolveSize(mBallPulseView.getMeasuredHeight(), heightMeasureSpec)
        );
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int pwidth = getMeasuredWidth();
        int pheight = getMeasuredHeight();
        int cwidth = mBallPulseView.getMeasuredWidth();
        int cheight = mBallPulseView.getMeasuredHeight();
        int left = pwidth / 2 - cwidth / 2;
        int top = pheight / 2 - cheight / 2;
        mBallPulseView.layout(left, top, left + cwidth, top + cheight);
    }
    //</editor-fold>

    //<editor-fold desc="RefreshFooter">
    @Override
    public void onInitialized(RefreshKernel kernel, int height, int extendHeight) {

    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {
    }

    @Override
    public void onPullingUp(float percent, int offset, int footerHeight, int extendHeight) {
    }

    @Override
    public void onPullReleasing(float percent, int offset, int footerHeight, int extendHeight) {
    }

    @Override
    public void onStartAnimator(RefreshLayout layout, int footerHeight, int extendHeight) {
        mBallPulseView.startAnim();
    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
    }

    @Override
    public int onFinish(RefreshLayout layout, boolean success) {
        mBallPulseView.stopAnim();
        return 0;
    }

    @Override
    public boolean setLoadmoreFinished(boolean finished) {
        return false;
    }

    @Override@Deprecated
    public void setPrimaryColors(int... colors) {
        if (colors.length > 1) {
            mBallPulseView.setNormalColor(colors[1]);
            mBallPulseView.setAnimatingColor(colors[0]);
        } else if (colors.length > 0) {
            mBallPulseView.setNormalColor(ColorUtils.compositeColors(0x99ffffff,colors[0]));
            mBallPulseView.setAnimatingColor(colors[0]);
        }
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @Override
    public SpinnerStyle getSpinnerStyle() {
        return mSpinnerStyle;
    }
    //</editor-fold>

    //<editor-fold desc="API">
    public BallPulseFooter setSpinnerStyle(SpinnerStyle mSpinnerStyle) {
        this.mSpinnerStyle = mSpinnerStyle;
        return this;
    }
    //</editor-fold>
}
