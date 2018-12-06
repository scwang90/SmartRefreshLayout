package com.scwang.refreshlayout.fragment.example;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.scwang.refreshlayout.R;
import com.scwang.refreshlayout.adapter.BaseRecyclerAdapter;
import com.scwang.refreshlayout.adapter.SmartViewHolder;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.scwang.smartrefresh.layout.util.ViscousFluidInterpolator;

import java.util.Arrays;
import java.util.Collection;

import static android.R.layout.simple_list_item_2;

/**
 * 使用示例-嵌套滚动-ViewPager
 * A simple {@link Fragment} subclass.
 */
public class NestedScrollExampleFragmentViewPager extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_example_nestedscroll_view_pager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        Toolbar toolbar = root.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        ViewPager viewPager = root.findViewById(R.id.viewPager);
        viewPager.setAdapter(new SmartPagerAdapter());

        /*
         * 监听 AppBarLayout 的关闭和开启 ActionButton 设置关闭隐藏动画
         */
        AppBarLayout appBarLayout = root.findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean misAppbarExpand = true;
            View fab = root.findViewById(R.id.fab);
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int scrollRange = appBarLayout.getTotalScrollRange();
                float fraction = 1f * (scrollRange + verticalOffset) / scrollRange;
                if (fraction < 0.1 && misAppbarExpand) {
                    misAppbarExpand = false;
                    fab.animate().scaleX(0).scaleY(0);
                }
                if (fraction > 0.8 && !misAppbarExpand) {
                    misAppbarExpand = true;
                    fab.animate().scaleX(1).scaleY(1);
                }
            }
        });

    }

    private class SmartPagerAdapter extends FragmentStatePagerAdapter {

        private final SmartFragment[] fragments;

        SmartPagerAdapter() {
            super(getChildFragmentManager());
            this.fragments = new SmartFragment[]{
                    new SmartFragment(),new SmartFragment()
            };
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }
    }

    public static class SmartFragment extends Fragment implements OnRefreshLoadMoreListener {

        private RecyclerView mRecyclerView;
        private BaseRecyclerAdapter<Void> mAdapter;

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            SmartRefreshLayout refreshLayout = new SmartRefreshLayout(inflater.getContext());
            refreshLayout.addView(mRecyclerView = new RecyclerView(inflater.getContext()), -1, -1);
//            refreshLayout.setRefreshHeader(new PhoenixHeader(inflater.getContext()));
//            refreshLayout.setRefreshContent(mRecyclerView = new RecyclerView(inflater.getContext()));
//            refreshLayout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);
//            refreshLayout.setEnableLoadMore(false);
//            refreshLayout.setOnRefreshLoadMoreListener(this);
            return refreshLayout;//.getLayout();
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
            mRecyclerView.setAdapter(mAdapter = new BaseRecyclerAdapter<Void>(initData(), simple_list_item_2) {
                @Override
                protected void onBindViewHolder(SmartViewHolder holder, Void model, int position) {
                    holder.text(android.R.id.text1, getString(R.string.item_example_number_title, position));
                    holder.text(android.R.id.text2, getString(R.string.item_example_number_abstract, position));
                    holder.textColorId(android.R.id.text2, R.color.colorTextAssistant);
                }
            });
        }

        private Collection<Void> initData() {
            return Arrays.asList(null,null,null);
        }

        public void onRefresh(final RefreshLayout refreshLayout) {
//            refreshLayout.getLayout().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    mAdapter.refresh(initData());
//                    refreshLayout.finishRefresh();
//                    refreshLayout.resetNoMoreData();//setNoMoreData(false);
//                }
//            }, 2000);
        }

        public void onLoadMore(final RefreshLayout refreshLayout) {
            refreshLayout.getLayout().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAdapter.loadMore(initData());
                    if (mAdapter.getItemCount() > 60) {
                        Toast.makeText(getContext(), "数据全部加载完毕", Toast.LENGTH_SHORT).show();
                        refreshLayout.finishLoadMoreWithNoMoreData();//将不会再次触发加载更多事件
                    } else {
                        refreshLayout.finishLoadMore();
                    }
                }
            }, 2000);
        }
    }

    public static class SmartRefreshLayout extends LinearLayout implements NestedScrollingParent {

//        NestedScrollingParent parent;
        protected int[] mParentOffsetInWindow = new int[2];
        protected NestedScrollingChildHelper mNestedChild = new NestedScrollingChildHelper(this);
        protected NestedScrollingParentHelper mNestedParent = new NestedScrollingParentHelper(this);
        protected ValueAnimator reboundAnimator;

        public SmartRefreshLayout(Context context) {
            super(context);
            mNestedChild.setNestedScrollingEnabled(true);
        }

        @Override
        public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int nestedScrollAxes) {
            return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
        }

        @Override
        public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int nestedScrollAxes) {
            mNestedParent.onNestedScrollAccepted(child, target, nestedScrollAxes);
            mNestedChild.startNestedScroll(nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL);
            if (reboundAnimator != null) {
                reboundAnimator.cancel();
            }
        }

        @Override
        public void onStopNestedScroll(@NonNull View target) {
            mNestedParent.onStopNestedScroll(target);
            mNestedChild.stopNestedScroll();
            animSpinner(0);

        }

        @Override
        public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
            mNestedChild.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, mParentOffsetInWindow);
            dellScroll(dyUnconsumed + mParentOffsetInWindow[1]);
        }

        @Override
        public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed) {

            int scrollY = this.getScrollY();
            if (scrollY * dy < 0) {
                if (Math.abs(dy) > Math.abs(scrollY)) {
                    consumed[1] = scrollY;
                    dellScroll(0);
                } else {
                    consumed[1] = dy;
                    dellScroll(dy);
                }

            }

            final int[] parentConsumed = mParentOffsetInWindow;
            if (mNestedChild.dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], parentConsumed, null)) {
                consumed[0] += parentConsumed[0];
                consumed[1] += parentConsumed[1];
            }
        }

        @Override
        public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed) {
            return mNestedChild.dispatchNestedFling(velocityX, velocityY, consumed);
        }

        @Override
        public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
            return mNestedChild.dispatchNestedPreFling(velocityX, velocityY);
        }

        @Override
        public int getNestedScrollAxes() {
            return mNestedParent.getNestedScrollAxes();
        }

        private void dellScroll(int dy) {
            int scrollY = this.getScrollY();
            setScrollY(scrollY + dy);
        }

        /**
         * 执行回弹动画
         */
        protected void animSpinner(int endSpinner) {
            final int mSpinner = this.getScrollY();
            if (getScrollY() != endSpinner) {
                if (reboundAnimator != null) {
                    reboundAnimator.cancel();
                }
                reboundAnimator = ValueAnimator.ofInt(mSpinner, endSpinner);
                reboundAnimator.setDuration(300);
                reboundAnimator.setInterpolator(new ViscousFluidInterpolator());
                reboundAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        super.onAnimationEnd(animation);
                    }
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        reboundAnimator = null;
                    }
                });
                reboundAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (int) animation.getAnimatedValue();
                        dellScroll(value - getScrollY());
                    }
                });
                reboundAnimator.start();
            }
        }
    }
}
