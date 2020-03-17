package com.scwang.refreshlayout;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import com.scwang.refreshlayout.util.DynamicTimeFormat;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.squareup.leakcanary.LeakCanary;

/**
 *
 * Created by scwang on 2017/6/11.
 */
public class App extends Application {

    static {
        //启用矢量图兼容
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        //设置全局默认配置（优先级最低，会被其他设置覆盖）
        SmartRefreshLayout.setDefaultRefreshInitializer((context, layout) -> {
            //全局设置（优先级最低）
            layout.setEnableAutoLoadMore(true);
            layout.setEnableOverScrollDrag(false);
            layout.setEnableOverScrollBounce(true);
            layout.setEnableLoadMoreWhenContentNotFull(true);
            layout.setEnableScrollContentWhenRefreshed(true);
            layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);
            layout.getLayout().setTag("close egg");
        });
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
            //全局设置主题颜色（优先级第二低，可以覆盖 DefaultRefreshInitializer 的配置，与下面的ClassicsHeader绑定）
            return new ClassicsHeader(context).setTimeFormat(new DynamicTimeFormat("更新于 %s"));
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }

}
