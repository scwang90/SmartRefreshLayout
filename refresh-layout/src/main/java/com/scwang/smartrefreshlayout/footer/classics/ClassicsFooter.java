package com.scwang.smartrefreshlayout.footer.classics;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scwang.smartrefreshlayout.R;
import com.scwang.smartrefreshlayout.api.RefreshFooter;
import com.scwang.smartrefreshlayout.constant.RefreshState;
import com.scwang.smartrefreshlayout.constant.SpinnerStyle;
import com.scwang.smartrefreshlayout.internal.pathview.PathsView;
import com.scwang.smartrefreshlayout.util.DensityUtil;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * 经典上拉底部组件
 * Created by SCWANG on 2017/5/28.
 */

public class ClassicsFooter extends LinearLayout implements RefreshFooter {

    private static final String REFRESH_BOTTOM_PULLUP = "上拉加载更多";
    private static final String REFRESH_BOTTOM_RELEASE = "释放立即加载";
    private static final String REFRESH_BOTTOM_LOADING = "正在加载...";

    private TextView mBottomText;
    private PathsView mProgressView;
    private SpinnerStyle mSpinnerStyle = SpinnerStyle.Translate;

    public ClassicsFooter(Context context) {
        super(context);
        this.init(context, null, 0);
    }

    public ClassicsFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context, attrs, 0);
    }

    public ClassicsFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        DensityUtil density = new DensityUtil();

//        int padding = density.dip2px(20);
        setGravity(Gravity.CENTER);
//        setPadding(padding,padding,padding,padding);
        setMinimumHeight(density.dip2px(60));

        mProgressView = new PathsView(context);
//        mProgressView.parserPaths("M17.65,6.35C16.2,4.9 14.21,4 12,4c-4.42,0 -7.99,3.58 -7.99,8s3.57,8 7.99,8c3.73,0 6.84,-2.55 7.73,-6h-2.08c-0.82,2.33 -3.04,4 -5.65,4 -3.31,0 -6,-2.69 -6,-6s2.69,-6 6,-6c1.66,0 3.14,0.69 4.22,1.78L13,11h7V4l-2.35,2.35z");
        mProgressView.parserPaths("M176.5,63.5C162,49 142.1,40 120,40c-44.2,0 -79.9,35.8 -79.9,80s35.7,80 79.9,80c37.3,0 68.4,-25.5 77.3,-60h-20.8c-8.2,23.3 -30.4,40 -56.5,40 -33.1,0 -60,-26.9 -60,-60s26.9,-60 60,-60c16.6,0 31.4,06.9 42.2,17.8L130,110h70V40l-23.5,23.5z");
        mProgressView.animate().setInterpolator(new LinearInterpolator());
        mProgressView.parserColors(0xff666666);
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

        int primaryColor = ta.getColor(R.styleable.ClassicsFooter_srlPrimaryColor, 0);
        int accentColor = ta.getColor(R.styleable.ClassicsFooter_srlAccentColor, 0);
        if (primaryColor != 0) {
            if (accentColor != 0) {
                setPrimaryColors(primaryColor, accentColor);
            } else {
                setPrimaryColors(primaryColor);
            }
        } else if (accentColor != 0) {
            setPrimaryColors(0, accentColor);
        }

        ta.recycle();
    }

    @Override
    public void onPullingUp(float percent, int offset, int bottomHeight, int extendHeight) {

    }

    @Override
    public void onPullReleasing(float percent, int offset, int headHeight, int extendHeight) {

    }

    @Override
    public void startAnimator(int headHeight, int extendHeight) {
        mProgressView.setVisibility(VISIBLE);
        mProgressView.animate().rotation(36000).setDuration(100000);
    }

    @Override
    public void onFinish() {
        mProgressView.animate().rotation(0).setDuration(300);
        mProgressView.setVisibility(GONE);
    }

    @Override
    public void setPrimaryColors(int... colors) {
        if (colors.length > 0) {
            setBackgroundColor(colors[0]);
        }
        if (colors.length > 1) {
            mBottomText.setTextColor(colors[1]);
            mProgressView.parserColors(colors[1]);
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
    public void onStateChanged(RefreshState state) {
        switch (state) {
            case PullUpLoad:
            case None:
                mBottomText.setText(REFRESH_BOTTOM_PULLUP);
                break;
            case Loading:
                mBottomText.setText(REFRESH_BOTTOM_LOADING);
                break;
            case ReleaseLoad:
                mBottomText.setText(REFRESH_BOTTOM_RELEASE);
                break;
        }
    }

}
