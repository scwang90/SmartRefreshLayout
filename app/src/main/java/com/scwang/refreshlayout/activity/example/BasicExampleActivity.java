package com.scwang.refreshlayout.activity.example;

import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.scwang.refreshlayout.R;
import com.scwang.refreshlayout.adapter.BaseRecyclerAdapter;
import com.scwang.refreshlayout.adapter.SmartViewHolder;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

import ezy.ui.layout.LoadingLayout;

import static android.R.layout.simple_list_item_2;

/**
 * 基本的功能使用
 */
public class BasicExampleActivity extends AppCompatActivity {


    private Random random = new Random();
    private BaseRecyclerAdapter<Void> mAdapter;
    private LoadingLayout mLoadingLayout;
    private com.scwang.smartrefresh.layout.api.RefreshLayout mRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_basic);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(mAdapter = new BaseRecyclerAdapter<Void>(simple_list_item_2) {
            @Override
            protected void onBindViewHolder(SmartViewHolder holder, Void model, int position) {
                holder.text(android.R.id.text1, getString(R.string.item_example_number_title, position));
                holder.text(android.R.id.text2, getString(R.string.item_example_number_abstract, position));
                holder.textColorId(android.R.id.text2, R.color.colorTextAssistant);
            }
        });
        //todo SCROLL_STATE_IDLE
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            int SCROLL_STATE_IDLE = 0;
            int SCROLL_STATE_TOUCH_SCROLL = 1;
            int SCROLL_STATE_FLING = 2;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    System.out.println("SCROLL_STATE_IDLE");
                } else if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    System.out.println("SCROLL_STATE_TOUCH_SCROLL");
                } else if (scrollState == SCROLL_STATE_FLING) {
                    System.out.println("SCROLL_STATE_FLING");
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        mLoadingLayout = findViewById(R.id.loading);
        mRefreshLayout = findViewById(R.id.refreshLayout);
        mRefreshLayout.setEnableAutoLoadMore(true);//开启自动加载功能（非必须）
        mRefreshLayout.setOnRefreshListener(this::refresh);
        mRefreshLayout.setOnLoadMoreListener(this::loadMore);
        mLoadingLayout.setRetryListener(v -> {
            mLoadingLayout.showContent();
            mRefreshLayout.autoRefresh();
        });

        //触发自动刷新
        mLoadingLayout.showContent();
        mRefreshLayout.autoRefresh();
        //item 点击测试
        mAdapter.setOnItemClickListener((parent, view, position, id) -> {
            BottomSheetDialog dialog=new BottomSheetDialog(BasicExampleActivity.this);
            View dialogView = View.inflate(getBaseContext(), R.layout.activity_example_basic, null);
            com.scwang.smartrefresh.layout.api.RefreshLayout refreshLayout1 = dialogView.findViewById(R.id.refreshLayout);
            RecyclerView recyclerView = new RecyclerView(getBaseContext());
            recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
            recyclerView.setAdapter(mAdapter);
            refreshLayout1.setEnableRefresh(false);
            refreshLayout1.setEnableNestedScroll(false);
            refreshLayout1.setRefreshContent(recyclerView);
            dialog.setContentView(dialogView);
            dialog.show();
        });

        //点击测试
        RefreshFooter footer = mRefreshLayout.getRefreshFooter();
        if (footer instanceof com.scwang.smartrefresh.layout.footer.ClassicsFooter) {
            mRefreshLayout.getRefreshFooter().getView().findViewById(ClassicsFooter.ID_TEXT_TITLE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getBaseContext(), "点击测试", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadMore(com.scwang.smartrefresh.layout.api.RefreshLayout layout) {
        layout.getLayout().postDelayed(() -> {
            if (random.nextBoolean()) {
                //如果刷新成功
                mAdapter.loadMore(initData(10));
                if (mAdapter.getItemCount() <= 30) {
                    //还有多的数据
                    layout.finishLoadMore();
                } else {
                    //没有更多数据（上拉加载功能将显示没有更多数据）
                    Toast.makeText(getApplication(), "数据全部加载完毕", Toast.LENGTH_SHORT).show();
                    layout.finishLoadMoreWithNoMoreData();//将不会再次触发加载更多事件
                }
            } else {
                //刷新失败
                layout.finishLoadMore(false);
            }
        }, 2000);
    }

    private void refresh(RefreshLayout refresh) {
        refresh.getLayout().postDelayed(() -> {
            if (random.nextBoolean()) {
                //如果刷新成功
                mAdapter.refresh(initData(40));
                if (mAdapter.getItemCount() <= 30) {
                    //还有多的数据
                    refresh.finishRefresh();
                } else {
                    //没有更多数据（上拉加载功能将显示没有更多数据）
                    refresh.finishRefreshWithNoMoreData();
                }
            } else {
                //刷新失败
                refresh.finishRefresh(false);
                if (mAdapter.isEmpty()) {
                    mLoadingLayout.showError();
                    mLoadingLayout.setErrorText("随机触发刷新失败演示！");
                }
            }
        }, 2000);
    }

    private Collection<Void> initData(int max) {
        int min = Math.min(10, max);
        max = Math.max(0, max);
        if (max > min) {
            return Arrays.asList(new Void[min + random.nextInt(max - min)]);
        } else {
            return Arrays.asList(new Void[min]);
        }
    }
}
