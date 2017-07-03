package com.scwang.smartrefresh.layout.impl;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.PointF;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerAdapterWrapper;
import android.support.v4.view.ScrollingView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.Space;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ScrollView;
import android.widget.WrapperListAdapter;

import com.scwang.smartrefresh.layout.api.RefreshContent;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * 刷新内容包装
 * Created by SCWANG on 2017/5/26.
 */

public class RefreshContentWrapper implements RefreshContent {

    private int mHeaderHeight = Integer.MAX_VALUE;
    private int mFooterHeight = mHeaderHeight - 1;
    private View mContentView;
    private View mRealContentView;
    private View mScrollableView;
    private View mFixedHeader;
    private View mFixedFooter;
    private MotionEvent mMotionEvent;

    public RefreshContentWrapper(View view) {
        this.mContentView = mRealContentView = view;
        this.findScrollableView(view);
    }

    public RefreshContentWrapper(Context context) {
        this.mContentView = mRealContentView = new View(context);
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
        if (mScrollableView == null) {
            mScrollableView = content;
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

    @Override
    public void setupComponent(RefreshKernel kernel, View fixedHeader, View fixedFooter) {
        TimeInterpolator interpolator = new DecelerateInterpolator();
        AnimatorUpdateListener updateListener = animation -> kernel.moveSpinner((int) animation.getAnimatedValue(), true);
        if (mScrollableView instanceof RecyclerView) {
            RecyclerViewScrollComponent component = new RecyclerViewScrollComponent(kernel, interpolator, updateListener);
            component.attach((RecyclerView) mScrollableView);
        } else if (mScrollableView instanceof AbsListView) {
            AbsListViewScrollComponent component = new AbsListViewScrollComponent(kernel, interpolator, updateListener);
            component.attach(((AbsListView) mScrollableView));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mScrollableView != null) {
            mScrollableView.setOnScrollChangeListener(new Api23ViewScrollComponent(kernel, interpolator, updateListener));
        }
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
                ViewGroup.LayoutParams lp = fixedHeader.getLayoutParams();
                ViewGroup parent = (ViewGroup) fixedHeader.getParent();
                int index = parent.indexOfChild(fixedHeader);
                parent.removeView(fixedHeader);
                lp.height = measureViewHeight(fixedHeader);
                parent.addView(new Space(mContentView.getContext()), index, lp);
                frameLayout.addView(fixedHeader);
            }
            if (fixedFooter != null) {
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
    public AnimatorUpdateListener onLoadingFinish(int footerHeight, Interpolator interpolator, int duration) {
        if (mScrollableView != null) {
            if (mScrollableView instanceof RecyclerView) ((RecyclerView) mScrollableView).smoothScrollBy(0, footerHeight, interpolator);
            else if (mScrollableView instanceof ScrollView) ((ScrollView) mScrollableView).smoothScrollBy(0, footerHeight);
            else if (mScrollableView instanceof AbsListView) ((AbsListView) mScrollableView).smoothScrollBy(footerHeight, duration);
            else {
                try {
                    Method method = mScrollableView.getClass().getDeclaredMethod("smoothScrollBy", Integer.class, Integer.class);
                    method.invoke(mScrollableView, 0, footerHeight);
                } catch (Exception e) {
                    int scrollX = mScrollableView.getScrollX();
                    int scrollY = mScrollableView.getScrollY();
                    return animation -> mScrollableView.scrollTo(scrollX, scrollY + (int) animation.getAnimatedValue());
                }
            }
            return null;
        }
        return null;
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

    //<editor-fold desc="滚动组件">
    @RequiresApi(api = Build.VERSION_CODES.M)
    private class Api23ViewScrollComponent implements View.OnScrollChangeListener {
        long lastTime = 0;
        long lastTimeOld = 0;
        int lastScrollY = 0;
        int lastOldScrollY = 0;
        ValueAnimator animator;
        RefreshKernel kernel;
        TimeInterpolator interpolator;
        AnimatorUpdateListener updateListener;

        Api23ViewScrollComponent(RefreshKernel kernel, TimeInterpolator interpolator, AnimatorUpdateListener updateListener) {
            this.kernel = kernel;
            this.interpolator = interpolator;
            this.updateListener = updateListener;
        }

        @Override
        public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            if (lastScrollY == scrollY && lastOldScrollY == oldScrollY) {
                return;
            }
//            System.out.printf("%d,%d,%d,%d\n", scrollX, scrollY, oldScrollX, oldScrollY);
            if (scrollY <= 0 && oldScrollY > 0 && animator == null && mMotionEvent == null) {
                RefreshLayout layout = kernel.getRefreshLayout();
                boolean overScroll = layout.isEnableOverScrollBounce()
                        && !layout.isRefreshing()
                        && !layout.isLoading();
                if (overScroll) {
                    //time:16000000 value:160
                    final int velocity = (lastOldScrollY - oldScrollY) * 16000 / (int)((lastTime - lastTimeOld)/1000f);
//                    System.out.println("ValueAnimator - " + (lastTime - lastTimeOld) + " - " + velocity+"("+(lastOldScrollY - oldScrollY)+")");
                    animator = ValueAnimator.ofInt(0, Math.min(velocity, mHeaderHeight), 0);
                    animator.setDuration(500);
                    animator.addUpdateListener(updateListener);
                    animator.setInterpolator(interpolator);
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            animator = null;
                        }
                    });
                    animator.start();
                }
            } else if (animator == null && mMotionEvent == null && oldScrollY < scrollY && !canScrollDown(mScrollableView)) {
                RefreshLayout layout = kernel.getRefreshLayout();
                boolean overScroll = layout.isEnableOverScrollBounce()
                        && !layout.isRefreshing()
                        && !layout.isLoading();
                if (overScroll) {
                    final int velocity = (lastOldScrollY - oldScrollY) * 16000 / (int)((lastTime - lastTimeOld)/1000f);
//                    System.out.println("ValueAnimator - " + (lastTime - lastTimeOld) + " - " + velocity+"("+(lastOldScrollY - oldScrollY)+")");
                    animator = ValueAnimator.ofInt(0, Math.max(velocity, -mFooterHeight), 0);
                    animator.setDuration(500);
                    animator.addUpdateListener(updateListener);
                    animator.setInterpolator(interpolator);
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            animator = null;
                        }
                    });
                    animator.start();
                }
            }
            lastScrollY = scrollY;
            lastOldScrollY = oldScrollY;
            lastTimeOld = lastTime;
            lastTime = System.nanoTime();
        }
    }

    private class AbsListViewScrollComponent implements AbsListView.OnScrollListener {

        int lasty;
        int lastDy;
        int mlastVisiblePosition;
        int mFirstVisiblePosition;
        RefreshKernel kernel;
        TimeInterpolator interpolator;
        AnimatorUpdateListener updateListener;
        SparseArray<ItemRecod> recordSp = new SparseArray<>(0);

        AbsListViewScrollComponent(RefreshKernel kernel, TimeInterpolator interpolator, AnimatorUpdateListener updateListener) {
            this.kernel = kernel;
            this.interpolator = interpolator;
            this.updateListener = updateListener;
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

        @Override
        public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            int scrollY = getScrollY(absListView, firstVisibleItem);
            RefreshLayout layout = kernel.getRefreshLayout();
            ListAdapter adapter = absListView.getAdapter();
            if (adapter instanceof WrapperListAdapter) {
                adapter = ((WrapperListAdapter) adapter).getWrappedAdapter();
            }
            lastDy = lasty - scrollY;
            lasty = scrollY;
            int lastVisiblePosition = absListView.getLastVisiblePosition();
            int firstVisiblePosition = absListView.getFirstVisiblePosition();
            boolean overScroll = layout.isEnableOverScrollBounce()
                    && !layout.isRefreshing()
                    && !layout.isLoading()
                    && mMotionEvent == null;
            if (mFirstVisiblePosition != firstVisiblePosition && lastDy > 0 && overScroll) {
                mFirstVisiblePosition = firstVisiblePosition;
                if (adapter != null && firstVisiblePosition == 0) {
                    ValueAnimator animator = ValueAnimator.ofInt(0, Math.min(lastDy, mHeaderHeight), 0);
                    animator.setDuration(500);
                    animator.addUpdateListener(updateListener);
                    animator.setInterpolator(interpolator);
                    animator.start();
                }
            } else if (layout.isEnableLoadmore() && !layout.isLoadmoreFinished()) {
                if (mlastVisiblePosition != lastVisiblePosition && lastVisiblePosition > 0) {
                    mlastVisiblePosition = lastVisiblePosition;
                    if (adapter != null && lastVisiblePosition == adapter.getCount() - 1) {
                        kernel.getRefreshLayout().autoLoadmore(0, 1);
                    }
                }
            } else if (overScroll) {
                if (mlastVisiblePosition != lastVisiblePosition && lastDy < 0 && layout.isEnableLoadmore()) {
                    mlastVisiblePosition = lastVisiblePosition;
                    if (adapter != null && lastVisiblePosition == adapter.getCount() - 1) {
                        ValueAnimator animator = ValueAnimator.ofInt(0, Math.max(lastDy, -mFooterHeight), 0);
                        animator.setDuration(500);
                        animator.addUpdateListener(updateListener);
                        animator.setInterpolator(interpolator);
                        animator.start();
                    }
                }
            }
        }

        void attach(AbsListView listView) {
            listView.setOnScrollListener(this);
        }

        private int getScrollY(AbsListView view, int firstVisibleItem) {
            View firstView = view.getChildAt(0);
            if (null != firstView) {
                ItemRecod itemRecord = recordSp.get(firstVisibleItem);
                if (null == itemRecord) {
                    itemRecord = new ItemRecod();
                }
                itemRecord.height = firstView.getHeight();
                itemRecord.top = firstView.getTop();
                recordSp.append(firstVisibleItem, itemRecord);

                int height = 0,lastheight = 0;
                for (int i = 0; i < firstVisibleItem; i++) {
                    ItemRecod itemRecod = recordSp.get(i);
                    if (itemRecod != null) {
                        height += itemRecod.height;
                        lastheight = itemRecod.height;
                    } else {
                        height += lastheight;
                    }
                }
                ItemRecod itemRecod = recordSp.get(firstVisibleItem);
                if (null == itemRecod) {
                    itemRecod = new ItemRecod();
                }
                return height - itemRecod.top;
            }
            return 0;
        }

        class ItemRecod {
            int height = 0;
            int top = 0;
        }
    }

    private class RecyclerViewScrollComponent extends RecyclerView.OnScrollListener {
        int lastDy;
        long lastFlingTime;
        RefreshKernel kernel;
        TimeInterpolator interpolator;
        AnimatorUpdateListener updateListener;

        RecyclerViewScrollComponent(RefreshKernel kernel, TimeInterpolator interpolator, AnimatorUpdateListener updateListener) {
            this.kernel = kernel;
            this.interpolator = interpolator;
            this.updateListener = updateListener;
        }
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            RefreshLayout layout = kernel.getRefreshLayout();
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                boolean intime = System.currentTimeMillis() - lastFlingTime < 1000;
                boolean overScroll = layout.isEnableOverScrollBounce() && !layout.isRefreshing() && !layout.isLoading();
                if (lastDy < -1 && intime && overScroll) {
                    ValueAnimator animator = ValueAnimator.ofInt(0, Math.min(-lastDy * 2, mHeaderHeight), 0);
                    animator.setDuration(500);
                    animator.addUpdateListener(updateListener);
                    animator.setInterpolator(interpolator);
                    animator.start();
                } else if (layout.isEnableLoadmore() && !layout.isLoadmoreFinished()) {
                    RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
                    if (manager instanceof LinearLayoutManager) {
                        LinearLayoutManager linearManager = ((LinearLayoutManager) manager);
                        int lastVisiblePosition = linearManager.findLastVisibleItemPosition();
                        if(lastVisiblePosition >= linearManager.getItemCount() - 1){
                            kernel.getRefreshLayout().autoLoadmore(0,1);
                        }
                    }
                } else if (lastDy > 1 && intime && overScroll && layout.isEnableLoadmore()) {
                    ValueAnimator animator = ValueAnimator.ofInt(0, Math.max(-lastDy * 2, -mFooterHeight), 0);
                    animator.setDuration(500);
                    animator.addUpdateListener(updateListener);
                    animator.setInterpolator(interpolator);
                    animator.start();
                }
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
    //</editor-fold>

    //<editor-fold desc="private">
    private static int measureViewHeight(View view) {
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
