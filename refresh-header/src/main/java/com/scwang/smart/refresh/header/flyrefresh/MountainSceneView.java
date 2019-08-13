package com.scwang.smart.refresh.header.flyrefresh;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;

import com.scwang.smartrefresh.header.R;

/**
 * 山丘树木场景视图
 * Created by scwang on 2018/5/28.
 * from https://github.com/race604/FlyRefresh
 */
@SuppressWarnings("UnnecessaryLocalVariable")
public class MountainSceneView extends View {

    protected int COLOR_BACKGROUND = 0xFF7ECEC9;
    protected int COLOR_MOUNTAIN_1 = 0xFF86DAD7;
    protected int COLOR_MOUNTAIN_2 = 0xFF3C929C;
    protected int COLOR_MOUNTAIN_3 = 0xFF3E5F73;
    protected int COLOR_TREE_1_BRANCH = 0xFF1F7177;
    protected int COLOR_TREE_1_BRINK = 0xFF0C3E48;
    protected int COLOR_TREE_2_BRANCH = 0xFF34888F;
    protected int COLOR_TREE_2_BRINK = 0xFF1B6169;
    protected int COLOR_TREE_3_BRANCH = 0xFF57B1AE;
    protected int COLOR_TREE_3_BRINK = 0xFF62A4AD;

    protected static final int WIDTH = 240;
    protected static final int HEIGHT = 180;

    protected static final int TREE_WIDTH = 100;
    protected static final int TREE_HEIGHT = 200;

    protected Paint mMountPaint = new Paint();
    protected Paint mTrunkPaint = new Paint();
    protected Paint mBranchPaint = new Paint();
    protected Paint mBoarderPaint = new Paint();

    protected Path mMount1 = new Path();
    protected Path mMount2 = new Path();
    protected Path mMount3 = new Path();
    protected Path mTrunk = new Path();
    protected Path mBranch = new Path();
    protected Matrix mTransMatrix = new Matrix();

    protected float mScaleX = 5f;
    protected float mScaleY = 5f;
    protected float mMoveFactor = 0;
    protected float mBounceMax = 1;
    protected float mTreeBendFactor = Float.MAX_VALUE;
    protected int mViewportHeight = 0;

    //<editor-fold desc="MountainSceneView">
    public MountainSceneView(Context context) {
        this(context, null);
    }

    public MountainSceneView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mMountPaint.setAntiAlias(true);
        mMountPaint.setStyle(Paint.Style.FILL);

        mTrunkPaint.setAntiAlias(true);
        mBranchPaint.setAntiAlias(true);
        mBoarderPaint.setAntiAlias(true);
        mBoarderPaint.setStyle(Paint.Style.STROKE);
        mBoarderPaint.setStrokeWidth(2);
        mBoarderPaint.setStrokeJoin(Paint.Join.ROUND);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MountainSceneView);
        if (ta.hasValue(R.styleable.MountainSceneView_msvPrimaryColor)) {
            setPrimaryColor(ta.getColor(R.styleable.MountainSceneView_msvPrimaryColor, 0xff000000));
        }
        mViewportHeight = ta.getDimensionPixelOffset(R.styleable.MountainSceneView_msvViewportHeight, 0);
        ta.recycle();

        updateMountainPath(mMoveFactor, HEIGHT);
        updateTreePath(mMoveFactor, true);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final View thisView = this;
        final int width = thisView.getMeasuredWidth();
        final int height = thisView.getMeasuredHeight();
        mScaleX = 1f * width / WIDTH;
        mScaleY = 1f * (mViewportHeight > 0 ? mViewportHeight : height) / HEIGHT;

        updateMountainPath(mMoveFactor, height);
        updateTreePath(mMoveFactor, true);
    }

    protected void updateMountainPath(float factor,int height) {

        mTransMatrix.reset();
        mTransMatrix.setScale(mScaleX, mScaleY);

        float offset1 = (10 * factor);
        mMount1.reset();
        mMount1.moveTo(0, 95 + offset1);
        mMount1.lineTo(55, 74 + offset1);
        mMount1.lineTo(146, 104 + offset1);
        mMount1.lineTo(227, 72 + offset1);
        mMount1.lineTo(WIDTH, 80 + offset1);
        mMount1.lineTo(WIDTH, HEIGHT);
        mMount1.lineTo(0, HEIGHT);
        mMount1.close();
        mMount1.transform(mTransMatrix);

        float offset2 = (20 * factor);
        mMount2.reset();
        mMount2.moveTo(0, 103 + offset2);
        mMount2.lineTo(67, 90 + offset2);
        mMount2.lineTo(165, 115 + offset2);
        mMount2.lineTo(221, 87 + offset2);
        mMount2.lineTo(WIDTH, 100 + offset2);
        mMount2.lineTo(WIDTH, HEIGHT);
        mMount2.lineTo(0, HEIGHT);
        mMount2.close();
        mMount2.transform(mTransMatrix);

        float offset3 = (30 * factor);
        mMount3.reset();
        mMount3.moveTo(0, 114 + offset3);
        mMount3.cubicTo(30, 106 + offset3, 196, 97 + offset3, WIDTH, 104 + offset3);
        mMount3.lineTo(WIDTH, height / mScaleY);
        mMount3.lineTo(0, height / mScaleY);
        mMount3.close();
        mMount3.transform(mTransMatrix);
    }

    protected void updateTreePath(float factor, boolean force) {
        if (factor == mTreeBendFactor && !force) {
            return;
        }

        final Interpolator interpolator = PathInterpolatorCompat.create(0.8f, -0.5f * factor);

        final float width = TREE_WIDTH;
        final float height = TREE_HEIGHT;

        final float maxMove = width * 0.3f * factor;
        final float trunkSize = width * 0.05f;
        final float branchSize = width * 0.2f;
        final float x0 = width / 2;
        final float y0 = height;

        final int N = 25;
        final float dp = 1f / N;
        final float dy = -dp * height;
        float y = y0;
        float p = 0;
        float[] xx = new float[N + 1];
        float[] yy = new float[N + 1];
        for (int i = 0; i <= N; i++) {
            xx[i] = interpolator.getInterpolation(p) * maxMove + x0;
            yy[i] = y;

            y += dy;
            p += dp;
        }

        mTrunk.reset();
        mTrunk.moveTo(x0 - trunkSize, y0);
        int max = (int) (N * 0.7f);
        int max1 = (int) (max * 0.5f);
        float diff = max - max1;
        for (int i = 0; i < max; i++) {
            if (i < max1) {
                mTrunk.lineTo(xx[i] - trunkSize, yy[i]);
            } else {
                mTrunk.lineTo(xx[i] - trunkSize * (max - i) / diff, yy[i]);
            }
        }

        for (int i = max - 1; i >= 0; i--) {
            if (i < max1) {
                mTrunk.lineTo(xx[i] + trunkSize, yy[i]);
            } else {
                mTrunk.lineTo(xx[i] + trunkSize * (max - i) / diff, yy[i]);
            }
        }
        mTrunk.close();

        mBranch.reset();
        int min = (int) (N * 0.4f);
        diff = N - min;

        mBranch.moveTo(xx[min] - branchSize, yy[min]);
        mBranch.addArc(new RectF(xx[min] - branchSize, yy[min] - branchSize, xx[min] + branchSize, yy[min] + branchSize), 0f, 180f);
        for (int i = min; i <= N; i++) {
            float f = (i - min) / diff;
            mBranch.lineTo(xx[i] - branchSize + f * f * branchSize, yy[i]);
        }
        for (int i = N; i >= min; i--) {
            float f = (i - min) / diff;
            mBranch.lineTo(xx[i] + branchSize - f * f * branchSize, yy[i]);
        }

    }

    protected void drawTree(Canvas canvas, float scale, float baseX, float baseY,
                          int colorTrunk, int colorBranch) {
        canvas.save();

        final float dx = baseX - TREE_WIDTH * scale / 2;
        final float dy = baseY - TREE_HEIGHT * scale;
        canvas.translate(dx, dy);
        canvas.scale(scale, scale);

        mBranchPaint.setColor(colorBranch);
        canvas.drawPath(mBranch, mBranchPaint);
        mTrunkPaint.setColor(colorTrunk);
        canvas.drawPath(mTrunk, mTrunkPaint);
        mBoarderPaint.setColor(colorTrunk);
        canvas.drawPath(mBranch, mBoarderPaint);

        canvas.restore();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final View thisView = this;
        canvas.drawColor(COLOR_BACKGROUND);

        mMountPaint.setColor(COLOR_MOUNTAIN_1);
        canvas.drawPath(mMount1, mMountPaint);

        canvas.save();
        canvas.scale(-1, 1, thisView.getWidth() / 2f, 0);
        drawTree(canvas, 0.12f * mScaleX, 180 * mScaleX, (93 + 20 * mMoveFactor) * mScaleY,
                COLOR_TREE_3_BRINK, COLOR_TREE_3_BRANCH);
        drawTree(canvas, 0.1f * mScaleX, 200 * mScaleX, (96 + 20 * mMoveFactor) * mScaleY,
                COLOR_TREE_3_BRINK, COLOR_TREE_3_BRANCH);
        canvas.restore();
        mMountPaint.setColor(COLOR_MOUNTAIN_2);
        canvas.drawPath(mMount2, mMountPaint);

        drawTree(canvas, 0.2f * mScaleX, 160 * mScaleX, (105 + 30 * mMoveFactor) * mScaleY,
                COLOR_TREE_1_BRINK, COLOR_TREE_1_BRANCH);

        drawTree(canvas, 0.14f * mScaleX, 180 * mScaleX, (105 + 30 * mMoveFactor) * mScaleY,
                COLOR_TREE_2_BRINK, COLOR_TREE_2_BRANCH);

        drawTree(canvas, 0.16f * mScaleX, 140 * mScaleX, (105 + 30 * mMoveFactor) * mScaleY,
                COLOR_TREE_2_BRINK, COLOR_TREE_2_BRANCH);

        mMountPaint.setColor(COLOR_MOUNTAIN_3);
        canvas.drawPath(mMount3, mMountPaint);
    }
    //</editor-fold>

    public void setPrimaryColor(@ColorInt int color) {
//        private int COLOR_BACKGROUND = 0xFF7ECEC9;
//        private int COLOR_MOUNTAIN_1 = 0xFF86DAD7;
//        private int COLOR_MOUNTAIN_2 = 0xFF3C929C;
//        private int COLOR_MOUNTAIN_3 = 0xFF3E5F73;
//        private int COLOR_TREE_1_BRANCH = 0xFF1F7177;
//        private int COLOR_TREE_1_BRINK = 0xFF0C3E48;
//        private int COLOR_TREE_2_BRANCH = 0xFF34888F;
//        private int COLOR_TREE_2_BRINK = 0xFF1B6169;
//        private int COLOR_TREE_3_BRANCH = 0xFF57B1AE;
//        private int COLOR_TREE_3_BRINK = 0xFF62A4AD;
        COLOR_BACKGROUND = color;
        COLOR_MOUNTAIN_1 = ColorUtils.compositeColors(0x99ffffff,color);
        COLOR_MOUNTAIN_2 = ColorUtils.compositeColors(0x993C929C,color);
        COLOR_MOUNTAIN_3 = ColorUtils.compositeColors(0xCC3E5F73,color);
        COLOR_TREE_1_BRANCH = ColorUtils.compositeColors(0x551F7177,color);
        COLOR_TREE_1_BRINK = ColorUtils.compositeColors(0xCC0C3E48,color);
        COLOR_TREE_2_BRANCH = ColorUtils.compositeColors(0x5534888F,color);
        COLOR_TREE_2_BRINK = ColorUtils.compositeColors(0xCC1B6169,color);
        COLOR_TREE_3_BRANCH = ColorUtils.compositeColors(0x5557B1AE,color);
        COLOR_TREE_3_BRINK = ColorUtils.compositeColors(0xCC62A4AD,color);
    }

    public void updatePercent(float percent) {
        mBounceMax = percent;
        float bendFactor = Math.max(0, percent);

        mMoveFactor = Math.max(0, mBounceMax);

        final View thisView = MountainSceneView.this;
        int height = thisView.getMeasuredHeight();
        updateMountainPath(mMoveFactor, height > 0 ? height : HEIGHT);
        updateTreePath(bendFactor, false);
    }


}
