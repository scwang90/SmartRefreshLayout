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
        this.initView(context, null, 0);
    }

    public FunGameBase(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initView(context, attrs, 0);
    }

    public FunGameBase(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FunGameBase(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
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
                    mRefreshKernel.moveSpinnerInfinitely(0);
                    break;
                case MotionEvent.ACTION_MOVE:
                    float dy = event.getRawY() - mTouchY;
                    mRefreshKernel.moveSpinnerInfinitely(Math.max(dy, 0));
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

    protected void onManualOperationStart() {
        mManualOperation = true;
        mRefreshContent = mRefreshKernel.getRefreshContent();
        mRefreshContent.getView().offsetTopAndBottom(mHeaderHeight);
    }

    protected void onManualOperationMove(float percent, int offset, int headHeight, int extendHeight) {

    }

    protected void onManualOperationRelease() {
        mManualOperation = false;
        mRefreshContent.getView().offsetTopAndBottom(-mHeaderHeight);
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
    public void onStateChanged(RefreshState state) {
        mState = state;
    }

    @Override
    public void onSizeDefined(RefreshKernel kernel, int height, int extendHeight) {
        mRefreshKernel = kernel;
        mHeaderHeight = height;
        setTranslationY(mOffset - mHeaderHeight);
        kernel.registHeaderHook(new RefreshLayoutHeaderHooker() {
            @Override
            public void onHookFinishRefresh(SuperMethod method, RefreshLayout layout) {
                if (mManualOperation) {
                    mManualOperationListener = method::invoke;
                    onFinish(layout);
                } else {
                    method.invoke();
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
