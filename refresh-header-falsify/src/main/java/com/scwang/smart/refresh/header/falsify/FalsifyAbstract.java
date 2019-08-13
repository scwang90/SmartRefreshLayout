package com.scwang.smart.refresh.header.falsify;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshKernel;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.simple.SimpleComponent;
import com.scwang.smart.refresh.layout.util.SmartUtil;

import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.makeMeasureSpec;

public abstract class FalsifyAbstract extends SimpleComponent implements RefreshHeader {

    protected RefreshKernel mRefreshKernel;

    //<editor-fold desc="FalsifyAbstract">
    protected FalsifyAbstract(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        final View thisView = this;
        if (thisView.isInEditMode()) {//这段代码在运行时不会执行，只会在Studio编辑预览时运行，不用在意性能问题
            final int d = SmartUtil.dp2px(5);
            final Context context = thisView.getContext();

            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(0xcccccccc);
            paint.setStrokeWidth(SmartUtil.dp2px(1));
            paint.setPathEffect(new DashPathEffect(new float[]{d, d, d, d}, 1));
            canvas.drawRect(d, d, thisView.getWidth() - d, thisView.getBottom() - d, paint);

            TextView textView = new TextView(context);
            textView.setText(context.getString(R.string.srl_component_falsify, getClass().getSimpleName(), SmartUtil.px2dp(thisView.getHeight())));
            textView.setTextColor(0xcccccccc);
            textView.setGravity(Gravity.CENTER);
            //noinspection UnnecessaryLocalVariable
            View view = textView;
            view.measure(makeMeasureSpec(thisView.getWidth(), EXACTLY), makeMeasureSpec(thisView.getHeight(), EXACTLY));
            view.layout(0, 0, thisView.getWidth(), thisView.getHeight());
            view.draw(canvas);
        }
    }
    //</editor-fold>

    //<editor-fold desc="RefreshHeader">
    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {
        mRefreshKernel = kernel;
    }

    @Override
    public void onReleased(@NonNull RefreshLayout layout, int height, int maxDragHeight) {
        if (mRefreshKernel != null) {
            mRefreshKernel.setState(RefreshState.None);
            //onReleased 的时候 调用 setState(RefreshState.None); 并不会立刻改变成 None
            //而是先执行一个回弹动画，RefreshFinish 是介于 Refreshing 和 None 之间的状态
            //RefreshFinish 用于在回弹动画结束时候能顺利改变为 None
            mRefreshKernel.setState(RefreshState.RefreshFinish);
        }
    }
    //</editor-fold>

}
