package com.scwang.smartrefresh.layout.footer;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.R;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.internal.ProgressDrawable;
import com.scwang.smartrefresh.layout.internal.pathview.PathsDrawable;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * 经典上拉底部组件
 * Created by SCWANG on 2017/5/28.
 */

@SuppressWarnings("unused")
public class ClassicsFooter extends RelativeLayout implements RefreshFooter {

    public static String REFRESH_FOOTER_PULLUP = "上拉加载更多";
    public static String REFRESH_FOOTER_RELEASE = "释放立即加载";
    public static String REFRESH_HEADER_REFRESHING = "正在刷新...";
    public static String REFRESH_FOOTER_LOADING = "正在加载...";
    public static String REFRESH_FOOTER_FINISH = "加载完成";
    public static String REFRESH_FOOTER_FAILED = "加载失败";
    public static String REFRESH_FOOTER_ALLLOADED = "全部加载完成";

    protected TextView mBottomText;
    protected ImageView mArrowView;
    protected ImageView mProgressView;
    protected PathsDrawable mArrowDrawable;
    protected ProgressDrawable mProgressDrawable;
    protected SpinnerStyle mSpinnerStyle = SpinnerStyle.Translate;
    protected RefreshKernel mRefreshKernel;
    protected int mBackgroundColor = 0;
    protected boolean mLoadmoreFinished = false;

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

        setMinimumHeight(density.dip2px(60));

        mBottomText = new TextView(context);
        mBottomText.setId(android.R.id.widget_frame);
        mBottomText.setTextColor(0xff666666);
        mBottomText.setTextSize(16);
        mBottomText.setText(REFRESH_FOOTER_PULLUP);

        LayoutParams lpBottomText = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lpBottomText.addRule(CENTER_IN_PARENT);
        addView(mBottomText, lpBottomText);

        mProgressView = new ImageView(context);
        mProgressView.animate().setInterpolator(new LinearInterpolator());
        LayoutParams lpPathView = new LayoutParams(density.dip2px(18), density.dip2px(18));
        lpPathView.rightMargin = density.dip2px(20);
        lpPathView.addRule(CENTER_VERTICAL);
        lpPathView.addRule(LEFT_OF, android.R.id.widget_frame);
        addView(mProgressView, lpPathView);
        mArrowView = new ImageView(context);
        addView(mArrowView, lpPathView);

        if (!isInEditMode()) {
            mProgressView.setVisibility(GONE);
        } else {
            mArrowView.setVisibility(GONE);
        }

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ClassicsFooter);

        mSpinnerStyle = SpinnerStyle.values()[ta.getInt(R.styleable.ClassicsFooter_srlClassicsSpinnerStyle, mSpinnerStyle.ordinal())];

        if (ta.hasValue(R.styleable.ClassicsFooter_srlArrowDrawable)) {
            mArrowView.setImageDrawable(ta.getDrawable(R.styleable.ClassicsFooter_srlArrowDrawable));
        } else {
            mArrowDrawable = new PathsDrawable();
            mArrowDrawable.parserColors(0xff666666);
            mArrowDrawable.parserPaths("M20,12l-1.41,-1.41L13,16.17V4h-2v12.17l-5.58,-5.59L4,12l8,8 8,-8z");
            mArrowView.setImageDrawable(mArrowDrawable);
        }

        if (ta.hasValue(R.styleable.ClassicsFooter_srlProgressDrawable)) {
            mProgressView.setImageDrawable(ta.getDrawable(R.styleable.ClassicsFooter_srlProgressDrawable));
        } else {
            mProgressDrawable = new ProgressDrawable();
            mProgressDrawable.setColor(0xff666666);
            mProgressView.setImageDrawable(mProgressDrawable);
        }

        if (ta.hasValue(R.styleable.ClassicsFooter_srlPrimaryColor)) {
            setPrimaryColor(ta.getColor(R.styleable.ClassicsFooter_srlPrimaryColor, 0));
        }
        if (ta.hasValue(R.styleable.ClassicsFooter_srlAccentColor)) {
            setAccentColor(ta.getColor(R.styleable.ClassicsFooter_srlAccentColor, 0));
        }

        ta.recycle();
    }

    //</editor-fold>

    //<editor-fold desc="RefreshFooter">

    @Override
    public void onInitialized(RefreshKernel kernel, int height, int extendHeight) {
        mRefreshKernel = kernel;
        mRefreshKernel.requestDrawBackgoundForFooter(mBackgroundColor);
    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {
    }

    @Override
    public void onPullingUp(float percent, int offset, int footerHeight, int extendHeight) {

    }

    @Override
    public void onPullReleasing(float percent, int offset, int headHeight, int extendHeight) {

    }

    @Override
    public void onStartAnimator(RefreshLayout layout, int headHeight, int extendHeight) {
        if (!mLoadmoreFinished) {
            mProgressView.setVisibility(VISIBLE);
            if (mProgressDrawable != null) {
                mProgressDrawable.start();
            } else {
                mProgressView.animate().rotation(36000).setDuration(100000);
            }
        }
    }

    @Override
    public int onFinish(RefreshLayout layout, boolean success) {
        if (!mLoadmoreFinished) {
            if (mProgressDrawable != null) {
                mProgressDrawable.stop();
            } else {
                mProgressView.animate().rotation(0).setDuration(300);
            }
            mProgressView.setVisibility(GONE);
            if (success) {
                mBottomText.setText(REFRESH_FOOTER_FINISH);
            } else {
                mBottomText.setText(REFRESH_FOOTER_FAILED);
            }
            return 500;
        }
        return 0;
    }

    /**
     * ClassicsFooter 没有主题色
     * ClassicsFooter has no primary colors
     */
    @Override
    public void setPrimaryColors(int... colors) {
        if (mSpinnerStyle == SpinnerStyle.FixedBehind) {
            if (colors.length > 1) {
                setBackgroundColor(mBackgroundColor = colors[0]);
                if (mRefreshKernel != null) {
                    mRefreshKernel.requestDrawBackgoundForFooter(mBackgroundColor);
                }
                mBottomText.setTextColor(colors[1]);
                if (mProgressDrawable != null) {
                    mProgressDrawable.setColor(colors[1]);
                }
                if (mArrowDrawable != null) {
                    mArrowDrawable.parserColors(colors[1]);
                }
            } else if (colors.length > 0) {
                setBackgroundColor(mBackgroundColor = colors[0]);
                if (mRefreshKernel != null) {
                    mRefreshKernel.requestDrawBackgoundForFooter(mBackgroundColor);
                }
                if (colors[0] == 0xffffffff) {
                    mBottomText.setTextColor(0xff666666);
                    if (mProgressDrawable != null) {
                        mProgressDrawable.setColor(0xff666666);
                    }
                    if (mArrowDrawable != null) {
                        mArrowDrawable.parserColors(0xff666666);
                    }
                } else {
                    mBottomText.setTextColor(0xffffffff);
                    if (mProgressDrawable != null) {
                        mProgressDrawable.setColor(0xffffffff);
                    }
                    if (mArrowDrawable != null) {
                        mArrowDrawable.parserColors(0xffffffff);
                    }
                }
            }
        }
    }

    /**
     * 设置数据全部加载完成，将不能再次触发加载功能
     */
    @Override
    public boolean setLoadmoreFinished(boolean finished) {
        if (mLoadmoreFinished != finished) {
            mLoadmoreFinished = finished;
            if (finished) {
                mBottomText.setText(REFRESH_FOOTER_ALLLOADED);
            } else {
                mBottomText.setText(REFRESH_FOOTER_PULLUP);
            }
            if (mProgressDrawable != null) {
                mProgressDrawable.stop();
            } else {
                mProgressView.animate().rotation(0).setDuration(300);
            }
            mProgressView.setVisibility(GONE);
            mArrowView.setVisibility(GONE);
        }
        return true;
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
        if (!mLoadmoreFinished) {
            switch (newState) {
                case None:
//                    restoreRefreshLayoutBackground();
                    mArrowView.setVisibility(VISIBLE);
                case PullToUpLoad:
                    mBottomText.setText(REFRESH_FOOTER_PULLUP);
                    mArrowView.animate().rotation(180);
                    break;
                case Loading:
                    mArrowView.setVisibility(GONE);
                    mBottomText.setText(REFRESH_FOOTER_LOADING);
                    break;
                case ReleaseToLoad:
                    mBottomText.setText(REFRESH_FOOTER_RELEASE);
                    mArrowView.animate().rotation(0);
//                    replaceRefreshLayoutBackground(refreshLayout);
                    break;
                case Refreshing:
                    mBottomText.setText(REFRESH_HEADER_REFRESHING);
                    mProgressView.setVisibility(GONE);
                    mArrowView.setVisibility(GONE);
                    break;
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="Background">
//    private Runnable restoreRunable;
//    private void restoreRefreshLayoutBackground() {
//        if (restoreRunable != null) {
//            restoreRunable.run();
//            restoreRunable = null;
//        }
//    }
//
//    private void replaceRefreshLayoutBackground(final RefreshLayout refreshLayout) {
//        if (restoreRunable == null && mSpinnerStyle == SpinnerStyle.FixedBehind) {
//            restoreRunable = new Runnable() {
//                Drawable drawable = refreshLayout.getLayout().getBackground();
//                @Override
//                public void run() {
//                    refreshLayout.getLayout().setBackgroundDrawable(drawable);
//                }
//            };
//            refreshLayout.getLayout().setBackgroundDrawable(getBackground());
//        }
//    }
    //</editor-fold>

    //<editor-fold desc="API">
    public ClassicsFooter setProgressBitmap(Bitmap bitmap) {
        mProgressDrawable = null;
        mProgressView.setImageBitmap(bitmap);
        return this;
    }
    public ClassicsFooter setProgressDrawable(Drawable drawable) {
        mProgressDrawable = null;
        mProgressView.setImageDrawable(drawable);
        return this;
    }
    public ClassicsFooter setProgressResource(@DrawableRes int resId) {
        mProgressDrawable = null;
        mProgressView.setImageResource(resId);
        return this;
    }
    public ClassicsFooter setArrowBitmap(Bitmap bitmap) {
        mArrowDrawable = null;
        mArrowView.setImageBitmap(bitmap);
        return this;
    }
    public ClassicsFooter setArrowDrawable(Drawable drawable) {
        mArrowDrawable = null;
        mArrowView.setImageDrawable(drawable);
        return this;
    }
    public ClassicsFooter setArrowResource(@DrawableRes int resId) {
        mArrowDrawable = null;
        mArrowView.setImageResource(resId);
        return this;
    }
    public ClassicsFooter setSpinnerStyle(SpinnerStyle style) {
        this.mSpinnerStyle = style;
        return this;
    }
    public ClassicsFooter setAccentColor(int accentColor) {
        mBottomText.setTextColor(accentColor);
        if (mProgressDrawable != null) {
            mProgressDrawable.setColor(accentColor);
        }
        if (mArrowDrawable != null) {
            mArrowDrawable.parserColors(accentColor);
        }
        return this;
    }
    public ClassicsFooter setPrimaryColor(int primaryColor) {
        setBackgroundColor(mBackgroundColor = primaryColor);
        if (mRefreshKernel != null) {
            mRefreshKernel.requestDrawBackgoundForFooter(mBackgroundColor);
        }
        return this;
    }
    //</editor-fold>

}
