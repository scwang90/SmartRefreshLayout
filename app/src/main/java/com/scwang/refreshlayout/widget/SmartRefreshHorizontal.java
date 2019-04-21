package com.scwang.refreshlayout.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

import com.scwang.refreshlayout.R;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.api.ScrollBoundaryDecider;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.impl.ScrollBoundaryDeciderAdapter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnMultiPurposeListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

public class SmartRefreshHorizontal extends FrameLayout implements RefreshLayout {

    protected SmartRefreshContent mRefreshLayout;

    public SmartRefreshHorizontal(Context context) {
        this(context, null);
    }

    public SmartRefreshHorizontal(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmartRefreshHorizontal(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRefreshLayout = new SmartRefreshContent(context, attrs, defStyleAttr);
        mRefreshLayout.setEnableAutoLoadMore(false);
        mRefreshLayout.setScrollBoundaryDecider(new ScrollBoundaryDeciderAdapter(){
            @Override
            public boolean canRefresh(View content) {
                return ScrollBoundaryHorizontal.canRefresh(content, mActionEvent);
            }
            @Override
            public boolean canLoadMore(View content) {
                return ScrollBoundaryHorizontal.canLoadMore(content, mActionEvent, mEnableLoadMoreWhenContentNotFull);
            }
        });
    }

    //<editor-fold desc="重写方法">
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        while (getChildCount() > 0) {
            View child = getChildAt(0);
            removeViewAt(0);
            mRefreshLayout.addView(child);
        }
        mRefreshLayout.onFinishInflate();
        addView(mRefreshLayout);
        mRefreshLayout.setRotation(-90);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mRefreshLayout.getParent() == null) {
            mRefreshLayout.setRotation(-90);
            addView(mRefreshLayout);
        }
    }

    @Override
    @SuppressWarnings("SuspiciousNameCombination")
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mRefreshLayout.measure(heightMeasureSpec, widthMeasureSpec);
//        RefreshHeader header = mRefreshLayout.getRefreshHeader();
//        RefreshFooter footer = mRefreshLayout.getRefreshFooter();
//
//        for (int i = 0, len = mRefreshLayout.getChildCount(); i < len; i++) {
//            View child = mRefreshLayout.getChildAt(i);
//            if ((header == null || child != header.getView()) && (footer == null || child != footer.getView())) {
//                if (child.getVisibility() != GONE) {
//                    child.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY),
//                            MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY));
//                }
//            }
//        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        super.onLayout(changed, left, top, right, bottom);

        int width = right - left;
        int height = bottom - top;
        int div = (height - width) / 2;
        top = div;
        left = -div;

        RefreshHeader header = mRefreshLayout.getRefreshHeader();
        RefreshFooter footer = mRefreshLayout.getRefreshFooter();

        for (int i = 0, len = mRefreshLayout.getChildCount(); i < len; i++) {
            View child = mRefreshLayout.getChildAt(i);
            if ((header == null || child != header.getView()) && (footer == null || child != footer.getView())) {
                if (child.getVisibility() != GONE) {
                    child.setTag(R.string.srl_component_falsify, child);
                    child.setRotation(90);
                    child.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
                    child.layout(div, -div, width + div, height - div);
                }

            }
        }

        mRefreshLayout.layout(left, top, left + height, top + width);
    }
    //</editor-fold>

    //<editor-fold desc="委托方法">
    @Override
    public RefreshLayout setFooterHeight(float dp) {
        return mRefreshLayout.setFooterHeight(dp);
    }

    @Override
    public RefreshLayout setHeaderHeight(float dp) {
        return mRefreshLayout.setHeaderHeight(dp);
    }

    @Override
    public RefreshLayout setHeaderInsetStart(float dp) {
        return mRefreshLayout.setHeaderInsetStart(dp);
    }

    @Override
    public RefreshLayout setFooterInsetStart(float dp) {
        return mRefreshLayout.setFooterInsetStart(dp);
    }

    @Override
    public RefreshLayout setDragRate(float rate) {
        return mRefreshLayout.setDragRate(rate);
    }

    @Override
    public RefreshLayout setHeaderMaxDragRate(float rate) {
        return mRefreshLayout.setHeaderMaxDragRate(rate);
    }

    @Override
    public RefreshLayout setFooterMaxDragRate(float rate) {
        return mRefreshLayout.setFooterMaxDragRate(rate);
    }

    @Override
    public RefreshLayout setHeaderTriggerRate(float rate) {
        return mRefreshLayout.setHeaderTriggerRate(rate);
    }

    @Override
    public RefreshLayout setFooterTriggerRate(float rate) {
        return mRefreshLayout.setFooterTriggerRate(rate);
    }

    @Override
    public RefreshLayout setReboundInterpolator(@NonNull Interpolator interpolator) {
        return mRefreshLayout.setReboundInterpolator(interpolator);
    }

    @Override
    public RefreshLayout setReboundDuration(int duration) {
        return mRefreshLayout.setReboundDuration(duration);
    }

    @Override
    public RefreshLayout setRefreshFooter(@NonNull RefreshFooter footer) {
        return mRefreshLayout.setRefreshFooter(footer);
    }

    @Override
    public RefreshLayout setRefreshFooter(@NonNull RefreshFooter footer, int width, int height) {
        return mRefreshLayout.setRefreshFooter(footer, width, height);
    }

    @Override
    public RefreshLayout setRefreshHeader(@NonNull RefreshHeader header) {
        return mRefreshLayout.setRefreshHeader(header);
    }

    @Override
    public RefreshLayout setRefreshHeader(@NonNull RefreshHeader header, int width, int height) {
        return mRefreshLayout.setRefreshHeader(header, width, height);
    }

    @Override
    public RefreshLayout setRefreshContent(@NonNull View content) {
        return mRefreshLayout.setRefreshContent(content);
    }

    @Override
    public RefreshLayout setRefreshContent(@NonNull View content, int width, int height) {
        return mRefreshLayout.setRefreshContent(content, width, height);
    }

    @Override
    public RefreshLayout setEnableRefresh(boolean enabled) {
        return mRefreshLayout.setEnableRefresh(enabled);
    }

    @Override
    public RefreshLayout setEnableLoadMore(boolean enabled) {
        return mRefreshLayout.setEnableLoadMore(enabled);
    }

    @Override
    public RefreshLayout setEnableAutoLoadMore(boolean enabled) {
        return mRefreshLayout.setEnableAutoLoadMore(enabled);
    }

    @Override
    public RefreshLayout setEnableHeaderTranslationContent(boolean enabled) {
        return mRefreshLayout.setEnableHeaderTranslationContent(enabled);
    }

    @Override
    public RefreshLayout setEnableFooterTranslationContent(boolean enabled) {
        return mRefreshLayout.setEnableFooterTranslationContent(enabled);
    }

    @Override
    public RefreshLayout setEnableOverScrollBounce(boolean enabled) {
        return mRefreshLayout.setEnableOverScrollBounce(enabled);
    }

    @Override
    public RefreshLayout setEnablePureScrollMode(boolean enabled) {
        return mRefreshLayout.setEnablePureScrollMode(enabled);
    }

    @Override
    public RefreshLayout setEnableScrollContentWhenLoaded(boolean enabled) {
        return mRefreshLayout.setEnableScrollContentWhenLoaded(enabled);
    }

    @Override
    public RefreshLayout setEnableScrollContentWhenRefreshed(boolean enabled) {
        return mRefreshLayout.setEnableScrollContentWhenRefreshed(enabled);
    }

    @Override
    public RefreshLayout setEnableLoadMoreWhenContentNotFull(boolean enabled) {
        return mRefreshLayout.setEnableLoadMoreWhenContentNotFull(enabled);
    }

    @Override
    public RefreshLayout setEnableOverScrollDrag(boolean enabled) {
        return mRefreshLayout.setEnableOverScrollDrag(enabled);
    }

    @Override
    @Deprecated
    public RefreshLayout setEnableFooterFollowWhenLoadFinished(boolean enabled) {
        return mRefreshLayout.setEnableFooterFollowWhenLoadFinished(enabled);
    }

    @Override
    public RefreshLayout setEnableFooterFollowWhenNoMoreData(boolean enabled) {
        return mRefreshLayout.setEnableFooterFollowWhenNoMoreData(enabled);
    }

    @Override
    public RefreshLayout setEnableClipHeaderWhenFixedBehind(boolean enabled) {
        return mRefreshLayout.setEnableClipHeaderWhenFixedBehind(enabled);
    }

    @Override
    public RefreshLayout setEnableClipFooterWhenFixedBehind(boolean enabled) {
        return mRefreshLayout.setEnableClipFooterWhenFixedBehind(enabled);
    }

    @Override
    public RefreshLayout setEnableNestedScroll(boolean enabled) {
        return mRefreshLayout.setEnableNestedScroll(enabled);
    }

    @Override
    public RefreshLayout setDisableContentWhenRefresh(boolean disable) {
        return mRefreshLayout.setDisableContentWhenRefresh(disable);
    }

    @Override
    public RefreshLayout setDisableContentWhenLoading(boolean disable) {
        return mRefreshLayout.setDisableContentWhenLoading(disable);
    }

    @Override
    public RefreshLayout setOnRefreshListener(OnRefreshListener listener) {
        return mRefreshLayout.setOnRefreshListener(listener);
    }

    @Override
    public RefreshLayout setOnLoadMoreListener(OnLoadMoreListener listener) {
        return mRefreshLayout.setOnLoadMoreListener(listener);
    }

    @Override
    public RefreshLayout setOnRefreshLoadMoreListener(OnRefreshLoadMoreListener listener) {
        return mRefreshLayout.setOnRefreshLoadMoreListener(listener);
    }

    @Override
    public RefreshLayout setOnMultiPurposeListener(OnMultiPurposeListener listener) {
        return mRefreshLayout.setOnMultiPurposeListener(listener);
    }

    @Override
    public RefreshLayout setScrollBoundaryDecider(ScrollBoundaryDecider boundary) {
        return mRefreshLayout.setScrollBoundaryDecider(boundary);
    }

    @Override
    public RefreshLayout setPrimaryColors(int... primaryColors) {
        return mRefreshLayout.setPrimaryColors(primaryColors);
    }

    @Override
    public RefreshLayout setPrimaryColorsId(int... primaryColorId) {
        return mRefreshLayout.setPrimaryColorsId(primaryColorId);
    }

    @Override
    public RefreshLayout finishRefresh() {
        return mRefreshLayout.finishRefresh();
    }

    @Override
    public RefreshLayout finishRefresh(int delayed) {
        return mRefreshLayout.finishRefresh(delayed);
    }

    @Override
    public RefreshLayout finishRefresh(boolean success) {
        return mRefreshLayout.finishRefresh(success);
    }

    @Override
    public RefreshLayout finishRefresh(int delayed, boolean success) {
        return mRefreshLayout.finishRefresh(delayed, success);
    }

    @Override
    public RefreshLayout finishLoadMore() {
        return mRefreshLayout.finishLoadMore();
    }

    @Override
    public RefreshLayout finishLoadMore(int delayed) {
        return mRefreshLayout.finishLoadMore(delayed);
    }

    @Override
    public RefreshLayout finishLoadMore(boolean success) {
        return mRefreshLayout.finishLoadMore(success);
    }

    @Override
    public RefreshLayout finishLoadMore(int delayed, boolean success, boolean noMoreData) {
        return mRefreshLayout.finishLoadMore(delayed, success, noMoreData);
    }

    @Override
    public RefreshLayout finishLoadMoreWithNoMoreData() {
        return mRefreshLayout.finishLoadMoreWithNoMoreData();
    }

    @Override
    public RefreshLayout closeHeaderOrFooter() {
        return mRefreshLayout.closeHeaderOrFooter();
    }

    @Override
    @Deprecated
    public RefreshLayout setNoMoreData(boolean noMoreData) {
        return mRefreshLayout.setNoMoreData(noMoreData);
    }

    @Override
    public RefreshLayout resetNoMoreData() {
        return mRefreshLayout.resetNoMoreData();
    }

    @Override
    @Nullable
    public RefreshHeader getRefreshHeader() {
        return mRefreshLayout.getRefreshHeader();
    }

    @Override
    @Nullable
    public RefreshFooter getRefreshFooter() {
        return mRefreshLayout.getRefreshFooter();
    }

    @NonNull
    @Override
    public RefreshState getState() {
        return mRefreshLayout.getState();
    }

    @NonNull
    @Override
    public ViewGroup getLayout() {
        return mRefreshLayout.getLayout();
    }

    @Override
    public boolean autoRefresh() {
        return mRefreshLayout.autoRefresh();
    }

    @Override
    @Deprecated
    public boolean autoRefresh(int delayed) {
        return mRefreshLayout.autoRefresh(delayed);
    }

    @Override
    public boolean autoRefreshAnimationOnly() {
        return mRefreshLayout.autoRefreshAnimationOnly();
    }

    @Override
    public boolean autoRefresh(int delayed, int duration, float dragRate, boolean animationOnly) {
        return mRefreshLayout.autoRefresh(delayed, duration, dragRate, animationOnly);
    }

    @Override
    public boolean autoLoadMore() {
        return mRefreshLayout.autoLoadMore();
    }

//    @Override
//    @Deprecated
//    public boolean autoLoadMore(int delayed) {
//        return mRefreshLayout.autoLoadMore(delayed);
//    }

    @Override
    public boolean autoLoadMoreAnimationOnly() {
        return mRefreshLayout.autoLoadMoreAnimationOnly();
    }

    @Override
    public boolean autoLoadMore(int delayed, int duration, float dragRate, boolean animationOnly) {
        return mRefreshLayout.autoLoadMore(delayed, duration, dragRate, animationOnly);
    }
    //</editor-fold>
}
