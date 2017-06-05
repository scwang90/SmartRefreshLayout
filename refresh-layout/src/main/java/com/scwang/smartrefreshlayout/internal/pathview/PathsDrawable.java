package com.scwang.smartrefreshlayout.internal.pathview;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import static com.scwang.smartrefreshlayout.internal.pathview.TextScanner.parserPath;

/**
 * 路径
 * Created by SCWANG on 2017/6/1.
 */

public class PathsDrawable extends Drawable {

    private Paint mPaint;
    private List<Path> mPaths;
    private List<Integer> mColors;
    private int mWidth = 1,mHeight = 1;
    private int mStartX = 0,mStartY = 0;
    private int mOrginWidth;
    private int mOrginHeight;
    private static final Region REGION = new Region();
    private static final Region MAX_CLIP = new Region(Integer.MIN_VALUE,
            Integer.MIN_VALUE,Integer.MAX_VALUE, Integer.MAX_VALUE);
    private String[] mOrginPaths;

    public PathsDrawable() {
        mPaint = new Paint();
        mPaint.setColor(0xff11bbff);
        mPaint.setAntiAlias(true);
    }

    protected void onMeasure() {
        int suggestedMinimumWidth = 1;
        int suggestedMinimumHeight = 1;
        mWidth = mHeight = 1;
        mStartX = mStartY = -1;
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
                if (mStartX == -1 || mStartX > bounds.left) {
                    mStartX = bounds.left;
                }
                if (mStartY == -1 || mStartY > bounds.top) {
                    mStartY = bounds.top;
                }
            }
        }
        if (mStartX == -1) {
            mStartX = 0;
        }
        if (mStartY == -1) {
            mStartY = 0;
        }
        if (mOrginWidth == 0) {
            mOrginWidth = mWidth;
        }
        if (mOrginHeight == 0) {
            mOrginHeight = mHeight;
        }
        super.setBounds(0, 0, mWidth, mHeight);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        this.setBounds(new Rect(left, top, right, bottom));
    }

    @Override
    public void setBounds(@NonNull Rect bounds) {
        if (mOrginPaths != null && mOrginPaths.length > 0) {
            if (bounds.width() != mWidth || bounds.height() != mHeight) {
                float ratioWidth = 1f * bounds.width() / mOrginWidth;
                float ratioHeight = 1f * bounds.height() / mOrginHeight;
                String[] paths = zoomPaths(mOrginPaths, ratioWidth, ratioHeight);
                mPaths = new ArrayList<>();
                for (String path : paths) {
                    Path parser = parserPath(path);
                    mPaths.add(parser);
                }
                onMeasure();
            }
        }
    }

    private String[] zoomPaths(String[] paths, float ratioWidth, float ratioHeight) {
        String[] outpaths = new String[paths.length];
        for (int i = 0; i < paths.length; i++) {
            outpaths[i] = zoomPath(paths[i], ratioWidth, ratioHeight);
        }
        return outpaths;
    }

    private String zoomPath(String path, float ratioWidth, float ratioHeight) {
        return TextScanner.zoomPath(path, ratioWidth, ratioHeight).toString();
    }

    public void parserPaths(String... paths) {
        mOrginPaths = paths;
        mOrginWidth = mOrginHeight = 0;
        mPaths = new ArrayList<>();
        for (String path : paths) {
            Path parser = parserPath(path);
            mPaths.add(parser);
        }
        onMeasure();
    }

    public void parserColors(int... colors) {
        mColors = new ArrayList<>();
        for (int color : colors) {
            mColors.add(color);
        }
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.save();
        Rect bounds = getBounds();
        int width = bounds.width();
        int height = bounds.height();
        canvas.translate(bounds.left, bounds.top);
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

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public int width() {
        return getBounds().width();
    }

    public int height() {
        return getBounds().height();
    }
}
