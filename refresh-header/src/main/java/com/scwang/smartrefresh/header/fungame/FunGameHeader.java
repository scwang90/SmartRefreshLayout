package com.scwang.smartrefresh.header.fungame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.header.R;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.util.ColorUtils;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * 游戏 header
 * Created by SCWANG on 2017/6/17.
 */

public class FunGameHeader extends FunGameBase implements RefreshHeader {

    //<editor-fold desc="Field">
    /**
     * 分割线默认宽度大小
     */
    protected float DIVIDING_LINE_SIZE = 1.f;

    private RelativeLayout curtainReLayout, maskReLayout;

    private TextView topMaskView, bottomMaskView;

    private int halfHitBlockHeight;

    private boolean isStart = false;

    private String topMaskViewText = "下拉即将展开";//"Pull To Break Out!";
    private String bottomMaskViewText = "拖动控制游戏";//"Scrooll to move handle";

    private int topMaskTextSize;

    private int bottomMaskTextSize;

    //</editor-fold>

    //<editor-fold desc="View">
    public FunGameHeader(Context context) {
        super(context);
        this.initView(context, null);
    }

    public FunGameHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initView(context, attrs);
    }

    public FunGameHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView(context, attrs);
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public FunGameHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FunGameHeader);

        if (ta.hasValue(R.styleable.FunGameHeader_fgvMaskTopText)) {
            topMaskViewText = ta.getString(R.styleable.FunGameHeader_fgvMaskTopText);
        }
        if (ta.hasValue(R.styleable.FunGameHeader_fgvMaskBottomText)) {
            bottomMaskViewText = ta.getString(R.styleable.FunGameHeader_fgvMaskBottomText);
        }

        topMaskTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics());
        bottomMaskTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics());

        topMaskTextSize = ta.getDimensionPixelSize(R.styleable.FunGameHeader_fgvBottomTextSize, topMaskTextSize);
        bottomMaskTextSize = ta.getDimensionPixelSize(R.styleable.FunGameHeader_fgvBottomTextSize, bottomMaskTextSize);

        ta.recycle();

        curtainReLayout = new RelativeLayout(context);
        maskReLayout = new RelativeLayout(context);
        maskReLayout.setBackgroundColor(Color.parseColor("#3A3A3A"));

        topMaskView = createMaskTextView(context,topMaskViewText, topMaskTextSize, Gravity.BOTTOM);
        bottomMaskView = createMaskTextView(context,bottomMaskViewText, bottomMaskTextSize, Gravity.TOP);

        DIVIDING_LINE_SIZE = Math.max(1, DensityUtil.dp2px(0.5f));
    }


    private TextView createMaskTextView(Context context, String text, int textSize, int gravity) {
        TextView maskTextView = new TextView(context);
        maskTextView.setTextColor(Color.BLACK);
        maskTextView.setBackgroundColor(Color.WHITE);
        maskTextView.setGravity(gravity | Gravity.CENTER_HORIZONTAL);
        maskTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        maskTextView.setText(text);
        return maskTextView;
    }

    private void coverMaskView() {
        if (getChildCount() < 2 && !isInEditMode()) {
            LayoutParams maskLp = new LayoutParams(MATCH_PARENT,mHeaderHeight);
//            maskLp.topMargin = (int) FunGameView.DIVIDING_LINE_SIZE;
//            maskLp.bottomMargin = (int) FunGameView.DIVIDING_LINE_SIZE;

            addView(maskReLayout, maskLp);
            addView(curtainReLayout, maskLp);

            halfHitBlockHeight = (int) ((mHeaderHeight/* - 2 * DIVIDING_LINE_SIZE*/) * .5f);
            RelativeLayout.LayoutParams topRelayLayoutParams = new RelativeLayout.LayoutParams(MATCH_PARENT, halfHitBlockHeight);
            RelativeLayout.LayoutParams bottomRelayLayoutParams = new RelativeLayout.LayoutParams(MATCH_PARENT, halfHitBlockHeight);
            bottomRelayLayoutParams.topMargin = mHeaderHeight - halfHitBlockHeight;
            curtainReLayout.addView(topMaskView, topRelayLayoutParams);
            curtainReLayout.addView(bottomMaskView, bottomRelayLayoutParams);
        }
    }

    private void doStart(long delay) {
        ObjectAnimator topMaskAnimator = ObjectAnimator.ofFloat(topMaskView, "translationY", topMaskView.getTranslationY(), -halfHitBlockHeight);
        ObjectAnimator bottomMaskAnimator = ObjectAnimator.ofFloat(bottomMaskView, "translationY", bottomMaskView.getTranslationY(), halfHitBlockHeight);
        ObjectAnimator maskShadowAnimator = ObjectAnimator.ofFloat(maskReLayout, "alpha", maskReLayout.getAlpha(), 0);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(topMaskAnimator).with(bottomMaskAnimator).with(maskShadowAnimator);
        animatorSet.setDuration(800);
        animatorSet.setStartDelay(delay);
        animatorSet.start();

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                topMaskView.setVisibility(View.GONE);
                bottomMaskView.setVisibility(View.GONE);
                maskReLayout.setVisibility(View.GONE);
                onGameStart();
            }
        });
    }

    protected void onGameStart() {

    }

    public void postStart() {
        if (!isStart) {
            doStart(200);
            isStart = true;
        }
    }

    public void postEnd() {
        isStart = false;

        topMaskView.setTranslationY(topMaskView.getTranslationY() + halfHitBlockHeight);
        bottomMaskView.setTranslationY(bottomMaskView.getTranslationY() - halfHitBlockHeight);
        maskReLayout.setAlpha(1.f);

        topMaskView.setVisibility(View.VISIBLE);
        bottomMaskView.setVisibility(View.VISIBLE);
        maskReLayout.setVisibility(View.VISIBLE);
    }

    public void setTopMaskViewText(String topMaskViewText) {
        this.topMaskViewText = topMaskViewText;
        topMaskView.setText(topMaskViewText);
    }

    public void setBottomMaskViewText(String bottomMaskViewText) {
        this.bottomMaskViewText = bottomMaskViewText;
        bottomMaskView.setText(bottomMaskViewText);
    }

    //<editor-fold desc="RefreshHeader">


    @Override@Deprecated
    public void setPrimaryColors(int... colors) {
        super.setPrimaryColors(colors);
        if (colors.length > 0) {
            topMaskView.setTextColor(colors[0]);
            bottomMaskView.setTextColor(colors[0]);
            if (colors.length > 1) {
                maskReLayout.setBackgroundColor(ColorUtils.setAlphaComponent(colors[1],200));
                topMaskView.setBackgroundColor(ColorUtils.setAlphaComponent(colors[1],200));
                bottomMaskView.setBackgroundColor(ColorUtils.setAlphaComponent(colors[1],200));
            }
        }
    }

    @Override
    public void onInitialized(RefreshKernel kernel, int height, int extendHeight) {
        super.onInitialized(kernel, height, extendHeight);
        coverMaskView();
    }

    @Override
    public void onStartAnimator(RefreshLayout layout, int headHeight, int extendHeight) {
        super.onStartAnimator(layout, headHeight, extendHeight);
        postStart();
    }

    @Override
    public int onFinish(RefreshLayout layout, boolean success) {
        if (!mManualOperation) {
            postEnd();
        }
        return super.onFinish(layout, success);
    }

    //</editor-fold>
}
