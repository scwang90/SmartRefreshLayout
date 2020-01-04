package com.scwang.smart.refresh.header.fungame;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.scwang.smart.refresh.layout.api.RefreshContent;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshKernel;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.simple.SimpleComponent;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;
import com.scwang.smart.refresh.layout.util.SmartUtil;

import static android.view.MotionEvent.ACTION_MASK;

/**
 * 游戏 header
 * Created by scwang on 2017/6/17.
 */
@SuppressLint("RestrictedApi")
public abstract class FunGameBase extends SimpleComponent implements RefreshHeader {

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
    public FunGameBase(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final View thisView = this;
        thisView.setMinimumHeight(SmartUtil.dp2px(100));
        mScreenHeightPixels = thisView.getResources().getDisplayMetrics().heightPixels;
        mSpinnerStyle = SpinnerStyle.MatchLayout;
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
                        final double H = mScreenHeightPixels * 2 / 3f;
                        final double x = Math.max(0, dy * 0.5);
                        final double y = Math.min(M * (1 - Math.pow(100, -x / H)), x);// 公式 y = M(1-40^(-x/H))
                        mRefreshKernel.moveSpinner(Math.max(1, (int) y), false);
                    } else {
//                        final double M = mHeaderHeight * 2;
//                        final double H = mScreenHeightPixels * 2 / 3;
//                        final double x = -Math.min(0, dy * 0.5);
//                        final double y = -Math.min(M * (1 - Math.pow(100, -x / H)), x);// 公式 y = M(1-40^(-x/H))
//                        mRefreshKernel.moveSpinner((int) y, false);
                        mRefreshKernel.moveSpinner(1, false);
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
    //</editor-fold>

    //<editor-fold desc="abstract">
//    boolean enableLoadMore;
    protected void onManualOperationStart() {
        if (!mManualOperation) {
            mManualOperation = true;
            mRefreshContent = mRefreshKernel.getRefreshContent();
//            if (mRefreshContent instanceof CoordinatorLayoutListener) {
//                ((CoordinatorLayoutListener) mRefreshContent).onCoordinatorUpdate(true, false);
//            }
//            enableLoadMore = mRefreshKernel.getRefreshLayout().isEnableLoadMore();
//            mRefreshKernel.getRefreshLayout().setEnableLoadMore(false);
            View contentView = mRefreshContent.getView();
            MarginLayoutParams params = (MarginLayoutParams)contentView.getLayoutParams();
            params.topMargin += mHeaderHeight;
            contentView.setLayoutParams(params);
        }
    }

    protected abstract void onManualOperationMove(float percent, int offset, int height, int maxDragHeight);

    protected void onManualOperationRelease() {
        if (mIsFinish) {
            mManualOperation = false;
//            if (mRefreshContent instanceof CoordinatorLayoutListener) {
//                ((CoordinatorLayoutListener) mRefreshContent).onCoordinatorUpdate(true, true);
//            }
//            mRefreshKernel.getRefreshLayout().setEnableLoadMore(enableLoadMore);
            if (mTouchY != -1) {//还没松手
                onFinish(mRefreshKernel.getRefreshLayout(), mLastFinish);
                mRefreshKernel.setState(RefreshState.RefreshFinish);
                mRefreshKernel.animSpinner(0);
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
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
        if (mManualOperation) onManualOperationMove(percent, offset, height, maxDragHeight);
        else {
            mOffset = offset;
            final View thisView = this;
            thisView.setTranslationY(mOffset - mHeaderHeight);
        }
    }

//    @Override
//    public void onPulling(float percent, int offset, int height, int maxDragHeight) {
//        if (mManualOperation) onManualOperationMove(percent, offset, height, maxDragHeight);
//        else {
//            mOffset = offset;
//            setTranslationY(mOffset - mHeaderHeight);
//        }
//    }
//
//    @Override
//    public void onReleasing(float percent, int offset, int height, int maxDragHeight) {
//        onPulling(percent, offset, height, maxDragHeight);
//    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
        mIsFinish = false;
        final View thisView = this;
        thisView.setTranslationY(0);
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        mState = newState;

    }

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {
        mRefreshKernel = kernel;
        mHeaderHeight = height;
        final View thisView = this;
        if (!thisView.isInEditMode()) {
            thisView.setTranslationY(mOffset - mHeaderHeight);
            kernel.requestNeedTouchEventFor(this, true);
//            kernel.requestNeedTouchEventWhenRefreshing(true);
        }
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
//
//    @NonNull
//    @Override
//    public SpinnerStyle getSpinnerStyle() {
//        return SpinnerStyle.MatchLayout;
//    }
    //</editor-fold>
}
