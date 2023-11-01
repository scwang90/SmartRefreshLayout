package com.scwang.refreshlayout.fragment.example;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.scwang.refreshlayout.R;
import com.scwang.refreshlayout.adapter.BaseRecyclerAdapter;
import com.scwang.refreshlayout.adapter.SmartViewHolder;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.Arrays;
import java.util.Collection;

import static android.R.layout.simple_list_item_2;

/**
 * 使用示例-ViewPager页面
 * A simple {@link Fragment} subclass.
 */
public class ViewPagerExampleFragment extends Fragment implements OnRefreshListener, OnRefreshLoadMoreListener {

    private enum Item {
        NestedInner(R.string.item_example_pager_left, SmartFragment.class),
        NestedOuter(R.string.item_example_pager_right, SmartFragment.class),
        ;
        public final int nameId;
        public final Class<? extends Fragment> clazz;
        Item(@StringRes int nameId, Class<? extends Fragment> clazz) {
            this.nameId = nameId;
            this.clazz = clazz;
        }
    }

    private ViewPager2 mViewPager;
    private SmartPagerAdapter mAdapter;
    private TabLayoutMediator mTabLayoutMediator;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_example_viewpager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        final Toolbar toolbar = root.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> requireActivity().finish());

        RefreshLayout refreshLayout = root.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshLoadMoreListener(this);
        refreshLayout.setRefreshHeader(new ClassicsHeader(getContext()));
        refreshLayout.setRefreshFooter(new ClassicsFooter(getContext()));

        mViewPager = root.findViewById(R.id.viewPager);
        TabLayout mTabLayout = root.findViewById(R.id.tableLayout);

        mViewPager.setAdapter(mAdapter = new SmartPagerAdapter(this, Item.values()));

        this.mTabLayoutMediator = new TabLayoutMediator(mTabLayout, mViewPager, true, true, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(getString(Item.values()[position].nameId));
            }
        });
        this.mTabLayoutMediator.attach();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        mAdapter.fragments[mViewPager.getCurrentItem()].onRefresh(refreshLayout);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        mAdapter.fragments[mViewPager.getCurrentItem()].onLoadMore(refreshLayout);
    }


    private static class SmartPagerAdapter extends FragmentStateAdapter {

        private final Item[] items;
        private final SmartFragment[] fragments;

        SmartPagerAdapter(Fragment fragment, Item... items) {
            super(fragment);
            this.items = items;
            this.fragments = new SmartFragment[items.length];
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (fragments[position] == null) {
                fragments[position] = new SmartFragment();
            }
            return fragments[position];
        }

        @Override
        public int getItemCount() {
            return items.length;
        }
    }

    public static class SmartFragment extends Fragment {

        private BaseRecyclerAdapter<Void> mAdapter;

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return new RecyclerView(inflater.getContext());
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            RecyclerView recyclerView = (RecyclerView) view;

            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(),DividerItemDecoration.VERTICAL));
            recyclerView.setAdapter(mAdapter = new BaseRecyclerAdapter<Void>(initData(), simple_list_item_2) {
                @Override
                protected void onBindViewHolder(SmartViewHolder holder, Void model, int position) {
                    holder.text(android.R.id.text1, getString(R.string.item_example_number_title, position));
                    holder.text(android.R.id.text2, getString(R.string.item_example_number_abstract, position));
                    holder.textColorId(android.R.id.text2, R.color.colorTextAssistant);
                }
            });
        }

        private Collection<Void> initData() {
            return Arrays.asList(null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
        }

        public void onRefresh(final RefreshLayout refreshLayout) {
            refreshLayout.getLayout().postDelayed(() -> {
                mAdapter.refresh(initData());
                refreshLayout.finishRefresh();
                refreshLayout.resetNoMoreData();//setNoMoreData(false);
            }, 2000);
        }

        public void onLoadMore(final RefreshLayout refreshLayout) {
            refreshLayout.getLayout().postDelayed(() -> {
                mAdapter.loadMore(initData());
                if (mAdapter.getItemCount() > 60) {
                    Toast.makeText(getContext(), "数据全部加载完毕", Toast.LENGTH_SHORT).show();
                    refreshLayout.finishLoadMoreWithNoMoreData();//将不会再次触发加载更多事件
                } else {
                    refreshLayout.finishLoadMore();
                }
            }, 2000);
        }
    }
}
