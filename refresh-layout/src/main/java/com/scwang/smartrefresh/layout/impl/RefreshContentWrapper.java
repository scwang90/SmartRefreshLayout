package com.scwang.smartrefresh.layout.impl;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerAdapterWrapper;
import android.support.v4.view.ScrollingView;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.ScrollView;

import com.scwang.smartrefresh.layout.api.RefreshContent;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 刷新内容包装
 * Created by SCWANG on 2017/5/26.
 */

public class RefreshContentWrapper implements RefreshContent {

    private View mContentView;
    private View mScrollableView;
    private MotionEvent mMotionEvent;
    private boolean mEnableAutoLoadmore = false;

    public RefreshContentWrapper(View view) {
        this.mContentView = view;
        this.findScrollableView(view);
    }

    public RefreshContentWrapper(Context context) {
        this.mContentView = new View(context);
        this.findScrollableView(mContentView);
    }

    //<editor-fold desc="findScrollableView">
    private void findScrollableView(View content) {
        mScrollableView = findScrollableViewInternal(content, true);
        if (mScrollableView instanceof NestedScrollingParent
                && !(mScrollableView instanceof NestedScrollingChild)) {
            mScrollableView = findScrollableViewInternal(mScrollableView, false);
        }
        if (mScrollableView instanceof ViewPager) {
            wrapperViewPager((ViewPager) this.mScrollableView);
        }
    }

    private void wrapperViewPager(final ViewPager viewPager) {
        wrapperViewPager(viewPager, null);
    }

    private void wrapperViewPager(final ViewPager viewPager, PagerPrimaryAdapter primaryAdapter) {
        viewPager.post(new Runnable() {
            int count = 0;
            PagerPrimaryAdapter mAdapter = primaryAdapter;
            @Override
            public void run() {
                count++;
                PagerAdapter adapter = viewPager.getAdapter();
                if (adapter != null) {
                    if (adapter instanceof PagerPrimaryAdapter) {
                        if (adapter == primaryAdapter) {
                            viewPager.postDelayed(this, 500);
                        }
                    } else {
                        if (mAdapter == null) {
                            mAdapter = new PagerPrimaryAdapter(adapter);
                        } else {
                            mAdapter.wrapper(adapter);
                        }
                        mAdapter.attachViewPager(viewPager);
                    }
                } else if (count < 10) {
                    viewPager.postDelayed(this, 500);
                }
            }
        });
    }

    private View findScrollableViewInternal(View content, boolean selfable) {
        View scrollableView = null;
        Queue<View> views = new LinkedBlockingQueue<>(Collections.singletonList(content));
        while (!views.isEmpty() && scrollableView == null) {
            View view = views.poll();
            if (view != null) {
                if ((selfable || view != content) && (view instanceof AbsListView
                        || view instanceof ScrollView
                        || view instanceof ScrollingView
                        || view instanceof NestedScrollingChild
                        || view instanceof NestedScrollingParent
                        || view instanceof WebView
                        || view instanceof ViewPager)) {
                    scrollableView = view;
                } else if (view instanceof ViewGroup) {
                    ViewGroup group = (ViewGroup) view;
                    for (int j = 0; j < group.getChildCount(); j++) {
                        views.add(group.getChildAt(j));
                    }
                }
            }
        }
        return scrollableView;
    }
    //</editor-fold>

    @NonNull
    public View getView() {
        return mContentView;
    }

    @Override
    public void moveSpinner(int spinner) {
        mContentView.setTranslationY(spinner);
    }

    @Override
    public boolean canScrollUp() {
        return canScrollUp(mContentView, mMotionEvent);
    }

    @Override
    public boolean canScrollDown() {
        return canScrollDown(mContentView, mMotionEvent);
    }

    @Override
    public void measure(int widthSpec, int heightSpec) {
        mContentView.measure(widthSpec, heightSpec);
    }

    @Override
    public ViewGroup.LayoutParams getLayoutParams() {
        return mContentView.getLayoutParams();
    }

    @Override
    public int getMeasuredWidth() {
        return mContentView.getMeasuredWidth();
    }

    @Override
    public int getMeasuredHeight() {
        return mContentView.getMeasuredHeight();
    }

    @Override
    public void layout(int left, int top, int right, int bottom) {
        mContentView.layout(left, top, right, bottom);
    }

    @Override
    public View getScrollableView() {
        return mScrollableView;
    }

    @Override
    public void onActionDown(MotionEvent e) {
        mMotionEvent = MotionEvent.obtain(e);
        mMotionEvent.offsetLocation(-mContentView.getLeft(), -mContentView.getTop());
    }

    @Override
    public void onActionUpOrCancel(MotionEvent e) {
        mMotionEvent = null;
    }

    private class RecyclerViewScrollComponent extends RecyclerView.OnScrollListener {
        int lastDy;
        long lastFlingTime;
        boolean autoLoadmore;
        RefreshKernel kernel;
        TimeInterpolator interpolator = new DecelerateInterpolator();
        ValueAnimator.AnimatorUpdateListener updateListener = animation -> kernel.moveSpinner((int) animation.getAnimatedValue(), true);

        RecyclerViewScrollComponent(boolean autoLoadmore, RefreshKernel kernel) {
            this.autoLoadmore = autoLoadmore;
            this.kernel = kernel;
        }
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == 0 && Math.abs(lastDy) > 1 && System.currentTimeMillis() - lastFlingTime < 1000) {
                RefreshLayout layout = kernel.getRefreshLayout();
                if ((autoLoadmore && !layout.isLoadmoreFinished() && lastDy > 0)
                        || layout.isRefreshing() || layout.isLoading()) {
                    return;
                }
                ValueAnimator animator = ValueAnimator.ofInt(0, -lastDy * 2, 0);
                animator.setDuration(400);
                animator.addUpdateListener(updateListener);
                animator.setInterpolator(interpolator);
                animator.start();
                lastDy = 0;
            }
        }
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            lastDy = dy;
        }

        void attach(RecyclerView recyclerView) {
            recyclerView.addOnScrollListener(this);
            recyclerView.setOnFlingListener(new RecyclerView.OnFlingListener() {
                @Override
                public boolean onFling(int velocityX, int velocityY) {
                    lastFlingTime = System.currentTimeMillis();
                    return false;
                }
            });
        }
    }

    @Override
    public void setupComponent(boolean autoLoadmore, RefreshKernel kernel) {
        mEnableAutoLoadmore = autoLoadmore;
        if (mScrollableView != null && autoLoadmore) {
            setUpAutoLoadmore(mScrollableView, kernel);
        }
        if (mScrollableView instanceof RecyclerView) {
            RecyclerViewScrollComponent component = new RecyclerViewScrollComponent(autoLoadmore, kernel);
            component.attach((RecyclerView) mScrollableView);
        }
    }

    @Override
    public boolean onLoadingFinish(int footerHeight) {
        return mScrollableView != null && scrollAViewBy(mScrollableView, footerHeight);
    }

    //<editor-fold desc="private">
    private static boolean scrollAViewBy(View view, int height) {
        if (view instanceof RecyclerView) ((RecyclerView) view).smoothScrollBy(0, height);
        else if (view instanceof ScrollView) ((ScrollView) view).smoothScrollBy(0, height);
        else if (view instanceof AbsListView) ((AbsListView) view).smoothScrollBy(height, 150);
        else {
            try {
                Method method = view.getClass().getDeclaredMethod("smoothScrollBy", Integer.class, Integer.class);
                method.invoke(view, 0, height);
            } catch (Exception e) {
                view.scrollBy(0, height);
                return false;
            }
        }
        return true;
    }

    private void setUpAutoLoadmore(View scrollableView, RefreshKernel kernel) {
        if (scrollableView instanceof AbsListView) {
            AbsListView absListView = ((AbsListView) scrollableView);
            absListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (absListView.getAdapter() != null && absListView.getLastVisiblePosition() == absListView.getAdapter().getCount() - 1) {
                        kernel.getRefreshLayout().autoLoadmore(0,1);
                    }
                }
            });
        } else if (scrollableView instanceof RecyclerView) {
            RecyclerView recyclerView = ((RecyclerView) scrollableView);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
                    if (manager instanceof LinearLayoutManager) {
                        LinearLayoutManager linearManager = ((LinearLayoutManager) manager);
                        if(newState == RecyclerView.SCROLL_STATE_IDLE){
                            int lastVisiblePosition = linearManager.findLastVisibleItemPosition();
                            if(lastVisiblePosition >= linearManager.getItemCount() - 1){
                                kernel.getRefreshLayout().autoLoadmore(0,1);
                            }
                        }
                    }
                }
            });
        }
    }
    //</editor-fold>

    //<editor-fold desc="滚动判断">
    private static boolean canScrollUp(View targetView, MotionEvent event) {
        if (canScrollUp(targetView)) {
            return true;
        }
        if (targetView instanceof ViewGroup && event != null) {
            ViewGroup viewGroup = (ViewGroup) targetView;
            final int childCount = viewGroup.getChildCount();
            PointF point = new PointF();
            for (int i = 0; i < childCount; i++) {
                View child = viewGroup.getChildAt(i);
                if (isTransformedTouchPointInView(viewGroup,child, event.getX(), event.getY() , point)) {
                    event = MotionEvent.obtain(event);
                    event.offsetLocation(point.x, point.y);
                    return canScrollUp(child, event);
                }
            }
        }
        return false;
    }

    private static boolean pointInView(View view, float localX, float localY, float slop) {
        return localX >= -slop && localY >= -slop && localX < ((view.getWidth()) + slop) &&
                localY < ((view.getHeight()) + slop);
    }

    private static boolean canScrollUp(View targetView) {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (targetView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) targetView;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return targetView.getScrollY() > 0;
            }
        } else {
            return targetView.canScrollVertically(-1);
        }
    }

    private static boolean canScrollDown(View targetView, MotionEvent event) {
        if (canScrollDown(targetView)) {
            return true;
        }
        if (targetView instanceof ViewGroup && event != null) {
            ViewGroup viewGroup = (ViewGroup) targetView;
            final int childCount = viewGroup.getChildCount();
            PointF point = new PointF();
            for (int i = 0; i < childCount; i++) {
                View child = viewGroup.getChildAt(i);
                if (isTransformedTouchPointInView(viewGroup,child, event.getX(), event.getY() , point)) {
                    event = MotionEvent.obtain(event);
                    event.offsetLocation(point.x, point.y);
                    return canScrollDown(child, event);
                }
            }
        }
        return false;
    }

    private static boolean canScrollDown(View mScrollableView) {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mScrollableView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mScrollableView;
                return absListView.getChildCount() > 0
                        && (absListView.getLastVisiblePosition() < absListView.getChildCount() - 1
                        || absListView.getChildAt(absListView.getChildCount() - 1).getBottom() > absListView.getPaddingBottom());
            } else {
                return mScrollableView.getScrollY() < 0;
            }
        } else {
            return mScrollableView.canScrollVertically(1);
        }
    }

    private static boolean isTransformedTouchPointInView(ViewGroup group, View child, float x, float y,PointF outLocalPoint) {
        final float[] point = new float[2];
        point[0] = x;
        point[1] = y;
        transformPointToViewLocal(group, child, point);
        final boolean isInView = pointInView(child, point[0], point[1], 0);
        if (isInView && outLocalPoint != null) {
            outLocalPoint.set(point[0]-x, point[1]-y);
        }
        return isInView;
    }

    private static void transformPointToViewLocal(ViewGroup group, View child, float[] point) {
        point[0] += group.getScrollX() - child.getLeft();
        point[1] += group.getScrollY() - child.getTop();
    }
    //</editor-fold>

    private class PagerPrimaryAdapter extends PagerAdapterWrapper {
        private ViewPager mViewPager;

        PagerPrimaryAdapter(PagerAdapter wrapped) {
            super(wrapped);
        }

        void wrapper(PagerAdapter adapter) {
            wrapped = adapter;
        }

        @Override
        public void attachViewPager(ViewPager viewPager) {
            mViewPager = viewPager;
            super.attachViewPager(viewPager);
        }

        @Override
        public void setViewPagerObserver(DataSetObserver observer) {
            super.setViewPagerObserver(observer);
            if (observer == null) {
                wrapperViewPager(mViewPager, this);
            }
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            if (object instanceof View) {
                mScrollableView = ((View) object);
            } else if (object instanceof Fragment) {
                mScrollableView = ((Fragment) object).getView();
            }
            if (mScrollableView != null) {
                mScrollableView = findScrollableViewInternal(mScrollableView, true);
                if (mScrollableView instanceof NestedScrollingParent
                        && !(mScrollableView instanceof NestedScrollingChild)) {
                    mScrollableView = findScrollableViewInternal(mScrollableView, false);
                }
            }
        }
    }
}
