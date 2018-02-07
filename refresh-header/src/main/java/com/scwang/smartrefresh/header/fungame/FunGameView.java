package com.scwang.smartrefresh.header.fungame;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.ColorUtils;
import android.text.TextPaint;
import android.util.AttributeSet;

import com.scwang.smartrefresh.header.R;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.util.DensityUtil;


/**
 * Created by Hitomis on 2016/3/9.
 * email:196425254@qq.com
 * https://github.com/Hitomis/FunGameRefresh
 */
@SuppressWarnings("unused")
public abstract class FunGameView extends FunGameHeader {

    //<editor-fold desc="Field - Arena">
    protected static final int STATUS_GAME_PREPARE = 0;
    protected static final int STATUS_GAME_PLAY = 1;
    protected static final int STATUS_GAME_OVER = 2;
    protected static final int STATUS_GAME_FINISHED = 3;
    protected static final int STATUS_GAME_FAIL = 4;

    /**
     * 控件高度占屏幕高度比率
     */
    protected static final float VIEW_HEIGHT_RATIO = .161f;

    public String mTextGameOver;
    public String mTextLoading;
    public String mTextLoadingFinished;
    public String mTextLoadingFailed;
//    private String loadingText = "Loading...";
//    private String loadingFinishedText = "Loading Finished";
//    private String gameOverText = "Game Over";

    protected Paint mPaint;

    protected TextPaint mPaintText;

    protected float controllerPosition;

    protected int controllerSize;

    protected int status = STATUS_GAME_PREPARE;

    protected int lModelColor, rModelColor, mModelColor;
    protected int mBackColor, mBoundaryColor = 0xff606060;
    //</editor-fold>

    public FunGameView(Context context) {
        this(context, null);
    }

    public FunGameView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FunGameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FunGameView);

        //<editor-fold desc="init - Arena">
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(DIVIDING_LINE_SIZE);
        controllerPosition = DIVIDING_LINE_SIZE;

        mPaintText = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mPaintText.setColor(0xFFC1C2C2);

        mTextGameOver = context.getString(R.string.fgh_mask_bottom);
        mTextLoading = context.getString(R.string.fgh_mask_bottom);
        mTextLoadingFinished = context.getString(R.string.fgh_mask_bottom);
        mTextLoadingFailed = context.getString(R.string.fgh_mask_bottom);

        mBackColor = ta.getColor(R.styleable.FunGameView_fgvBackColor, 0);
        lModelColor = ta.getColor(R.styleable.FunGameView_fgvLeftColor, Color.rgb(0, 0, 0));
        mModelColor = ta.getColor(R.styleable.FunGameView_fgvMiddleColor, Color.BLACK);
        rModelColor = ta.getColor(R.styleable.FunGameView_fgvRightColor, 0xFFA5A5A5);

        if (ta.hasValue(R.styleable.FunGameView_fgvTextGameOver)) {
            mTextGameOver = ta.getString(R.styleable.FunGameView_fgvTextGameOver);
        }
        if (ta.hasValue(R.styleable.FunGameView_fgvTextLoading)) {
            mTextLoading = ta.getString(R.styleable.FunGameView_fgvTextLoading);
        }
        if (ta.hasValue(R.styleable.FunGameView_fgvTextLoadingFinished)) {
            mTextLoadingFinished = ta.getString(R.styleable.FunGameView_fgvTextLoadingFinished);
        }
        if (ta.hasValue(R.styleable.FunGameView_fgvTextLoadingFailed)) {
            mTextLoadingFailed = ta.getString(R.styleable.FunGameView_fgvTextLoadingFailed);
        }
        //</editor-fold>

        ta.recycle();
    }

    //<editor-fold desc="子类接口">
    protected abstract void resetConfigParams();

    protected abstract void drawGame(Canvas canvas, int width, int height);
    //</editor-fold>

    //<editor-fold desc="绘制方法">
    @Override
    protected void dispatchDraw(Canvas canvas) {
        final int width = getWidth();
        final int height = mHeaderHeight;
        drawBoundary(canvas, width, height);
        drawText(canvas, width, height);
        drawGame(canvas, width, height);
        super.dispatchDraw(canvas);
    }

    /**
     * 绘制分割线
     * @param canvas 默认画布
     */
    private void drawBoundary(Canvas canvas,int width,int height) {
        mPaint.setColor(mBackColor);
        canvas.drawRect(0, 0, width, height, mPaint);
        mPaint.setColor(mBoundaryColor);
        canvas.drawLine(0, 0, width, 0, mPaint);
        canvas.drawLine(0, height - DIVIDING_LINE_SIZE,
                width, height - DIVIDING_LINE_SIZE,
                mPaint);
    }

    /**
     * 绘制文字内容
     * @param canvas 默认画布
     */
    private void drawText(Canvas canvas, int width, int height) {
        switch (status) {
            case STATUS_GAME_PREPARE:
            case STATUS_GAME_PLAY:
                mPaintText.setTextSize(DensityUtil.dp2px(25));
                promptText(canvas, mTextLoading, width, height);
                break;
            case STATUS_GAME_FINISHED:
                mPaintText.setTextSize(DensityUtil.dp2px(20));
                promptText(canvas, mTextLoadingFinished, width, height);
                break;
            case STATUS_GAME_FAIL:
                mPaintText.setTextSize(DensityUtil.dp2px(20));
                promptText(canvas, mTextLoadingFailed, width, height);
                break;
            case STATUS_GAME_OVER:
                mPaintText.setTextSize(DensityUtil.dp2px(25));
                promptText(canvas, mTextGameOver, width, height);
                break;
        }
    }

    /**
     * 提示文字信息
     * @param canvas 默认画布
     * @param text 相关文字字符串
     */
    private void promptText(Canvas canvas, String text, int width, int height) {
        float textX = (width - mPaintText.measureText(text)) * .5f;
        float textY = height  * .5f - (mPaintText.ascent() + mPaintText.descent()) * .5f;
        canvas.drawText(text, textX, textY, mPaintText);
    }
    //</editor-fold>

    //<editor-fold desc="控制方法">


    @Override
    protected void onGameStart() {
        postStatus(FunGameView.STATUS_GAME_PLAY);
    }

    @Override
    protected void onManualOperationMove(float percent, int offset, int height, int extendHeight) {
        moveController(Math.max(offset, 0));
    }
    /**
     * 移动控制器（控制器对象为具体控件中的右边图像模型）
     * @param distance 移动的距离
     */
    public void moveController(float distance) {
        float maxDistance = (mHeaderHeight -  2 * DIVIDING_LINE_SIZE - controllerSize);

        if (distance > maxDistance) {
            distance = maxDistance;
        }

        controllerPosition = distance;
        postInvalidate();
    }

    /**
     * 更新当前控件状态
     * @param status 状态码
     */
    public void postStatus(int status) {
        this.status = status;

        if (status == STATUS_GAME_PREPARE) {
            resetConfigParams();
        }

        postInvalidate();
    }
    //</editor-fold>

    //<editor-fold desc="RefreshHeader">
    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int extendHeight) {
        super.onInitialized(kernel, height, extendHeight);
        postStatus(STATUS_GAME_PREPARE);
    }

    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        if (mManualOperation) {
            postStatus(success ? FunGameView.STATUS_GAME_FINISHED : FunGameView.STATUS_GAME_FAIL);
        } else {
            postStatus(FunGameView.STATUS_GAME_PREPARE);
        }
        return super.onFinish(layout, success);
    }

    @Override@Deprecated
    public void setPrimaryColors(@ColorInt int ... colors) {
        super.setPrimaryColors(colors);
        if (colors.length > 0) {
            mBoundaryColor = mBackColor = colors[0];
            if (mBackColor == 0 || mBackColor == 0xffffffff) {
                mBoundaryColor = 0xff606060;
            }
            if (colors.length > 1) {
                mModelColor = colors[1];
                lModelColor = ColorUtils.setAlphaComponent(colors[1], 225);
                rModelColor = ColorUtils.setAlphaComponent(colors[1], 200);
                mPaintText.setColor(ColorUtils.setAlphaComponent(colors[1], 150));
            }
        }
    }

    //</editor-fold>


}
