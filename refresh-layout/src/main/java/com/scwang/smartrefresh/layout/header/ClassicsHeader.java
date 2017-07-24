package com.scwang.smartrefresh.layout.header;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.R;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.internal.ProgressDrawable;
import com.scwang.smartrefresh.layout.internal.pathview.PathsDrawable;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * 经典下拉头部
 * Created by SCWANG on 2017/5/28.
 */
public class ClassicsHeader extends RelativeLayout implements RefreshHeader {

    public static String REFRESH_HEADER_PULLDOWN = "下拉可以刷新";
    public static String REFRESH_HEADER_REFRESHING = "正在刷新";
    public static String REFRESH_HEADER_RELEASE = "释放立即刷新";
    public static String REFRESH_HEADER_FINISH = "刷新完成";
    public static String REFRESH_HEADER_FAILED = "刷新失败";

    private String KEY_LAST_UPDATE_TIME = "LAST_UPDATE_TIME";

    private Date mLastTime;
    private TextView mHeaderText;
    private TextView mLastUpdateText;
    private ImageView mArrowView;
    private ImageView mProgressView;
    private PathsDrawable mArrowDrawable;
    private ProgressDrawable mProgressDrawable;
    private DateFormat mFormat = new SimpleDateFormat("上次更新 M-d HH:mm", Locale.CHINA);
    private SpinnerStyle mSpinnerStyle = SpinnerStyle.Translate;
    private SharedPreferences mShared;

    //<editor-fold desc="RelativeLayout">
    public ClassicsHeader(Context context) {
        super(context);
        this.initView(context, null);
    }

    public ClassicsHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initView(context, attrs);
    }

    public ClassicsHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView(context, attrs);
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public ClassicsHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        DensityUtil density = new DensityUtil();

        setMinimumHeight(density.dip2px(80));

        LinearLayout layout = new LinearLayout(context);
        layout.setId(android.R.id.widget_frame);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.setOrientation(LinearLayout.VERTICAL);
        mHeaderText = new TextView(context);
        mHeaderText.setText(REFRESH_HEADER_PULLDOWN);
        mHeaderText.setTextColor(0xff666666);
        mHeaderText.setTextSize(16);

        mLastUpdateText = new TextView(context);
        mLastUpdateText.setTextColor(0xff7c7c7c);
        mLastUpdateText.setTextSize(12);
        LinearLayout.LayoutParams lpHeaderText = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        layout.addView(mHeaderText, lpHeaderText);
        LinearLayout.LayoutParams lpUpdateText = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        layout.addView(mLastUpdateText, lpUpdateText);

        LayoutParams lpHeaderLayout = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lpHeaderLayout.addRule(CENTER_IN_PARENT);
        addView(layout,lpHeaderLayout);

        mProgressView = new ImageView(context);
        mProgressView.animate().setInterpolator(new LinearInterpolator());
        LayoutParams lpProgress = new LayoutParams(density.dip2px(20), density.dip2px(20));
        lpProgress.rightMargin = density.dip2px(20);
        lpProgress.addRule(CENTER_VERTICAL);
        lpProgress.addRule(LEFT_OF, android.R.id.widget_frame);
        addView(mProgressView, lpProgress);

        mArrowView = new ImageView(context);
        addView(mArrowView, lpProgress);

        if (isInEditMode()) {
            mArrowView.setVisibility(GONE);
            mHeaderText.setText(REFRESH_HEADER_REFRESHING);
        } else {
            mProgressView.setVisibility(GONE);
        }

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ClassicsHeader);

        mSpinnerStyle = SpinnerStyle.values()[ta.getInt(R.styleable.ClassicsHeader_srlClassicsSpinnerStyle,mSpinnerStyle.ordinal())];

        if (ta.hasValue(R.styleable.ClassicsHeader_srlArrowDrawable)) {
            mArrowView.setImageDrawable(ta.getDrawable(R.styleable.ClassicsHeader_srlArrowDrawable));
        } else {
            mArrowDrawable = new PathsDrawable();
            mArrowDrawable.parserColors(0xff666666);
            mArrowDrawable.parserPaths("M20,12l-1.41,-1.41L13,16.17V4h-2v12.17l-5.58,-5.59L4,12l8,8 8,-8z");
            mArrowView.setImageDrawable(mArrowDrawable);
        }

        if (ta.hasValue(R.styleable.ClassicsHeader_srlProgressDrawable)) {
            mProgressView.setImageDrawable(ta.getDrawable(R.styleable.ClassicsHeader_srlProgressDrawable));
        } else {
            mProgressDrawable = new ProgressDrawable();
            mProgressDrawable.setColor(0xff666666);
            mProgressView.setImageDrawable(mProgressDrawable);
        }


        int primaryColor = ta.getColor(R.styleable.ClassicsHeader_srlPrimaryColor, 0);
        int accentColor = ta.getColor(R.styleable.ClassicsHeader_srlAccentColor, 0);
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

        try {//try 不能删除-否则会出现兼容性问题
            if (context instanceof FragmentActivity) {
                FragmentManager manager = ((FragmentActivity) context).getSupportFragmentManager();
                if (manager != null) {
                    List<Fragment> fragments = manager.getFragments();
                    if (fragments != null && fragments.size() > 0) {
                        setLastUpdateTime(new Date());
                        return;
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        KEY_LAST_UPDATE_TIME += context.getClass().getName();
        mShared = context.getSharedPreferences("ClassicsHeader", Context.MODE_PRIVATE);
        setLastUpdateTime(new Date(mShared.getLong(KEY_LAST_UPDATE_TIME, System.currentTimeMillis())));
    }

    //</editor-fold>

    //<editor-fold desc="RefreshHeader">
    @Override
    public void onInitialized(RefreshKernel kernel, int height, int extendHeight) {
    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {
    }

    @Override
    public void onPullingDown(float percent, int offset, int headHeight, int extendHeight) {
    }

    @Override
    public void onReleasing(float percent, int offset, int headHeight, int extendHeight) {

    }

    @Override
    public void onStartAnimator(RefreshLayout layout, int headHeight, int extendHeight) {
        if (mProgressDrawable != null) {
            mProgressDrawable.start();
        } else {
            mProgressView.animate().rotation(36000).setDuration(100000);
        }
    }

    @Override
    public int onFinish(RefreshLayout layout, boolean success) {
        if (mProgressDrawable != null) {
            mProgressDrawable.stop();
        } else {
            mProgressView.animate().rotation(0).setDuration(300);
        }
        mProgressView.setVisibility(GONE);
        if (success) {
            mHeaderText.setText(REFRESH_HEADER_FINISH);
            setLastUpdateTime(new Date());
        } else {
            mHeaderText.setText(REFRESH_HEADER_FAILED);
        }
        return 500;//延迟500毫秒之后再弹回
    }

    @Override
    public void setPrimaryColors(int... colors) {
        if (colors.length > 1) {
            if (!(getBackground() instanceof BitmapDrawable)) {
                setBackgroundColor(colors[0]);
            }
            if (mArrowDrawable != null) {
                mArrowDrawable.parserColors(colors[1]);
            }
            mHeaderText.setTextColor(colors[1]);
            if (mProgressDrawable != null) {
                mProgressDrawable.setColor(colors[1]);
            }
            mLastUpdateText.setTextColor(colors[1]&0x00ffffff|0x99000000);
        } else if (colors.length > 0) {
            if (!(getBackground() instanceof BitmapDrawable)) {
                setBackgroundColor(colors[0]);
            }
            if (colors[0] == 0xffffffff) {
                if (mArrowDrawable != null) {
                    mArrowDrawable.parserColors(0xff666666);
                }
                mHeaderText.setTextColor(0xff666666);
                if (mProgressDrawable != null) {
                    mProgressDrawable.setColor(0xff666666);
                }
                mLastUpdateText.setTextColor(0xff666666&0x00ffffff|0x99000000);
            } else {
                if (mArrowDrawable != null) {
                    mArrowDrawable.parserColors(0xffffffff);
                }
                mHeaderText.setTextColor(0xffffffff);
                if (mProgressDrawable != null) {
                    mProgressDrawable.setColor(0xffffffff);
                }
                mLastUpdateText.setTextColor(0xaaffffff);
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
//                restoreRefreshLayoutBackground();
            case PullDownToRefresh:
                mHeaderText.setText(REFRESH_HEADER_PULLDOWN);
                mArrowView.setVisibility(VISIBLE);
                mProgressView.setVisibility(GONE);
                mArrowView.animate().rotation(0);
                break;
            case Refreshing:
                mHeaderText.setText(REFRESH_HEADER_REFRESHING);
                mProgressView.setVisibility(VISIBLE);
                mArrowView.setVisibility(GONE);
                break;
            case ReleaseToRefresh:
                mHeaderText.setText(REFRESH_HEADER_RELEASE);
                mArrowView.animate().rotation(180);
//                replaceRefreshLayoutBackground(refreshLayout);
                break;
        }
    }
    //</editor-fold>

    //<editor-fold desc="background">
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
    public ClassicsHeader setProgressBitmap(Bitmap bitmap) {
        mProgressDrawable = null;
        mProgressView.setImageBitmap(bitmap);
        return this;
    }
    public ClassicsHeader setProgressDrawable(Drawable drawable) {
        mProgressDrawable = null;
        mProgressView.setImageDrawable(drawable);
        return this;
    }
    public ClassicsHeader setProgressResource(@DrawableRes int resId) {
        mProgressDrawable = null;
        mProgressView.setImageResource(resId);
        return this;
    }
    public ClassicsHeader setArrowBitmap(Bitmap bitmap) {
        mArrowDrawable = null;
        mArrowView.setImageBitmap(bitmap);
        return this;
    }
    public ClassicsHeader setArrowDrawable(Drawable drawable) {
        mArrowDrawable = null;
        mArrowView.setImageDrawable(drawable);
        return this;
    }
    public ClassicsHeader setArrowResource(@DrawableRes int resId) {
        mArrowDrawable = null;
        mArrowView.setImageResource(resId);
        return this;
    }

    public ClassicsHeader setLastUpdateTime(Date time) {
        mLastTime = time;
        mLastUpdateText.setText(mFormat.format(time));
        if (mShared != null && !isInEditMode()) {
            mShared.edit().putLong(KEY_LAST_UPDATE_TIME, time.getTime()).apply();
        }
        return this;
    }

    public ClassicsHeader setTimeFormat(DateFormat format) {
        mFormat = format;
        mLastUpdateText.setText(mFormat.format(mLastTime));
        return this;
    }

    public ClassicsHeader setSpinnerStyle(SpinnerStyle style) {
        this.mSpinnerStyle = style;
        return this;
    }

    public ClassicsHeader setAccentColor(int accentColor) {
        if (mArrowDrawable != null) {
            mArrowDrawable.parserColors(accentColor);
        }
        if (mProgressDrawable != null) {
            mProgressDrawable.setColor(accentColor);
        }
        mHeaderText.setTextColor(accentColor);
        mLastUpdateText.setTextColor(accentColor&0x00ffffff|0x99000000);
        return this;
    }
    //</editor-fold>

}
