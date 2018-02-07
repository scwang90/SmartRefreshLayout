package com.scwang.smartrefresh.layout.impl;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.PointF;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.ScrollingView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.Space;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import com.scwang.smartrefresh.layout.api.RefreshContent;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.ScrollBoundaryDecider;
import com.scwang.smartrefresh.layout.util.CoordinatorLayoutListener;
import com.scwang.smartrefresh.layout.util.DesignUtil;

import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.scwang.smartrefresh.layout.util.ScrollBoundaryUtil.canScrollDown;
import static com.scwang.smartrefresh.layout.util.ScrollBoundaryUtil.canScrollUp;
import static com.scwang.smartrefresh.layout.util.ScrollBoundaryUtil.isTransformedTouchPointInView;

/**
 * 刷新内容包装
 * Created by SCWANG on 2017/5/26.
 */
@SuppressWarnings("WeakerAccess")
public class RefreshContentWrapper implements RefreshContent {

    protected int mHeaderHeight = Integer.MAX_VALUE;
    protected int mFooterHeight = mHeaderHeight - 1;
    protected View mContentView;//直接内容视图
    protected View mRealContentView;//被包裹的原真实视图
    protected View mScrollableView;
    protected View mFixedHeader;
    protected View mFixedFooter;
    protected boolean mEnableRefresh = true;
    protected boolean mEnableLoadMore = true;
    protected MotionEvent mMotionEvent;
    protected ScrollBoundaryDeciderAdapter mBoundaryAdapter = new ScrollBoundaryDeciderAdapter();

    public RefreshContentWrapper(View view) {
        this.mContentView = mRealContentView = view;
    }

    //<editor-fold desc="findScrollableView">
    protected void findScrollableView(View content, RefreshKernel kernel) {
        mScrollableView = null;
        CoordinatorLayoutListener listener = null;
        boolean isInEditMode = mContentView.isInEditMode();
        while (mScrollableView == null || (mScrollableView instanceof NestedScrollingParent
                && !(mScrollableView instanceof NestedScrollingChild))) {
            content = findScrollableViewInternal(content, mScrollableView == null);
            if (content == mScrollableView) {
                break;
            }
            if (!isInEditMode) {
                if (listener == null) {
                    listener = new CoordinatorLayoutListener() {
                        @Override
                        public void update(boolean enableRefresh, boolean enableLoadMore) {
                            mEnableRefresh = enableRefresh;
                            mEnableLoadMore = enableLoadMore;
                        }
                    };
                }
                DesignUtil.checkCoordinatorLayout(content, kernel, listener);
            }
            mScrollableView = content;
        }
    }

    protected View findScrollableViewInternal(View content, boolean selfable) {
        View scrollableView = null;
        Queue<View> views = new LinkedBlockingQueue<>(Collections.singletonList(content));
        while (!views.isEmpty() && scrollableView == null) {
            View view = views.poll();
            if (view != null) {
                if ((selfable || view != content) && isScrollableView(view)) {
                    scrollableView = view;
                } else if (view instanceof ViewGroup) {
                    ViewGroup group = (ViewGroup) view;
                    for (int j = 0; j < group.getChildCount(); j++) {
                        views.add(group.getChildAt(j));
                    }
                }
            }
        }
        return scrollableView == null ? content : scrollableView;
    }

    protected View findScrollableViewByEvent(View content, MotionEvent event, View orgScrollableView) {
        if (content instanceof ViewGroup && event != null) {
            ViewGroup viewGroup = (ViewGroup) content;
            final int childCount = viewGroup.getChildCount();
            PointF point = new PointF();
            for (int i = childCount; i > 0; i--) {
                View child = viewGroup.getChildAt(i - 1);
                if (isTransformedTouchPointInView(viewGroup, child, event.getX(), event.getY(), point)) {
                    if (!(child instanceof ViewPager) && isScrollableView(child)) {
                        return child;
                    } else {
                        event = MotionEvent.obtain(event);
                        event.offsetLocation(point.x, point.y);
                        return findScrollableViewByEvent(child, event, orgScrollableView);
                    }
                }
            }
        }
        return orgScrollableView;
    }
    //</editor-fold>

    //<editor-fold desc="implements">
    @NonNull
    public View getView() {
        return mContentView;
    }

    @Override
    public void moveSpinner(int spinner) {
        mRealContentView.setTranslationY(spinner);
        if (mFixedHeader != null) {
            mFixedHeader.setTranslationY(Math.max(0, spinner));
        }
        if (mFixedFooter != null) {
            mFixedFooter.setTranslationY(Math.min(0, spinner));
        }
    }

    @Override
    public boolean canRefresh() {
        return mEnableRefresh && mBoundaryAdapter.canRefresh(mContentView);
    }

    @Override
    public boolean canLoadMore() {
        return mEnableLoadMore && mBoundaryAdapter.canLoadMore(mContentView);
    }

    @Override
    public View getScrollableView() {
        return mScrollableView;
    }

    @Override
    public void onActionDown(MotionEvent e) {
        mMotionEvent = MotionEvent.obtain(e);
        mMotionEvent.offsetLocation(-mContentView.getLeft(), -mContentView.getTop());
        if (mScrollableView != mContentView) {
            //如果内容视图不是 ScrollableView 说明使用了Layout嵌套内容，需要动态搜索 ScrollableView
            mScrollableView = findScrollableViewByEvent(mContentView, mMotionEvent, mScrollableView);
        }
        if (mScrollableView == mContentView) {
            //如果内容视图就是 ScrollableView 就不需要使用事件来动态搜索 而浪费CPU时间和性能了
            mBoundaryAdapter.setActionEvent(null);
        } else {
            mBoundaryAdapter.setActionEvent(mMotionEvent);
        }
    }

    @Override
    public void onActionUpOrCancel() {
        mMotionEvent = null;
    }

    @Override
    public void fling(int velocity) {
        if (mScrollableView instanceof ScrollView) {
            ((ScrollView) mScrollableView).fling(velocity);
        } else if (mScrollableView instanceof AbsListView) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((AbsListView) mScrollableView).fling(velocity);
            }
        } else if (mScrollableView instanceof WebView) {
            ((WebView) mScrollableView).flingScroll(0, velocity);
        } else if (mScrollableView instanceof NestedScrollView) {
            ((NestedScrollView) mScrollableView).fling(velocity);
        } else if (mScrollableView instanceof RecyclerView) {
            ((RecyclerView) mScrollableView).fling(0, velocity);
        }
    }

    @Override
    public void setUpComponent(RefreshKernel kernel, View fixedHeader, View fixedFooter) {
        findScrollableView(mContentView, kernel);

        if (fixedHeader != null || fixedFooter != null) {
            mFixedHeader = fixedHeader;
            mFixedFooter = fixedFooter;
            FrameLayout frameLayout = new FrameLayout(mContentView.getContext());
            kernel.getRefreshLayout().getLayout().removeView(mContentView);
            ViewGroup.LayoutParams layoutParams = mContentView.getLayoutParams();
            frameLayout.addView(mContentView, MATCH_PARENT, MATCH_PARENT);
            kernel.getRefreshLayout().getLayout().addView(frameLayout, layoutParams);
            mContentView = frameLayout;
            if (fixedHeader != null) {
                fixedHeader.setClickable(true);
                ViewGroup.LayoutParams lp = fixedHeader.getLayoutParams();
                ViewGroup parent = (ViewGroup) fixedHeader.getParent();
                int index = parent.indexOfChild(fixedHeader);
                parent.removeView(fixedHeader);
                lp.height = measureViewHeight(fixedHeader);
                parent.addView(new Space(mContentView.getContext()), index, lp);
                frameLayout.addView(fixedHeader);
            }
            if (fixedFooter != null) {
                fixedFooter.setClickable(true);
                ViewGroup.LayoutParams lp = fixedFooter.getLayoutParams();
                ViewGroup parent = (ViewGroup) fixedFooter.getParent();
                int index = parent.indexOfChild(fixedFooter);
                parent.removeView(fixedFooter);
                FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(lp);
                lp.height = measureViewHeight(fixedFooter);
                parent.addView(new Space(mContentView.getContext()), index, lp);
                flp.gravity = Gravity.BOTTOM;
                frameLayout.addView(fixedFooter, flp);
            }
        }
    }

    @Override
    public void onInitialHeaderAndFooter(int headerHeight, int footerHeight) {
        mHeaderHeight = headerHeight;
        mFooterHeight = footerHeight;
    }

    @Override
    public void setScrollBoundaryDecider(ScrollBoundaryDecider boundary) {
        if (boundary instanceof ScrollBoundaryDeciderAdapter) {
            mBoundaryAdapter = ((ScrollBoundaryDeciderAdapter) boundary);
        } else {
            mBoundaryAdapter.setScrollBoundaryDecider(boundary);
        }
    }

    @Override
    public void setEnableLoadMoreWhenContentNotFull(boolean enable) {
        mBoundaryAdapter.setEnableLoadMoreWhenContentNotFull(enable);
    }

    @Override
    public AnimatorUpdateListener scrollContentWhenFinished(final int spinner) {
        if (mScrollableView != null && spinner != 0) {
            if ((spinner < 0 && canScrollDown(mScrollableView)) || (spinner > 0 && canScrollUp(mScrollableView))) {
                return new AnimatorUpdateListener() {
                    int lastValue = spinner;
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (int) animation.getAnimatedValue();
                        try {
                            if (mScrollableView instanceof AbsListView) {
                                scrollListBy((AbsListView) mScrollableView, value - lastValue);
                            } else {
                                mScrollableView.scrollBy(0, value - lastValue);
                            }
                        } catch (Throwable ignored) {
                            //根据用户反馈，此处可能会有BUG
                        }
                        lastValue = value;
                    }
                };
            }
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold desc="static">

    protected static int measureViewHeight(View view) {
        ViewGroup.LayoutParams p = view.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(MATCH_PARENT,WRAP_CONTENT);
        }
        int childHeightSpec;
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        if (p.height > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(p.height, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        view.measure(childWidthSpec, childHeightSpec);
        return view.getMeasuredHeight();
    }

    protected static void scrollListBy(@NonNull AbsListView listView, int y) {
        if (Build.VERSION.SDK_INT >= 19) {
            // Call the framework version directly
            listView.scrollListBy(y);
        } else if (listView instanceof ListView) {
            // provide backport on earlier versions
            final int firstPosition = listView.getFirstVisiblePosition();
            if (firstPosition == ListView.INVALID_POSITION) {
                return;
            }

            final View firstView = listView.getChildAt(0);
            if (firstView == null) {
                return;
            }

            final int newTop = firstView.getTop() - y;
            ((ListView) listView).setSelectionFromTop(firstPosition, newTop);
        } else {
            listView.smoothScrollBy(y, 0);
        }
    }

    public static boolean isScrollableView(View view) {
        return view instanceof AbsListView
                || view instanceof ScrollView
                || view instanceof ScrollingView
                || view instanceof WebView
                || view instanceof ViewPager
                || view instanceof NestedScrollingChild
                || view instanceof NestedScrollingParent;
    }

    //</editor-fold>

}
