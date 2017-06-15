package com.scwang.smartrefresh.layout.api;

import android.support.annotation.Nullable;
import android.view.animation.Interpolator;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
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

    SmartRefreshLayout setFooterExtendRate(float rate);

    RefreshLayout setReboundInterpolator(Interpolator interpolator);

    RefreshLayout setReboundDuration(int duration);

    RefreshLayout setEnableLoadmore(boolean enable);

    RefreshLayout setEnableRefresh(boolean enable);

    RefreshLayout setEnableHeaderTranslationContent(boolean enable);

    RefreshLayout setEnableFooterTranslationContent(boolean enable);

    RefreshLayout setDisableContentWhenRefresh(boolean disable);

    RefreshLayout setDisableContentWhenLoading(boolean disable);

    RefreshLayout setRefreshFooter(RefreshFooter bottom);

    RefreshLayout setRefreshHeader(RefreshHeader header);

    RefreshLayout setOnRefreshListener(OnRefreshListener listener);

    RefreshLayout setOnLoadmoreListener(OnLoadmoreListener listener);

    RefreshLayout setOnRefreshLoadmoreListener(OnRefreshLoadmoreListener listener);

    RefreshLayout setOnMultiPurposeListener(OnMultiPurposeListener listener);

    RefreshLayout setPrimaryColorsId(int... primaryColorId);

    RefreshLayout setPrimaryColors(int... colors);

    RefreshLayout setHeaderExtendRate(float rate);

    RefreshLayout finisRefresh();

    RefreshLayout finisLoadmore();

    RefreshLayout finisRefresh(int delayed);

    RefreshLayout finisLoadmore(int delayed);

    RefreshLayout registHook(RefreshLayoutHook hook);

    @Nullable
    RefreshFooter getRefreshFooter();

    @Nullable
    RefreshHeader getRefreshHeader();

    boolean isRefreshing();

    boolean isLoading();

    boolean autoRefresh();

    boolean autoRefresh(int delayed);

    boolean autoLoadmore();

    boolean autoLoadmore(int delayed);
}
