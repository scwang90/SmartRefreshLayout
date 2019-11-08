package com.scwang.refreshlayout.fragment.example;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.scwang.refreshlayout.R;
import com.scwang.refreshlayout.adapter.BaseRecyclerAdapter;
import com.scwang.refreshlayout.adapter.SmartViewHolder;
import com.scwang.refreshlayout.fragment.example.NestedScrollExampleFragment.Item;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.youth.banner.Banner;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static android.R.layout.simple_list_item_2;
import static com.scwang.refreshlayout.R.mipmap.image_weibo_home_1;
import static com.scwang.refreshlayout.R.mipmap.image_weibo_home_2;

/**
 * 使用示例-嵌套滚动-整体
 * A simple {@link Fragment} subclass.
 */
public class NestedScrollExampleFragmentIntegral extends Fragment implements AdapterView.OnItemClickListener, OnRefreshLoadMoreListener {

    private ViewPager mViewPager;
    private SmartPagerAdapter mAdapter;

//    private BaseRecyclerAdapter<Item> mAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_example_nestedscroll_integral, container, false);
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

        Banner banner = root.findViewById(R.id.banner);
        banner.setImageLoader(new BannerImageLoader());
        banner.setImages(Arrays.asList(image_weibo_home_1, image_weibo_home_2));
        banner.start();

        mViewPager = root.findViewById(R.id.viewPager);
        mViewPager.setAdapter(mAdapter = new SmartPagerAdapter(getChildFragmentManager()));
//        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), VERTICAL));
//        recyclerView.setAdapter(mAdapter = new BaseRecyclerAdapter<Item>(buildItems(), simple_list_item_2, NestedScrollExampleFragmentIntegral.this) {
//            @Override
//            protected void onBindViewHolder(SmartViewHolder holder, Item model, int position) {
//                holder.text(android.R.id.text1, model.name());
//                holder.text(android.R.id.text2, model.name);
//                holder.textColorId(android.R.id.text2, R.color.colorTextAssistant);
//            }
//        });

        RefreshLayout refreshLayout = root.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshLoadMoreListener(this);
//        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
//            @Override
//            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
//                refreshLayout.getLayout().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        mAdapter.loadMore(buildItems());
//                        refreshLayout.finishLoadMore();
//                    }
//                }, 2000);
//            }
//        });

        TextView textView = root.findViewById(R.id.target);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "点击测试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        mAdapter.fragments[mViewPager.getCurrentItem()].onRefresh(refreshLayout);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        mAdapter.fragments[mViewPager.getCurrentItem()].onLoadMore(refreshLayout);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    private class BannerImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource((Integer)path);
        }
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
        private BaseRecyclerAdapter<Item> mAdapter;

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return mRecyclerView = new RecyclerView(inflater.getContext());
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
            mRecyclerView.setAdapter(mAdapter = new BaseRecyclerAdapter<Item>(buildItems(), simple_list_item_2) {
                @Override
                protected void onBindViewHolder(SmartViewHolder holder, Item model, int position) {
                    holder.text(android.R.id.text1, model.name());
                    holder.text(android.R.id.text2, model.name);
                    holder.textColorId(android.R.id.text2, R.color.colorTextAssistant);
                }
            });
        }

        private Collection<Item> buildItems() {
            List<Item> items = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                items.addAll(Arrays.asList(Item.values()));
            }
            return items;
        }


        public void onRefresh(final RefreshLayout refreshLayout) {
            refreshLayout.getLayout().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAdapter.refresh(buildItems());
                    refreshLayout.finishRefresh();
                    refreshLayout.resetNoMoreData();//setNoMoreData(false);
                }
            }, 2000);
        }

        public void onLoadMore(final RefreshLayout refreshLayout) {
            refreshLayout.getLayout().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAdapter.loadMore(buildItems());
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
