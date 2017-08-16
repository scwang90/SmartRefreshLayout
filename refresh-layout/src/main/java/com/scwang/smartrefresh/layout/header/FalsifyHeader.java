package com.scwang.smartrefresh.layout.header;

import android.annotation.SuppressLint;
import android.support.annotation.RequiresApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.makeMeasureSpec;

/**
 * 虚假的 Header
 * 用于 正真的 Header 在 RefreshLayout 外部时，
 * 使用本虚假的 FalsifyHeader 填充在 RefreshLayout 内部
 * 具体使用方法 参考 QQ空间风格（QzoneHeader） 和 纸飞机（FlyRefreshHeader）
 * Created by SCWANG on 2017/6/14.
 */

public class FalsifyHeader extends View implements RefreshHeader {

    protected RefreshKernel mRefreshKernel;

    //<editor-fold desc="FalsifyHeader">
    public FalsifyHeader(Context context) {
        super(context);
    }

    public FalsifyHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FalsifyHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public FalsifyHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    @Override@SuppressLint("DrawAllocation")
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
            textView.setText(getClass().getSimpleName()+" 虚假区域\n运行时代表下拉Header的高度【" + DensityUtil.px2dp(getHeight()) + "dp】\n而不会显示任何东西");
            textView.setTextColor(0x44ffffff);
            textView.setGravity(Gravity.CENTER);
            textView.measure(makeMeasureSpec(getWidth(), EXACTLY), makeMeasureSpec(getHeight(), EXACTLY));
            textView.layout(0, 0, getWidth(), getHeight());
            textView.draw(canvas);
        }
    }

    //</editor-fold>

    //<editor-fold desc="RefreshHeader">

    @Override
    public void onInitialized(RefreshKernel kernel, int height, int extendHeight) {
        mRefreshKernel = kernel;
    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {
    }

    @Override
    public void onPullingDown(float percent, int offset, int headHeight, int extendHeight) {

    }

    @Override
    public void onReleasing(float percent, int offset, int headHeight, int extendHeight) {

    }

    @Override
    public void onStartAnimator(RefreshLayout layout, int headHeight, int extendHeight) {
        if (mRefreshKernel != null) {
            mRefreshKernel.resetStatus();
        }
    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {

    }

    @Override
    public int onFinish(RefreshLayout layout, boolean success) {
        return 0;
    }

    @Override@Deprecated
    public void setPrimaryColors(int... colors) {

    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Scale;
    }
    //</editor-fold>

}
