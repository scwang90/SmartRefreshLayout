package com.scwang.smartrefresh.layout.api;

import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnMultiPurposeListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

/**
 * 刷新布局
 * Created by SCWANG on 2017/5/26.
 */

public interface RefreshLayout {

    RefreshLayout setFooterHeight(float heightDp);

    RefreshLayout setFooterHeight(int heightPx);

    RefreshLayout setHeaderHeight(float heightDp);

    RefreshLayout setHeaderHeight(int heightPx);

    RefreshLayout setExtendFooterRate(float rate);

    RefreshLayout setExtendHeaderRate(float rate);

    RefreshLayout setReboundInterpolator(Interpolator interpolator);

    RefreshLayout setReboundDuration(int duration);

    RefreshLayout setEnableLoadmore(boolean enable);

    RefreshLayout setEnableRefresh(boolean enable);

    RefreshLayout setEnableHeaderTranslationContent(boolean enable);

    RefreshLayout setEnableFooterTranslationContent(boolean enable);

    RefreshLayout setDisableContentWhenRefresh(boolean disable);

    RefreshLayout setDisableContentWhenLoading(boolean disable);

    RefreshLayout setEnableAutoLoadmore(boolean enable);

    RefreshLayout setRefreshFooter(RefreshFooter bottom);

    RefreshLayout setRefreshHeader(RefreshHeader header);

    RefreshLayout setOnRefreshListener(OnRefreshListener listener);

    RefreshLayout setOnLoadmoreListener(OnLoadmoreListener listener);

    RefreshLayout setOnRefreshLoadmoreListener(OnRefreshLoadmoreListener listener);

    RefreshLayout setOnMultiPurposeListener(OnMultiPurposeListener listener);

    RefreshLayout setPrimaryColorsId(int... primaryColorId);

    RefreshLayout setPrimaryColors(int... colors);

    RefreshLayout setHeaderExtendRate(float rate);

    RefreshLayout setLoadmoreFinished(boolean finished);

    RefreshLayout finishRefresh();

    RefreshLayout finishLoadmore();

    RefreshLayout finishRefresh(int delayed);

    RefreshLayout finishLoadmore(int delayed);

    @Nullable
    RefreshFooter getRefreshFooter();

    @Nullable
    RefreshHeader getRefreshHeader();

    RefreshState getState();

    ViewGroup getLayout();

    boolean isRefreshing();

    boolean isLoading();

    boolean autoRefresh();

    boolean autoRefresh(int delayed);

    boolean autoRefresh(int delayed, float dragrate);

    boolean autoLoadmore();

    boolean autoLoadmore(int delayed);

    boolean autoLoadmore(int delayed, float dragrate);

    boolean isEnableRefresh();

    boolean isEnableLoadmore();
}
