package com.scwang.smartrefresh.header;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v4.graphics.ColorUtils;
import android.util.AttributeSet;

import com.scwang.smartrefresh.header.fungame.FunGameView;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hitomis on 2016/2/29.
 * email:196425254@qq.com
 * from https://github.com/Hitomis/FunGameRefresh
 */
public class FunGameHitBlockHeader extends FunGameView {

    //<editor-fold desc="属性变量">
    /**
     * 默认矩形块竖向排列的数目
     */
    private static final int BLOCK_VERTICAL_NUM = 5;

    /**
     * 默认矩形块横向排列的数目
     */
    private static final int BLOCK_HORIZONTAL_NUM = 3;

    /**
     * 矩形块的宽度占屏幕宽度比率
     */
    private static final float BLOCK_WIDTH_RATIO = .01806f;

    /**
     * 挡板所在位置占屏幕宽度的比率
     */
    private static final float RACKET_POSITION_RATIO = .8f;

    /**
     * 矩形块所在位置占屏幕宽度的比率
     */
    private static final float BLOCK_POSITION_RATIO = .08f;

    /**
     * 小球默认其实弹射角度
     */
    private static final int DEFAULT_ANGLE = 30;

    /**
     * 分割线默认宽度大小
     */
    static final float DIVIDING_LINE_SIZE = 1.f;

    /**
     * 小球移动速度
     */
    private static final int SPEED = 3;

    /**
     * 矩形砖块的高度、宽度
     */
    private float blockHeight, blockWidth;

    /**
     * 小球半径
     */
    private float BALL_RADIUS;

    private Paint blockPaint;

    private float blockLeft, racketLeft;

    private float cx, cy;

    private List<Point> pointList;

    private boolean isleft;

    private int angle;

    private int blockHorizontalNum;

    private int speed;
    //</editor-fold>

    //<editor-fold desc="初始方法">
    public FunGameHitBlockHeader(Context context) {
        this(context, null);
    }

    public FunGameHitBlockHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FunGameHitBlockHeader(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FunGameHitBlockHeader);
        speed = ta.getInt(R.styleable.FunGameHitBlockHeader_fghBallSpeed, DensityUtil.dp2px(SPEED));
        blockHorizontalNum = ta.getInt(R.styleable.FunGameHitBlockHeader_fghBlockHorizontalNum, BLOCK_HORIZONTAL_NUM);
        ta.recycle();

        blockPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        blockPaint.setStyle(Paint.Style.FILL);
        BALL_RADIUS = DensityUtil.dp2px(4);
    }

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int extendHeight) {
        final int measuredWidth = getMeasuredWidth();
        blockHeight = height / BLOCK_VERTICAL_NUM - DIVIDING_LINE_SIZE;
        blockWidth = measuredWidth * BLOCK_WIDTH_RATIO;

        blockLeft = measuredWidth * BLOCK_POSITION_RATIO;
        racketLeft = measuredWidth * RACKET_POSITION_RATIO;

        controllerSize = (int) (blockHeight * 1.6f);
        super.onInitialized(kernel, height, extendHeight);
    }
    //</editor-fold>

    //<editor-fold desc="游戏控制">
    protected void resetConfigParams() {
        cx = racketLeft - 3 * BALL_RADIUS;
        cy = (int) (mHeaderHeight * .5f);

        controllerPosition = DIVIDING_LINE_SIZE;

        angle = DEFAULT_ANGLE;

        isleft = true;

        if (pointList == null) {
            pointList = new ArrayList<>();
        } else {
            pointList.clear();
        }
    }
    /**
     * 检查小球是否撞击到挡板
     *
     * @param y 小球当前坐标Y值
     * @return 小球位于挡板Y值区域范围内：true，反之：false
     */
    private boolean checkTouchRacket(float y) {
        boolean flag = false;
        float diffVal = y - controllerPosition;
        if (diffVal >= 0 && diffVal <= controllerSize) { // 小球位于挡板Y值区域范围内
            flag = true;
        }
        return flag;
    }

    /**
     * 检查小球是否撞击到矩形块
     *
     * @param x 小球坐标X值
     * @param y 小球坐标Y值
     * @return 撞击到：true，反之：false
     */
    private boolean checkTouchBlock(float x, float y) {
        int columnX = (int) ((x - blockLeft - BALL_RADIUS - speed) / blockWidth);
        columnX = columnX == blockHorizontalNum ? columnX - 1 : columnX;
        int rowY = (int) (y / blockHeight);
        rowY = rowY == BLOCK_VERTICAL_NUM ? rowY - 1 : rowY;
        Point p = new Point();
        p.set(columnX, rowY);

        boolean flag = false;
        for (Point point : pointList) {
            if (point.equals(p.x, p.y)) {
                flag = true;
                break;
            }
        }

        if (!flag) {
            pointList.add(p);
        }
        return !flag;
    }
    //</editor-fold>

    //<editor-fold desc="绘制方法">
    @Override
    protected void drawGame(Canvas canvas, int width, int height) {
        drawColorBlock(canvas);
        drawRacket(canvas);
        if (status == STATUS_GAME_PLAY
                || status == STATUS_GAME_FINISHED
                || status == STATUS_GAME_FAIL
                || isInEditMode()) {
            drawBallPath(canvas, width);
        }
    }

    /**
     * 绘制挡板
     *
     * @param canvas 默认画布
     */
    private void drawRacket(Canvas canvas) {
        mPaint.setColor(rModelColor);
        canvas.drawRect(racketLeft, controllerPosition, racketLeft + blockWidth, controllerPosition + controllerSize, mPaint);
    }

    /**
     * 绘制并处理小球运动的轨迹
     *
     * @param canvas 默认画布
     * @param width  视图宽度
     */
    private void drawBallPath(Canvas canvas, int width) {
        mPaint.setColor(mModelColor);

        if (cx <= blockLeft + blockHorizontalNum * blockWidth + (blockHorizontalNum - 1) * DIVIDING_LINE_SIZE + BALL_RADIUS) { // 小球进入到色块区域
            if (checkTouchBlock(cx, cy)) { // 反弹回来
                isleft = false;
            }
        }
        if (cx <= blockLeft + BALL_RADIUS) { // 小球穿过色块区域
            isleft = false;
        }

        if (cx + BALL_RADIUS >= racketLeft && cx - BALL_RADIUS < racketLeft + blockWidth) { //小球当前坐标X值在挡板X值区域范围内
            if (checkTouchRacket(cy)) { // 小球与挡板接触
                if (pointList.size() == blockHorizontalNum * BLOCK_VERTICAL_NUM) { // 矩形块全部被消灭，游戏结束
                    status = STATUS_GAME_OVER;
                    return;
                }
                isleft = true;
            }
        } else if (cx > width) { // 小球超出挡板区域
            status = STATUS_GAME_OVER;
        }

        if (cy <= BALL_RADIUS + DIVIDING_LINE_SIZE) { // 小球撞到上边界
            angle = 180 - DEFAULT_ANGLE;
        } else if (cy >= mHeaderHeight - BALL_RADIUS - DIVIDING_LINE_SIZE) { // 小球撞到下边界
            angle = 180 + DEFAULT_ANGLE;
        }

        if (isleft) {
            cx -= speed;
        } else {
            cx += speed;
        }
        cy -= (float) Math.tan(Math.toRadians(angle)) * speed;

        canvas.drawCircle(cx, cy, BALL_RADIUS, mPaint);

        invalidate();

    }

    /**
     * 绘制矩形色块
     *
     * @param canvas 默认画布
     */
    private void drawColorBlock(Canvas canvas) {
        float left, top;
        int column, row;
        for (int i = 0; i < blockHorizontalNum * BLOCK_VERTICAL_NUM; i++) {
            row = i / blockHorizontalNum;
            column = i % blockHorizontalNum;

            boolean flag = false;
            for (Point point : pointList) {
                if (point.equals(column, row)) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                continue;
            }

            blockPaint.setColor(ColorUtils.setAlphaComponent(lModelColor, 255 / (column + 1)));

            left = blockLeft + column * (blockWidth + DIVIDING_LINE_SIZE);
            top = DIVIDING_LINE_SIZE + row * (blockHeight + DIVIDING_LINE_SIZE);
            canvas.drawRect(left, top, left + blockWidth, top + blockHeight, blockPaint);
        }
    }
    //</editor-fold>

}
