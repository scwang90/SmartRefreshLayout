package com.scwang.refreshlayout.activity.example;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.scwang.refreshlayout.R;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

/**
 * 在XML中指定Header和Footer
 */
public class AssignXmlExampleActivity extends AppCompatActivity {

    private static boolean isFirstEnter = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_assign_xml);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /*
         * 以下代码仅仅为了演示效果而已，不是必须的
         * 关键代码在 activity_example_assign_xml 中
         */
        final RefreshLayout refreshLayout = findViewById(R.id.refreshLayout);
        if (isFirstEnter) {
            isFirstEnter = false;
//            //触发上拉加载
//            mRefreshLayout.autoLoadMore(250, 250, 1.5f);
            //通过多功能监听接口实现 在第一次加载完成之后 自动刷新
            refreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener(){
                @Override
                public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
                    if (oldState == RefreshState.LoadFinish && newState == RefreshState.None) {
                        refreshLayout.autoRefresh();
                        refreshLayout.setOnMultiPurposeListener(null);
                    }
                }
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    refreshLayout.finishLoadMore(2000);
                }
                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    refreshLayout.finishRefresh(3000);
                }
            });
        }
    }

}
