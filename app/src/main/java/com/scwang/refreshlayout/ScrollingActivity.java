package com.scwang.refreshlayout;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.scwang.smartrefreshheader.FlyRefreshHeader;
import com.scwang.smartrefreshheader.flyrefresh.FlyView;
import com.scwang.smartrefreshheader.flyrefresh.MountanScenceView;
import com.scwang.smartrefreshlayout.SmartRefreshLayout;
import com.scwang.smartrefreshlayout.api.RefreshHeader;
import com.scwang.smartrefreshlayout.listener.OnRefreshListener;
import com.scwang.smartrefreshlayout.listener.SimpleMultiPurposeListener;

public class ScrollingActivity extends AppCompatActivity {

    private boolean isAppbarExpand = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            startActivity(new Intent(this, ItemActivity.class));
        });


        FlyView flyView = (FlyView) findViewById(R.id.flyview);
        FlyRefreshHeader refreshHeader = new FlyRefreshHeader(this);
        MountanScenceView scenceView = (MountanScenceView) findViewById(R.id.flyrefresh);
        SmartRefreshLayout refreshLayout = (SmartRefreshLayout) findViewById(R.id.refresh);
        refreshHeader.setUp(scenceView, flyView);
        refreshLayout.setRefreshHeader(refreshHeader);
        refreshLayout.setPrimaryColorsId(R.color.colorPrimary);

        AppBarLayout appBar = (AppBarLayout) findViewById(R.id.app_bar);
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
        appBar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
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
        });

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(SmartRefreshLayout refreshlayout) {
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
