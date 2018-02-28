package com.scwang.smartrefresh.header.internal.pathview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * 路径视图
 * Created by SCWANG on 2017/5/29.
 */
public class PathsView extends View {

    protected PathsDrawable mPathsDrawable = new PathsDrawable();

    public PathsView(Context context) {
        this(context, null);
    }

    public PathsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPathsDrawable = new PathsDrawable();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getTag() instanceof String) {
            parserPaths(getTag().toString());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.setMeasuredDimension(
                View.resolveSize(mPathsDrawable.getBounds().width()+super.getPaddingLeft()+super.getPaddingRight(), widthMeasureSpec),
                View.resolveSize(mPathsDrawable.getBounds().height()+super.getPaddingTop()+super.getPaddingBottom(), heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mPathsDrawable.setBounds(super.getPaddingLeft(), super.getPaddingTop(),
                Math.max((right - left) - super.getPaddingRight(), super.getPaddingLeft()),
                Math.max((bottom - top) - super.getPaddingTop(), super.getPaddingTop()));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPathsDrawable.draw(canvas);
    }

    public void parserPaths(String... paths) {
        mPathsDrawable.parserPaths(paths);
    }

    public void parserColors(int... colors) {
        mPathsDrawable.parserColors(colors);
    }


}
