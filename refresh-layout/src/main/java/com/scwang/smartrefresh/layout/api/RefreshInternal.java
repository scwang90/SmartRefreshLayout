package com.scwang.smartrefresh.layout.api;

import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.view.View;

import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.listener.OnStateChangedListener;

import static android.support.annotation.RestrictTo.Scope.LIBRARY;
import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;
import static android.support.annotation.RestrictTo.Scope.SUBCLASSES;


/**
 * 刷新内部组件
 * Created by scwang on 2017/5/26.
 */
public interface RefreshInternal extends OnStateChangedListener {
    /**
     * 获取实体视图
     * @return 实体视图
     */
    @NonNull
    View getView();

    /**
     * 获取变换方式 {@link SpinnerStyle} 必须返回 非空
     * @return 变换方式
     */
    @NonNull
    SpinnerStyle getSpinnerStyle();

    /**
     * 【仅限框架内调用】设置主题颜色
     * @param colors 对应Xml中配置的 srlPrimaryColor srlAccentColor
     */
    @RestrictTo({LIBRARY,LIBRARY_GROUP,SUBCLASSES})
    void setPrimaryColors(@ColorInt int... colors);

	/**
	 * 【仅限框架内调用】自动刷新 （如果通过autoRefreshXxx或autoLoadMoreXxx方法触发才调用，在RefreshLayout#autoXxx中调用）
	 * @param isAutoRefresh 当触发autoRefreshXxx或autoLoadMoreXxx方法时候才进行回调且为true，当刷新完成时必定回调且为false
	 */
	@RestrictTo({LIBRARY, LIBRARY_GROUP, SUBCLASSES})
	void onAutoRefresh(boolean isAutoRefresh);

    /**
     * 【仅限框架内调用】尺寸定义完成 （如果高度不改变（代码修改：setHeader），只调用一次, 在RefreshLayout#onMeasure中调用）
     * @param kernel RefreshKernel
     * @param height HeaderHeight or FooterHeight
     * @param maxDragHeight 最大拖动高度
     */
    @RestrictTo({LIBRARY,LIBRARY_GROUP,SUBCLASSES})
    void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight);
    /**
     * 【仅限框架内调用】手指拖动下拉（会连续多次调用，添加isDragging并取代之前的onPulling、onReleasing）
     * @param isDragging true 手指正在拖动 false 回弹动画
     * @param percent 下拉的百分比 值 = offset/footerHeight (0 - percent - (footerHeight+maxDragHeight) / footerHeight )
     * @param offset 下拉的像素偏移量  0 - offset - (footerHeight+maxDragHeight)
     * @param height 高度 HeaderHeight or FooterHeight (offset 可以超过 height 此时 percent 大于 1)
     * @param maxDragHeight 最大拖动高度 offset 可以超过 height 参数 但是不会超过 maxDragHeight
     */
    @RestrictTo({LIBRARY,LIBRARY_GROUP,SUBCLASSES})
    void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight);

    /**
     * 【仅限框架内调用】释放时刻（调用一次，将会触发加载）
     * @param refreshLayout RefreshLayout
     * @param height 高度 HeaderHeight or FooterHeight
     * @param maxDragHeight 最大拖动高度
     */
    @RestrictTo({LIBRARY,LIBRARY_GROUP,SUBCLASSES})
    void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight);

    /**
     * 【仅限框架内调用】开始动画
     * @param refreshLayout RefreshLayout
     * @param height HeaderHeight or FooterHeight
     * @param maxDragHeight 最大拖动高度
     */
    @RestrictTo({LIBRARY,LIBRARY_GROUP,SUBCLASSES})
    void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight);

    /**
     * 【仅限框架内调用】动画结束
     * @param refreshLayout RefreshLayout
     * @param success 数据是否成功刷新或加载
     * @return 完成动画所需时间 如果返回 Integer.MAX_VALUE 将取消本次完成事件，继续保持原有状态
     */
    @RestrictTo({LIBRARY,LIBRARY_GROUP,SUBCLASSES})
    int onFinish(@NonNull RefreshLayout refreshLayout, boolean success);

    /**
     * 【仅限框架内调用】水平方向的拖动
     * @param percentX 下拉时，手指水平坐标对屏幕的占比（0 - percentX - 1）
     * @param offsetX 下拉时，手指水平坐标对屏幕的偏移（0 - offsetX - LayoutWidth）
     * @param offsetMax 最大的偏移量
     */
    @RestrictTo({LIBRARY,LIBRARY_GROUP,SUBCLASSES})
    void onHorizontalDrag(float percentX, int offsetX, int offsetMax);

    /**
     * 是否支持水平方向的拖动（将会影响到onHorizontalDrag的调用）
     * @return 水平拖动需要消耗更多的时间和资源，所以如果不支持请返回false
     */
    boolean isSupportHorizontalDrag();
}
