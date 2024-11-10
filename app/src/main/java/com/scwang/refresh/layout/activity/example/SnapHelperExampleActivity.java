package com.scwang.refresh.layout.activity.example;

import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.scwang.refresh.layout.R;
import com.scwang.refresh.layout.adapter.BaseRecyclerAdapter;
import com.scwang.refresh.layout.adapter.SmartViewHolder;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.Arrays;
import java.util.Collection;

/**
 * 结合 SnapHelper 使用
 * Created by scwang on 2017/8/4.
 */
public class SnapHelperExampleActivity extends AppCompatActivity {

    private BaseRecyclerAdapter<Integer> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_snaphelper);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        //初始化列表和监听
        View view = findViewById(R.id.recyclerView);
        if (view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter = new BaseRecyclerAdapter<Integer>(loadModels(), R.layout.item_example_snap_helper) {
                @Override
                protected void onBindViewHolder(SmartViewHolder holder, Integer model, int position) {
                    holder.image(R.id.imageView, model);
                }
            });
            SnapHelper snapHelper = new PagerSnapHelper();
            snapHelper.attachToRecyclerView(recyclerView);
        }

        RefreshLayout refreshLayout = findViewById(R.id.refreshLayout);
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

        Switch switcher = findViewById(R.id.switch_scroll_content);
        if (switcher != null) {
            refreshLayout.setEnableScrollContentWhenLoaded(switcher.isChecked());
            switcher.setOnCheckedChangeListener((buttonView, isChecked) -> refreshLayout.setEnableScrollContentWhenLoaded(isChecked));
        }
    }

    private Collection<Integer> loadModels() {
        return Arrays.asList( R.mipmap.image_weibo_home_1, R.mipmap.image_weibo_home_2);
    }

}
