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
import com.scwang.smart.refresh.layout.api.RefreshComponent;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnMultiListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.ScrollBoundaryDecider;
import com.scwang.smart.refresh.layout.util.SmartUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.impl.ScrollBoundaryDeciderAdapter;

public class SmartRefreshHorizontal extends SmartRefreshLayout {

    protected boolean isInLayout = false;

    public SmartRefreshHorizontal(Context context) {
        this(context, null);
    }

    public SmartRefreshHorizontal(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEnableAutoLoadMore(false);
        setScrollBoundaryDecider(new ScrollBoundaryDeciderAdapter(){
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
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mRefreshContent != null && !(mRefreshContent instanceof RefreshContentHorizontal)) {
            mRefreshContent = new RefreshContentHorizontal(mRefreshContent.getView());
            View fixedHeaderView = mFixedHeaderViewId > 0 ? findViewById(mFixedHeaderViewId) : null;
            View fixedFooterView = mFixedFooterViewId > 0 ? findViewById(mFixedFooterViewId) : null;

            mRefreshContent.setScrollBoundaryDecider(mScrollBoundaryDecider);
            mRefreshContent.setEnableLoadMoreWhenContentNotFull(mEnableLoadMoreWhenContentNotFull);
            mRefreshContent.setUpComponent(mKernel, fixedHeaderView, fixedFooterView);
        }

        setRotation(-90);
    }

    @Override
    @SuppressWarnings("SuspiciousNameCombination")
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = right - left;
        int height = bottom - top;
        int div = (height - width) / 2;
        if (isInLayout) {
            RefreshComponent header = mRefreshHeader;
            RefreshComponent footer = mRefreshFooter;

            final View thisView = this;
            int paddingLeft = thisView.getPaddingLeft();
            int paddingRight = thisView.getPaddingRight();
            int paddingTop = thisView.getPaddingTop();
            int paddingBottom = thisView.getPaddingBottom();

            for (int i = 0, len = getChildCount(); i < len; i++) {
                View child = getChildAt(i);
                if ((header == null || child != header.getView()) && (footer == null || child != footer.getView())) {
                    if (child.getVisibility() != GONE) {

                        int w = height;
                        int h = width;
                        int l = paddingBottom;
                        int t = paddingLeft;

                        h -= paddingTop + paddingBottom;
                        w -= paddingLeft + paddingRight;

                        ViewGroup.LayoutParams params = child.getLayoutParams();
                        if (params instanceof MarginLayoutParams) {
                            MarginLayoutParams lp = (MarginLayoutParams) params;
                            h -= lp.topMargin + lp.bottomMargin;
                            w -= lp.leftMargin + lp.rightMargin;
                            l += lp.bottomMargin;
                            t += lp.leftMargin;
                        }

                        div = (h - w) / 2;
                        l += div;
                        t -= div;

                        child.setRotation(90);
                        child.setTag(R.id.srl_tag, "GONE");
                        child.measure(MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY));
                        child.layout(l, t, l + w, t + h);
                    }
                }
            }
            super.onLayout(changed, left, top, right, bottom);
        } else {
            top -= div;
            left += div;
            isInLayout = true;
            super.layout(left, top, left + width, top + height);
            isInLayout = false;
        }

    }
    //</editor-fold>
}
