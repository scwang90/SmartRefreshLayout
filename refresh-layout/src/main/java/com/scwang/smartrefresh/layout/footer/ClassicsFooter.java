package com.scwang.smartrefresh.layout.footer;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.R;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.internal.ProgressDrawable;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * 经典上拉底部组件
 * Created by SCWANG on 2017/5/28.
 */

public class ClassicsFooter extends LinearLayout implements RefreshFooter {

    private static final String REFRESH_BOTTOM_PULLUP = "上拉加载更多";
    private static final String REFRESH_BOTTOM_RELEASE = "释放立即加载";
    private static final String REFRESH_BOTTOM_LOADING = "正在加载...";
    private static final String REFRESH_BOTTOM_FINISH = "加载完成";

    private TextView mBottomText;
    private ImageView mProgressView;
    private ProgressDrawable mProgressDrawable;
    private SpinnerStyle mSpinnerStyle = SpinnerStyle.Translate;

    //<editor-fold desc="LinearLayout">
    public ClassicsFooter(Context context) {
        super(context);
        this.initView(context, null, 0);
    }

    public ClassicsFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initView(context, attrs, 0);
    }

    public ClassicsFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        DensityUtil density = new DensityUtil();

        setGravity(Gravity.CENTER);
        setMinimumHeight(density.dip2px(60));

        mProgressDrawable = new ProgressDrawable();
        mProgressDrawable.setColor(0xff666666);
        mProgressView = new ImageView(context);
        mProgressView.setImageDrawable(mProgressDrawable);
        LayoutParams lpPathView = new LayoutParams(density.dip2px(16), density.dip2px(16));
        lpPathView.rightMargin = density.dip2px(10);
        addView(mProgressView, lpPathView);

        mBottomText = new AppCompatTextView(context, attrs, defStyleAttr);
        mBottomText.setTextColor(0xff666666);
        mBottomText.setTextSize(16);
        mBottomText.setText(REFRESH_BOTTOM_PULLUP);

        addView(mBottomText, WRAP_CONTENT, WRAP_CONTENT);

        if (!isInEditMode()) {
            mProgressView.setVisibility(GONE);
        }

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ClassicsFooter);

        mSpinnerStyle = SpinnerStyle.values()[ta.getInt(R.styleable.ClassicsFooter_srlClassicsSpinnerStyle, mSpinnerStyle.ordinal())];

        if (ta.hasValue(R.styleable.ClassicsFooter_srlPrimaryColor)) {
            int primaryColor = ta.getColor(R.styleable.ClassicsFooter_srlPrimaryColor, 0);
            if (ta.hasValue(R.styleable.ClassicsFooter_srlAccentColor)) {
                int accentColor = ta.getColor(R.styleable.ClassicsFooter_srlAccentColor, 0);
                setPrimaryColors(primaryColor, accentColor);
            } else {
                setPrimaryColors(primaryColor);
            }
        } else if (ta.hasValue(R.styleable.ClassicsFooter_srlAccentColor)) {
            int accentColor = ta.getColor(R.styleable.ClassicsFooter_srlAccentColor, 0);
            setPrimaryColors(0, accentColor);
        }

        ta.recycle();
    }

    //</editor-fold>

    //<editor-fold desc="RefreshFooter">

    @Override
    public void onInitialized(RefreshKernel layout, int height, int extendHeight) {

    }

    @Override
    public void onPullingUp(float percent, int offset, int footerHeight, int extendHeight) {

    }

    @Override
    public void onPullReleasing(float percent, int offset, int headHeight, int extendHeight) {

    }

    @Override
    public void onStartAnimator(RefreshLayout layout, int headHeight, int extendHeight) {
        mProgressView.setVisibility(VISIBLE);
        mProgressDrawable.start();
    }

    @Override
    public int onFinish(RefreshLayout layout) {
        mProgressDrawable.stop();
        mProgressView.setVisibility(GONE);
        mBottomText.setText(REFRESH_BOTTOM_FINISH);
        return 500;
    }

    /**
     * ClassicsFooter 没有主题色
     * ClassicsFooter has no primary colors
     */
    @Override
    public void setPrimaryColors(int... colors) {
        if (mSpinnerStyle == SpinnerStyle.FixedBehind) {
            if (colors.length > 1) {
                setBackgroundColor(colors[0]);
                mBottomText.setTextColor(colors[1]);
                mProgressDrawable.setColor(colors[1]);
            } else if (colors.length > 0) {
                setBackgroundColor(colors[0]);
                if (colors[0] == 0xffffffff) {
                    mBottomText.setTextColor(0xff666666);
                    mProgressDrawable.setColor(0xff666666);
                } else {
                    mBottomText.setTextColor(0xffffffff);
                    mProgressDrawable.setColor(0xffffffff);
                }
            }
        }
    }


    @NonNull
    public View getView() {
        return this;
    }

    @Override
    public SpinnerStyle getSpinnerStyle() {
        return mSpinnerStyle;
    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        switch (newState) {
            case None:
                restoreRefreshLayoutBackground();
            case PullToUpLoad:
                mBottomText.setText(REFRESH_BOTTOM_PULLUP);
                break;
            case Loading:
                mBottomText.setText(REFRESH_BOTTOM_LOADING);
                break;
            case ReleaseToLoad:
                mBottomText.setText(REFRESH_BOTTOM_RELEASE);
                replaceRefreshLayoutBackground(refreshLayout);
                break;
        }
    }
    //</editor-fold>

    //<editor-fold desc="private">
    private Runnable restoreRunable;
    private void restoreRefreshLayoutBackground() {
        if (restoreRunable != null) {
            restoreRunable.run();
            restoreRunable = null;
        }
    }

    private void replaceRefreshLayoutBackground(RefreshLayout refreshLayout) {
        if (restoreRunable == null && mSpinnerStyle == SpinnerStyle.FixedBehind) {
            restoreRunable = new Runnable() {
                Drawable drawable = refreshLayout.getLayout().getBackground();
                @Override
                public void run() {
                    refreshLayout.getLayout().setBackgroundDrawable(drawable);
                }
            };
            refreshLayout.getLayout().setBackgroundDrawable(getBackground());
        }
    }
    //</editor-fold>

    //<editor-fold desc="API">
    public ClassicsFooter setSpinnerStyle(SpinnerStyle style) {
        this.mSpinnerStyle = style;
        return this;
    }
    public ClassicsFooter setAccentColor(int accentColor) {
        mBottomText.setTextColor(accentColor);
        mProgressDrawable.setColor(accentColor);
        return this;
    }
    //</editor-fold>

}
