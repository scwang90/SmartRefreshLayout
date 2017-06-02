package com.scwang.smartrefreshlayout.internal.pathview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import static com.scwang.smartrefreshlayout.internal.pathview.TextScanner.parserPath;

/**
 * 路径视图
 * Created by SCWANG on 2017/5/29.
 */

public class PathsView extends View {

    private Paint mPaint;
    private List<Path> mPaths;
    private List<Integer> mColors;
    private int mWidth = 1,mHeight = 1;
    private int mStartX = 0,mStartY = 0;
    private static final Region REGION = new Region();
    private static final Region MAX_CLIP = new Region(Integer.MIN_VALUE,
            Integer.MIN_VALUE,Integer.MAX_VALUE, Integer.MAX_VALUE);

    public PathsView(Context context) {
        super(context);
        this.init(context, null, 0);
    }

    public PathsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context, attrs, 0);
    }

    public PathsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mPaint = new Paint();
        mPaint.setColor(0xffffbb11);
        mPaint.setAntiAlias(true);
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
        int suggestedMinimumWidth = 1;
        int suggestedMinimumHeight = 1;
        mWidth = mHeight = 1;
        mStartX = mStartY = 0;
        if (mPaths != null) {
            for (Path path : mPaths) {
                REGION.setPath(path, MAX_CLIP);
                Rect bounds = REGION.getBounds();
                if (suggestedMinimumWidth < bounds.width()) {
                    mWidth = suggestedMinimumWidth = bounds.width();
                }
                if (suggestedMinimumHeight < bounds.height()) {
                    mHeight = suggestedMinimumHeight = bounds.height();
                }
                if (mStartX == 0 || mStartX > bounds.left) {
                    mStartX = bounds.left;
                }
                if (mStartY == 0 || mStartY > bounds.top) {
                    mStartY = bounds.top;
                }
            }
        }
        setMeasuredDimension(getDefaultSize(suggestedMinimumWidth+getPaddingLeft()+getPaddingRight(), widthMeasureSpec),
                getDefaultSize(suggestedMinimumHeight+getPaddingTop()+getPaddingBottom(), heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();
        int width = getWidth() - paddingLeft - paddingRight;
        int height = getHeight() - paddingTop - paddingBottom;
        canvas.translate(paddingLeft,paddingRight);
        canvas.scale(1f * width / mWidth, 1f * height / mHeight);
        canvas.translate(-mStartX, -mStartY);
        if (mPaths != null) {
            for (int i = 0; i < mPaths.size(); i++) {
                if (mColors != null && i < mColors.size()) {
                    mPaint.setColor(mColors.get(i));
                }
                canvas.drawPath(mPaths.get(i), mPaint);
            }
        }
        canvas.restore();
    }

    public void parserPaths(String... paths) {
        mPaths = new ArrayList<>();
        for (String path : paths) {
            Path parser = parserPath(path);
            mPaths.add(parser);
        }
    }

    public void parserColors(int... colors) {
        mColors = new ArrayList<>();
        for (int color : colors) {
            mColors.add(color);
        }
    }


}
