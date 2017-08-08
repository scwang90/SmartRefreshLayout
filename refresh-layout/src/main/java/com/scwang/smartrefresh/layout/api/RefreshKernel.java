package com.scwang.smartrefresh.layout.api;

import android.support.annotation.NonNull;

/**
 * 刷新布局核心功能接口
 * 为功能复杂的 Header 或者 Footer 开放的接口
 * Created by SCWANG on 2017/5/26.
 */

public interface RefreshKernel {

    @NonNull
    RefreshLayout getRefreshLayout();
    @NonNull
    RefreshContent getRefreshContent();

    //<editor-fold desc="状态更改 state changes">
    RefreshKernel setStatePullUpToLoad() ;
    RefreshKernel setStateReleaseToLoad() ;
    RefreshKernel setStateReleaseToRefresh() ;
    RefreshKernel setStatePullDownToRefresh() ;
    RefreshKernel setStatePullDownCanceled() ;
    RefreshKernel setStatePullUpCanceled() ;
    RefreshKernel setStateLoding() ;
    RefreshKernel setStateRefresing() ;
    RefreshKernel setStateLodingFinish() ;
    RefreshKernel setStateRefresingFinish() ;
    RefreshKernel resetStatus() ;
    //</editor-fold>

    //<editor-fold desc="视图位移 Spinner">

    /**
     * 结束视图位移（调用之后，如果没有在初始位移状态，会执行动画回到初始位置）
     * moveSpinner 的取名来自 谷歌官方的 @{@link android.support.v4.widget.SwipeRefreshLayout#moveSpinner(float)}
     */
    RefreshKernel overSpinner() ;

    /**
     * 移动视图到预设距离（dy 会被内部函数计算，将会出现无限接近最大值（height+extendHeader）的阻尼效果）
     * moveSpinner 的取名来自 谷歌官方的 @{@link android.support.v4.widget.SwipeRefreshLayout#moveSpinner(float)}
     * @param dy 距离 (px) 大于0表示下拉 小于0表示上啦
     */
    RefreshKernel moveSpinnerInfinitely(float dy);

    /**
     * 移动视图到指定位置
     * moveSpinner 的取名来自 谷歌官方的 @{@link android.support.v4.widget.SwipeRefreshLayout#moveSpinner(float)}
     * @param spinner 位置 (px)
     * @param isAnimator 标记是否是动画执行
     */
    RefreshKernel moveSpinner(int spinner, boolean isAnimator);

    /**
     * 执行动画使视图位移到指定的 位置
     * moveSpinner 的取名来自 谷歌官方的 @{@link android.support.v4.widget.SwipeRefreshLayout#moveSpinner(float)}
     * @param endSpinner 指定的结束位置 (px)
     */
    RefreshKernel animSpinner(int endSpinner);

    /**
     * 回弹动画
     * @param bounceSpinner 回弹的最大位置 (px)
     */
    RefreshKernel animSpinnerBounce(int bounceSpinner);

    /**
     * 获取 Spinner
     */
    int getSpinner();
    //</editor-fold>

    //<editor-fold desc="请求事件">

    /**
     * 指定在下拉时候为 Header 绘制背景
     * @param backgroundColor 背景颜色
     */
    RefreshKernel requestDrawBackgoundForHeader(int backgroundColor);
    /**
     * 指定在下拉时候为 Footer 绘制背景
     * @param backgroundColor 背景颜色
     */
    RefreshKernel requestDrawBackgoundForFooter(int backgroundColor);
    /**
     * 请求事件
     */
    RefreshKernel requestHeaderNeedTouchEventWhenRefreshing(boolean request);
    /**
     * 请求事件
     */
    RefreshKernel requestFooterNeedTouchEventWhenLoading(boolean request);
    /**
     * 请求重新测量
     */
    RefreshKernel requestRemeasureHeightForHeader();
    /**
     * 请求重新测量
     */
    RefreshKernel requestRemeasureHeightForFooter();
    //</editor-fold>
}
