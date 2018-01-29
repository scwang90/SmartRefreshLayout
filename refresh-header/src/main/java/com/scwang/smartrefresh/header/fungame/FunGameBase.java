package com.scwang.smartrefresh.header.fungame;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.scwang.smartrefresh.layout.api.RefreshContent;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;

import static android.view.MotionEvent.ACTION_MASK;

/**
 * 游戏 header
 * Created by SCWANG on 2017/6/17.
 */
@SuppressLint("RestrictedApi")
public class FunGameBase extends FrameLayout implements RefreshHeader {

    //<editor-fold desc="Field">
    protected int mOffset;
    protected int mHeaderHeight;
    protected int mScreenHeightPixels;
    protected float mTouchY;
    protected boolean mIsFinish;
    protected boolean mLastFinish;
    protected boolean mManualOperation;
    protected RefreshState mState;
    protected RefreshKernel mRefreshKernel;
    protected RefreshContent mRefreshContent;
    //</editor-fold>

    //<editor-fold desc="View">
    public FunGameBase(Context context) {
        super(context);
        initView(context);
    }

    public FunGameBase(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public FunGameBase(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public FunGameBase(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        mScreenHeightPixels = context.getResources().getDisplayMetrics().heightPixels;
    }

    @Override
    public void setTranslationY(float translationY) {
        if (!isInEditMode()) {
            super.setTranslationY(translationY);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mState == RefreshState.Refreshing || super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mState == RefreshState.Refreshing || mState == RefreshState.RefreshFinish) {
            if (!mManualOperation) {
                onManualOperationStart();
            }
            switch (event.getAction() & ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    mTouchY = event.getRawY();
                    mRefreshKernel.moveSpinner(0, true);
                    break;
                case MotionEvent.ACTION_MOVE:
                    float dy = event.getRawY() - mTouchY;
                    if (dy >= 0) {
                        final double M = mHeaderHeight * 2;
                        final double H = mScreenHeightPixels * 2 / 3;
                        final double x = Math.max(0, dy * 0.5);
                        final double y = Math.min(M * (1 - Math.pow(100, -x / H)), x);// 公式 y = M(1-40^(-x/H))
                        mRefreshKernel.moveSpinner((int) y, false);
                    } else {
                        final double M = mHeaderHeight * 2;
                        final double H = mScreenHeightPixels * 2 / 3;
                        final double x = -Math.min(0, dy * 0.5);
                        final double y = -Math.min(M * (1 - Math.pow(100, -x / H)), x);// 公式 y = M(1-40^(-x/H))
                        mRefreshKernel.moveSpinner((int) y, false);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    onManualOperationRelease();
                    mTouchY = -1;
                    if (mIsFinish) {
                        mRefreshKernel.moveSpinner(mHeaderHeight, true);
                    }
                    break;
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mRefreshKernel = null;
        mRefreshContent = null;
    }

    //</editor-fold>

    //<editor-fold desc="abstract">
    boolean enableLoadmore;
    protected void onManualOperationStart() {
        if (!mManualOperation) {
            mManualOperation = true;
            mRefreshContent = mRefreshKernel.getRefreshContent();
            enableLoadmore = mRefreshKernel.getRefreshLayout().isEnableLoadMore();
            mRefreshKernel.getRefreshLayout().setEnableLoadMore(false);
            View contentView = mRefreshContent.getView();
            MarginLayoutParams params = (MarginLayoutParams)contentView.getLayoutParams();
            params.topMargin += mHeaderHeight;
            contentView.setLayoutParams(params);
        }
    }

    protected void onManualOperationMove(float percent, int offset, int height, int extendHeight) {

    }

    protected void onManualOperationRelease() {
        if (mIsFinish) {
            mManualOperation = false;
            mRefreshKernel.getRefreshLayout().setEnableLoadMore(enableLoadmore);
            if (mTouchY != -1) {//还没松手
                onFinish(mRefreshKernel.getRefreshLayout(), mLastFinish);
//                mRefreshKernel.setStateRefresingFinish();
                mRefreshKernel.setState(RefreshState.RefreshFinish);
                mRefreshKernel.animSpinner(0);
//                mRefreshKernel.getRefreshLayout().finishRefresh(0);
            } else {
                mRefreshKernel.moveSpinner(mHeaderHeight, true);
            }
            View contentView = mRefreshContent.getView();
            MarginLayoutParams params = (MarginLayoutParams)contentView.getLayoutParams();
            params.topMargin -= mHeaderHeight;
            contentView.setLayoutParams(params);
        } else {
            mRefreshKernel.moveSpinner(0, true);
        }
    }
    //</editor-fold>

    //<editor-fold desc="RefreshHeader">

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {
    }

    @Override
    public void onPulling(float percent, int offset, int height, int extendHeight) {
        if (mManualOperation) onManualOperationMove(percent, offset, height, extendHeight);
        else {
            mOffset = offset;
            setTranslationY(mOffset - mHeaderHeight);
        }
    }

    @Override
    public void onReleasing(float percent, int offset, int height, int extendHeight) {
        onPulling(percent, offset, height, extendHeight);
    }

    @Override
    public void onReleased(RefreshLayout layout, int height, int extendHeight) {

    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout layout, int height, int extendHeight) {
        mIsFinish = false;
        setTranslationY(0);
    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        mState = newState;
    }

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int extendHeight) {
        mRefreshKernel = kernel;
        mHeaderHeight = height;
        setTranslationY(mOffset - mHeaderHeight);
        kernel.requestHeaderNeedTouchEventWhenRefreshing(true);
    }

    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        mLastFinish = success;
        if (!mIsFinish) {
            mIsFinish = true;
            if (mManualOperation) {
                if (mTouchY == -1) {//已经放手
                    onManualOperationRelease();
                    onFinish(layout, success);
                    return 0;
                }
                return Integer.MAX_VALUE;
            }
        }
        return 0;
    }

    /**
     * @param colors 对应Xml中配置的 srlPrimaryColor srlAccentColor
     * @deprecated 请使用 {@link RefreshLayout#setPrimaryColorsId(int...)}
     */
    @Override@Deprecated
    public void setPrimaryColors(@ColorInt int ... colors) {
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.MatchLayout;
    }
    //</editor-fold>
}
