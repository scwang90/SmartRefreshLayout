package com.scwang.refreshlayout.fragment.example;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scwang.refreshlayout.R;
import com.scwang.refreshlayout.adapter.BaseRecyclerAdapter;
import com.scwang.refreshlayout.adapter.SmartViewHolder;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.Arrays;
import java.util.Collection;

/**
 * A simple {@link Fragment} subclass.
 */
public class StaggeredGridExampleFragment extends Fragment {

    private BaseRecyclerAdapter<Integer> mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_example_staggered_grid, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);//初始化列表和监听
        View recycler = view.findViewById(R.id.recyclerView);
        if (recycler instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) recycler;
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter = new BaseRecyclerAdapter<Integer>(loadModels(), R.layout.item_example_snap_helper) {
                @Override
                protected void onBindViewHolder(SmartViewHolder holder, Integer model, int position) {
                    holder.image(R.id.imageView, model);
                }
            });
        }

        RefreshLayout refreshLayout = view.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.getLayout().postDelayed(() -> {
                    mAdapter.refresh(loadModels());
                    refreshLayout.finishRefresh();
                }, 2000);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.getLayout().postDelayed(() -> {
                    mAdapter.loadMore(loadModels());
                    refreshLayout.finishLoadMore();
                }, 2000);
            }
        });

    }

    private Collection<Integer> loadModels() {
        return Arrays.asList(R.mipmap.image_weibo_home_1, R.mipmap.image_weibo_home_2, R.mipmap.image_weibo_home_1, R.mipmap.image_weibo_home_2, R.mipmap.image_weibo_home_1, R.mipmap.image_weibo_home_2, R.mipmap.image_weibo_home_1, R.mipmap.image_weibo_home_2, R.mipmap.image_weibo_home_1, R.mipmap.image_weibo_home_2, R.mipmap.image_weibo_home_1, R.mipmap.image_weibo_home_2, R.mipmap.image_weibo_home_1, R.mipmap.image_weibo_home_2, R.mipmap.image_weibo_home_1, R.mipmap.image_weibo_home_2, R.mipmap.image_weibo_home_1, R.mipmap.image_weibo_home_2, R.mipmap.image_weibo_home_1, R.mipmap.image_weibo_home_2);
    }


}
