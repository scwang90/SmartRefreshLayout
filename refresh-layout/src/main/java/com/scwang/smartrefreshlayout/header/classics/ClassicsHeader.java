package com.scwang.smartrefreshlayout.header.classics;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scwang.smartrefreshlayout.api.RefreshHeader;
import com.scwang.smartrefreshlayout.constant.RefreshState;
import com.scwang.smartrefreshlayout.constant.SpinnerStyle;
import com.scwang.smartrefreshlayout.internal.pathview.PathsView;
import com.scwang.smartrefreshlayout.util.DensityUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * 经典下拉头部
 * Created by SCWANG on 2017/5/28.
 */
public class ClassicsHeader extends RelativeLayout implements RefreshHeader {

    private static final int CIRCLE_BG_LIGHT = 0xFFFAFAFA;
    private static final String REFRESH_HEADER_PULLDOWN = "下拉可以刷新";
    private static final String REFRESH_HEADER_REFRESHING = "正在刷新...";
    private static final String REFRESH_HEADER_RELEASE = "释放立即刷新";

    private TextView mHeaderText;
    private TextView mLastUpdateText;
    private PathsView mArrowView;
    private PathsView mProgressView;
    private DateFormat mFormat = new SimpleDateFormat("上次更新 HH:mm", Locale.CHINA);

    public ClassicsHeader(Context context) {
        super(context);
        this.init(context, null, 0);
    }

    public ClassicsHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context, attrs, 0);
    }

    public ClassicsHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        DensityUtil density = new DensityUtil();

        setMinimumHeight(density.dip2px(80));

        mProgressView = new PathsView(context);
        mProgressView.parserColors(0xff666666);
//        mProgressView.parserPaths("M17.65,6.35C16.2,4.9 14.21,4 12,4c-4.42,0 -7.99,3.58 -7.99,8s3.57,8 7.99,8c3.73,0 6.84,-2.55 7.73,-6h-2.08c-0.82,2.33 -3.04,4 -5.65,4 -3.31,0 -6,-2.69 -6,-6s2.69,-6 6,-6c1.66,0 3.14,0.69 4.22,1.78L13,11h7V4l-2.35,2.35z");
        mProgressView.parserPaths("M176.5,63.5C162,49 142.1,40 120,40c-44.2,0 -79.9,35.8 -79.9,80s35.7,80 79.9,80c37.3,0 68.4,-25.5 77.3,-60h-20.8c-8.2,23.3 -30.4,40 -56.5,40 -33.1,0 -60,-26.9 -60,-60s26.9,-60 60,-60c16.6,0 31.4,06.9 42.2,17.8L130,110h70V40l-23.5,23.5z");
        mProgressView.animate().setDuration(1000*5).setInterpolator(new LinearInterpolator());
        LayoutParams lpProgress = new LayoutParams(density.dip2px(20), density.dip2px(20));
        lpProgress.leftMargin = density.dip2px(80);
        lpProgress.addRule(CENTER_VERTICAL);
        lpProgress.addRule(ALIGN_PARENT_LEFT);
        addView(mProgressView, lpProgress);

        mArrowView = new PathsView(context);
        mArrowView.parserColors(0xff666666);
        //mArrowView.parserPaths("M20,12l-1.41,-1.41L13,16.17V4h-2v12.17l-5.58,-5.59L4,12l8,8 8,-8z");
//        mArrowView.parserPaths("M200,120l-14.1,-14.1L130,161.7V40h-20v121.7l-55.8,-55.9L40,120l80,80 80,-80z");
        mArrowView.parserPaths("M60.68,16.15l-0.13,-0.11c-1.09,-0.76 -2.63,-1.16 -4.47,-1.16c-2.92,0 -5.95,0.99 -7.32,1.92l-10.76,7.35l-20.97,4.45c-0.18,0.04 -0.35,0.11 -0.51,0.22c-0.41,0.28 -0.64,0.76 -0.62,1.25c0.04,0.71 0.58,1.27 1.28,1.34l8.87,0.89l-8.65,5.9c-2.57,-1.18 -5.02,-2.33 -7.27,-3.4c-3.48,-1.67 -5.76,-1.96 -6.83,-0.89c-1.11,1.11 -0.39,3.02 0.01,3.6l8.33,10.8c0.28,0.41 0.6,0.64 0.99,0.71c0.64,0.11 1.2,-0.27 1.78,-0.68l2.11,-1.45l11.72,-5.69l-1.71,6.12c-0.19,0.68 0.14,1.38 0.78,1.68c0.18,0.08 0.39,0.13 0.59,0.13c0.29,0 0.57,-0.09 0.81,-0.25c0.16,-0.1 0.28,-0.23 0.38,-0.39l6.7,-10.19l4.1,-4.8L58.08,21.08c0.28,-0.19 0.55,-0.36 0.82,-0.54c0.63,-0.4 1.22,-0.78 1.65,-1.21C61.47,18.41 61.52,17.39 60.68,16.15z");
        addView(mArrowView, lpProgress);

        LinearLayout layout = new LinearLayout(context, attrs, defStyleAttr);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.setOrientation(LinearLayout.VERTICAL);
        mHeaderText = new TextView(context);
        mHeaderText.setText(REFRESH_HEADER_PULLDOWN);
        mHeaderText.setTextColor(0xff666666);
        mHeaderText.setTextSize(16);

        mLastUpdateText = new TextView(context);
        mLastUpdateText.setText(mFormat.format(new Date()));
        mLastUpdateText.setTextColor(0xff7c7c7c);
        mLastUpdateText.setTextSize(12);
        LinearLayout.LayoutParams lpHeaderText = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lpHeaderText.leftMargin = density.dip2px(20);
        lpHeaderText.rightMargin = density.dip2px(20);
        layout.addView(mHeaderText, lpHeaderText);
        LinearLayout.LayoutParams lpUpdateText = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        layout.addView(mLastUpdateText, lpUpdateText);

        LayoutParams lpHeaderLayout = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lpHeaderLayout.addRule(CENTER_IN_PARENT);
        addView(layout,lpHeaderLayout);

        mProgressView.setVisibility(GONE);
    }

    public void setLastUpdateTime(Date time) {
        mLastUpdateText.setText(mFormat.format(time));
    }

    @Override
    public void onPullingDown(int offset, int headHeight, int extendHeight) {
    }

    @Override
    public void onReleasing(int offset, int headHeight, int extendHeight) {

    }

    @Override
    public void startAnimator(int headHeight, int extendHeight) {
        mProgressView.animate().rotation(36000).setDuration(100000).start();
    }

    @Override
    public void onFinish() {
        mProgressView.animate().rotation(0).setDuration(300).start();
    }

    @Override
    public void setPrimaryColors(int... colors) {
        if (colors.length > 0) {
            setBackgroundColor(colors[0]);
        }
        if (colors.length > 1) {
            mHeaderText.setTextColor(colors[1]);
            mArrowView.parserColors(colors[1]);
            mProgressView.parserColors(colors[1]);
            mLastUpdateText.setTextColor(colors[1]&0x00ffffff|0x99000000);
        }
    }

    @NonNull
    public View getView() {
        return this;
    }

    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;
    }

    @Override
    public void onStateChanged(RefreshState state) {
        switch (state) {
            case PullDownRefresh:
            case None:
                mHeaderText.setText(REFRESH_HEADER_PULLDOWN);
                mArrowView.setVisibility(VISIBLE);
                mProgressView.setVisibility(GONE);
                mArrowView.animate().rotation(0).start();
                break;
            case Refreshing:
                mHeaderText.setText(REFRESH_HEADER_REFRESHING);
                mProgressView.setVisibility(VISIBLE);
                mArrowView.setVisibility(GONE);
                break;
            case ReleaseRefresh:
                mHeaderText.setText(REFRESH_HEADER_RELEASE);
                mArrowView.animate().rotation(180).start();
                break;
        }
    }
}
