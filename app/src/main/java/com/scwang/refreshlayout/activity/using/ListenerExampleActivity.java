package com.scwang.refreshlayout.activity.using;

import android.os.Bundle;
import android.support.annotation.NonNull;
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
public class ListenerExampleActivity extends AppCompatActivity {

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
    private String mLoadMore;
    private String mStateChanged;

    private static boolean isFirstEnter = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_listener);

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
            public void onHeaderPulling(@NonNull RefreshHeader header, float percent, int offset, int headerHeight, int extendHeight) {
                mHeaderPulling = String.format(Locale.CHINA, "%s\npercent=%.02f offset=%03d\nheight=%03d extend=%03d",
                        FORMAT.format(new Date()),
                        percent,offset,headerHeight,extendHeight);
                updateContent();
            }

            @Override
            public void onHeaderReleasing(@NonNull RefreshHeader header, float percent, int offset, int headerHeight, int extendHeight) {
                mHeaderReleasing = String.format(Locale.CHINA, "%s\npercent=%.02f offset=%03d\nheight=%03d extend=%03d",
                        FORMAT.format(new Date()),
                        percent,offset,headerHeight,extendHeight);
                updateContent();
            }

            @Override
            public void onHeaderStartAnimator(@NonNull RefreshHeader header, int headerHeight, int extendHeight) {
                mHeaderStartAnimator = String.format(Locale.CHINA, "%s\nheight=%03d extend=%03d",
                        FORMAT.format(new Date()),
                        headerHeight,extendHeight);
                updateContent();
            }

            @Override
            public void onHeaderFinish(@NonNull RefreshHeader header, boolean success) {
                mHeaderFinish = String.format(Locale.CHINA, "%s - " + success,FORMAT.format(new Date()));
                updateContent();
            }

            @Override
            public void onFooterPulling(@NonNull RefreshFooter footer, float percent, int offset, int footerHeight, int extendHeight) {
                mFooterPulling = String.format(Locale.CHINA, "%s\npercent=%.02f\noffset=%03d height=%03d\nextend=%03d",
                        FORMAT.format(new Date()),
                        percent,offset,footerHeight,extendHeight);
                updateContent();
            }

            @Override
            public void onFooterReleasing(@NonNull RefreshFooter footer, float percent, int offset, int footerHeight, int extendHeight) {
                mFooterReleasing = String.format(Locale.CHINA, "%s\npercent=%.02f\noffset=%03d height=%03d\nextend=%03d",
                        FORMAT.format(new Date()),
                        percent,offset,footerHeight,extendHeight);
                updateContent();
            }

            @Override
            public void onFooterStartAnimator(@NonNull RefreshFooter footer, int footerHeight, int extendHeight) {
                mFooterStartAnimator = String.format(Locale.CHINA, "%s\nheight=%03d extend=%03d",
                        FORMAT.format(new Date()),
                        footerHeight,extendHeight);
                updateContent();
            }

            @Override
            public void onFooterFinish(@NonNull RefreshFooter footer, boolean success) {
                mFooterFinish = String.format(Locale.CHINA, "%s - " + success,FORMAT.format(new Date()));
                updateContent();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mRefresh = String.format(Locale.CHINA, "%s",FORMAT.format(new Date()));
                updateContent();
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mLoadMore = String.format(Locale.CHINA, "%s",FORMAT.format(new Date()));
                updateContent();
            }

            @Override
            public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
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
                        "onLoadMore:%s\n\n",
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
                mLoadMore));
    }

}
