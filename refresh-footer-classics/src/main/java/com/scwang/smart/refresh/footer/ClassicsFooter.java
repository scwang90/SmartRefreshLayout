package com.scwang.smart.refresh.footer;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.scwang.smart.drawable.ProgressDrawable;
import com.scwang.smart.refresh.classics.ArrowDrawable;
import com.scwang.smart.refresh.classics.ClassicsAbstract;
import com.scwang.smart.refresh.footer.classics.R;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;
import com.scwang.smart.refresh.layout.util.SmartUtil;

/**
 * 经典上拉底部
 * Created by scwang on 2017/5/28.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ClassicsFooter extends ClassicsAbstract<ClassicsFooter> implements RefreshFooter {

    public static String REFRESH_FOOTER_PULLING = null;//"上拉加载更多";
    public static String REFRESH_FOOTER_RELEASE = null;//"释放立即加载";
    public static String REFRESH_FOOTER_LOADING = null;//"正在加载...";
    public static String REFRESH_FOOTER_REFRESHING = null;//"正在刷新...";
    public static String REFRESH_FOOTER_FINISH = null;//"加载完成";
    public static String REFRESH_FOOTER_FAILED = null;//"加载失败";
    public static String REFRESH_FOOTER_NOTHING = null;//"没有更多数据了";

    protected String mTextPulling;//"上拉加载更多";
    protected String mTextRelease;//"释放立即加载";
    protected String mTextLoading;//"正在加载...";
    protected String mTextRefreshing;//"正在刷新...";
    protected String mTextFinish;//"加载完成";
    protected String mTextFailed;//"加载失败";
    protected String mTextNothing;//"没有更多数据了";

    protected boolean mNoMoreData = false;

    //<editor-fold desc="LinearLayout">
    public ClassicsFooter(Context context) {
        this(context, null);
    }

    public ClassicsFooter(Context context, AttributeSet attrs) {
        super(context, attrs, 0);

        View.inflate(context, R.layout.srl_classics_footer, this);

        final View thisView = this;
        final View arrowView = mArrowView = thisView.findViewById(R.id.srl_classics_arrow);
        final View progressView = mProgressView = thisView.findViewById(R.id.srl_classics_progress);

        mTitleText = thisView.findViewById(R.id.srl_classics_title);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ClassicsFooter);

        LayoutParams lpArrow = (LayoutParams) arrowView.getLayoutParams();
        LayoutParams lpProgress = (LayoutParams) progressView.getLayoutParams();
        lpProgress.rightMargin = ta.getDimensionPixelSize(R.styleable.ClassicsFooter_srlDrawableMarginRight, SmartUtil.dp2px(20));
        lpArrow.rightMargin = lpProgress.rightMargin;

        lpArrow.width = ta.getLayoutDimension(R.styleable.ClassicsFooter_srlDrawableArrowSize, lpArrow.width);
        lpArrow.height = ta.getLayoutDimension(R.styleable.ClassicsFooter_srlDrawableArrowSize, lpArrow.height);
        lpProgress.width = ta.getLayoutDimension(R.styleable.ClassicsFooter_srlDrawableProgressSize, lpProgress.width);
        lpProgress.height = ta.getLayoutDimension(R.styleable.ClassicsFooter_srlDrawableProgressSize, lpProgress.height);

        lpArrow.width = ta.getLayoutDimension(R.styleable.ClassicsFooter_srlDrawableSize, lpArrow.width);
        lpArrow.height = ta.getLayoutDimension(R.styleable.ClassicsFooter_srlDrawableSize, lpArrow.height);
        lpProgress.width = ta.getLayoutDimension(R.styleable.ClassicsFooter_srlDrawableSize, lpProgress.width);
        lpProgress.height = ta.getLayoutDimension(R.styleable.ClassicsFooter_srlDrawableSize, lpProgress.height);

        mFinishDuration = ta.getInt(R.styleable.ClassicsFooter_srlFinishDuration, mFinishDuration);
        mSpinnerStyle = SpinnerStyle.values[ta.getInt(R.styleable.ClassicsFooter_srlClassicsSpinnerStyle, mSpinnerStyle.ordinal)];

        if (ta.hasValue(R.styleable.ClassicsFooter_srlDrawableArrow)) {
            mArrowView.setImageDrawable(ta.getDrawable(R.styleable.ClassicsFooter_srlDrawableArrow));
        } else if (mArrowView.getDrawable() == null) {
            mArrowDrawable = new ArrowDrawable();
            mArrowDrawable.setColor(0xff666666);
            mArrowView.setImageDrawable(mArrowDrawable);
        }

        if (ta.hasValue(R.styleable.ClassicsFooter_srlDrawableProgress)) {
            mProgressView.setImageDrawable(ta.getDrawable(R.styleable.ClassicsFooter_srlDrawableProgress));
        } else if (mProgressView.getDrawable() == null) {
            mProgressDrawable = new ProgressDrawable();
            mProgressDrawable.setColor(0xff666666);
            mProgressView.setImageDrawable(mProgressDrawable);
        }

        if (ta.hasValue(R.styleable.ClassicsFooter_srlTextSizeTitle)) {
            mTitleText.setTextSize(TypedValue.COMPLEX_UNIT_PX, ta.getDimensionPixelSize(R.styleable.ClassicsFooter_srlTextSizeTitle, SmartUtil.dp2px(16)));
        }

        if (ta.hasValue(R.styleable.ClassicsFooter_srlPrimaryColor)) {
            super.setPrimaryColor(ta.getColor(R.styleable.ClassicsFooter_srlPrimaryColor, 0));
        }
        if (ta.hasValue(R.styleable.ClassicsFooter_srlAccentColor)) {
            super.setAccentColor(ta.getColor(R.styleable.ClassicsFooter_srlAccentColor, 0));
        }

        if(ta.hasValue(R.styleable.ClassicsFooter_srlTextPulling)){
            mTextPulling = ta.getString(R.styleable.ClassicsFooter_srlTextPulling);
        } else if(REFRESH_FOOTER_PULLING != null) {
            mTextPulling = REFRESH_FOOTER_PULLING;
        } else {
            mTextPulling = context.getString(R.string.srl_footer_pulling);
        }
        if(ta.hasValue(R.styleable.ClassicsFooter_srlTextRelease)){
            mTextRelease = ta.getString(R.styleable.ClassicsFooter_srlTextRelease);
        } else if(REFRESH_FOOTER_RELEASE != null) {
            mTextRelease = REFRESH_FOOTER_RELEASE;
        } else {
            mTextRelease = context.getString(R.string.srl_footer_release);
        }
        if(ta.hasValue(R.styleable.ClassicsFooter_srlTextLoading)){
            mTextLoading = ta.getString(R.styleable.ClassicsFooter_srlTextLoading);
        } else if(REFRESH_FOOTER_LOADING != null) {
            mTextLoading = REFRESH_FOOTER_LOADING;
        } else {
            mTextLoading = context.getString(R.string.srl_footer_loading);
        }
        if(ta.hasValue(R.styleable.ClassicsFooter_srlTextRefreshing)){
            mTextRefreshing = ta.getString(R.styleable.ClassicsFooter_srlTextRefreshing);
        } else if(REFRESH_FOOTER_REFRESHING != null) {
            mTextRefreshing = REFRESH_FOOTER_REFRESHING;
        } else {
            mTextRefreshing = context.getString(R.string.srl_footer_refreshing);
        }
        if(ta.hasValue(R.styleable.ClassicsFooter_srlTextFinish)){
            mTextFinish = ta.getString(R.styleable.ClassicsFooter_srlTextFinish);
        } else if(REFRESH_FOOTER_FINISH != null) {
            mTextFinish = REFRESH_FOOTER_FINISH;
        } else {
            mTextFinish = context.getString(R.string.srl_footer_finish);
        }
        if(ta.hasValue(R.styleable.ClassicsFooter_srlTextFailed)){
            mTextFailed = ta.getString(R.styleable.ClassicsFooter_srlTextFailed);
        } else if(REFRESH_FOOTER_FAILED != null) {
            mTextFailed = REFRESH_FOOTER_FAILED;
        } else {
            mTextFailed = context.getString(R.string.srl_footer_failed);
        }
        if(ta.hasValue(R.styleable.ClassicsFooter_srlTextNothing)){
            mTextNothing = ta.getString(R.styleable.ClassicsFooter_srlTextNothing);
        } else if(REFRESH_FOOTER_NOTHING != null) {
            mTextNothing = REFRESH_FOOTER_NOTHING;
        } else {
            mTextNothing = context.getString(R.string.srl_footer_nothing);
        }

        ta.recycle();

        progressView.animate().setInterpolator(null);
        mTitleText.setText(thisView.isInEditMode() ? mTextLoading : mTextPulling);

        if (thisView.isInEditMode()) {
            arrowView.setVisibility(GONE);
        } else {
            progressView.setVisibility(GONE);
        }
    }
    //</editor-fold>

    //<editor-fold desc="RefreshFooter">
//    @Override
//    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
//        if (!mNoMoreData) {
//            super.onStartAnimator(refreshLayout, height, maxDragHeight);
//        }
//    }

    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        /*
         * 2020-5-15 修复BUG
         * https://github.com/scwang90/SmartRefreshLayout/issues/1003
         * 修复 没有更多数据之后 loading 还在显示问题
         */
        super.onFinish(layout, success);
        if (!mNoMoreData) {
            mTitleText.setText(success ? mTextFinish : mTextFailed);
            return mFinishDuration;
        }
        return 0;
    }

    /**
     * ClassicsFooter 在(SpinnerStyle.FixedBehind)时才有主题色
     */
    @Override@Deprecated
    public void setPrimaryColors(@ColorInt int ... colors) {
        if (mSpinnerStyle == SpinnerStyle.FixedBehind) {
            super.setPrimaryColors(colors);
        }
    }

    /**
     * 设置数据全部加载完成，将不能再次触发加载功能
     */
    @Override
    public boolean setNoMoreData(boolean noMoreData) {
        if (mNoMoreData != noMoreData) {
            mNoMoreData = noMoreData;
            final View arrowView = mArrowView;
            if (noMoreData) {
                mTitleText.setText(mTextNothing);
                arrowView.setVisibility(GONE);
            } else {
                mTitleText.setText(mTextPulling);
                arrowView.setVisibility(VISIBLE);
            }
        }
        return true;
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        final View arrowView = mArrowView;
        if (!mNoMoreData) {
            switch (newState) {
                case None:
                    arrowView.setVisibility(VISIBLE);
                case PullUpToLoad:
                    mTitleText.setText(mTextPulling);
                    arrowView.animate().rotation(180);
                    break;
                case Loading:
                case LoadReleased:
                    arrowView.setVisibility(GONE);
                    mTitleText.setText(mTextLoading);
                    break;
                case ReleaseToLoad:
                    mTitleText.setText(mTextRelease);
                    arrowView.animate().rotation(0);
                    break;
                case Refreshing:
                    mTitleText.setText(mTextRefreshing);
                    arrowView.setVisibility(GONE);
                    break;
            }
        }
    }
    //</editor-fold>

}
