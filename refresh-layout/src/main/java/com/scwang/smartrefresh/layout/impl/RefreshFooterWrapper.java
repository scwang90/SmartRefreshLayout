package com.scwang.smartrefresh.layout.impl;

import android.annotation.SuppressLint;
import android.view.View;

import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.internal.InternalAbstract;

/**
 * 刷新底部包装
 * Created by SCWANG on 2017/5/26.
 */
@SuppressLint("ViewConstructor")
public class RefreshFooterWrapper extends InternalAbstract implements RefreshFooter/*, InvocationHandler */{

//    private RefreshKernel mRefreshKernel;
//    private Method mRequestDrawBackgroundForHeaderMethod;
//    private Method mRequestRemeasureHeightForHeaderMethod;
//    private Method mRequestNeedTouchEventWhenRefreshingMethod;

    public RefreshFooterWrapper(View wrapper) {
        super(wrapper);
    }

//    @Override
//    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {
//        if (mWrapperView instanceof RefreshInternal) {
//            RefreshKernel proxy = (RefreshKernel) Proxy.newProxyInstance(RefreshKernel.class.getClassLoader(), new Class[]{RefreshKernel.class}, this);
//            proxy.requestDrawBackgroundForHeader(0);
//            proxy.requestRemeasureHeightForHeader();
//            proxy.requestNeedTouchEventWhenRefreshing(false);
//            mRefreshKernel = kernel;
//            ((RefreshInternal) mWrapperView).onInitialized(proxy, height, maxDragHeight);
//        } else {
//            ViewGroup.LayoutParams params = mWrapperView.getLayoutParams();
//            if (params instanceof SmartRefreshLayout.LayoutParams) {
//                kernel.requestDrawBackgroundForFooter(((SmartRefreshLayout.LayoutParams) params).backgroundColor);
//            }
//        }
//    }

    @Override
    public boolean setNoMoreData(boolean noMoreData) {
        return mWrappedInternal instanceof RefreshFooter && ((RefreshFooter) mWrappedInternal).setNoMoreData(noMoreData);
    }

//    @Override
//    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//        Object returnValue = null;
//        if (mRefreshKernel != null) {
//            if (method.equals(mRequestDrawBackgroundForHeaderMethod)) {
//                mRefreshKernel.requestDrawBackgroundForFooter((int) args[0]);
//            } else if (method.equals(mRequestRemeasureHeightForHeaderMethod)) {
//                mRefreshKernel.requestRemeasureHeightForFooter();
//            } else if (method.equals(mRequestNeedTouchEventWhenRefreshingMethod)) {
//                mRefreshKernel.requestNeedTouchEventWhenLoading((boolean) args[0]);
//            } else {
//                returnValue = method.invoke(mRefreshKernel, args);
//            }
//        }
//        if (method.getReturnType().equals(RefreshKernel.class)) {
//            if (mRefreshKernel == null && RefreshKernel.class.equals(method.getDeclaringClass())) {
//                if (mRequestDrawBackgroundForHeaderMethod == null) {
//                    mRequestDrawBackgroundForHeaderMethod = method;
//                } else if (mRequestRemeasureHeightForHeaderMethod == null) {
//                    mRequestRemeasureHeightForHeaderMethod = method;
//                } else if (mRequestNeedTouchEventWhenRefreshingMethod == null) {
//                    mRequestNeedTouchEventWhenRefreshingMethod = method;
//                }
//            }
//            return proxy;
//        }
//        return returnValue;
//    }
}
