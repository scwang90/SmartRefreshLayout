package com.scwang.smartrefresh.header.fungame;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.scwang.smartrefresh.header.R;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.util.ColorUtils;
import com.scwang.smartrefresh.layout.util.DensityUtil;


/**
 * Created by Hitomis on 2016/3/9.
 * email:196425254@qq.com
 */
public abstract class FunGameView extends FunGameHeader {

    //<editor-fold desc="Field">
    protected static final int STATUS_GAME_PREPAR = 0;

    protected static final int STATUS_GAME_PLAY = 1;

    protected static final int STATUS_GAME_OVER = 2;

    protected static final int STATUS_GAME_FINISHED = 3;

    protected static final int STATUS_GAME_FAIL = 4;

    /**
     * 控件高度占屏幕高度比率
     */
    protected static final float VIEW_HEIGHT_RATIO = .161f;

    public static String textGameOver = "游戏结束";
    public static String textLoading = "玩个游戏解解闷";
    public static String textLoadingFinished = "刷新完成";
    public static String textLoadingFail = "刷新失败";
//    private String loadingText = "Loading...";
//    private String loadingFinishedText = "Loading Finished";
//    private String gameOverText = "Game Over";

    protected Paint mPaint;

    protected TextPaint textPaint;

    protected float controllerPosition;

    protected int controllerSize;

    protected int status = STATUS_GAME_PREPAR;

    protected int lModelColor, rModelColor, mModelColor;
    protected int mBackColor, mBoundaryColor = 0xff606060;
    //</editor-fold>

    public FunGameView(Context context) {
        super(context);
        this.initView(context, null);
    }

    public FunGameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initView(context, attrs);
    }

    public FunGameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView(context, attrs);
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public FunGameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FunGameView);
        mBackColor = ta.getColor(R.styleable.FunGameView_fgvBackColor, 0);
        lModelColor = ta.getColor(R.styleable.FunGameView_fgvLeftColor, Color.rgb(0, 0, 0));
        mModelColor = ta.getColor(R.styleable.FunGameView_fgvMiddleColor, Color.BLACK);
        rModelColor = ta.getColor(R.styleable.FunGameView_fgvRightColor, Color.parseColor("#A5A5A5"));
        if (ta.hasValue(R.styleable.FunGameView_fgvTextGameOver)) {
            textGameOver = ta.getString(R.styleable.FunGameView_fgvTextGameOver);
        }
        if (ta.hasValue(R.styleable.FunGameView_fgvTextGameOver)) {
            textLoading = ta.getString(R.styleable.FunGameView_fgvTextLoading);
        }
        if (ta.hasValue(R.styleable.FunGameView_fgvTextGameOver)) {
            textLoadingFinished = ta.getString(R.styleable.FunGameView_fgvTextLoadingFinished);
        }
        ta.recycle();

        initBaseTools();
        initBaseConfigParams();
        initConcreteView();
    }

    protected void initBaseTools() {
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.parseColor("#C1C2C2"));

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(DIVIDING_LINE_SIZE);
    }

    protected void initBaseConfigParams() {
        controllerPosition = DIVIDING_LINE_SIZE;
    }

    protected abstract void initConcreteView();

    protected abstract void drawGame(Canvas canvas, int width, int height);

    protected abstract void resetConfigParams();

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
     * 绘制文字内容
     * @param canvas 默认画布
     */
    private void drawText(Canvas canvas, int width, int height) {
        switch (status) {
            case STATUS_GAME_PREPAR:
            case STATUS_GAME_PLAY:
                textPaint.setTextSize(DensityUtil.dp2px(25));
                promptText(canvas, textLoading, width, height);
                break;
            case STATUS_GAME_FINISHED:
                textPaint.setTextSize(DensityUtil.dp2px(20));
                promptText(canvas, textLoadingFinished, width, height);
                break;
            case STATUS_GAME_FAIL:
                textPaint.setTextSize(DensityUtil.dp2px(20));
                promptText(canvas, textLoadingFail, width, height);
                break;
            case STATUS_GAME_OVER:
                textPaint.setTextSize(DensityUtil.dp2px(25));
                promptText(canvas, textGameOver, width, height);
                break;
        }
    }

    /**
     * 提示文字信息
     * @param canvas 默认画布
     * @param text 相关文字字符串
     */
    private void promptText(Canvas canvas, String text, int width, int height) {
        float textX = (width - textPaint.measureText(text)) * .5f;
        float textY = height  * .5f - (textPaint.ascent() + textPaint.descent()) * .5f;
        canvas.drawText(text, textX, textY, textPaint);
    }


    /**
     * 获取当前控件状态
     */
    public int getCurrStatus() {
        return status;
    }

    public String getTextGameOver() {
        return textGameOver;
    }

    public void setTextGameOver(String textGameOver) {
        this.textGameOver = textGameOver;
    }

    public String getTextLoading() {
        return textLoading;
    }

    public void setTextLoading(String textLoading) {
        this.textLoading = textLoading;
    }

    public String getTextLoadingFinished() {
        return textLoadingFinished;
    }

    public void setTextLoadingFinished(String textLoadingFinished) {
        this.textLoadingFinished = textLoadingFinished;
    }

    /**
     * 获取屏幕尺寸
     *
     * @param context context
     * @return 手机屏幕尺寸
     */
    private DisplayMetrics getScreenMetrics(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        return dm;
    }


    //<editor-fold desc="控制方法">


    @Override
    protected void onGameStart() {
        postStatus(FunGameView.STATUS_GAME_PLAY);
    }

    @Override
    protected void onManualOperationMove(float percent, int offset, int headHeight, int extendHeight) {
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

        if (status == STATUS_GAME_PREPAR) {
            resetConfigParams();
        }

        postInvalidate();
    }
    //</editor-fold>

    //<editor-fold desc="RefreshHeader">
    @Override
    public void onInitialized(RefreshKernel kernel, int height, int extendHeight) {
        super.onInitialized(kernel, height, extendHeight);
        initConcreteView();
        postStatus(STATUS_GAME_PREPAR);
    }

    @Override
    public int onFinish(RefreshLayout layout, boolean success) {
        if (mManualOperation) {
            postStatus(success ? FunGameView.STATUS_GAME_FINISHED : FunGameView.STATUS_GAME_FAIL);
        } else {
            postStatus(FunGameView.STATUS_GAME_PREPAR);
        }
        return super.onFinish(layout, success);
    }

    @Override@Deprecated
    public void setPrimaryColors(int... colors) {
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
                textPaint.setColor(ColorUtils.setAlphaComponent(colors[1], 150));
            }
        }
    }

    //</editor-fold>


}
