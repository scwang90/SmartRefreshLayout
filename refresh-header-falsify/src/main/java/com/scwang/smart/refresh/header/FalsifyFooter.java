package com.scwang.smart.refresh.header;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.AttributeSet;

import com.scwang.smart.refresh.header.falsify.FalsifyAbstract;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshKernel;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

/**
 * 虚假的 Footer
 * 用于 正真的 Footer 在 RefreshLayout 外部时，
 * Created by scwang on 2017/6/14.
 */
@SuppressWarnings("unused")
public class FalsifyFooter extends FalsifyAbstract implements RefreshFooter {

    //<editor-fold desc="FalsifyHeader">
    public FalsifyFooter(Context context) {
        this(context, null);
    }

    public FalsifyFooter(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }
    //</editor-fold>

    //<editor-fold desc="RefreshFooter">
    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {
        mRefreshKernel = kernel;
        kernel.getRefreshLayout().setEnableAutoLoadMore(false);
    }

    @Override
    public void onReleased(@NonNull RefreshLayout layout, int height, int maxDragHeight) {
        if (mRefreshKernel != null) {
            /*
             * 2020-3-15 BUG修复
             * https://github.com/scwang90/SmartRefreshLayout/issues/1018
             * 强化了 closeHeaderOrFooter 的关闭逻辑，帮助 Footer 取消刷新
             * FalsifyFooter 是不能触发加载的
             */
            layout.closeHeaderOrFooter();
//            mRefreshKernel.setState(RefreshState.None);
//            //onReleased 的时候 调用 setState(RefreshState.None); 并不会立刻改变成 None
//            //而是先执行一个回弹动画，LoadFinish 是介于 Refreshing 和 None 之间的状态
//            //LoadFinish 用于在回弹动画结束时候能顺利改变为 None
//            mRefreshKernel.setState(RefreshState.LoadFinish);
        }
    }
    //</editor-fold>

}
