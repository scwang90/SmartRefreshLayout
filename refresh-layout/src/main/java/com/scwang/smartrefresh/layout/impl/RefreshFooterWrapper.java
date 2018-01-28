package com.scwang.smartrefresh.layout.impl;

import android.annotation.SuppressLint;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshInternal;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;

/**
 * 刷新底部包装
 * Created by SCWANG on 2017/5/26.
 */
@SuppressLint("RestrictedApi")
public class RefreshFooterWrapper implements RefreshFooter {

    private View mWrapperView;
    private SpinnerStyle mSpinnerStyle;

    public RefreshFooterWrapper(View wrapper) {
        this.mWrapperView = wrapper;
    }

    @NonNull
    public View getView() {
        return mWrapperView;
    }

    @Override
    public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
        if (mWrapperView instanceof RefreshInternal) {
            return ((RefreshInternal) mWrapperView).onFinish(refreshLayout, success);
        }
		return 0;
	}

    @Override@Deprecated
    public void setPrimaryColors(@ColorInt int ... colors) {
        if (mWrapperView instanceof RefreshInternal) {
            ((RefreshInternal) mWrapperView).setPrimaryColors(colors);
        }
    }

    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        if (mWrapperView instanceof RefreshInternal) {
            return ((RefreshInternal) mWrapperView).getSpinnerStyle();
        }
        if (mSpinnerStyle != null) {
            return mSpinnerStyle;
        }
        ViewGroup.LayoutParams params = mWrapperView.getLayoutParams();
        if (params instanceof SmartRefreshLayout.LayoutParams) {
            mSpinnerStyle = ((SmartRefreshLayout.LayoutParams) params).spinnerStyle;
            if (mSpinnerStyle != null) {
                return mSpinnerStyle;
            }
        }
        if (params != null) {
            if (params.height == 0) {
                return mSpinnerStyle = SpinnerStyle.Scale;
            }
        }
        return mSpinnerStyle = SpinnerStyle.Translate;
    }

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int extendHeight) {
        if (mWrapperView instanceof RefreshInternal) {
            ((RefreshInternal) mWrapperView).onInitialized(kernel, height, extendHeight);
        } else {
            ViewGroup.LayoutParams params = mWrapperView.getLayoutParams();
            if (params instanceof SmartRefreshLayout.LayoutParams) {
                kernel.requestDrawBackgoundForFooter(((SmartRefreshLayout.LayoutParams) params).backgroundColor);
            }
        }
    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return mWrapperView instanceof RefreshInternal && ((RefreshInternal) mWrapperView).isSupportHorizontalDrag();
    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {
        if (mWrapperView instanceof RefreshInternal) {
            ((RefreshInternal) mWrapperView).onHorizontalDrag(percentX, offsetX, offsetMax);
        }
    }

    @Override
    public void onPullingUp(float percent, int offset, int footerHeight, int extendHeight) {
        if (mWrapperView instanceof RefreshFooter) {
            ((RefreshFooter) mWrapperView).onPullingUp(percent, offset, footerHeight, extendHeight);
        } else if (mWrapperView instanceof RefreshHeader) {
            ((RefreshHeader) mWrapperView).onReleasing(percent, offset, footerHeight, extendHeight);
        }
    }

    @Override
    public void onPullReleasing(float percent, int offset, int footerHeight, int extendHeight) {
        if (mWrapperView instanceof RefreshFooter) {
            ((RefreshFooter) mWrapperView).onPullReleasing(percent, offset, footerHeight, extendHeight);
        } else if (mWrapperView instanceof RefreshHeader) {
            ((RefreshHeader) mWrapperView).onPullingDown(percent, offset, footerHeight, extendHeight);
        }
    }

    @Override
    public void onLoadmoreReleased(RefreshLayout refreshLayout, int footerHeight, int extendHeight) {
        if (mWrapperView instanceof RefreshFooter) {
            ((RefreshFooter) mWrapperView).onLoadmoreReleased(refreshLayout, footerHeight, extendHeight);
        } else if (mWrapperView instanceof RefreshHeader) {
            ((RefreshHeader) mWrapperView).onRefreshReleased(refreshLayout, footerHeight, extendHeight);
        }
    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int footerHeight, int extendHeight) {
        if (mWrapperView instanceof RefreshInternal) {
            ((RefreshInternal) mWrapperView).onStartAnimator(refreshLayout, footerHeight, extendHeight);
        }
    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        if (mWrapperView instanceof RefreshInternal) {
            ((RefreshInternal) mWrapperView).onStateChanged(refreshLayout, oldState, newState);

        }
    }

    @Override
    public boolean setLoadmoreFinished(boolean finished) {
        if (mWrapperView instanceof RefreshFooter) {
            ((RefreshFooter) mWrapperView).setLoadmoreFinished(finished);
        }
        return false;
    }
}
