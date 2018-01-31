package com.scwang.smartrefresh.layout.footer;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;

import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.header.FalsifyHeader;

/**
 * 虚假的 Footer
 * 用于 正真的 Footer 在 RefreshLayout 外部时，
 * Created by SCWANG on 2017/6/14.
 */
@SuppressWarnings("unused")
public class FalsifyFooter extends FalsifyHeader implements RefreshFooter {

    //<editor-fold desc="FalsifyHeader">
    public FalsifyFooter(Context context) {
        super(context);
    }

    public FalsifyFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FalsifyFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public FalsifyFooter(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    //</editor-fold>

    //<editor-fold desc="RefreshFooter">

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int extendHeight) {
        super.onInitialized(kernel, height, extendHeight);
        kernel.getRefreshLayout().setEnableAutoLoadMore(false);
    }

    @Override
    public boolean setNoMoreData(boolean noMoreData) {
        return false;
    }

    //</editor-fold>

}
