package com.scwang.refreshlayout;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.BezierRadarHeader;

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
                BezierRadarHeader header = new BezierRadarHeader(context);
                layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);
                return header;
            }
        });
    }
}
