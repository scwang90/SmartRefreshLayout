/**
 * @file XListViewHeader.java
 * @create Apr 18, 2012 5:22:27 PM
 * @author Maxwin
 * @description XListView's header
 */
package com.scwang.smartrefreshheader;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.scwang.smartrefreshheader.waterdrop.WaterDropView;
import com.scwang.smartrefreshlayout.api.RefreshHeader;
import com.scwang.smartrefreshlayout.api.SizeObserver;
import com.scwang.smartrefreshlayout.constant.RefreshState;
import com.scwang.smartrefreshlayout.constant.SpinnerStyle;
import com.scwang.smartrefreshlayout.util.DensityUtil;

import static android.view.View.MeasureSpec.AT_MOST;
import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.getSize;
import static android.view.View.MeasureSpec.makeMeasureSpec;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;


public class WaterDropHeader extends ViewGroup implements RefreshHeader, SizeObserver {

    private ProgressBar mProgressBar;
    private WaterDropView mWaterDropView;
    private RefreshState mState;
    private int mOffset;
    private int mWaterPadding;

    public WaterDropHeader(Context context) {
        super(context);
        initView(context);
    }

    public WaterDropHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        setBackgroundColor(0xff11bbff);
        DensityUtil density = new DensityUtil();
        mWaterDropView = new WaterDropView(context);
//        mWaterDropView.setMaxCircleRadius(density.dip2px(20));
//        mWaterDropView.setMinCircleRadius(density.dip2px(5));
//        mWaterDropView.setWaterDropColor(0xff9ba2ab);
        addView(mWaterDropView, MATCH_PARENT, MATCH_PARENT);
        mWaterDropView.updateComleteState(0);

        mProgressBar = new ProgressBar(context);
        addView(mProgressBar, density.dip2px(27), density.dip2px(27));

        mWaterPadding = density.dip2px(4);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        LayoutParams lpProgress = mProgressBar.getLayoutParams();
        mProgressBar.measure(
                makeMeasureSpec(lpProgress.width, EXACTLY),
                makeMeasureSpec(lpProgress.height, EXACTLY)
        );
        mWaterDropView.measure(
                makeMeasureSpec(getSize(widthMeasureSpec), AT_MOST),
                makeMeasureSpec(Math.max(0, getSize(heightMeasureSpec) - 2 * mWaterPadding), EXACTLY)
        );
        int maxWidth = Math.max(mProgressBar.getMeasuredWidth(), mWaterDropView.getMeasuredHeight());
        int maxHeight = Math.max(mProgressBar.getMeasuredHeight(), mWaterDropView.getMeasuredHeight());
        setMeasuredDimension(resolveSize(maxWidth, widthMeasureSpec), resolveSize(maxHeight, heightMeasureSpec));
    }

    @Override
    public void onSizeDefined(int height, int extendHeight) {

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int measuredWidth = getMeasuredWidth();
        final int measuredHeight = getMeasuredHeight();
        final int widthProgress = mProgressBar.getMeasuredWidth();
        final int heightProgress = mProgressBar.getMeasuredHeight();
        final int leftProgress = measuredWidth / 2 - widthProgress / 2;
        final int topProgress = measuredHeight / 2 - heightProgress / 2;
        mProgressBar.layout(leftProgress, topProgress, leftProgress + widthProgress, topProgress + heightProgress);

        final int widthWaterDrop = mWaterDropView.getMeasuredWidth();
        final int heightWaterDrop = mWaterDropView.getMeasuredHeight();
        final int leftWaterDrop = measuredWidth / 2 - widthWaterDrop / 2;
        final int topWaterDrop = mWaterPadding;

        mWaterDropView.layout(leftWaterDrop, topWaterDrop, leftWaterDrop + widthWaterDrop, topWaterDrop + heightWaterDrop);
    }

    /**
     * 处理处于normal状态的值
     */
    private void handleStateNormal() {
        mWaterDropView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    /**
     * 处理水滴拉伸状态
     */
    private void handleStateStretch() {
        mWaterDropView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    /**
     * 处理水滴ready状态，回弹效果
     */
    private void handleStateReady() {
        mWaterDropView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    /**
     * 处理正在进行刷新状态
     */
    private void handleStateRefreshing() {
//        mWaterDropView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * 处理刷新完毕状态
     */
    private void handleStateEnd() {
        mWaterDropView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onPullingDown(float percent, int offset, int headHeight, int extendHeight) {
        mOffset = offset;
        mWaterDropView.updateComleteState(Math.max(offset - 2 * mWaterPadding, 0), headHeight + extendHeight - 2 * mWaterPadding);
        mWaterDropView.postInvalidate();
    }

    @Override
    public void onReleasing(float percent, int offset, int headHeight, int extendHeight) {
        mOffset = offset;
        if (mState != RefreshState.Refreshing) {
            mWaterDropView.updateComleteState(Math.max(offset - 2 * mWaterPadding, 0), headHeight + extendHeight - 2 * mWaterPadding);
            mWaterDropView.postInvalidate();
        }
    }

    @Override
    public void onStateChanged(RefreshState state) {
        mState = state;
        switch (state) {
            case None:
                handleStateNormal();
                break;
            case PullDownRefresh:
                handleStateStretch();
                break;
            case PullDownCanceled:
                break;
            case ReleaseToRefresh:
                handleStateReady();
                break;
            case Refreshing:
                handleStateRefreshing();
                break;
            case RefreshFinish:
                handleStateEnd();
                break;
        }
    }

    @Override
    public void startAnimator(int headHeight, int extendHeight) {
        Animator animator = mWaterDropView.createAnimator();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mWaterDropView.setVisibility(GONE);
            }
        });
        animator.start();//开始回弹
    }

    @Override
    public void onFinish() {

    }

    @Override
    public void setPrimaryColors(int... colors) {

    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Scale;
    }
}