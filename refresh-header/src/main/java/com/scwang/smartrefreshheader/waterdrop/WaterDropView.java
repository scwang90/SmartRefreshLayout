package com.scwang.smartrefreshheader.waterdrop;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.scwang.smartrefreshheader.internal.MaterialProgressDrawable;
import com.scwang.smartrefreshlayout.util.DensityUtil;


/**
 * 下拉头中间的“水滴”
 * Created by xiayong on 2015/6/23.
 */
public class WaterDropView extends View {

    private Circle topCircle;
    private Circle bottomCircle;

    private Path mPath;
    private Paint mPaint;
    private float mMaxCircleRadius;//圆半径最大值
    private float mMinCircleRaidus;//圆半径最小值
//    private Bitmap arrowBitmap;//箭头
    private MaterialProgressDrawable mProgress;
    private static float STROKE_WIDTH = 2;//边线宽度
    private final static int BACK_ANIM_DURATION = 180;

    public WaterDropView(Context context) {
        super(context);
        init(context, null);
    }

    public WaterDropView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public WaterDropView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setBackgroundColor(0xffbbff11);
        topCircle = new Circle();
        bottomCircle = new Circle();
        mPath = new Path();
        mPaint = new Paint();
        mPaint.setColor(Color.GRAY);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeWidth(STROKE_WIDTH = DensityUtil.dp2px(1));
        mPaint.setShadowLayer(STROKE_WIDTH, 0, 0, 0xff000000);
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        mProgress = new MaterialProgressDrawable(context, this);
//        Drawable drawable = getResources().getDrawable(R.drawable.refresh_arrow);
//        arrowBitmap = Utils.drawableToBitmap(drawable);

        mPaint.setColor(Color.GRAY);
        mMaxCircleRadius = DensityUtil.dp2px(20);
        mMinCircleRaidus = mMaxCircleRadius / 4;

        topCircle.radius = (mMaxCircleRadius);
        bottomCircle.radius = (mMaxCircleRadius);

        topCircle.x = (STROKE_WIDTH + mMaxCircleRadius);
        topCircle.y = (STROKE_WIDTH + mMaxCircleRadius);

        bottomCircle.x = (STROKE_WIDTH + mMaxCircleRadius);
        bottomCircle.y = (STROKE_WIDTH + mMaxCircleRadius);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //宽度：上圆和下圆的最大直径
        int width = (int) ((mMaxCircleRadius + STROKE_WIDTH) * 2);
        //高度：上圆半径 + 圆心距 + 下圆半径
        int height = (int) Math.ceil(bottomCircle.y+bottomCircle.radius + STROKE_WIDTH * 2);
        setMeasuredDimension(width, resolveSize(height, heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int height = getHeight();
        if (height <= 2 * mMaxCircleRadius) {
            canvas.restore();
            canvas.translate(0, height - 2 * mMaxCircleRadius);
            canvas.drawCircle(topCircle.x, topCircle.y, topCircle.radius, mPaint);
        } else {
            makeBezierPath();
//        mPaint.setColor(Color.RED);
//        mPaint.setAlpha(200);
            canvas.drawPath(mPath, mPaint);
//        mPaint.setColor(Color.GRAY);
//        mPaint.setAlpha(50);
//        canvas.drawCircle(topCircle.x, topCircle.y, topCircle.radius, mPaint);
//        canvas.drawCircle(bottomCircle.x, bottomCircle.y, bottomCircle.radius, mPaint);
//        canvas.drawBitmap(arrowBitmap, topCircle.x - topCircle.radius, topCircle.y - topCircle.radius, mPaint);
//        RectF bitmapArea = new RectF(topCircle.x-0.5f*topCircle.radius,topCircle.y-0.5f*topCircle.radius,topCircle.x+ 0.5f*topCircle.radius,topCircle.y+0.5f*topCircle.radius);
//        canvas.drawBitmap(arrowBitmap,null,bitmapArea,mPaint);
//        mProgress.setBounds(
//                (int)(topCircle.x - 0.5f * topCircle.radius),
//                (int)(topCircle.y - 0.5f * topCircle.radius),
//                (int)(topCircle.x + 0.5f * topCircle.radius),
//                (int)(topCircle.y + 0.5f * topCircle.radius));
//        mProgress.draw(canvas);
//        super.onDraw(canvas);
        }
    }


    private void makeBezierPath() {
        mPath.reset();
        //获取两圆的两个切线形成的四个切点
        double angle = getAngle();
        float top_x1 = (float) (topCircle.x - topCircle.radius * Math.cos(angle));
        float top_y1 = (float) (topCircle.y + topCircle.radius * Math.sin(angle));

        float top_x2 = (float) (topCircle.x + topCircle.radius * Math.cos(angle));
        float top_y2 = top_y1;

        float bottom_x1 = (float) (bottomCircle.x - bottomCircle.radius * Math.cos(angle));
        float bottom_y1 = (float) (bottomCircle.y + bottomCircle.radius * Math.sin(angle));

        float bottom_x2 = (float) (bottomCircle.x + bottomCircle.radius * Math.cos(angle));
        float bottom_y2 = bottom_y1;

        mPath.moveTo(topCircle.x, topCircle.y);

        mPath.lineTo(top_x1, top_y1);

        mPath.quadTo((bottomCircle.x - bottomCircle.radius),
                (bottomCircle.y + topCircle.y) / 2,

                bottom_x1,
                bottom_y1);
        mPath.lineTo(bottom_x2, bottom_y2);

        mPath.quadTo((bottomCircle.x + bottomCircle.radius),
                (bottomCircle.y + top_y2) / 2,
                top_x2,
                top_y2);

        mPath.addCircle(topCircle.x, topCircle.y, topCircle.radius, Path.Direction.CCW);
        mPath.addCircle(bottomCircle.x, bottomCircle.y, bottomCircle.radius, Path.Direction.CCW);

        mPath.close();
    }

    /**
     * 获得两个圆切线与圆心连线的夹角
     */
    private double getAngle() {
        if (bottomCircle.radius > topCircle.radius) {
            throw new IllegalStateException("bottomCircle's radius must be less than the topCircle's");
        }
        return Math.asin((topCircle.radius - bottomCircle.radius) / (bottomCircle.y - topCircle.y));
    }

    /**
     * 创建回弹动画
     * 上圆半径减速恢复至最大半径
     * 下圆半径减速恢复至最大半径
     * 圆心距减速从最大值减到0(下圆Y从当前位置移动到上圆Y)。
     */
    public Animator createAnimator() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1, 0).setDuration(BACK_ANIM_DURATION);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(valueAnimator1 -> updateComleteState((float) valueAnimator1.getAnimatedValue()));
        return valueAnimator;
    }

    /**
     * 完成的百分比
     */
    public void updateComleteState(int offset, int maxHeight) {
        updateComleteState(Math.max(0, 1f * (offset - mMaxCircleRadius * 2) / maxHeight));
    }

    /**
     * 完成的百分比
     */
    public void updateComleteState(float percent) {
        float top_r = (float) (mMaxCircleRadius - 0.25 * percent * mMaxCircleRadius);
        float bottom_r = (mMinCircleRaidus - mMaxCircleRadius) * percent + mMaxCircleRadius;
        float bottomCricleOffset = 4 * percent * mMaxCircleRadius;

//        float max = getHeight() - bottom_r;
//        if (bottomCricleOffset > max) {
//            bottomCricleOffset = max;
//        }

        topCircle.radius = (top_r);
        bottomCircle.radius = (bottom_r);
        bottomCircle.y = (topCircle.y + bottomCricleOffset);
//        requestLayout();
//        postInvalidate();
    }

    public Circle getTopCircle() {
        return topCircle;
    }

    public Circle getBottomCircle() {
        return bottomCircle;
    }

    public void setIndicatorColor(int color) {
        mPaint.setColor(color);
    }

    public int getIndicatorColor() {
        return mPaint.getColor();
    }
}