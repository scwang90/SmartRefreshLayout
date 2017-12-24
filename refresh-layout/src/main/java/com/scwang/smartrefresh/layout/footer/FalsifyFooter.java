package com.scwang.smartrefresh.layout.footer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.header.FalsifyHeader;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.makeMeasureSpec;

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

    @Override
    @SuppressLint({"DrawAllocation", "SetTextI18n"})
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isInEditMode()) {//这段代码在运行时不会执行，只会在Studio编辑预览时运行，不用在意性能问题
            int d = DensityUtil.dp2px(5);

            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(0x44ffffff);
            paint.setStrokeWidth(DensityUtil.dp2px(1));
            paint.setPathEffect(new DashPathEffect(new float[]{d, d, d, d}, 1));
            canvas.drawRect(d, d, getWidth() - d, getBottom() - d, paint);

            TextView textView = new TextView(getContext());
            textView.setText(getClass().getSimpleName()+" 虚假区域\n运行时代表上拉Footer的高度【" + DensityUtil.px2dp(getHeight()) + "dp】\n而不会显示任何东西");
            textView.setTextColor(0x44ffffff);
            textView.setGravity(Gravity.CENTER);
            textView.measure(makeMeasureSpec(getWidth(), EXACTLY), makeMeasureSpec(getHeight(), EXACTLY));
            textView.layout(0, 0, getWidth(), getHeight());
            textView.draw(canvas);
        }
    }


    //</editor-fold>

    //<editor-fold desc="RefreshFooter">


    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int extendHeight) {
        super.onInitialized(kernel, height, extendHeight);
        kernel.getRefreshLayout().setEnableAutoLoadmore(false);
    }

    @Override
    public void onPullingUp(float percent, int offset, int footerHeight, int extendHeight) {

    }

    @Override
    public void onPullReleasing(float percent, int offset, int footerHeight, int extendHeight) {

    }

    @Override
    public void onLoadmoreReleased(RefreshLayout layout, int footerHeight, int extendHeight) {
        if (mRefreshKernel != null) {
            mRefreshKernel.setState(RefreshState.None);
            //onLoadmoreReleased 的时候 调用 setState(RefreshState.None); 并不会立刻改变成 None
            //而是先执行一个回弹动画，LoadFinish 是介于 Loading 和 None 之间的状态
            //LoadFinish 用于在回弹动画结束时候能顺利改变为 None
            mRefreshKernel.setState(RefreshState.LoadFinish);
        }
    }

    @Override
    public boolean setLoadmoreFinished(boolean finished) {
        return false;
    }

    //</editor-fold>

}
