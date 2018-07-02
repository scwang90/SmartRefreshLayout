package com.scwang.smartrefresh.header.internal.pathview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
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
        final View thisView = this;
        if (thisView.getTag() instanceof String) {
            parserPaths(thisView.getTag().toString());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final View thisView = this;
        final Drawable drawable = mPathsDrawable;
        super.setMeasuredDimension(
                View.resolveSize(drawable.getBounds().width()+thisView.getPaddingLeft()+thisView.getPaddingRight(), widthMeasureSpec),
                View.resolveSize(drawable.getBounds().height()+thisView.getPaddingTop()+thisView.getPaddingBottom(), heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final View thisView = this;
        super.onLayout(changed, left, top, right, bottom);
        mPathsDrawable.setBounds(thisView.getPaddingLeft(), thisView.getPaddingTop(),
                Math.max((right - left) - thisView.getPaddingRight(), thisView.getPaddingLeft()),
                Math.max((bottom - top) - thisView.getPaddingTop(), thisView.getPaddingTop()));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPathsDrawable.draw(canvas);
    }

    public boolean parserPaths(String... paths) {
        return mPathsDrawable.parserPaths(paths);
    }

    public void parserColors(int... colors) {
        mPathsDrawable.parserColors(colors);
    }


}
