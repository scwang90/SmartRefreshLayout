package com.scwang.refreshlayout.activity.example;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

    private static final DateFormat FORMAT = new SimpleDateFormat("HH:mm sss", Locale.CHINA);

    private TextView mTvContent;
    private String mHeaderMoving;
//    private String mHeaderPulling;
//    private String mHeaderReleasing;
    private String mFooterMoving;
//    private String mFooterPulling;
//    private String mFooterReleasing;
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
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mTvContent = findViewById(R.id.content);
        mTvContent.setMovementMethod(ScrollingMovementMethod.getInstance());

        final RefreshLayout refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {

            @Override
            public void onHeaderMoving(RefreshHeader header, boolean isDragging, float percent, int offset, int headerHeight, int maxDragHeight) {
                mHeaderMoving = String.format(Locale.CHINA, "%s\nisDragging=%b,percent=%.02f offset=%03d\nheight=%03d extend=%03d",
                        FORMAT.format(new Date()),isDragging,
                        percent,offset,headerHeight,maxDragHeight);
                updateContent();
            }

//            @Override
//            public void onHeaderPulling(@NonNull RefreshHeader header, float percent, int offset, int headerHeight, int maxDragHeight) {
//                mHeaderPulling = String.format(Locale.CHINA, "%s\npercent=%.02f offset=%03d\nheight=%03d extend=%03d",
//                        FORMAT.format(new Date()),
//                        percent,offset,headerHeight,maxDragHeight);
//                updateContent();
//            }
//
//            @Override
//            public void onHeaderReleasing(@NonNull RefreshHeader header, float percent, int offset, int headerHeight, int maxDragHeight) {
//                mHeaderReleasing = String.format(Locale.CHINA, "%s\npercent=%.02f offset=%03d\nheight=%03d extend=%03d",
//                        FORMAT.format(new Date()),
//                        percent,offset,headerHeight,maxDragHeight);
//                updateContent();
//            }

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

//            @Override
//            public void onFooterPulling(@NonNull RefreshFooter footer, float percent, int offset, int footerHeight, int maxDragHeight) {
//                mFooterPulling = String.format(Locale.CHINA, "%s\npercent=%.02f\noffset=%03d height=%03d\nextend=%03d",
//                        FORMAT.format(new Date()),
//                        percent,offset,footerHeight,maxDragHeight);
//                updateContent();
//            }
//
//            @Override
//            public void onFooterReleasing(@NonNull RefreshFooter footer, float percent, int offset, int footerHeight, int maxDragHeight) {
//                mFooterReleasing = String.format(Locale.CHINA, "%s\npercent=%.02f\noffset=%03d height=%03d\nextend=%03d",
//                        FORMAT.format(new Date()),
//                        percent,offset,footerHeight,maxDragHeight);
//                updateContent();
//            }

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
//                        "onHeaderPulling:%s\n\n" +
//                        "onHeaderReleasing:%s\n\n" +
                        "onHeaderStartAnimator:%s\n\n" +
                        "onHeaderFinish:%s\n\n" +
                        "onFooterMoving:%s\n\n" +
//                        "onFooterPulling:%s\n\n" +
//                        "onFooterReleasing:%s\n\n" +
                        "onFooterStartAnimator:%s\n\n" +
                        "onFooterFinish:%s\n\n" +
                        "onRefresh:%s\n\n" +
                        "onLoadMore:%s\n\n",
                mStateChanged,
                mHeaderMoving,
//                mHeaderPulling,
//                mHeaderReleasing,
                mHeaderStartAnimator,
                mHeaderFinish,
                mFooterMoving,
//                mFooterPulling,
//                mFooterReleasing,
                mFooterStartAnimator,
                mFooterFinish,
                mRefresh,
                mLoadMore));
    }

}
