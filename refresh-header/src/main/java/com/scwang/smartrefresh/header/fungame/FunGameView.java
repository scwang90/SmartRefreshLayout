package com.scwang.smartrefresh.header.fungame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.graphics.ColorUtils;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.header.R;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;


/**
 * Created by Hitomis on 2016/3/9.
 * email:196425254@qq.com
 * https://github.com/Hitomis/FunGameRefresh
 */
@SuppressWarnings("unused")
public abstract class FunGameView<T extends FunGameView> extends FunGameBase {

    //<editor-fold desc="Field - Curtain">
    /**
     * 分割线默认宽度大小
     */
    protected float DIVIDING_LINE_SIZE = 1.f;

    protected View mShadowView;
    protected TextView mMaskViewTop;
    protected TextView mMaskViewBottom;

    public String mMaskTextBottom;
    public String mMaskTextTopPull;
    public String mMaskTextTopRelease;

    protected int mHalfHeaderHeight;

    //</editor-fold>

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
    public String mTextLoadingFinish;
    public String mTextLoadingFailed;

    protected Paint mPaint;
    protected Paint mPaintText;

    protected float controllerPosition;

    protected int controllerSize;

    protected int status = STATUS_GAME_PREPARE;

    protected int lModelColor, rModelColor, mModelColor;
    protected int mBackColor, mBoundaryColor = 0xff606060;
    //</editor-fold>

    public FunGameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final View thisView = this;
        final ViewGroup thisGroup = this;
        final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FunGameView);

        //<editor-fold desc="init - Curtain">

        mMaskTextBottom = thisView.getResources().getString(R.string.fgh_mask_bottom);//"拖动控制游戏";//"Scroll to move handle";
        mMaskTextTopPull = thisView.getResources().getString(R.string.fgh_mask_top_pull);//"下拉即将展开";//"Pull To Break Out!";
        mMaskTextTopRelease = thisView.getResources().getString(R.string.fgh_mask_top_release);//"放手即将展开";//"Release To Break Out!";
        if (ta.hasValue(R.styleable.FunGameView_fghMaskTextTop)) {
            mMaskTextTopPull = mMaskTextTopRelease = ta.getString(R.styleable.FunGameView_fghMaskTextTop);
        }
        if (ta.hasValue(R.styleable.FunGameView_fghMaskTextTopPull)) {
            mMaskTextTopPull = ta.getString(R.styleable.FunGameView_fghMaskTextTopPull);
        }
        if (ta.hasValue(R.styleable.FunGameView_fghMaskTextTopRelease)) {
            mMaskTextTopRelease = ta.getString(R.styleable.FunGameView_fghMaskTextTopRelease);
        }
        if (ta.hasValue(R.styleable.FunGameView_fghMaskTextBottom)) {
            mMaskTextBottom = ta.getString(R.styleable.FunGameView_fghMaskTextBottom);
        }

        DisplayMetrics metrics = thisView.getResources().getDisplayMetrics();
        int maskTextSizeTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, metrics);
        int maskTextSizeBottom = maskTextSizeTop * 14 / 16;

        maskTextSizeTop = ta.getDimensionPixelSize(R.styleable.FunGameView_fghMaskTextSizeTop, maskTextSizeTop);
        maskTextSizeBottom = ta.getDimensionPixelSize(R.styleable.FunGameView_fghMaskTextSizeBottom, maskTextSizeBottom);

        ViewGroup curtainLayout = new RelativeLayout(context);
        mShadowView = new RelativeLayout(context);
        mShadowView.setBackgroundColor(0xFF3A3A3A);

        mMaskViewTop = createMaskView(context, mMaskTextTopPull, maskTextSizeTop, Gravity.BOTTOM);
        mMaskViewBottom = createMaskView(context, mMaskTextBottom, maskTextSizeBottom, Gravity.TOP);

        if (!thisView.isInEditMode()) {
            int height = DensityUtil.dp2px(100);
            LayoutParams maskLp = new LayoutParams(MATCH_PARENT, height);
//            maskLp.topMargin = (int) FunGameView.DIVIDING_LINE_SIZE;
//            maskLp.bottomMargin = (int) FunGameView.DIVIDING_LINE_SIZE;
            thisGroup.addView(mShadowView, maskLp);
            thisGroup.addView(curtainLayout, maskLp);

            mHalfHeaderHeight = (int) ((height/* - 2 * DIVIDING_LINE_SIZE*/) * .5f);
            LayoutParams lpTop = new LayoutParams(MATCH_PARENT, mHalfHeaderHeight);
            LayoutParams lpBottom = new LayoutParams(MATCH_PARENT, mHalfHeaderHeight);
            lpBottom.topMargin = height - mHalfHeaderHeight;
            curtainLayout.addView(mMaskViewTop, lpTop);
            curtainLayout.addView(mMaskViewBottom, lpBottom);
        }

        //</editor-fold>

        //<editor-fold desc="init - Arena">
        DIVIDING_LINE_SIZE = Math.max(1, DensityUtil.dp2px(0.5f));

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(DIVIDING_LINE_SIZE);
        controllerPosition = DIVIDING_LINE_SIZE;

        mPaintText = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mPaintText.setColor(0xFFC1C2C2);

        mTextGameOver = context.getString(R.string.fgh_text_game_over);
        mTextLoading = context.getString(R.string.fgh_text_loading);
        mTextLoadingFinish = context.getString(R.string.fgh_text_loading_finish);
        mTextLoadingFailed = context.getString(R.string.fgh_text_loading_failed);

        mBackColor = ta.getColor(R.styleable.FunGameView_fghBackColor, 0);
        lModelColor = ta.getColor(R.styleable.FunGameView_fghLeftColor, Color.BLACK);
        mModelColor = ta.getColor(R.styleable.FunGameView_fghMiddleColor, Color.BLACK);
        rModelColor = ta.getColor(R.styleable.FunGameView_fghRightColor, 0xFFA5A5A5);

        if (ta.hasValue(R.styleable.FunGameView_fghTextGameOver)) {
            mTextGameOver = ta.getString(R.styleable.FunGameView_fghTextGameOver);
        }
        if (ta.hasValue(R.styleable.FunGameView_fghTextLoading)) {
            mTextLoading = ta.getString(R.styleable.FunGameView_fghTextLoading);
        }
        if (ta.hasValue(R.styleable.FunGameView_fghTextLoadingFinished)) {
            mTextLoadingFinish = ta.getString(R.styleable.FunGameView_fghTextLoadingFinished);
        }
        if (ta.hasValue(R.styleable.FunGameView_fghTextLoadingFailed)) {
            mTextLoadingFailed = ta.getString(R.styleable.FunGameView_fghTextLoadingFailed);
        }
        //</editor-fold>

        ta.recycle();
    }

    protected TextView createMaskView(Context context, String text, int textSize, int gravity) {
        final TextView maskView = new TextView(context);
        maskView.setTextColor(Color.BLACK);
        maskView.setGravity(gravity | Gravity.CENTER_HORIZONTAL);
        maskView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        maskView.setText(text);
        //noinspection UnnecessaryLocalVariable
        final View view = maskView;
        view.setBackgroundColor(Color.WHITE);
        return maskView;
    }

    //<editor-fold desc="绘制方法">

    protected abstract void drawGame(Canvas canvas, int width, int height);

    @Override
    protected void dispatchDraw(Canvas canvas) {
        final View thisView = this;
        final int width = thisView.getWidth();
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
                width, height - DIVIDING_LINE_SIZE, mPaint);
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
                promptText(canvas, mTextLoadingFinish, width, height);
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

    protected abstract void resetConfigParams();

    /**
     * 更新当前控件状态
     * @param status 状态码
     */
    public void postStatus(int status) {
        this.status = status;

        if (status == STATUS_GAME_PREPARE) {
            resetConfigParams();
        }
        final View thisView = this;
        thisView.postInvalidate();
    }

    /**
     * 移动控制器（控制器对象为具体控件中的右边图像模型）
     */
    @Override
    protected void onManualOperationMove(float percent, int offset, int height, int maxDragHeight) {
        final View thisView = this;
        float distance = Math.max(offset, 0);
        float maxDistance = (mHeaderHeight -  2 * DIVIDING_LINE_SIZE - controllerSize);
        if (distance > maxDistance) {
            distance = maxDistance;
        }
        controllerPosition = distance;
        thisView.postInvalidate();
    }

    //</editor-fold>

    //<editor-fold desc="生命周期">

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {
        final View thisView = this;
        if (mHeaderHeight != height && !thisView.isInEditMode()) {
            final View topView = mMaskViewTop;
            final View bottomView = mMaskViewBottom;
            mHalfHeaderHeight = (int) ((height/* - 2 * DIVIDING_LINE_SIZE*/) * .5f);
            LayoutParams lpTop = (LayoutParams)topView.getLayoutParams();
            LayoutParams lpBottom = (LayoutParams)bottomView.getLayoutParams();
            lpTop.height = lpBottom.height = mHalfHeaderHeight;
            lpBottom.topMargin = height - mHalfHeaderHeight;
            topView.setLayoutParams(lpTop);
            bottomView.setLayoutParams(lpBottom);
        }
        super.onInitialized(kernel, height, maxDragHeight);
        postStatus(STATUS_GAME_PREPARE);
    }

    @Override@Deprecated
    public void setPrimaryColors(@ColorInt int ... colors) {
        super.setPrimaryColors(colors);
        if (colors.length > 0) {
            mMaskViewTop.setTextColor(colors[0]);
            mMaskViewBottom.setTextColor(colors[0]);

            mBoundaryColor = mBackColor = colors[0];
            if (mBackColor == 0 || mBackColor == 0xffffffff) {
                mBoundaryColor = 0xff606060;
            }
            if (colors.length > 1) {
                final View topView = mMaskViewTop;
                final View bottomView = mMaskViewBottom;
                final View shadowView = mShadowView;
                shadowView.setBackgroundColor(colors[1]);
                topView.setBackgroundColor(colors[1]);
                bottomView.setBackgroundColor(colors[1]);

                mModelColor = colors[1];
                lModelColor = ColorUtils.setAlphaComponent(colors[1], 225);
                rModelColor = ColorUtils.setAlphaComponent(colors[1], 200);
                mPaintText.setColor(ColorUtils.setAlphaComponent(colors[1], 150));
            }
        }
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        super.onStateChanged(refreshLayout, oldState, newState);
        switch (newState) {
            case PullDownToRefresh:
                mMaskViewTop.setText(mMaskTextTopPull);
                break;
            case ReleaseToRefresh:
                mMaskViewTop.setText(mMaskTextTopRelease);
                break;
//            case ReleaseToTwoLevel:
//                mMaskViewTop.setText(com.scwang.smartrefresh.layout.R.string.srl_header_secondary);
//                break;
        }
    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout layout, int height, int maxDragHeight) {
        super.onStartAnimator(layout, height, maxDragHeight);
        final View topView = mMaskViewTop;
        final View shadowView = mShadowView;
        final View bottomView = mMaskViewBottom;
        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(ObjectAnimator.ofFloat(topView, "translationY", topView.getTranslationY(), -mHalfHeaderHeight))
                .with(ObjectAnimator.ofFloat(bottomView, "translationY", bottomView.getTranslationY(), mHalfHeaderHeight))
                .with(ObjectAnimator.ofFloat(shadowView, "alpha", shadowView.getAlpha(), 0));
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                topView.setVisibility(View.GONE);
                bottomView.setVisibility(View.GONE);
                shadowView.setVisibility(View.GONE);
                postStatus(FunGameView.STATUS_GAME_PLAY);
            }
        });
        animatorSet.setDuration(800);
        animatorSet.setStartDelay(200);
        animatorSet.start();
    }

    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {

        if (mManualOperation) {
            postStatus(success ? STATUS_GAME_FINISHED : STATUS_GAME_FAIL);
        } else {
            postStatus(STATUS_GAME_PREPARE);

            final View topView = mMaskViewTop;
            final View bottomView = mMaskViewBottom;
            final View shadowView = mShadowView;
            topView.setTranslationY(topView.getTranslationY() + mHalfHeaderHeight);
            bottomView.setTranslationY(bottomView.getTranslationY() - mHalfHeaderHeight);
            shadowView.setAlpha(1.f);

            topView.setVisibility(View.VISIBLE);
            bottomView.setVisibility(View.VISIBLE);
            shadowView.setVisibility(View.VISIBLE);
        }

        return super.onFinish(layout, success);
    }

    //</editor-fold>

}
