package com.scwang.refresh.layout.activity.example;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.scwang.refresh.layout.R;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.simple.SimpleMultiListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 多功能监听器的使用
 */
public class ListenerExampleActivity extends AppCompatActivity {

    private static final DateFormat FORMAT = new SimpleDateFormat("HH:mm sss", Locale.CHINA);

    private TextView mTvContent;
    private String mHeaderMoving;
    private String mFooterMoving;
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

        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        mTvContent = findViewById(R.id.content);
        mTvContent.setMovementMethod(ScrollingMovementMethod.getInstance());

        final RefreshLayout refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setOnMultiListener(new SimpleMultiListener() {

            @Override
            public void onHeaderMoving(RefreshHeader header, boolean isDragging, float percent, int offset, int headerHeight, int maxDragHeight) {
                mHeaderMoving = String.format(Locale.CHINA, "%s\nisDragging=%b,percent=%.02f offset=%03d\nheight=%03d extend=%03d",
                        FORMAT.format(new Date()),isDragging,
                        percent,offset,headerHeight,maxDragHeight);
                updateContent();
            }

            @Override
            public void onHeaderStartAnimator(@NonNull RefreshHeader header, int headerHeight, int maxDragHeight) {
                mHeaderStartAnimator = String.format(Locale.CHINA, "%s\nheight=%03d extend=%03d",
                        FORMAT.format(new Date()),
                        headerHeight,maxDragHeight);
                updateContent();
            }

            @Override
            public void onHeaderFinish(@NonNull RefreshHeader header, boolean success) {
                mHeaderFinish = String.format(Locale.CHINA, "%s - " + success,FORMAT.format(new Date()));
                updateContent();
            }

            @Override
            public void onFooterMoving(RefreshFooter footer, boolean isDragging, float percent, int offset, int footerHeight, int maxDragHeight) {
                mFooterMoving = String.format(Locale.CHINA, "%s\nisDragging=%b,percent=%.02f offset=%03d\nheight=%03d extend=%03d",
                        FORMAT.format(new Date()),isDragging,
                        percent,offset,footerHeight,maxDragHeight);
                updateContent();
            }

            @Override
            public void onFooterStartAnimator(@NonNull RefreshFooter footer, int footerHeight, int maxDragHeight) {
                mFooterStartAnimator = String.format(Locale.CHINA, "%s\nheight=%03d extend=%03d",
                        FORMAT.format(new Date()),
                        footerHeight,maxDragHeight);
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
                refreshLayout.finishRefresh(2000);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mLoadMore = String.format(Locale.CHINA, "%s",FORMAT.format(new Date()));
                updateContent();
                refreshLayout.finishLoadMore(1000);
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
                        "onHeaderMoving:%s\n\n" +
                        "onHeaderStartAnimator:%s\n\n" +
                        "onHeaderFinish:%s\n\n" +
                        "onFooterMoving:%s\n\n" +
                        "onFooterStartAnimator:%s\n\n" +
                        "onFooterFinish:%s\n\n" +
                        "onRefresh:%s\n\n" +
                        "onLoadMore:%s\n\n",
                mStateChanged,
                mHeaderMoving,
                mHeaderStartAnimator,
                mHeaderFinish,
                mFooterMoving,
                mFooterStartAnimator,
                mFooterFinish,
                mRefresh,
                mLoadMore));
    }

}
