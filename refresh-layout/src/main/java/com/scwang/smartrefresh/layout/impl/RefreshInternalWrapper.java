package com.scwang.smartrefresh.layout.impl;

import android.annotation.SuppressLint;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshInternal;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * 组件底部包装
 * Created by SCWANG on 2017/5/26.
 */
@SuppressLint("RestrictedApi")
public class RefreshInternalWrapper implements RefreshInternal {

    View mWrapperView;
    private SpinnerStyle mSpinnerStyle;

    RefreshInternalWrapper(View wrapper) {
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
            if (params.height == 0 || params.height == MATCH_PARENT) {
                return mSpinnerStyle = SpinnerStyle.Scale;
            }
        }
        return mSpinnerStyle = SpinnerStyle.Translate;
    }

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int extendHeight) {
        if (mWrapperView instanceof RefreshInternal) {
            ((RefreshInternal) mWrapperView).onInitialized(kernel, height, extendHeight);
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
    public void onPulling(float percent, int offset, int height, int extendHeight) {
        if (mWrapperView instanceof RefreshInternal) {
            ((RefreshInternal) mWrapperView).onPulling(percent, offset, height, extendHeight);
        }
    }

    @Override
    public void onReleasing(float percent, int offset, int height, int extendHeight) {
        if (mWrapperView instanceof RefreshInternal) {
            ((RefreshInternal) mWrapperView).onReleasing(percent, offset, height, extendHeight);
        }
    }

    @Override
    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int extendHeight) {
        if (mWrapperView instanceof RefreshInternal) {
            ((RefreshInternal) mWrapperView).onReleased(refreshLayout, height, extendHeight);
        }
    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int extendHeight) {
        if (mWrapperView instanceof RefreshInternal) {
            ((RefreshInternal) mWrapperView).onStartAnimator(refreshLayout, height, extendHeight);
        }
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        if (mWrapperView instanceof RefreshInternal) {
            ((RefreshInternal) mWrapperView).onStateChanged(refreshLayout, oldState, newState);
        }
    }
}
