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

    /**
     * 设置 Footer 高度
     * @param dp 虚拟像素
     * @return RefreshLayout
     */
    RefreshLayout setFooterHeight(float dp);

    /**
     * 设置 Footer 高度
     * @param px 像素
     * @return RefreshLayout
     */
    RefreshLayout setFooterHeightPx(int px);

    /**
     * 设置 Header 高度
     * @param dp 虚拟像素
     * @return RefreshLayout
     */
    RefreshLayout setHeaderHeight(float dp);

    /**
     * 设置 Header 高度
     * @param px 像素
     * @return RefreshLayout
     */
    RefreshLayout setHeaderHeightPx(int px);

    /**
     * 设置 Header 起始偏移量
     * @param insetDp 虚拟像素
     * @return RefreshLayout
     */
    RefreshLayout setHeaderInsetStart(float insetDp);

    /**
     * 设置 Header 起始偏移量
     * @param insetPx 像素
     * @return RefreshLayout
     */
    RefreshLayout setHeaderInsetStartPx(int insetPx);

    /**
     * 设置 Footer 起始偏移量
     * @param insetDp 虚拟像素
     * @return RefreshLayout
     */
    RefreshLayout setFooterInsetStart(float insetDp);

    /**
     * 设置 Footer 起始偏移量
     * @param insetPx 像素
     * @return RefreshLayout
     */
    RefreshLayout setFooterInsetStartPx(int insetPx);

    /**
     * 显示拖动高度/真实拖动高度 比率（默认0.5，阻尼效果）
     * @param rate 显示拖动高度/真实拖动高度 比率
     * @return RefreshLayout
     */
    RefreshLayout setDragRate(@FloatRange(from = 0,to = 1) float rate);

    /**
     * 设置下拉最大高度和Header高度的比率（将会影响可以下拉的最大高度）
     * @param rate 下拉最大高度和Header高度的比率
     * @return RefreshLayout
     */
    RefreshLayout setHeaderMaxDragRate(@FloatRange(from = 1,to = 100) float rate);

    /**
     * 设置上拉最大高度和Footer高度的比率（将会影响可以上拉的最大高度）
     * @param rate 上拉最大高度和Footer高度的比率
     * @return RefreshLayout
     */
    RefreshLayout setFooterMaxDragRate(@FloatRange(from = 1,to = 100) float rate);

    /**
     * 设置 触发刷新距离 与 HeaderHeight 的比率
     * @param rate 触发刷新距离 与 HeaderHeight 的比率
     * @return RefreshLayout
     */
    RefreshLayout setHeaderTriggerRate(@FloatRange(from = 0,to = 1.0) float rate);

    /**
     * 设置 触发加载距离 与 FooterHeight 的比率
     * @param rate 触发加载距离 与 FooterHeight 的比率
     * @return RefreshLayout
     */
    RefreshLayout setFooterTriggerRate(@FloatRange(from = 0,to = 1.0) float rate);

    /**
     * 设置回弹显示插值器 [放手时回弹动画,结束时收缩动画]
     * @param interpolator 动画插值器
     * @return RefreshLayout
     */
    RefreshLayout setReboundInterpolator(@NonNull Interpolator interpolator);

    /**
     * 设置回弹动画时长 [放手时回弹动画,结束时收缩动画]
     * @param duration 时长
     * @return RefreshLayout
     */
    RefreshLayout setReboundDuration(int duration);

    /**
     * 设置指定的Footer
     * @param footer 刷新尾巴
     * @return RefreshLayout
     */
    RefreshLayout setRefreshFooter(@NonNull RefreshFooter footer);

    /**
     * 设置指定的Footer
     * @param footer 刷新尾巴
     * @param width 宽度 可以使用 MATCH_PARENT, WRAP_CONTENT
     * @param height 高度 可以使用 MATCH_PARENT, WRAP_CONTENT
     * @return RefreshLayout
     */
    RefreshLayout setRefreshFooter(@NonNull RefreshFooter footer, int width, int height);

    /**
     * 设置指定的Header
     * @param header 刷新头
     * @return RefreshLayout
     */
    RefreshLayout setRefreshHeader(@NonNull RefreshHeader header);

    /**
     * 设置指定的Header
     * @param header 刷新头
     * @param width 宽度 可以使用 MATCH_PARENT, WRAP_CONTENT
     * @param height 高度 可以使用 MATCH_PARENT, WRAP_CONTENT
     * @return RefreshLayout
     */
    RefreshLayout setRefreshHeader(@NonNull RefreshHeader header, int width, int height);

    /**
     * 设置指定的 Content
     * @param content 内容视图
     * @return RefreshLayout
     */
    RefreshLayout setRefreshContent(@NonNull View content);

    /**
     * 设置指定的 Content
     * @param content 内容视图
     * @param width 宽度 可以使用 MATCH_PARENT, WRAP_CONTENT
     * @param height 高度 可以使用 MATCH_PARENT, WRAP_CONTENT
     * @return RefreshLayout
     */
    RefreshLayout setRefreshContent(@NonNull View content, int width, int height);


    /**
     * 设置是否启用上拉加载更多（默认启用）
     * @param enabled 是否启用
     * @return RefreshLayout
     */
    RefreshLayout setEnableLoadMore(boolean enabled);

    /**
     * 是否启用下拉刷新（默认启用）
     * @param enabled 是否启用
     * @return RefreshLayout
     */
    RefreshLayout setEnableRefresh(boolean enabled);

    /**
     * 设置是否监听列表在滚动到底部时触发加载事件（默认true）
     * @param enabled 是否启用
     * @return RefreshLayout
     */
    RefreshLayout setEnableAutoLoadMore(boolean enabled);

    /**
     * 设置是否启在下拉Header的同时下拉内容
     * @param enabled 是否启用
     * @return RefreshLayout
     */
    RefreshLayout setEnableHeaderTranslationContent(boolean enabled);

    /**
     * 设置是否启在上拉Footer的同时上拉内容
     * @param enabled 是否启用
     * @return RefreshLayout
     */
    RefreshLayout setEnableFooterTranslationContent(boolean enabled);

    /**
     * 设置是否启用越界回弹
     * @param enabled 是否启用
     * @return RefreshLayout
     */
    RefreshLayout setEnableOverScrollBounce(boolean enabled);

    /**
     * 设置是否开启纯滚动模式
     * @param enabled 是否启用
     * @return RefreshLayout
     */
    RefreshLayout setEnablePureScrollMode(boolean enabled);

    /**
     * 设置是否在加载更多完成之后滚动内容显示新数据
     * @param enabled 是否启用
     * @return RefreshLayout
     */
    RefreshLayout setEnableScrollContentWhenLoaded(boolean enabled);

    /**
     * 是否在刷新完成之后滚动内容显示新数据
     * @param enabled 是否启用
     * @return RefreshLayout
     */
    RefreshLayout setEnableScrollContentWhenRefreshed(boolean enabled);

    /**
     * 设置在内容不满一页的时候，是否可以上拉加载更多
     * @param enabled 是否启用
     * @return RefreshLayout
     */
    RefreshLayout setEnableLoadMoreWhenContentNotFull(boolean enabled);

    /**
     * 设置是否启用越界拖动（仿苹果效果）
     * @param enabled 是否启用
     * @return RefreshLayout
     */
    RefreshLayout setEnableOverScrollDrag(boolean enabled);

    /**
     * 设置是否在全部加载结束之后Footer跟随内容
     * @param enabled 是否启用
     * @return RefreshLayout
     */
    RefreshLayout setEnableFooterFollowWhenLoadFinished(boolean enabled);

    /**
     * 设置是否 当 Header FixedBehind 时候是否剪裁遮挡 Header
     * @param enabled 是否启用
     * @return RefreshLayout
     */
    RefreshLayout setEnableClipHeaderWhenFixedBehind(boolean enabled);

    /**
     * 设置是否 当 Footer FixedBehind 时候是否剪裁遮挡 Footer
     * @param enabled 是否启用
     * @return RefreshLayout
     */
    RefreshLayout setEnableClipFooterWhenFixedBehind(boolean enabled);

    /**
     * 设置是会否启用嵌套滚动功能（默认关闭+智能开启）
     * @param enabled 是否启用
     * @return RefreshLayout
     */
    RefreshLayout setEnableNestedScroll(boolean enabled);

    /**
     * 设置是否开启在刷新时候禁止操作内容视图
     * @param disable 是否禁止
     * @return RefreshLayout
     */
    RefreshLayout setDisableContentWhenRefresh(boolean disable);

    /**
     * 设置是否开启在加载时候禁止操作内容视图
     * @param disable 是否禁止
     * @return RefreshLayout
     */
    RefreshLayout setDisableContentWhenLoading(boolean disable);

    /**
     * 单独设置刷新监听器
     * @param listener 刷新监听器
     * @return RefreshLayout
     */
    RefreshLayout setOnRefreshListener(OnRefreshListener listener);

    /**
     * 单独设置加载监听器
     * @param listener 加载监听器
     * @return RefreshLayout
     */
    RefreshLayout setOnLoadMoreListener(OnLoadMoreListener listener);

    /**
     * 同时设置刷新和加载监听器
     * @param listener 刷新加载监听器
     * @return RefreshLayout
     */
    RefreshLayout setOnRefreshLoadMoreListener(OnRefreshLoadMoreListener listener);

    /**
     * 设置多功能监听器
     * @param listener 建议使用 {@link com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener}
     * @return RefreshLayout
     */
    RefreshLayout setOnMultiPurposeListener(OnMultiPurposeListener listener);

    /**
     * 设置滚动边界判断器
     * @param boundary 建议使用 {@link com.scwang.smartrefresh.layout.impl.ScrollBoundaryDeciderAdapter}
     * @return RefreshLayout
     */
    RefreshLayout setScrollBoundaryDecider(ScrollBoundaryDecider boundary);

    /**
     * 设置主题颜色
     * @param primaryColors 主题颜色
     * @return RefreshLayout
     */
    RefreshLayout setPrimaryColors(@ColorInt int... primaryColors);

    /**
     * 设置主题颜色
     * @param primaryColorId 主题颜色ID
     * @return RefreshLayout
     */
    RefreshLayout setPrimaryColorsId(@ColorRes int... primaryColorId);

    /**
     * 完成刷新
     * @return RefreshLayout
     */
    RefreshLayout finishRefresh();

    /**
     * 完成刷新
     * @param delayed 开始延时
     * @return RefreshLayout
     */
    RefreshLayout finishRefresh(int delayed);

    /**
     * 完成加载
     * @param success 数据是否成功刷新 （会影响到上次更新时间的改变）
     * @return RefreshLayout
     */
    RefreshLayout finishRefresh(boolean success);

    /**
     * 完成刷新
     * @param delayed 开始延时
     * @param success 数据是否成功刷新 （会影响到上次更新时间的改变）
     * @return RefreshLayout
     */
    RefreshLayout finishRefresh(int delayed, boolean success);

    /**
     * 完成加载
     * @return RefreshLayout
     */
    RefreshLayout finishLoadMore();

    /**
     * 完成加载
     * @param delayed 开始延时
     * @return RefreshLayout
     */
    RefreshLayout finishLoadMore(int delayed);

    /**
     * 完成加载
     * @param success 数据是否成功
     * @return RefreshLayout
     */
    RefreshLayout finishLoadMore(boolean success);

    /**
     * 完成加载
     * @param delayed 开始延时
     * @param success 数据是否成功
     * @param noMoreData 是否有更多数据
     * @return RefreshLayout
     */
    RefreshLayout finishLoadMore(int delayed, boolean success, boolean noMoreData);

    /**
     * 完成加载并标记没有更多数据
     * @return RefreshLayout
     */
    RefreshLayout finishLoadMoreWithNoMoreData();

    /**
     * 恢复没有更多数据的原始状态
     * @param noMoreData 是否有更多数据
     * @return RefreshLayout
     */
    RefreshLayout setNoMoreData(boolean noMoreData);

    /**
     * 获取当前 Header
     * @return RefreshLayout
     */
    @Nullable
    RefreshHeader getRefreshHeader();

    /**
     * 获取当前 Footer
     * @return RefreshLayout
     */
    @Nullable
    RefreshFooter getRefreshFooter();

    /**
     * 获取当前状态
     * @return RefreshLayout
     */
    RefreshState getState();

    /**
     * 获取实体布局视图
     * @return RefreshLayout
     */
    ViewGroup getLayout();

    /**
     * 自动刷新
     * @return RefreshLayout
     */
    boolean autoRefresh();

    /**
     * 自动刷新
     * @param delayed 开始延时
     * @return RefreshLayout
     */
    boolean autoRefresh(int delayed);

    /**
     * 自动刷新
     * @param delayed 开始延时
     * @param duration 拖拽动画持续时间
     * @param dragRate 拉拽的高度比率（要求 ≥ 1 ）
     * @return RefreshLayout
     */
    boolean autoRefresh(int delayed, int duration, float dragRate);

    /**
     * 自动加载
     * @return RefreshLayout
     */
    boolean autoLoadMore();

    /**
     * 自动加载
     * @param delayed 开始延时
     * @return RefreshLayout
     */
    boolean autoLoadMore(int delayed);

    /**
     * 自动加载
     * @param delayed 开始延时
     * @param duration 拖拽动画持续时间
     * @param dragRate 拉拽的高度比率（要求 ≥ 1 ）
     * @return RefreshLayout
     */
    boolean autoLoadMore(int delayed, int duration, float dragRate);

    /**
     * 是否启用下拉刷新
     * @return RefreshLayout
     */
    boolean isEnableRefresh();

    /**
     * 是否启用加载更多
     * @return RefreshLayout
     */
    boolean isEnableLoadMore();

    /**
     * 是否正在刷新
     * @deprecated 后续版本将会移除
     *      使用 {@link #getState()} == {@link RefreshState#Refreshing} 代替
     * @return RefreshLayout
     */
    @Deprecated
    boolean isRefreshing();

    /**
     * 是否正在加载
     * @deprecated 后续版本将会移除
     *      使用 {@link #getState()} == {@link RefreshState#Loading} 代替
     * @return RefreshLayout
     */
    @Deprecated
    boolean isLoading();

    /**
     * 恢复没有更多数据的原始状态
     * @deprecated 请使用{@link RefreshLayout#setNoMoreData(boolean)}
     * @return RefreshLayout
     */
    @Deprecated
    RefreshLayout resetNoMoreData();

}
