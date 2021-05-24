package com.scwang.smart.refresh.header.waterdrop;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.ColorInt;

import com.scwang.smartrefresh.layout.util.SmartUtil;


/**
 * 下拉头中间的 “水滴”
 * Created by scwang on 2018/6/23.
 * from https://github.com/THEONE10211024/WaterDropListView
 */
@SuppressWarnings("unused")
public class WaterDropView extends View {

    protected Circle topCircle;
    protected Circle bottomCircle;

    protected Path mPath;
    protected Paint mPaint;
    protected int mMaxCircleRadius;//圆半径最大值
    protected int mMinCircleRadius;//圆半径最小值
    protected static int STROKE_WIDTH = 2;//边线宽度
    protected final static int BACK_ANIM_DURATION = 180;

    public WaterDropView(Context context) {
        super(context);
        final View thisView = this;
        topCircle = new Circle();
        bottomCircle = new Circle();
        mPath = new Path();
        mPaint = new Paint();
        mPaint.setColor(Color.GRAY);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeWidth(STROKE_WIDTH = SmartUtil.dp2px(1f));
        mPaint.setShadowLayer(STROKE_WIDTH, STROKE_WIDTH/2f, STROKE_WIDTH, 0x99000000);
        thisView.setLayerType(LAYER_TYPE_SOFTWARE, null);

        int padding = 4 * STROKE_WIDTH;
        thisView.setPadding(padding, padding, padding, padding);

        mPaint.setColor(Color.GRAY);
        mMaxCircleRadius = SmartUtil.dp2px(20);
        mMinCircleRadius = mMaxCircleRadius / 5;

        topCircle.radius = (mMaxCircleRadius);
        bottomCircle.radius = (mMaxCircleRadius);

        topCircle.x = (STROKE_WIDTH + mMaxCircleRadius);
        topCircle.y = (STROKE_WIDTH + mMaxCircleRadius);

        bottomCircle.x = (STROKE_WIDTH + mMaxCircleRadius);
        bottomCircle.y = (STROKE_WIDTH + mMaxCircleRadius);
    }

    public int getMaxCircleRadius() {
        return mMaxCircleRadius;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final View thisView = this;
        //宽度：上圆和下圆的最大直径
        final int width = ((mMaxCircleRadius + STROKE_WIDTH) * 2);
        //高度：上圆半径 + 圆心距 + 下圆半径
        final int height = (int) Math.ceil(bottomCircle.y + bottomCircle.radius + STROKE_WIDTH * 2);
        super.setMeasuredDimension(width + thisView.getPaddingLeft() + thisView.getPaddingRight(),
                View.resolveSize(height + thisView.getPaddingTop() + thisView.getPaddingBottom(), heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        final View thisView = this;
        updateCompleteState(thisView.getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final View thisView = this;
        final int paddingTop = thisView.getPaddingTop();
        final int paddingLeft = thisView.getPaddingLeft();
        final int paddingBottom = thisView.getPaddingBottom();
        final int height = thisView.getHeight();
        canvas.save();
        if (height <= topCircle.radius * 2 + paddingTop + paddingBottom) {
            canvas.translate(paddingLeft, height - topCircle.radius * 2 - paddingBottom);
            canvas.drawCircle(topCircle.x, topCircle.y, topCircle.radius, mPaint);
        } else {
            canvas.translate(paddingLeft, paddingTop);
            makeBezierPath();
            canvas.drawPath(mPath, mPaint);
//        canvas.drawCircle(topCircle.x, topCircle.y, topCircle.radius, mPaint);
//        canvas.drawCircle(bottomCircle.x, bottomCircle.y, bottomCircle.radius, mPaint);
        }
        canvas.restore();
    }


    protected void makeBezierPath() {
        mPath.reset();
        mPath.addCircle(topCircle.x, topCircle.y, topCircle.radius, Path.Direction.CCW);
        if (bottomCircle.y > topCircle.y + SmartUtil.dp2px(1)) {
            mPath.addCircle(bottomCircle.x, bottomCircle.y, bottomCircle.radius, Path.Direction.CCW);
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
                    bottom_x1,bottom_y1);
            mPath.lineTo(bottom_x2, bottom_y2);

            mPath.quadTo((bottomCircle.x + bottomCircle.radius),
                    (bottomCircle.y + top_y2) / 2,
                    top_x2,top_y2);
        }
        mPath.close();
    }

    /**
     * 获得两个圆切线与圆心连线的夹角
     * @return 夹角
     */
    protected double getAngle() {
        if (bottomCircle.radius > topCircle.radius) {
//            throw new IllegalStateException("bottomCircle's radius must be less than the topCircle's");
            return 0;
        }
        return Math.asin((topCircle.radius - bottomCircle.radius) / (bottomCircle.y - topCircle.y));
    }

    /**
     * 创建回弹动画
     * 上圆半径减速恢复至最大半径
     * 下圆半径减速恢复至最大半径
     * 圆心距减速从最大值减到0(下圆Y从当前位置移动到上圆Y)。
     * @return Animator
     */
    public ValueAnimator createAnimator() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1, 0.001f).setDuration(BACK_ANIM_DURATION);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator1) {
                WaterDropView.this.updateCompleteState((float) valueAnimator1.getAnimatedValue());
                final View thisView = WaterDropView.this;
                thisView.postInvalidate();
            }
        });
        return valueAnimator;
    }

    /**
     * 完成的百分比
     * @param offset 偏移量
     * @param maxHeight 最大高度
     */
    public void updateCompleteState(int offset, int maxHeight) {
//        float space = mMaxCircleRadius * 2 + getPaddingTop() + getPaddingBottom();
//        updateCompleteState(Math.max(0, 1f * (offset - space) / (maxHeight - space)));
    }

    /**
     * 完成的百分比
     * @param percent 百分比
     */
    public void updateCompleteState(float percent) {
        float top_r = (float) (mMaxCircleRadius - 0.25 * percent * mMaxCircleRadius);
        float bottom_r = (mMinCircleRadius - mMaxCircleRadius) * percent + mMaxCircleRadius;
        float bottomCircleOffset = 4 * percent * mMaxCircleRadius;

        topCircle.radius = (top_r);
        bottomCircle.radius = (bottom_r);
        bottomCircle.y = (topCircle.y + bottomCircleOffset);
    }

    /**
     * 完成的百分比
     * @param height 高度
     */
    public void updateCompleteState(int height) {
        final View thisView = WaterDropView.this;
        final int paddingTop = thisView.getPaddingTop();
        final int paddingBottom = thisView.getPaddingBottom();
        float space = mMaxCircleRadius * 2 + paddingTop + paddingBottom;
        if (height < space) {
            topCircle.radius = mMaxCircleRadius;
            bottomCircle.radius = mMaxCircleRadius;
            bottomCircle.y = topCircle.y;
        } else {
            float limit = mMaxCircleRadius - mMinCircleRadius;
            float x = Math.max(0, height - space);
            float y = (float) (limit * (1 - Math.pow(100, -x / SmartUtil.dp2px(200))));
            topCircle.radius = mMaxCircleRadius - y / 4;
            bottomCircle.radius = mMaxCircleRadius - y;
            int validHeight = height - paddingTop - paddingBottom;
            bottomCircle.y = validHeight - bottomCircle.radius;
        }
    }

    public Circle getTopCircle() {
        return topCircle;
    }

    public Circle getBottomCircle() {
        return bottomCircle;
    }

    public void setIndicatorColor(@ColorInt int color) {
        mPaint.setColor(color);
    }

    public int getIndicatorColor() {
        return mPaint.getColor();
    }
}