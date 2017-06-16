package com.scwang.smartrefresh.layout.internal.pathview;

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
        super(context);
        this.initView(context, null, 0);
    }

    public PathsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initView(context, attrs, 0);
    }

    public PathsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        setMeasuredDimension(resolveSize(mPathsDrawable.width()+getPaddingLeft()+getPaddingRight(), widthMeasureSpec),
                resolveSize(mPathsDrawable.height()+getPaddingTop()+getPaddingBottom(), heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mPathsDrawable.setBounds(getPaddingLeft(), getPaddingTop(),
                Math.max((right - left) - getPaddingRight(), getPaddingLeft()),
                Math.max((bottom - top) - getPaddingTop(), getPaddingTop()));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPathsDrawable.draw(canvas);
    }

    public void parserPaths(String... paths) {
        mPathsDrawable.parserPaths(paths);
    }

    public void parserColors(int... colors) {
        mPathsDrawable.parserColors(colors);
    }


}
