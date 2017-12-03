package com.scwang.smartrefresh.layout.api;

import android.support.annotation.NonNull;

import com.scwang.smartrefresh.layout.constant.RefreshState;

/**
 * 刷新布局核心功能接口
 * 为功能复杂的 Header 或者 Footer 开放的接口
 * Created by SCWANG on 2017/5/26.
 */

@SuppressWarnings({"unused", "UnusedReturnValue", "SameParameterValue"})
public interface RefreshKernel {

    @NonNull
    RefreshLayout getRefreshLayout();
    @NonNull
    RefreshContent getRefreshContent();

    RefreshKernel setState(@NonNull RefreshState state);

    //<editor-fold desc="视图位移 Spinner">

    /**
     * 开始执行二极刷新
     * @param open 是否展开
     */
    void startTwoLevel(boolean open);

    /**
     * 结束关闭二极刷新
     */
    void finishTwoLevel();

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
     * 请求设置默认内容滚动设置
     */
    RefreshKernel requestDefaultHeaderTranslationContent(boolean translation);

    /**
     * 请求重新测量
     */
    RefreshKernel requestRemeasureHeightForHeader();
    /**
     * 请求重新测量
     */
    RefreshKernel requestRemeasureHeightForFooter();

    /**
     * 设置二楼回弹时长
     */
    RefreshKernel requestFloorDuration(int duration);
    //</editor-fold>
}
