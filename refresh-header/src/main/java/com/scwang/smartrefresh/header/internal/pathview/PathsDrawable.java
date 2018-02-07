package com.scwang.smartrefresh.header.internal.pathview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
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

/**
 * 路径
 * Created by SCWANG on 2017/6/1.
 */

@SuppressWarnings("WeakerAccess")
public class PathsDrawable extends Drawable {

    protected Paint mPaint;
    protected List<Path> mPaths;
    protected List<Integer> mColors;
    protected int mWidth = 1,mHeight = 1;
    protected int mStartX = 0,mStartY = 0;
    protected int mOriginWidth;
    protected int mOriginHeight;
    protected static final Region REGION = new Region();
    protected static final Region MAX_CLIP = new Region(Integer.MIN_VALUE,
            Integer.MIN_VALUE,Integer.MAX_VALUE, Integer.MAX_VALUE);
    protected List<Path> mOriginPaths;
    protected List<String> mOriginSvgs;

    public PathsDrawable() {
        mPaint = new Paint();
        mPaint.setColor(0xff11bbff);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
    }

    protected void onMeasure() {
        Integer top = null,left = null,right = null,bottom = null;
        if (mPaths != null) {
            for (Path path : mPaths) {
                REGION.setPath(path, MAX_CLIP);
                Rect bounds = REGION.getBounds();
                top = Math.min(top == null ? bounds.top : top, bounds.top);
                left = Math.min(left == null ? bounds.left : left, bounds.left);
                right = Math.max(right == null ? bounds.right : right, bounds.right);
                bottom = Math.max(bottom == null ? bounds.bottom : bottom, bounds.bottom);
            }
        }
        mStartX = left == null ? 0 : left;
        mStartY = top == null ? 0 : top;
        mWidth = right == null ? 0 : right - mStartX;
        mHeight = bottom == null ? 0 : bottom - mStartY;
        if (mOriginWidth == 0) {
            mOriginWidth = mWidth;
        }
        if (mOriginHeight == 0) {
            mOriginHeight = mHeight;
        }
        Rect bounds = getBounds();
        super.setBounds(bounds.left, bounds.top, bounds.left + mWidth, bounds.top + mHeight);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        final int width = right - left;
        final int height = bottom - top;
        if (mOriginPaths != null && mOriginPaths.size() > 0 &&
                (width != mWidth || height != mHeight)) {
            float ratioWidth = 1f * width / mOriginWidth;
            float ratioHeight = 1f * height / mOriginHeight;
            mPaths = PathParser.transformScale(ratioWidth, ratioHeight, mOriginPaths, mOriginSvgs);
            onMeasure();
        } else {
            super.setBounds(left, top, right, bottom);
        }
    }

    public void setBounds(@NonNull Rect bounds) {
        setBounds(bounds.left, bounds.top, bounds.right, bounds.bottom);
    }

    public void parserPaths(String... paths) {
        mOriginWidth = mOriginHeight = 0;
        mOriginSvgs = new ArrayList<>();
        mPaths = mOriginPaths = new ArrayList<>();
        for (String path : paths) {
            mOriginSvgs.add(path);
            mOriginPaths.add(PathParser.createPathFromPathData(path));
        }
        onMeasure();
    }

    public void parserColors(int... colors) {
        mColors = new ArrayList<>();
        for (int color : colors) {
            mColors.add(color);
        }
    }

    //<editor-fold desc="Drawable">
    @Override
    public void draw(@NonNull Canvas canvas) {
        Rect bounds = getBounds();
        int width = bounds.width();
        int height = bounds.height();
        if (mPaint.getAlpha() == 0xFF) {
            canvas.save();
            canvas.translate(bounds.left-mStartX, bounds.top-mStartY);
            if (mPaths != null) {
                for (int i = 0; i < mPaths.size(); i++) {
                    if (mColors != null && i < mColors.size()) {
                        mPaint.setColor(mColors.get(i));
                    }
                    canvas.drawPath(mPaths.get(i), mPaint);
                }
                mPaint.setAlpha(0xFF);
            }
            canvas.restore();
        } else {
            createCachedBitmapIfNeeded(width, height);
            if (mCacheDirty) {
                mCachedBitmap.eraseColor(Color.TRANSPARENT);
                Canvas tmpCanvas = new Canvas(mCachedBitmap);
                drawCachedBitmap(tmpCanvas);
                // Use shallow copy here and shallow comparison in canReuseCache(),
                // likely hit cache miss more, but practically not much difference.
                mCacheDirty = false;
            }
            canvas.drawBitmap(mCachedBitmap, bounds.left, bounds.top, mPaint);
        }
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
    //</editor-fold>

    //<editor-fold desc="API">

    public void setGeometricWidth(int width) {
        Rect bounds = getBounds();
        float rate = 1f * width / bounds.width();
        setBounds(
                (int) (bounds.left * rate),
                (int) (bounds.top * rate),
                (int) (bounds.right * rate),
                (int) (bounds.bottom * rate)
        );

    }

    public void setGeometricHeight(int height) {
        Rect bounds = getBounds();
        float rate = 1f * height / bounds.height();
        setBounds(
                (int) (bounds.left * rate),
                (int) (bounds.top * rate),
                (int) (bounds.right * rate),
                (int) (bounds.bottom * rate)
        );
    }

    //</editor-fold>

    //<editor-fold desc="CachedBitmap">

    private Bitmap mCachedBitmap;
    private boolean mCacheDirty;

    private void drawCachedBitmap(Canvas canvas) {
        canvas.translate(-mStartX, -mStartY);
        if (mPaths != null) {
            for (int i = 0; i < mPaths.size(); i++) {
                if (mColors != null && i < mColors.size()) {
                    mPaint.setColor(mColors.get(i));
                }
                canvas.drawPath(mPaths.get(i), mPaint);
            }
        }
    }

    public void createCachedBitmapIfNeeded(int width, int height) {
        if (mCachedBitmap == null || width != mCachedBitmap.getWidth() || height != mCachedBitmap.getHeight()) {
            mCachedBitmap = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            mCacheDirty = true;
        }

    }

    //</editor-fold>
}
