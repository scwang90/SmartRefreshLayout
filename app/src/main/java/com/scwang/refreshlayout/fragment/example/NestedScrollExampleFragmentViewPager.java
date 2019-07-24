package com.scwang.refreshlayout.fragment.example;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.scwang.refreshlayout.R;
import com.scwang.refreshlayout.adapter.BaseRecyclerAdapter;
import com.scwang.refreshlayout.adapter.SmartViewHolder;
import com.scwang.smartrefresh.header.PhoenixHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

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
        viewPager.setAdapter(new SmartPagerAdapter(getChildFragmentManager()));

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

    public static class SmartPagerAdapter extends FragmentStatePagerAdapter {

        private final SmartFragment[] fragments;

        SmartPagerAdapter(FragmentManager fm) {
            super(fm);
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

    public static class SmartFragment extends Fragment {

        private RecyclerView mRecyclerView;
        private BaseRecyclerAdapter<Void> mAdapter;

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            RefreshLayout refreshLayout = new SmartRefreshLayout(inflater.getContext());
            refreshLayout.setRefreshHeader(new PhoenixHeader(inflater.getContext()));
            refreshLayout.setRefreshContent(mRecyclerView = new RecyclerView(inflater.getContext()));
            refreshLayout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);
            refreshLayout.setEnableLoadMore(false);
            return refreshLayout.getLayout();
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
            refreshLayout.getLayout().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAdapter.refresh(initData());
                    refreshLayout.finishRefresh();
                    refreshLayout.resetNoMoreData();//setNoMoreData(false);
                }
            }, 2000);
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
}
