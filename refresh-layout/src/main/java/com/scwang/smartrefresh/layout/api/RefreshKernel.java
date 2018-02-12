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
     * @return RefreshKernel
     */
    RefreshKernel startTwoLevel(boolean open);

    /**
     * 结束关闭二极刷新
     * @return RefreshKernel
     */
    RefreshKernel finishTwoLevel();

    /**
     * 移动视图到指定位置
     * moveSpinner 的取名来自 谷歌官方的 {@link android.support.v4.widget.SwipeRefreshLayout}
     * @param spinner 位置 (px)
     * @param isAnimator 标记是否是动画执行
     * @return RefreshKernel
     */
    RefreshKernel moveSpinner(int spinner, boolean isAnimator);

    /**
     * 执行动画使视图位移到指定的 位置
     * moveSpinner 的取名来自 谷歌官方的 {@link android.support.v4.widget.SwipeRefreshLayout}
     * @param endSpinner 指定的结束位置 (px)
     * @return RefreshKernel
     */
    RefreshKernel animSpinner(int endSpinner);

    //</editor-fold>

    //<editor-fold desc="请求事件">

    /**
     * 指定在下拉时候为 Header 绘制背景
     * @param backgroundColor 背景颜色
     * @return RefreshKernel
     */
    RefreshKernel requestDrawBackgroundForHeader(int backgroundColor);
    /**
     * 指定在下拉时候为 Footer 绘制背景
     * @param backgroundColor 背景颜色
     * @return RefreshKernel
     */
    RefreshKernel requestDrawBackgroundForFooter(int backgroundColor);
    /**
     * 请求事件
     * @param request 请求
     * @return RefreshKernel
     */
    RefreshKernel requestNeedTouchEventWhenRefreshing(boolean request);
    /**
     * 请求事件
     * @param request 请求
     * @return RefreshKernel
     */
    RefreshKernel requestNeedTouchEventWhenLoading(boolean request);
    /**
     * 请求设置默认内容滚动设置
     * @param translation 移动
     * @return RefreshKernel
     */
    RefreshKernel requestDefaultHeaderTranslationContent(boolean translation);
    /**
     * 请求重新测量 headerHeight , 要求 header 高度为 WRAP_CONTENT
     * @return RefreshKernel
     */
    RefreshKernel requestRemeasureHeightForHeader();
    /**
     * 请求重新测量 footerHeight , 要求 footer 高度为 WRAP_CONTENT
     * @return RefreshKernel
     */
    RefreshKernel requestRemeasureHeightForFooter();
    /**
     * 设置二楼回弹时长
     * @param duration 二楼回弹时长
     * @return RefreshKernel
     */
    RefreshKernel requestFloorDuration(int duration);
    //</editor-fold>
}
