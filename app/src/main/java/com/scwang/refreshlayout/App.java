package com.scwang.refreshlayout;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.scwang.smartrefreshlayout.SmartRefreshLayout;
import com.scwang.smartrefreshlayout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefreshlayout.api.RefreshHeader;
import com.scwang.smartrefreshlayout.api.RefreshLayout;
import com.scwang.smartrefreshlayout.header.BezierHeader;

/**
 *
 * Created by SCWANG on 2017/6/11.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SmartRefreshLayout.setDefaultRefreshHeaderCreater(new DefaultRefreshHeaderCreater() {
            @NonNull
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                BezierHeader header = new BezierHeader(context);
                layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);
                return header;
            }
        });
    }
}
