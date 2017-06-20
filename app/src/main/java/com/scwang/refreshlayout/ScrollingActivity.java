package com.scwang.refreshlayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.scwang.refreshlayout.activity.style.FlyRefreshStyleActivity;
import com.scwang.smartrefresh.header.FlyRefreshHeader;
import com.scwang.smartrefresh.header.flyrefresh.FlyView;
import com.scwang.smartrefresh.header.flyrefresh.MountanScenceView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreater;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

public class ScrollingActivity extends AppCompatActivity {

    private boolean isAppbarExpand = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ScrollingActivity.this, FlyRefreshStyleActivity.class));
            }
        });

        SmartRefreshLayout.setDefaultRefreshFooterCreater(new DefaultRefreshFooterCreater() {
            @NonNull
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                return new ClassicsFooter(context);
            }
        });

        final FlyView flyView = (FlyView) findViewById(R.id.flyview);
        final FlyRefreshHeader refreshHeader = new FlyRefreshHeader(this);
        final MountanScenceView scenceView = (MountanScenceView) findViewById(R.id.flyrefresh);
        final SmartRefreshLayout refreshLayout = (SmartRefreshLayout) findViewById(R.id.refresh);
        refreshHeader.setUp(scenceView, flyView);
        refreshLayout.setRefreshHeader(refreshHeader);
        refreshLayout.setPrimaryColorsId(R.color.colorPrimary);

        final AppBarLayout appBar = (AppBarLayout) findViewById(R.id.app_bar);
        refreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onHeaderPulling(RefreshHeader header, float percent, int offset, int bottomHeight, int extendHeight) {
                appBar.setTranslationY(offset);
            }
            @Override
            public void onHeaderReleasing(RefreshHeader header, float percent, int offset, int bottomHeight, int extendHeight) {
                appBar.setTranslationY(offset);
            }
        });
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int scrollRange = appBarLayout.getTotalScrollRange();
                float fraction = 1f * (scrollRange + verticalOffset) / scrollRange;
                if (fraction < 0.1 && isAppbarExpand) {
                    isAppbarExpand = false;
                    fab.animate().scaleX(0).scaleY(0);
                    flyView.animate().scaleX(0).scaleY(0);
                }
                if (fraction > 0.8 && !isAppbarExpand) {
                    isAppbarExpand = true;
                    fab.animate().scaleX(1).scaleY(1);
                    flyView.animate().scaleX(1).scaleY(1);
                }
            }
        });

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshHeader.finishRefresh(null);
                    }
                },2000);
            }
        });
//
//        View view = findViewById(R.id.refresh);
//        if (view instanceof SwipeRefreshLayout) {
//            SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) view;
//            refreshLayout.setOnRefreshListener(() -> refreshLayout.postDelayed(() -> refreshLayout.setRefreshing(false), 3000));
//        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
