package com.scwang.smartrefresh.header;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.SparseArray;

import com.scwang.smartrefresh.header.fungame.FunGameView;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 * Created by Hitomis on 2016/3/09.
 * email:196425254@qq.com
 * from https://github.com/Hitomis/FunGameRefresh
 */
public class FunGameBattleCityHeader extends FunGameView {

    //<editor-fold desc="属性变量">
    /**
     * 轨道数量
     */
    private static int TANK_ROW_NUM = 3;

    /**
     * 炮管尺寸所在tank尺寸的比率
     */
    private static final float TANK_BARREL_RATIO = 1/3.f;

    /**
     * 默认子弹之间空隙间距
     */
    private static final int DEFAULT_BULLET_NUM_SPACING = 360;

    /**
     * 默认敌方坦克之间间距
     */
    private static final int DEFAULT_ENEMY_TANK_NUM_SPACING = 60;

    /**
     * 表示运行漏掉的敌方坦克总数量 和 升级后消灭坦克总数量的增量
     */
    private static final int DEFAULT_TANK_MAGIC_TOTAL_NUM = 8;

    /**
     * 所有轨道上敌方坦克矩阵集合
     */
    private SparseArray<Queue<RectF>> eTankSparseArray;

    /**
     * 屏幕上所有子弹坐标点集合
     */
    private Queue<Point> mBulletList;

    /**
     * 击中敌方坦克的子弹坐标点
     */
    private Point usedBullet;

    /**
     * 用于随机定位一个轨道下标值
     */
    private Random random;

    /**
     * 子弹半径
     */
    private float bulletRadius;

    /**
     * 敌方坦克间距、子弹间距
     */
    private int enemyTankSpace, bulletSpace;

    /**
     * 炮筒尺寸
     */
    private int barrelSize;

    /**
     * 敌方坦克速度、子弹速度
     */
    private int enemySpeed = 1, bulletSpeed = 4;

    /**
     * 当前前一辆敌方坦克和后一辆已经存在的间距值
     * 用于确定是否要派出新的一辆敌方坦克
     */
    private int offsetETankX;

    /**
     * 当前前一颗子弹和后一颗子弹的间距值
     * 用于确定是否要发射新的一颗子弹
     */
    private int  offsetMBulletX;

    /**
     * 当前漏掉的坦克数量
     */
    private int overstepNum;

    /**
     * 当前难度等级需要消灭坦克数量
     */
    private int levelNum;

    /**
     * 当前难度等级内消灭的敌方坦克数量
     */
    private int wipeOutNum;

    /**
     * 表示第一次标示值，用于添加第一辆敌方坦克逻辑
     */
    private boolean once = true;
    //</editor-fold>

    //<editor-fold desc="初始方法">
    public FunGameBattleCityHeader(Context context) {
        this(context, null);
    }

    public FunGameBattleCityHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FunGameBattleCityHeader(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        random = new Random();
    }

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int extendHeight) {
        controllerSize = height / TANK_ROW_NUM;
        barrelSize = (int) Math.floor(controllerSize * TANK_BARREL_RATIO + .5f);
        bulletRadius = (barrelSize - 2 * DIVIDING_LINE_SIZE) * .5f;
        super.onInitialized(kernel, height, extendHeight);
    }

    //</editor-fold>

    //<editor-fold desc="游戏控制">

    protected void resetConfigParams() {
        status = FunGameView.STATUS_GAME_PREPARE;
        controllerPosition = DIVIDING_LINE_SIZE;

        enemySpeed = DensityUtil.dp2px(1);
        bulletSpeed = DensityUtil.dp2px(4);

        levelNum = DEFAULT_TANK_MAGIC_TOTAL_NUM;
        wipeOutNum = 0;

        once = true;

        enemyTankSpace = controllerSize + barrelSize + DEFAULT_ENEMY_TANK_NUM_SPACING;
        bulletSpace = DEFAULT_BULLET_NUM_SPACING;

        eTankSparseArray = new SparseArray<>();
        for (int i = 0; i < TANK_ROW_NUM; i++) {
            Queue<RectF> rectFQueue = new LinkedList<>();
            eTankSparseArray.put(i, rectFQueue);
        }

        mBulletList = new LinkedList<>();
    }

    /**
     * 由index轨道下标从左边起始位置生成一个用于绘制敌方坦克的Rect
     * @param index 轨道下标
     * @return 敌方坦克矩阵
     */
    private RectF generateEnemyTank(int index) {
        float left = - (controllerSize + barrelSize);
        float top = index * (controllerSize) + DIVIDING_LINE_SIZE;
        return new RectF(left, top, left + barrelSize * 2.5f, top + controllerSize);
    }

    /**
     * 由Y坐标获取该坐标所在轨道的下标
     * @param y 坐标Y值
     * @return 轨道下标
     */
    private int getTrackIndex(int y) {
        int index = y / (mHeaderHeight / TANK_ROW_NUM);
        index = index >= TANK_ROW_NUM ? TANK_ROW_NUM - 1 : index;
        index = index < 0 ? 0 : index;
        return  index;
    }

    /**
     * 判断是否消灭敌方坦克
     * @param point 单签子弹坐标点
     * @return 消灭：true, 反之：false
     */
    private boolean checkWipeOutETank(Point point) {
        boolean beHit = false;
        int trackIndex = getTrackIndex(point.y);
        RectF rectF = eTankSparseArray.get(trackIndex).peek();
        if (rectF != null && rectF.contains(point.x, point.y)) { // 击中
            if (++wipeOutNum == levelNum) {
                upLevel();
            }
            eTankSparseArray.get(trackIndex).poll();
            beHit = true;
        }
        return beHit;
    }

    /**
     * 难度升级
     */
    private void upLevel() {
        levelNum += DEFAULT_TANK_MAGIC_TOTAL_NUM;
        enemySpeed += DensityUtil.dp2px(1);
        bulletSpeed += DensityUtil.dp2px(1);
        wipeOutNum = 0;

        if (enemyTankSpace > 12)
        enemyTankSpace -= 12;

        if (bulletSpace > 30)
        bulletSpace -= 30;
    }

    /**
     * 判断我方坦克是否与敌方坦克相撞
     * @param index 轨道下标
     * @param selfX 我方坦克所在坐标X值
     * @param selfY 我方坦克矩阵的top 或者 bottom 值
     * @return true：相撞，反之：false
     */
    private boolean checkTankCrash(int index, float selfX, float selfY) {
        boolean isCrash = false;
        RectF rectF = eTankSparseArray.get(index).peek();
        if (rectF != null && rectF.contains(selfX, selfY)) {
            isCrash = true;
        }
        return isCrash;
    }

    /**
     * 随机定位一个轨道下标值
     * @return 轨道下标
     */
    private int appearanceOption() {
        return random.nextInt(TANK_ROW_NUM);
    }
    //</editor-fold>

    //<editor-fold desc="绘制方法">

    @Override
    protected void drawGame(Canvas canvas, int width, int height) {
        drawSelfTank(canvas,width);
        if (status == STATUS_GAME_PLAY || status == STATUS_GAME_FINISHED || status == STATUS_GAME_FAIL) {
            drawEnemyTank(canvas,width);
            drawBulletPath(canvas,width);
        }
        if (isInEditMode()) {
            drawTank(canvas, new RectF(controllerSize, 0, controllerSize * 2, controllerSize));
            drawTank(canvas, new RectF(0, controllerSize, controllerSize, controllerSize*2));
            drawTank(canvas, new RectF(controllerSize * 3, controllerSize * 2, controllerSize * 4, controllerSize*3));
        }
    }

    /**
     * 绘制子弹路径
     * @param canvas 默认画布
     */
    private void drawBulletPath(Canvas canvas, int width) {
        mPaint.setColor(mModelColor);
        offsetMBulletX += bulletSpeed;
        if (offsetMBulletX / bulletSpace == 1) {
            offsetMBulletX = 0;
        }

        if (offsetMBulletX == 0) {
            Point bulletPoint = new Point();
            bulletPoint.x = width - controllerSize - barrelSize;
            bulletPoint.y = (int) (controllerPosition + controllerSize * .5f);
            mBulletList.offer(bulletPoint);
        }

        boolean isOverStep = false;
        for (Point point : mBulletList) {
            if (checkWipeOutETank(point)) {
                usedBullet = point;
                continue;
            }
            if (point.x + bulletRadius <= 0) {
                isOverStep = true;
            }
            drawBullet(canvas, point);
        }

        if (isOverStep) {
            mBulletList.poll();
        }

        mBulletList.remove(usedBullet);
        usedBullet = null;
    }

    /**
     * 绘制子弹
     * @param canvas 默认画布
     * @param point 子弹圆心坐标点
     */
    private void drawBullet(Canvas canvas, Point point) {
        point.x -= bulletSpeed;
        canvas.drawCircle(point.x, point.y, bulletRadius, mPaint);
    }

    /**
     * 绘制我方坦克
     * @param canvas 默认画布
     */
    private void drawSelfTank(Canvas canvas, int width) {
        mPaint.setColor(rModelColor);
        boolean isAboveCrash = checkTankCrash(getTrackIndex((int) controllerPosition),
                width - controllerSize,
                controllerPosition);
        boolean isBelowCrash = checkTankCrash(getTrackIndex((int) (controllerPosition + controllerSize)),
                width - controllerSize,
                controllerPosition + controllerSize);

        if (isAboveCrash || isBelowCrash) {
            status = STATUS_GAME_OVER;
        }

        canvas.drawRect(width - controllerSize, controllerPosition + DIVIDING_LINE_SIZE,
                width, controllerPosition + controllerSize + DIVIDING_LINE_SIZE,
                mPaint);
        canvas.drawRect(width - controllerSize - barrelSize,
                controllerPosition + (controllerSize - barrelSize) * .5f,
                width - controllerSize,
                controllerPosition + (controllerSize - barrelSize) * .5f + barrelSize,
                mPaint);
    }

    /**
     * 绘制三条轨道上的敌方坦克
     * @param canvas 默认画布
     */
    private void drawEnemyTank(Canvas canvas, int width) {
        mPaint.setColor(lModelColor);
        offsetETankX += enemySpeed;
        if (offsetETankX / enemyTankSpace == 1 || once) {
            offsetETankX = 0;
            once = false;
        }

        boolean isOverstep = false;
        int option = appearanceOption();
        for (int i = 0; i < TANK_ROW_NUM; i++) {
            Queue<RectF> rectFQueue = eTankSparseArray.get(i);

            if (offsetETankX == 0 && i == option) {
                rectFQueue.offer(generateEnemyTank(i));
            }

            for (RectF rectF : rectFQueue) {
                if (rectF.left >= width) {
                    isOverstep = true;
                    if (++overstepNum >= DEFAULT_TANK_MAGIC_TOTAL_NUM) {
                        status = STATUS_GAME_OVER;
                        break;
                    }
                    continue;
                }
                drawTank(canvas, rectF);
            }

            if (status == STATUS_GAME_OVER) break;
            if (isOverstep) {
                rectFQueue.poll();
                isOverstep = false;
            }
        }
        invalidate();
    }

    /**
     * 绘制一辆敌方坦克
     * @param canvas 默认画布
     * @param rectF 坦克矩阵
     */
    private void drawTank(Canvas canvas, RectF rectF) {
        rectF.set(rectF.left + enemySpeed, rectF.top, rectF.right + enemySpeed, rectF.bottom);
        canvas.drawRect(rectF, mPaint);
        float barrelTop = rectF.top + (controllerSize - barrelSize) * .5f;
        canvas.drawRect(rectF.right, barrelTop, rectF.right + barrelSize, barrelTop + barrelSize, mPaint);

    }

    //</editor-fold>

}
