package com.scwang.smartrefresh.layout.footer;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;

import com.scwang.smart.refresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.api.RefreshFooter;


/**
 * 球脉冲底部加载组件
 * Created by scwang on 2017/5/30.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class BallPulseFooter extends com.scwang.smart.refresh.footer.BallPulseFooter implements RefreshFooter {

    //<editor-fold desc="构造方法">
    public BallPulseFooter(Context context) {
        this(context, null);
    }

    public BallPulseFooter(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    //</editor-fold>


    @Override
    public BallPulseFooter setSpinnerStyle(SpinnerStyle mSpinnerStyle) {
        super.setSpinnerStyle(mSpinnerStyle);
        return this;
    }

    @Override
    public BallPulseFooter setNormalColor(int color) {
        super.setNormalColor(color);
        return this;
    }

    @Override
    public BallPulseFooter setAnimatingColor(int color) {
        super.setAnimatingColor(color);
        return this;
    }
}
