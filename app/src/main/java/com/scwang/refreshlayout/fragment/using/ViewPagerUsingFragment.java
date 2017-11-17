package com.scwang.refreshlayout.fragment.using;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
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
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

import static android.R.layout.simple_list_item_2;
import static com.scwang.refreshlayout.R.id.refreshLayout;

/**
 * 使用示例-ViewPager页面
 * A simple {@link Fragment} subclass.
 */
public class ViewPagerUsingFragment extends Fragment implements OnRefreshListener, OnRefreshLoadmoreListener {


    public enum Item {
        NestedInner("左边", SmartFragment.class),
        NestedOuter("右边", SmartFragment.class),
        ;
        public String name;
        public Class<? extends Fragment> clazz;
        Item(String name, Class<? extends Fragment> clazz) {
            this.name = name;
            this.clazz = clazz;
        }
    }

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private RefreshLayout mRefreshLayout;
    private SmartPagerAdapter mAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_using_viewpager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        final Toolbar toolbar = (Toolbar)root.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        mRefreshLayout = (RefreshLayout) root.findViewById(refreshLayout);
        mRefreshLayout.setOnRefreshLoadmoreListener(this);
        mRefreshLayout.setRefreshHeader(new ClassicsHeader(getContext()));
        mRefreshLayout.setRefreshFooter(new ClassicsFooter(getContext()));

        mViewPager = (ViewPager) root.findViewById(R.id.viewPager);
        mTabLayout = (TabLayout) root.findViewById(R.id.tableLayout);

        mViewPager.setAdapter(mAdapter = new SmartPagerAdapter(Item.values()));
        mTabLayout.setupWithViewPager(mViewPager, true);
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        mAdapter.fragments[mViewPager.getCurrentItem()].onRefresh(refreshlayout);
    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        mAdapter.fragments[mViewPager.getCurrentItem()].onLoadmore(refreshlayout);
    }


    private class SmartPagerAdapter extends FragmentStatePagerAdapter {

        private final Item[] items;
        private final SmartFragment[] fragments;

        SmartPagerAdapter(Item... items) {
            super(getChildFragmentManager());
            this.items = items;
            this.fragments = new SmartFragment[items.length];
        }

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return items[position].name;
        }

        @Override
        public Fragment getItem(int position) {
            if (fragments[position] == null) {
                fragments[position] = new SmartFragment();
            }
            return fragments[position];
        }
    }

    public static class SmartFragment extends Fragment {

        private RecyclerView mRecyclerView;
        private BaseRecyclerAdapter<Void> mAdapter;

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return new RecyclerView(inflater.getContext());
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            mRecyclerView = (RecyclerView) view;

            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
            mRecyclerView.setAdapter(mAdapter = new BaseRecyclerAdapter<Void>(initData(), simple_list_item_2) {
                @Override
                protected void onBindViewHolder(SmartViewHolder holder, Void model, int position) {
                    holder.text(android.R.id.text1, String.format(Locale.CHINA, "第%02d条数据", position));
                    holder.text(android.R.id.text2, String.format(Locale.CHINA, "这是测试的第%02d条数据", position));
                    holder.textColorId(android.R.id.text2, R.color.colorTextAssistant);
                }
            });
        }

        private Collection<Void> initData() {
            return Arrays.asList(null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
        }

        public void onRefresh(final RefreshLayout refreshlayout) {
            refreshlayout.getLayout().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAdapter.refresh(initData());
                    refreshlayout.finishRefresh();
                    refreshlayout.resetNoMoreData();
                }
            }, 2000);
        }

        public void onLoadmore(final RefreshLayout refreshlayout) {
            refreshlayout.getLayout().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAdapter.loadmore(initData());
                    if (mAdapter.getItemCount() > 60) {
                        Toast.makeText(getContext(), "数据全部加载完毕", Toast.LENGTH_SHORT).show();
                        refreshlayout.finishLoadmoreWithNoMoreData();//将不会再次触发加载更多事件
                    } else {
                        refreshlayout.finishLoadmore();
                    }
                }
            }, 2000);
        }
    }
}
