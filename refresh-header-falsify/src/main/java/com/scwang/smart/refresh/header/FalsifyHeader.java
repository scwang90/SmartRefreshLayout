package com.scwang.smart.refresh.header;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.AttributeSet;

import com.scwang.smart.refresh.header.falsify.FalsifyAbstract;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

/**
 * 虚假的 Header
 * 用于 正真的 Header 在 RefreshLayout 外部时，
 * 使用本虚假的 FalsifyHeader 填充在 RefreshLayout 内部
 * 具体使用方法 参考 纸飞机（FlyRefreshHeader）
 * Created by scwang on 2017/6/14.
 */
public class FalsifyHeader extends FalsifyAbstract implements RefreshHeader {

    //<editor-fold desc="FalsifyHeader">
    public FalsifyHeader(Context context) {
        this(context, null);
    }

    public FalsifyHeader(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }
    //</editor-fold>

    //<editor-fold desc="RefreshHeader">
    @Override
    public void onReleased(@NonNull RefreshLayout layout, int height, int maxDragHeight) {
        if (mRefreshKernel != null) {
            /*
             * 2020-3-15 BUG修复
             * https://github.com/scwang90/SmartRefreshLayout/issues/1018
             * 强化了 closeHeaderOrFooter 的关闭逻辑，帮助 Header 取消刷新
             * FalsifyHeader 是不能触发刷新的
             */
            layout.closeHeaderOrFooter();
//            mRefreshKernel.setState(RefreshState.None);
//            //onReleased 的时候 调用 setState(RefreshState.None); 并不会立刻改变成 None
//            //而是先执行一个回弹动画，RefreshFinish 是介于 Refreshing 和 None 之间的状态
//            //RefreshFinish 用于在回弹动画结束时候能顺利改变为 None
//            mRefreshKernel.setState(RefreshState.RefreshFinish);
        }
    }
    //</editor-fold>

}
