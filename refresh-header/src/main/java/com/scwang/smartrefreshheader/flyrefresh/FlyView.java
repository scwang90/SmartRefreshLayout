package com.scwang.smartrefreshheader.flyrefresh;

import android.content.Context;
import android.util.AttributeSet;

import com.scwang.smartrefreshlayout.internal.pathview.PathsView;
import com.scwang.smartrefreshlayout.util.DensityUtil;

/**
 * 纸飞机视图
 * Created by SCWANG on 2017/6/6.
 */

public class FlyView extends PathsView {

    public FlyView(Context context) {
        super(context);
        this.init(context, null, 0);
    }

    public FlyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context, attrs, 0);
    }

    public FlyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        parserColors(0xffffffff);
        parserPaths("M2.01,21L23,12 2.01,3 2,10l15,2 -15,2z");
        setMinimumWidth(DensityUtil.dp2px(25));
        setMinimumHeight(DensityUtil.dp2px(25));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

}
