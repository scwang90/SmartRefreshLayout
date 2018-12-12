package com.scwang.refreshlayout.activity.example;

import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Toast;

import com.scwang.refreshlayout.R;
import com.scwang.refreshlayout.adapter.BaseRecyclerAdapter;
import com.scwang.refreshlayout.adapter.SmartViewHolder;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.Arrays;
import java.util.Collection;

import static android.R.layout.simple_list_item_2;

/**
 * 基本的功能使用
 */
public class BasicExampleActivity extends AppCompatActivity {

    private BaseRecyclerAdapter<Void> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_basic);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        AbsListView listView = findViewById(R.id.listView);
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

        final RefreshLayout refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setEnableAutoLoadMore(true);//开启自动加载功能（非必须）
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                refreshLayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.refresh(initData());
                        refreshLayout.finishRefresh();
                        refreshLayout.resetNoMoreData();//setNoMoreData(false);
                    }
                }, 2000);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                refreshLayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mAdapter.getItemCount() > 30) {
                            Toast.makeText(getApplication(), "数据全部加载完毕", Toast.LENGTH_SHORT).show();
                            refreshLayout.finishLoadMoreWithNoMoreData();//将不会再次触发加载更多事件
                        } else {
                            mAdapter.loadMore(initData());
                            refreshLayout.finishLoadMore();
                        }
                    }
                }, 2000);
            }
        });

        //触发自动刷新
        refreshLayout.autoRefresh();
        //item 点击测试
        mAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BottomSheetDialog dialog=new BottomSheetDialog(BasicExampleActivity.this);
                View dialogView = View.inflate(getBaseContext(), R.layout.activity_example_basic, null);
                RefreshLayout refreshLayout = dialogView.findViewById(R.id.refreshLayout);
                RecyclerView recyclerView = new RecyclerView(getBaseContext());
                recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                recyclerView.setAdapter(mAdapter);
                refreshLayout.setEnableRefresh(false);
                refreshLayout.setEnableNestedScroll(false);
                refreshLayout.setRefreshContent(recyclerView);
                dialog.setContentView(dialogView);
                dialog.show();
            }
        });

        //点击测试
        RefreshFooter footer = refreshLayout.getRefreshFooter();
        if (footer != null) {
            refreshLayout.getRefreshFooter().getView().findViewById(ClassicsFooter.ID_TEXT_TITLE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getBaseContext(), "点击测试", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private Collection<Void> initData() {
        return Arrays.asList(null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
    }
}
