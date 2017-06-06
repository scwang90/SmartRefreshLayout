package com.scwang.smartrefreshlayout.api;

import android.view.animation.Interpolator;

import com.scwang.smartrefreshlayout.listener.OnLoadmoreListener;
import com.scwang.smartrefreshlayout.listener.OnMultiPurposeListener;
import com.scwang.smartrefreshlayout.listener.OnRefreshListener;
import com.scwang.smartrefreshlayout.listener.OnRefreshLoadmoreListener;

/**
 * 刷新布局
 * Created by SCWANG on 2017/5/26.
 */

public interface RefreshLayout {

    RefreshLayout setFooterHeightDp(float height);

    RefreshLayout setFooterHeightPx(int height);

    RefreshLayout setHeaderHeightDp(float height);

    RefreshLayout setHeaderHeightPx(int height);

    RefreshLayout setExtendHeaderHeightDp(float height);

    RefreshLayout setExtendHeaderHeightPx(int height);

    RefreshLayout setExtendFooterHeightDp(float height);

    RefreshLayout setExtendFooterHeightPx(int height);

    RefreshLayout setReboundInterpolator(Interpolator interpolator);

    RefreshLayout setEnableLoadmore(boolean enable);

    RefreshLayout setEnableRefresh(boolean enable);

    RefreshLayout setEnableHeaderTranslationContent(boolean enable);

    RefreshLayout setEnableFooterTranslationContent(boolean enable);

    RefreshLayout setDisableContentWhenRefresh(boolean disable);

    RefreshLayout setDisableContentWhenLoading(boolean disable);

    RefreshLayout setRefreshBottom(RefreshFooter bottom);

    RefreshLayout setRefreshHeader(RefreshHeader header);

    RefreshLayout setOnRefreshListener(OnRefreshListener listener);

    RefreshLayout setOnLoadmoreListener(OnLoadmoreListener listener);

    RefreshLayout setOnRefreshLoadmoreListener(OnRefreshLoadmoreListener listener);

    RefreshLayout setOnMultiPurposeListener(OnMultiPurposeListener listener);

    RefreshLayout setPrimaryColorsId(int... primaryColorId);

    RefreshLayout setPrimaryColors(int... colors);

    RefreshLayout setExtendRate(float rate);

    RefreshLayout finisRefresh();

    RefreshLayout finisLoadmore();

    RefreshLayout finisRefresh(int delayed);

    RefreshLayout finisLoadmore(int delayed);

    boolean isRefreshing();

    boolean isLoading();

    boolean autoRefresh();

    boolean autoRefresh(int delayed);

}
