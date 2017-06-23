package com.scwang.smartrefresh.header.fungame;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.scwang.smartrefresh.layout.api.RefreshContent;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.impl.RefreshLayoutHeaderHooker;

import static android.view.MotionEvent.ACTION_MASK;

/**
 * 游戏 header
 * Created by SCWANG on 2017/6/17.
 */

public class FunGameBase extends FrameLayout implements RefreshHeader {

    //<editor-fold desc="Field">
    protected int mOffset;
    protected int mHeaderHeight;
    protected RefreshState mState;
    protected boolean mManualOperation;
    protected Runnable mManualOperationListener;
    protected float mTouchY;
    protected RefreshKernel mRefreshKernel;
    protected RefreshContent mRefreshContent;
    //</editor-fold>

    //<editor-fold desc="View">
    public FunGameBase(Context context) {
        super(context);
    }

    public FunGameBase(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FunGameBase(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FunGameBase(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setTranslationY(float translationY) {
        if (!isInEditMode()) {
            super.setTranslationY(translationY);
        }
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
        params.height = -3;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mState == RefreshState.Refreshing || super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mState == RefreshState.Refreshing) {
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
                    mRefreshKernel.moveSpinnerInfinitely(dy);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    mTouchY = 0;
                    mRefreshKernel.moveSpinner(mHeaderHeight, true);
                    onManualOperationRelease();
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
        mManualOperation = true;
        mRefreshContent = mRefreshKernel.getRefreshContent();
        mRefreshContent.getView().offsetTopAndBottom(mHeaderHeight);
        enableLoadmore = mRefreshKernel.getRefreshLayout().isEnableLoadmore();
        mRefreshKernel.getRefreshLayout().setEnableLoadmore(false);
    }

    protected void onManualOperationMove(float percent, int offset, int headHeight, int extendHeight) {

    }

    protected void onManualOperationRelease() {
        mManualOperation = false;
        mRefreshContent.getView().offsetTopAndBottom(-mHeaderHeight);
        mRefreshKernel.getRefreshLayout().setEnableLoadmore(enableLoadmore);
        if (mManualOperationListener != null) {
            mManualOperationListener.run();
        }
    }
    //</editor-fold>

    //<editor-fold desc="RefreshHeader">
    @Override
    public void onPullingDown(float percent, int offset, int headHeight, int extendHeight) {
        if (mManualOperation) onManualOperationMove(percent, offset, headHeight, extendHeight);
        else {
            mOffset = offset;
            setTranslationY(mOffset - mHeaderHeight);
        }
    }

    @Override
    public void onReleasing(float percent, int offset, int headHeight, int extendHeight) {
        onPullingDown(percent, offset, headHeight, extendHeight);
    }

    @Override
    public void onStartAnimator(RefreshLayout layout, int headHeight, int extendHeight) {

    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        mState = newState;
    }

    @Override
    public void onInitialized(RefreshKernel kernel, int height, int extendHeight) {
        mRefreshKernel = kernel;
        mHeaderHeight = height;
        setTranslationY(mOffset - mHeaderHeight);
        kernel.registHeaderHook(new RefreshLayoutHeaderHooker() {
            @Override
            public void onHookFinishRefresh(SuperMethod supper, RefreshLayout layout) {
                if (mManualOperation) {
                    mManualOperationListener = supper::invoke;
                    onFinish(layout);
                } else {
                    mManualOperationListener = null;
                    supper.invoke();
                }
            }
        });
    }

    @Override
    public void onFinish(RefreshLayout layout) {

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
        return SpinnerStyle.FixedFront;
    }
    //</editor-fold>
}
