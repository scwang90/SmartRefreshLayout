package com.scwang.smartrefresh.layout.api;

import android.support.annotation.NonNull;

/**
 * 刷新布局核心功能接口
 * Created by SCWANG on 2017/5/26.
 */

public interface RefreshKernel {

    @NonNull
    RefreshLayout getRefreshLayout();
    @NonNull
    RefreshContent getRefreshContent();

    //<editor-fold desc="注册钩子 Hook">
    RefreshKernel registHeaderHook(RefreshLayoutHookHeader hook);
    RefreshKernel registFooterHook(RefreshLayoutHookFooter hook);
    //</editor-fold>

    //<editor-fold desc="状态更改 state changes">
    RefreshKernel setStatePullUpToLoad();
    RefreshKernel setStateReleaseLoad() ;
    RefreshKernel setStateReleaseRefresh() ;
    RefreshKernel setStatePullDownToRefresh() ;
    RefreshKernel setStatePullDownCanceled() ;
    RefreshKernel setStatePullUpCanceled() ;
    RefreshKernel setStateLoding() ;
    RefreshKernel setStateRefresing() ;
    RefreshKernel resetStatus();
    //</editor-fold>

    //<editor-fold desc="视图位移 Spinner">
    RefreshKernel overSpinner() ;
    RefreshKernel moveSpinnerInfinitely(float dy);
    RefreshKernel moveSpinner(int spinner, boolean isAnimator) ;
    RefreshKernel animSpinner(int endValue) ;

    //</editor-fold>

}
