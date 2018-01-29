package com.scwang.smartrefresh.layout.api;

import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnMultiPurposeListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

/**
 * 刷新布局
 * Created by SCWANG on 2017/5/26.
 */

@SuppressWarnings({"UnusedReturnValue", "SameParameterValue", "unused"})
public interface RefreshLayout {

    RefreshLayout setFooterHeight(float dp);

    RefreshLayout setFooterHeightPx(int px);

    RefreshLayout setHeaderHeight(float dp);

    RefreshLayout setHeaderHeightPx(int px);

    RefreshLayout setHeaderInsetStart(float insetDp);

    RefreshLayout setHeaderInsetStartPx(int insetPx);

    RefreshLayout setFooterInsetStart(float insetDp);

    RefreshLayout setFooterInsetStartPx(int insetPx);

    /**
     * 显示拖动高度/真实拖动高度（默认0.5，阻尼效果）
     */
    RefreshLayout setDragRate(@FloatRange(from = 0,to = 1) float rate);

    /**
     * 设置下拉最大高度和Header高度的比率（将会影响可以下拉的最大高度）
     */
    RefreshLayout setHeaderMaxDragRate(@FloatRange(from = 1,to = 100) float rate);

    /**
     * 设置上拉最大高度和Footer高度的比率（将会影响可以上拉的最大高度）
     */
    RefreshLayout setFooterMaxDragRate(@FloatRange(from = 1,to = 100) float rate);

    /**
     * 设置 触发刷新距离 与 HeaderHeight 的比率
     */
    RefreshLayout setHeaderTriggerRate(@FloatRange(from = 0,to = 1.0) float rate);

    /**
     * 设置 触发加载距离 与 FooterHeight 的比率
     */
    RefreshLayout setFooterTriggerRate(@FloatRange(from = 0,to = 1.0) float rate);

    /**
     * 设置回弹显示插值器
     */
    RefreshLayout setReboundInterpolator(@NonNull Interpolator interpolator);

    /**
     * 设置回弹动画时长
     */
    RefreshLayout setReboundDuration(int duration);

    /**
     * 设置是否启用上拉加载更多（默认启用）
     */
    RefreshLayout setEnableLoadMore(boolean enable);

    /**
     * 是否启用下拉刷新（默认启用）
     */
    RefreshLayout setEnableRefresh(boolean enable);

    /**
     * 设置是否启在下拉Header的同时下拉内容
     */
    RefreshLayout setEnableHeaderTranslationContent(boolean enable);

    /**
     * 设置是否启在上拉Footer的同时上拉内容
     */
    RefreshLayout setEnableFooterTranslationContent(boolean enable);

    /**
     * 设置是否开启在刷新时候禁止操作内容视图
     */
    RefreshLayout setDisableContentWhenRefresh(boolean disable);

    /**
     * 设置是否开启在加载时候禁止操作内容视图
     */
    RefreshLayout setDisableContentWhenLoading(boolean disable);

    /**
     * 设置是否监听列表在滚动到底部时触发加载事件（默认true）
     */
    RefreshLayout setEnableAutoLoadMore(boolean enable);

    /**
     * 设置指定的Footer
     */
    RefreshLayout setRefreshFooter(@NonNull RefreshFooter footer);

    /**
     * 设置指定的Footer
     */
    RefreshLayout setRefreshFooter(@NonNull RefreshFooter footer, int width, int height);

    /**
     * 设置指定的Header
     */
    RefreshLayout setRefreshHeader(@NonNull RefreshHeader header);

    /**
     * 设置指定的Header
     */
    RefreshLayout setRefreshHeader(@NonNull RefreshHeader header, int width, int height);

    /**
     * 设置指定的Content
     */
    RefreshLayout setRefreshContent(@NonNull View content);

    /**
     * 设置指定的Content
     */
    RefreshLayout setRefreshContent(@NonNull View content, int width, int height);

    /**
     * 设置是否启用越界回弹
     */
    RefreshLayout setEnableOverScrollBounce(boolean enable);

    /**
     * 设置是否开启纯滚动模式
     */
    RefreshLayout setEnablePureScrollMode(boolean enable);

    /**
     * 设置是否在加载更多完成之后滚动内容显示新数据
     */
    RefreshLayout setEnableScrollContentWhenLoaded(boolean enable);

    /**
     * 是否在刷新完成之后滚动内容显示新数据
     */
    RefreshLayout setEnableScrollContentWhenRefreshed(boolean enable);

    /**
     * 设置在内容不满一页的时候，是否可以上拉加载更多
     */
    RefreshLayout setEnableLoadMoreWhenContentNotFull(boolean enable);

    /**
     * 设置是否启用越界拖动（仿苹果效果）
     */
    RefreshLayout setEnableOverScrollDrag(boolean enable);

    /**
     * 设置是否在全部加载结束之后Footer跟随内容
     */
    RefreshLayout setEnableFooterFollowWhenLoadFinished(boolean enable);

    /**
     * 设置是否 当 Header FixedBehind 时候是否剪裁遮挡 Header
     */
    RefreshLayout setEnableClipHeaderWhenFixedBehind(boolean enable);

    /**
     * 设置是否 当 Footer FixedBehind 时候是否剪裁遮挡 Footer
     */
    RefreshLayout setEnableClipFooterWhenFixedBehind(boolean enable);

    /**
     * 设置是会否启用嵌套滚动功能（默认关闭+智能开启）
     */
    RefreshLayout setEnableNestedScroll(boolean enabled);

    /**
     * 单独设置刷新监听器
     */
    RefreshLayout setOnRefreshListener(OnRefreshListener listener);

    /**
     * 单独设置加载监听器
     */
    RefreshLayout setOnLoadMoreListener(OnLoadMoreListener listener);

    /**
     * 同时设置刷新和加载监听器
     */
    RefreshLayout setOnRefreshLoadMoreListener(OnRefreshLoadMoreListener listener);

    /**
     * 设置多功能监听器
     * @param listener 建议使用 @{@link com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener}
     */
    RefreshLayout setOnMultiPurposeListener(OnMultiPurposeListener listener);

    /**
     * 设置滚动边界判断器
     * @param boundary 建议使用 @{@link com.scwang.smartrefresh.layout.impl.ScrollBoundaryDeciderAdapter}
     */
    RefreshLayout setScrollBoundaryDecider(ScrollBoundaryDecider boundary);

    /**
     * 设置主题颜色
     */
    RefreshLayout setPrimaryColors(@ColorInt int... colors);

    /**
     * 设置主题颜色
     */
    RefreshLayout setPrimaryColorsId(@ColorRes int... primaryColorId);

    /**
     * 完成刷新
     */
    RefreshLayout finishRefresh();

    /**
     * 完成刷新
     */
    RefreshLayout finishRefresh(int delayed);

    /**
     * 完成加载
     * @param success 数据是否成功刷新 （会影响到上次更新时间的改变）
     */
    RefreshLayout finishRefresh(boolean success);

    /**
     * 完成刷新
     */
    RefreshLayout finishRefresh(int delayed, boolean success);

    /**
     * 完成加载
     */
    RefreshLayout finishLoadMore();

    /**
     * 完成加载
     */
    RefreshLayout finishLoadMore(int delayed);

    /**
     * 完成加载
     */
    RefreshLayout finishLoadMore(boolean success);

    /**
     * 完成加载
     */
    RefreshLayout finishLoadMore(int delayed, boolean success, boolean noMoreData);

    /**
     * 完成加载并标记没有更多数据
     */
    RefreshLayout finishLoadMoreWithNoMoreData();

    /**
     * 恢复没有更多数据的原始状态
     * @param noMoreData 是否有更多数据
     */
    RefreshLayout setNoMoreData(boolean noMoreData);

    /**
     * 获取当前 Header
     */
    @Nullable
    RefreshHeader getRefreshHeader();

    /**
     * 获取当前 Footer
     */
    @Nullable
    RefreshFooter getRefreshFooter();

    /**
     * 获取当前状态
     */
    RefreshState getState();

    /**
     * 获取实体布局视图
     */
    ViewGroup getLayout();

    /**
     * 自动刷新
     */
    boolean autoRefresh();

    /**
     * 自动刷新
     * @param delayed 开始延时
     */
    boolean autoRefresh(int delayed);

    /**
     * 自动刷新
     * @param delayed 开始延时
     * @param duration 拖拽动画持续时间
     * @param dragrate 拉拽的高度比率（要求 ≥ 1 ）
     */
    boolean autoRefresh(int delayed, int duration, float dragrate);

    /**
     * 自动加载
     */
    boolean autoLoadMore();

    /**
     * 自动加载
     * @param delayed 开始延时
     */
    boolean autoLoadMore(int delayed);

    /**
     * 自动加载
     * @param delayed 开始延时
     * @param duration 拖拽动画持续时间
     * @param dragrate 拉拽的高度比率（要求 ≥ 1 ）
     */
    boolean autoLoadMore(int delayed, int duration, float dragrate);

    /**
     * 是否启用下拉刷新
     */
    boolean isEnableRefresh();

    /**
     * 是否启用加载更多
     */
    boolean isEnableLoadMore();

    /**
     * 是否正在刷新
     * @deprecated 后续版本将会移除
     *      使用 {@link #getState()} == {@link RefreshState#Refreshing} 代替
     */
    @Deprecated
    boolean isRefreshing();

    /**
     * 是否正在加载
     * @deprecated 后续版本将会移除
     *      使用 {@link #getState()} == {@link RefreshState#Loading} 代替
     */
    @Deprecated
    boolean isLoading();

    /**
     * @deprecated 后续版本将会移除
     */
    boolean isLoadmoreFinished();

    /**
     * @deprecated 后续版本将会移除
     */
    @Deprecated
    boolean isEnableAutoLoadMore();

    /**
     * @deprecated 后续版本将会移除
     */
    @Deprecated
    boolean isEnableOverScrollBounce();

    /**
     * @deprecated 后续版本将会移除
     */
    @Deprecated
    boolean isEnablePureScrollMode();

    /**
     * @deprecated 后续版本将会移除
     */
    @Deprecated
    boolean isEnableScrollContentWhenLoaded();

    /**
     * 恢复没有更多数据的原始状态
     * @deprecated 使用 @{@link RefreshLayout#setNoMoreData(boolean false)} 代替
     */
    @Deprecated
    RefreshLayout resetNoMoreData();

    /**
     * 恢复没有更多数据的原始状态
     * @param finished 是否有更多数据
     * @deprecated 后续版本将会移除 使用 {@link RefreshLayout#setNoMoreData(boolean)} 代替
     */
    @Deprecated
    RefreshLayout setLoadmoreFinished(boolean finished);


    /**
     * 完成加载
     * @deprecated 使用 @{@link #finishLoadMore()} 代替
     */
    @Deprecated
    RefreshLayout finishLoadmore();

    /**
     * 完成加载
     * @deprecated 使用 @{@link #finishLoadMore(int)} 代替
     */
    @Deprecated
    RefreshLayout finishLoadmore(int delayed);

    /**
     * 完成加载
     * @deprecated 使用 @{@link #finishLoadMore(boolean)} 代替
     */
    @Deprecated
    RefreshLayout finishLoadmore(boolean success);

    /**
     * 单独设置加载监听器
     * @deprecated 使用 @{@link #setOnLoadMoreListener(OnLoadMoreListener)} 代替
     */
    @Deprecated
    RefreshLayout setOnLoadmoreListener(OnLoadmoreListener listener);

    /**
     * 同时设置刷新和加载监听器
     * @deprecated 使用 @{@link #setOnRefreshLoadMoreListener(OnRefreshLoadMoreListener)} 代替
     */
    @Deprecated
    RefreshLayout setOnRefreshLoadmoreListener(OnRefreshLoadmoreListener listener);

    /**
     * 完成加载并标记没有更多数据
     * @deprecated 使用 @{@link #finishLoadMoreWithNoMoreData()} 代替
     */
    @Deprecated
    RefreshLayout finishLoadmoreWithNoMoreData();
}
