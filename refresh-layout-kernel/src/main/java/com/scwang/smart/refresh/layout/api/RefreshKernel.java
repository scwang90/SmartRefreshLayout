package com.scwang.smart.refresh.layout.api;

import android.animation.ValueAnimator;
import android.support.annotation.NonNull;

import com.scwang.smart.refresh.layout.constant.RefreshState;

/**
 * 刷新布局核心功能接口
 * 为功能复杂的 Header 或者 Footer 开放的接口
 * Created by scwang on 2017/5/26.
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
     * @param isDragging true 手指正在拖动 false 回弹动画执行
     * @return RefreshKernel
     */
    RefreshKernel moveSpinner(int spinner, boolean isDragging);

    /**
     * 执行动画使视图位移到指定的 位置
     * moveSpinner 的取名来自 谷歌官方的 {@link android.support.v4.widget.SwipeRefreshLayout}
     * @param endSpinner 指定的结束位置 (px)
     * @return ValueAnimator 如果没有执行动画 null
     */
    ValueAnimator animSpinner(int endSpinner);
    //</editor-fold>

    //<editor-fold desc="请求事件">

    /**
     * 指定在下拉时候为 Header 或 Footer 绘制背景
     * @param internal Header Footer 调用时传 this
     * @param backgroundColor 背景颜色
     * @return RefreshKernel
     */
    RefreshKernel requestDrawBackgroundFor(@NonNull RefreshComponent internal, int backgroundColor);
    /**
     * 请求事件
     * @param internal Header Footer 调用时传 this
     * @param request 请求
     * @return RefreshKernel
     */
    RefreshKernel requestNeedTouchEventFor(@NonNull RefreshComponent internal, boolean request);
    /**
     * 请求设置默认内容滚动设置
     * @param internal Header Footer 调用时传 this
     * @param translation 移动
     * @return RefreshKernel
     */
    RefreshKernel requestDefaultTranslationContentFor(@NonNull RefreshComponent internal, boolean translation);
    /**
     * 请求重新测量 headerHeight 或 footerHeight , 要求 height 高度为 WRAP_CONTENT
     * @param internal Header Footer 调用时传 this
     * @return RefreshKernel
     */
    RefreshKernel requestRemeasureHeightFor(@NonNull RefreshComponent internal);
    /**
     * 设置二楼回弹时长
     * @param duration 二楼回弹时长
     * @return RefreshKernel
     */
    RefreshKernel requestFloorDuration(int duration);
    /**
     * 设置二楼底部上划关闭所占高度的比率
     * @return RefreshKernel
     */
    RefreshKernel requestFloorBottomPullUpToCloseRate(float rate);
    //</editor-fold>
}
