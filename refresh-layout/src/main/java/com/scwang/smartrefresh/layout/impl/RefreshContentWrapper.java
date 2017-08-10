package com.scwang.smartrefresh.layout.impl;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerAdapterWrapper;
import android.support.v4.view.ScrollingView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ListViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.Space;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
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
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.api.ScrollBoundaryDecider;
import com.scwang.smartrefresh.layout.constant.RefreshState;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.scwang.smartrefresh.layout.util.ScrollBoundaryUtil.canScrollDown;
import static com.scwang.smartrefresh.layout.util.ScrollBoundaryUtil.canScrollUp;

/**
 * 刷新内容包装
 * Created by SCWANG on 2017/5/26.
 */
@SuppressWarnings("WeakerAccess")
public class RefreshContentWrapper implements RefreshContent {

    protected static final String TAG_REFRESH_CONTENT_WRAPPER = "TAG_REFRESH_CONTENT_WRAPPER";

    protected int mHeaderHeight = Integer.MAX_VALUE;
    protected int mFooterHeight = mHeaderHeight - 1;
    protected View mContentView;//直接内容视图
    protected View mRealContentView;//被包裹的原真实视图
    protected View mScrollableView;
    protected View mFixedHeader;
    protected View mFixedFooter;
    protected boolean mEnableRefresh = true;
    protected boolean mEnableLoadmore = true;
    protected MotionEvent mMotionEvent;
    protected ScrollBoundaryDeciderAdapter mBoundaryAdapter = new ScrollBoundaryDeciderAdapter();

    public RefreshContentWrapper(View view) {
        this.mContentView = mRealContentView = view;
        this.mContentView.setTag(TAG_REFRESH_CONTENT_WRAPPER.hashCode(), TAG_REFRESH_CONTENT_WRAPPER);
    }

    public RefreshContentWrapper(Context context) {
        this.mContentView = mRealContentView = new View(context);
        this.mContentView.setTag(TAG_REFRESH_CONTENT_WRAPPER.hashCode(), TAG_REFRESH_CONTENT_WRAPPER);
    }

    public static boolean isTagedContent(View view) {
        return TAG_REFRESH_CONTENT_WRAPPER.equals(view.getTag(TAG_REFRESH_CONTENT_WRAPPER.hashCode()));
    }

    //<editor-fold desc="findScrollableView">
    protected void findScrollableView(View content, RefreshKernel kernel) {
        mScrollableView = findScrollableViewInternal(content, true);
        try {//try 不能删除，不然会出现兼容性问题
            if (mScrollableView instanceof CoordinatorLayout) {
                kernel.getRefreshLayout().setEnableNestedScroll(false);
                wrapperCoordinatorLayout(((CoordinatorLayout) mScrollableView), kernel.getRefreshLayout());
            }
        } catch (Throwable ignored) {
        }
        try {//try 不能删除，不然会出现兼容性问题
            if (mScrollableView instanceof ViewPager) {
                wrapperViewPager((ViewPager) this.mScrollableView);
            }
        } catch (Throwable ignored) {
        }
        if (mScrollableView instanceof NestedScrollingParent
                && !(mScrollableView instanceof NestedScrollingChild)) {
            mScrollableView = findScrollableViewInternal(mScrollableView, false);
        }
        if (mScrollableView == null) {
            mScrollableView = content;
        }
    }

    protected void wrapperCoordinatorLayout(CoordinatorLayout layout, final RefreshLayout refreshLayout) {
        for (int i = layout.getChildCount() - 1; i >= 0; i--) {
            View view = layout.getChildAt(i);
            if (view instanceof AppBarLayout) {
                ((AppBarLayout) view).addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                    @Override
                    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                        mEnableRefresh = verticalOffset >= 0;
                        mEnableLoadmore = refreshLayout.isEnableLoadmore() && (appBarLayout.getTotalScrollRange() + verticalOffset) <= 0;
                    }
                });
            }
        }
    }

    protected void wrapperViewPager(final ViewPager viewPager) {
        wrapperViewPager(viewPager, null);
    }

    protected void wrapperViewPager(final ViewPager viewPager, final PagerPrimaryAdapter primaryAdapter) {
        viewPager.post(new Runnable() {
            int count = 0;
            PagerPrimaryAdapter mAdapter = primaryAdapter;
            @Override
            public void run() {
                count++;
                PagerAdapter adapter = viewPager.getAdapter();
                if (adapter != null) {
                    if (adapter instanceof PagerPrimaryAdapter) {
                        if (adapter == primaryAdapter && count < 10) {
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

    protected View findScrollableViewInternal(View content, boolean selfable) {
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
    public boolean canRefresh() {
        return mEnableRefresh && mBoundaryAdapter.canRefresh(mContentView);
    }

    @Override
    public boolean canLoadmore() {
        return mEnableLoadmore && mBoundaryAdapter.canLoadmore(mContentView);
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
        mBoundaryAdapter.setActionEvent(mMotionEvent);
    }

    @Override
    public void onActionUpOrCancel() {
        mMotionEvent = null;
        mBoundaryAdapter.setActionEvent(null);
    }

    @Override
    public void setupComponent(RefreshKernel kernel, View fixedHeader, View fixedFooter) {
        this.findScrollableView(mContentView, kernel);
        try {//try 不能删除，不然会出现兼容性问题
            if (mScrollableView instanceof RecyclerView) {
                RecyclerViewScrollComponent component = new RecyclerViewScrollComponent(kernel);
                component.attach((RecyclerView) mScrollableView);
            }
        } catch (Throwable ignored) {
        }
        try {//try 不能删除，不然会出现兼容性问题
            if (mScrollableView instanceof NestedScrollView) {
                NestedScrollViewScrollComponent component = new NestedScrollViewScrollComponent(kernel);
                component.attach((NestedScrollView) mScrollableView);
            }
        } catch (Throwable ignored) {
        }

        if (mScrollableView instanceof AbsListView) {
            AbsListViewScrollComponent component = new AbsListViewScrollComponent(kernel);
            component.attach(((AbsListView) mScrollableView));
        } else if (Build.VERSION.SDK_INT >= 23 && mScrollableView != null) {
            Api23ViewScrollComponent component = new Api23ViewScrollComponent(kernel);
            component.attach(mScrollableView);
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
    public void setEnableLoadmoreWhenContentNotFull(boolean enable) {
        mBoundaryAdapter.setEnableLoadmoreWhenContentNotFull(enable);
    }

    @Override
    public AnimatorUpdateListener onLoadingFinish(final RefreshKernel kernel, final int footerHeight, int startDelay, final int duration) {
        if (mScrollableView != null && kernel.getRefreshLayout().isEnableScrollContentWhenLoaded()) {
            if (!canScrollDown(mScrollableView)) {
                return null;
            }
            if (mScrollableView instanceof AbsListView && !(mScrollableView instanceof ListView) && Build.VERSION.SDK_INT < 19) {
                if (startDelay > 0) {
                    kernel.getRefreshLayout().getLayout().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ((AbsListView) mScrollableView).smoothScrollBy(footerHeight, duration);
                        }
                    }, startDelay);
                } else {
                    ((AbsListView) mScrollableView).smoothScrollBy(footerHeight, duration);
                }
                return null;
            }
            return new AnimatorUpdateListener() {
                int lastValue = kernel.getSpinner();
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (int) animation.getAnimatedValue();
                    if (mScrollableView instanceof ListView) {
                        ListViewCompat.scrollListBy((ListView) mScrollableView, value - lastValue);
                    } else {
                        mScrollableView.scrollBy(0, value - lastValue);
                    }
                    lastValue = value;
                }
            };
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold desc="滚动组件">
    @RequiresApi(api = Build.VERSION_CODES.M)
    protected class Api23ViewScrollComponent implements View.OnScrollChangeListener {
        long lastTime = 0;
        long lastTimeOld = 0;
        int lastScrollY = 0;
        int lastOldScrollY = 0;
        RefreshKernel kernel;
        View.OnScrollChangeListener mScrollListener;

        Api23ViewScrollComponent(RefreshKernel kernel) {
            this.kernel = kernel;
        }

        @Override
        public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            if (mScrollListener != null) {
                mScrollListener.onScrollChange(v, scrollX, scrollY, oldScrollX, oldScrollY);
            }
            if (lastScrollY == scrollY && lastOldScrollY == oldScrollY) {
                return;
            }
            RefreshLayout layout = kernel.getRefreshLayout();
            boolean overScroll = layout.isEnableOverScrollBounce() || layout.isRefreshing() || layout.isLoading();
            if (scrollY <= 0 && oldScrollY > 0 && mMotionEvent == null && lastTime - lastTimeOld > 1000 && overScroll && layout.isEnableRefresh()) {
                //time:16000000 value:160
                final int velocity = (lastOldScrollY - oldScrollY) * 16000 / (int)((lastTime - lastTimeOld)/1000f);
                kernel.animSpinnerBounce(Math.min(velocity, mHeaderHeight));
            } else if (oldScrollY < scrollY && mMotionEvent == null && layout.isEnableLoadmore()) {
                if (!layout.isLoadmoreFinished() && layout.isEnableAutoLoadmore()
                        && !layout.isEnablePureScrollMode()
                        && layout.getState() == RefreshState.None
                        && !canScrollDown(v)) {
                    kernel.getRefreshLayout().autoLoadmore(0, 1);
                } else
                    if (overScroll && lastTime - lastTimeOld > 1000 && !canScrollDown(v)) {
                    final int velocity = (lastOldScrollY - oldScrollY) * 16000 / (int)((lastTime - lastTimeOld)/1000f);
                    kernel.animSpinnerBounce(Math.max(velocity, -mFooterHeight));
                }
            }
            lastScrollY = scrollY;
            lastOldScrollY = oldScrollY;
            lastTimeOld = lastTime;
            lastTime = System.nanoTime();
        }

        void attach(View view) {
            Field[] declaredFields = View.class.getDeclaredFields();
            if (declaredFields != null) {
                for (Field field : declaredFields) {
                    if (View.OnScrollChangeListener.class.equals(field.getType())) {
                        try {
                            field.setAccessible(true);
                            Object listener = field.get(view);
                            if (listener != null && !view.equals(listener)) {
                                mScrollListener = (View.OnScrollChangeListener) listener;
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            view.setOnScrollChangeListener(new Api23ViewScrollComponent(kernel));
        }
    }

    protected class NestedScrollViewScrollComponent implements NestedScrollView.OnScrollChangeListener {
        long lastTime = 0;
        long lastTimeOld = 0;
        int lastScrollY = 0;
        int lastOldScrollY = 0;
        RefreshKernel kernel;
        NestedScrollView.OnScrollChangeListener mScrollChangeListener;

        NestedScrollViewScrollComponent(RefreshKernel kernel) {
            this.kernel = kernel;
        }

        @Override
        public void onScrollChange(NestedScrollView scrollView, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            if (mScrollChangeListener != null) {
                mScrollChangeListener.onScrollChange(scrollView, scrollX, scrollY, oldScrollX, oldScrollY);
            }
            if (lastScrollY == scrollY && lastOldScrollY == oldScrollY) {
                return;
            }
            RefreshLayout layout = kernel.getRefreshLayout();
            boolean overScroll = layout.isEnableOverScrollBounce() || layout.isRefreshing() || layout.isLoading();
            if (scrollY <= 0 && oldScrollY > 0 && mMotionEvent == null && lastTime - lastTimeOld > 1000 && overScroll && layout.isEnableRefresh()) {
                final int velocity = (lastOldScrollY - oldScrollY) * 16000 / (int)((lastTime - lastTimeOld)/1000f);
                kernel.animSpinnerBounce(Math.min(velocity, mHeaderHeight));
            } else if (oldScrollY < scrollY && mMotionEvent == null && layout.isEnableLoadmore()) {
                if (!layout.isLoadmoreFinished() && layout.isEnableAutoLoadmore()
                        && !layout.isEnablePureScrollMode()
                        && layout.getState() == RefreshState.None
                        && !canScrollDown(scrollView)) {
                    kernel.getRefreshLayout().autoLoadmore(0, 1);
                } else if (overScroll && lastTime - lastTimeOld > 1000 && !canScrollDown(mScrollableView)) {
                    final int velocity = (lastOldScrollY - oldScrollY) * 16000 / (int)((lastTime - lastTimeOld)/1000f);
                    kernel.animSpinnerBounce(Math.max(velocity, -mFooterHeight));
                }
            }
            lastScrollY = scrollY;
            lastOldScrollY = oldScrollY;
            lastTimeOld = lastTime;
            lastTime = System.nanoTime();
        }

        void attach(NestedScrollView scrollView) {
            //获得原始监听器，用作转发
            Field[] declaredFields = NestedScrollView.class.getDeclaredFields();
            if (declaredFields != null) {
                for (Field field : declaredFields) {
                    if (NestedScrollView.OnScrollChangeListener.class.equals(field.getType())) {
                        try {
                            field.setAccessible(true);
                            Object listener = field.get(scrollView);
                            if (listener != null && !scrollView.equals(listener)) {
                                mScrollChangeListener = (NestedScrollView.OnScrollChangeListener) listener;
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            scrollView.setOnScrollChangeListener(this);
        }
    }

    protected class AbsListViewScrollComponent implements AbsListView.OnScrollListener {

        int scrollY;
        int scrollDy;
        int lastScrolly;
        int lastScrollDy;
        RefreshKernel kernel;
        SparseArray<ItemRecod> recordSp = new SparseArray<>(0);
        AbsListView.OnScrollListener mScrollListener;

        AbsListViewScrollComponent(RefreshKernel kernel) {
            this.kernel = kernel;
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (mScrollListener != null) {
                mScrollListener.onScrollStateChanged(view, scrollState);
            }
        }

        @Override
        public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (mScrollListener != null) {
                mScrollListener.onScroll(absListView, firstVisibleItem, visibleItemCount, totalItemCount);
            }

            lastScrolly = scrollY;
            lastScrollDy = scrollDy;
            scrollY = getScrollY(absListView, firstVisibleItem);
            scrollDy = lastScrolly - scrollY;

            final int dy = lastScrollDy + scrollDy;
            if (totalItemCount > 0 && mMotionEvent == null) {
                RefreshLayout layout = kernel.getRefreshLayout();
                if (dy > 0) {
                    if (firstVisibleItem == 0
                            && layout.isEnableRefresh()
                            && (layout.isEnableOverScrollBounce() || layout.isRefreshing())
                            && !canScrollUp(absListView)) {
                        kernel.animSpinnerBounce(Math.min(dy, mHeaderHeight));
                    }
                } else if (dy < 0) {
                    int lastVisiblePosition = absListView.getLastVisiblePosition();
                    if (lastVisiblePosition == totalItemCount - 1 && lastVisiblePosition > 0
                            && layout.isEnableLoadmore()
                            && !canScrollDown(absListView)) {
                        if (layout.getState() == RefreshState.None
                                && layout.isEnableAutoLoadmore()
                                && !layout.isLoadmoreFinished()
                                && !layout.isEnablePureScrollMode()) {
                            layout.autoLoadmore(0, 1);
                        } else if (layout.isEnableOverScrollBounce() || layout.isLoading()) {
                            kernel.animSpinnerBounce(Math.max(dy, -mFooterHeight));
                        }
                    }
                }
            }

        }

        void attach(AbsListView listView) {
            //获得原始监听器，用作转发
            Field[] declaredFields = AbsListView.class.getDeclaredFields();
            if (declaredFields != null) {
                for (Field field : declaredFields) {
                    if (AbsListView.OnScrollListener.class.equals(field.getType())) {
                        try {
                            field.setAccessible(true);
                            Object listener = field.get(listView);
                            if (listener != null && !listView.equals(listener)) {
                                mScrollListener = (AbsListView.OnScrollListener) listener;
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            listView.setOnScrollListener(this);
        }

        protected int getScrollY(AbsListView view, int firstVisibleItem) {
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

    protected class RecyclerViewScrollComponent extends RecyclerView.OnScrollListener {
        RefreshKernel kernel;
        RecyclerViewScrollComponent(RefreshKernel kernel) {
            this.kernel = kernel;
        }
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        }
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (mMotionEvent == null) {
                final RefreshLayout layout = kernel.getRefreshLayout();
                if (dy < 0 && layout.isEnableRefresh()
                        && (layout.isEnableOverScrollBounce() || layout.isRefreshing())
                        && !canScrollUp(recyclerView)) {
                    kernel.animSpinnerBounce(Math.min(-dy * 2, mHeaderHeight));
                } else if (dy > 0 && layout.isEnableLoadmore() && !canScrollDown(recyclerView)) {
                    if (layout.getState() == RefreshState.None
                            && layout.isEnableAutoLoadmore()
                            && !layout.isLoadmoreFinished()
                            && !layout.isEnablePureScrollMode()) {
                        layout.autoLoadmore(0,1);
                    } else if (layout.isEnableOverScrollBounce() || layout.isLoading()) {
                        kernel.animSpinnerBounce(Math.max(-dy * 2, -mFooterHeight));
                    }
                }
            }
        }
        void attach(RecyclerView recyclerView) {
            recyclerView.addOnScrollListener(this);
        }
    }
    //</editor-fold>

    //<editor-fold desc="protected">
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
    //</editor-fold>

    protected class PagerPrimaryAdapter extends PagerAdapterWrapper {
        protected ViewPager mViewPager;

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
