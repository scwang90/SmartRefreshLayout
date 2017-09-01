package com.scwang.refreshlayout.activity.using;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.scwang.refreshlayout.R;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 多功能监听器的使用
 */
public class ListenerUsingActivity extends AppCompatActivity {

    private static DateFormat FORMAT = new SimpleDateFormat("HH:mm sss", Locale.CHINA);

    private TextView mTvContent;
    private String mHeaderPulling;
    private String mHeaderReleasing;
    private String mFooterPulling;
    private String mFooterReleasing;
    private String mFooterStartAnimator;
    private String mHeaderStartAnimator;
    private String mFooterFinish;
    private String mHeaderFinish;
    private String mRefresh;
    private String mLoadmore;
    private String mStateChanged;

    private static boolean isFirstEnter = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_using_listener);

        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mTvContent = (TextView) findViewById(R.id.content);
        mTvContent.setMovementMethod(ScrollingMovementMethod.getInstance());

        final RefreshLayout refreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        refreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {

            @Override
            public void onHeaderPulling(RefreshHeader header, float percent, int offset, int headerHeight, int extendHeight) {
                mHeaderPulling = String.format(Locale.CHINA, "%s\npercent=%.02f offset=%03d\nheight=%03d extend=%03d",
                        FORMAT.format(new Date()),
                        percent,offset,headerHeight,extendHeight);
                updateContent();
            }

            @Override
            public void onHeaderReleasing(RefreshHeader header, float percent, int offset, int headerHeight, int extendHeight) {
                mHeaderReleasing = String.format(Locale.CHINA, "%s\npercent=%.02f offset=%03d\nheight=%03d extend=%03d",
                        FORMAT.format(new Date()),
                        percent,offset,headerHeight,extendHeight);
                updateContent();
            }

            @Override
            public void onHeaderStartAnimator(RefreshHeader header, int headerHeight, int extendHeight) {
                mHeaderStartAnimator = String.format(Locale.CHINA, "%s\nheight=%03d extend=%03d",
                        FORMAT.format(new Date()),
                        headerHeight,extendHeight);
                updateContent();
            }

            @Override
            public void onHeaderFinish(RefreshHeader header, boolean success) {
                mHeaderFinish = String.format(Locale.CHINA, "%s - " + success,FORMAT.format(new Date()));
                updateContent();
            }

            @Override
            public void onFooterPulling(RefreshFooter footer, float percent, int offset, int footerHeight, int extendHeight) {
                mFooterPulling = String.format(Locale.CHINA, "%s\npercent=%.02f\noffset=%03d height=%03d\nextend=%03d",
                        FORMAT.format(new Date()),
                        percent,offset,footerHeight,extendHeight);
                updateContent();
            }

            @Override
            public void onFooterReleasing(RefreshFooter footer, float percent, int offset, int footerHeight, int extendHeight) {
                mFooterReleasing = String.format(Locale.CHINA, "%s\npercent=%.02f\noffset=%03d height=%03d\nextend=%03d",
                        FORMAT.format(new Date()),
                        percent,offset,footerHeight,extendHeight);
                updateContent();
            }

            @Override
            public void onFooterStartAnimator(RefreshFooter footer, int footerHeight, int extendHeight) {
                mFooterStartAnimator = String.format(Locale.CHINA, "%s\nheight=%03d extend=%03d",
                        FORMAT.format(new Date()),
                        footerHeight,extendHeight);
                updateContent();
            }

            @Override
            public void onFooterFinish(RefreshFooter footer, boolean success) {
                mFooterFinish = String.format(Locale.CHINA, "%s - " + success,FORMAT.format(new Date()));
                updateContent();
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mRefresh = String.format(Locale.CHINA, "%s",FORMAT.format(new Date()));
                updateContent();
            }

            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                mLoadmore = String.format(Locale.CHINA, "%s",FORMAT.format(new Date()));
                updateContent();
            }

            @Override
            public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
                mStateChanged = String.format(Locale.CHINA, "%s\nnew=%s\nold=%s",
                        FORMAT.format(new Date()),
                        newState.name(),
                        oldState.name());
                updateContent();
            }
        });

        if (isFirstEnter) {
            isFirstEnter = false;
            //触发自动刷新
            refreshLayout.autoRefresh();
        } else {
            updateContent();
        }
    }

    private void updateContent() {
        mTvContent.setText(String.format(Locale.CHINA,
                "onStateChanged:%s\n\n" +
                        "onHeaderPulling:%s\n\n" +
                        "onHeaderReleasing:%s\n\n" +
                        "onHeaderStartAnimator:%s\n\n" +
                        "onHeaderFinish:%s\n\n" +
                        "onFooterPulling:%s\n\n" +
                        "onFooterReleasing:%s\n\n" +
                        "onFooterStartAnimator:%s\n\n" +
                        "onFooterFinish:%s\n\n" +
                        "onRefresh:%s\n\n" +
                        "onLoadmore:%s\n\n",
                mStateChanged,
                mHeaderPulling,
                mHeaderReleasing,
                mHeaderStartAnimator,
                mHeaderFinish,
                mFooterPulling,
                mFooterReleasing,
                mFooterStartAnimator,
                mFooterFinish,
                mRefresh,
                mLoadmore));
    }

}
