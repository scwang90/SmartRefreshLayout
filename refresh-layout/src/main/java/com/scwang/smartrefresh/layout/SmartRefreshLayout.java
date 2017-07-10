package com.scwang.smartrefresh.layout;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ScrollingView;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.ScrollView;

import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreater;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshContent;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshScrollBoundary;
import com.scwang.smartrefresh.layout.constant.DimensionStatus;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.header.BezierRadarHeader;
import com.scwang.smartrefresh.layout.header.FalsifyHeader;
import com.scwang.smartrefresh.layout.impl.RefreshContentWrapper;
import com.scwang.smartrefresh.layout.impl.RefreshFooterWrapper;
import com.scwang.smartrefresh.layout.impl.RefreshHeaderWrapper;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnMultiPurposeListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.scwang.smartrefresh.layout.util.DensityUtil;
import com.scwang.smartrefresh.layout.util.ViscousFluidInterpolator;

import static android.view.View.MeasureSpec.AT_MOST;
import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.getSize;
import static android.view.View.MeasureSpec.makeMeasureSpec;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.scwang.smartrefresh.layout.util.DensityUtil.dp2px;
import static java.lang.System.currentTimeMillis;

/**
 * 智能刷新布局
 * Intelligent Refreshlayout
 * Created by SCWANG on 2017/5/26.
 */
@SuppressWarnings({"unused","WeakerAccess"})
public class SmartRefreshLayout extends ViewGroup implements NestedScrollingParent, NestedScrollingChild, RefreshLayout {

    //<editor-fold desc="属性变量 property and variable">

    protected RefreshState mState = RefreshState.None;

    //<editor-fold desc="滑动属性">
    protected int mTouchSlop;
    protected int mSpinner;
    protected int mReboundDuration = 250;
    protected int mScreenHeightPixels;
    protected float mTouchX;
    protected float mTouchY;
    protected float mDragRate = .5f;
    protected float mInitialMotionY;
    protected Interpolator mReboundInterpolator;
    protected View mFixedHeaderView;//固定在头部的视图
    protected View mFixedFooterView;//固定在底部的视图
    protected int mFixedHeaderViewId;//固定在头部的视图Id
    protected int mFixedFooterViewId;//固定在头部的视图Id
    //</editor-fold>

    //<editor-fold desc="功能属性">
    protected int[] mPrimaryColors;
    protected boolean mEnableRefresh = true;
    protected boolean mEnableLoadmore = true;
    protected boolean mDisableContentWhenRefresh = false;//是否开启在刷新时候禁止操作内容视图
    protected boolean mDisableContentWhenLoading = false;//是否开启在刷新时候禁止操作内容视图
    protected boolean mEnableHeaderTranslationContent = true;//是否启用内容视图拖动效果
    protected boolean mEnableFooterTranslationContent = true;//是否启用内容视图拖动效果
    protected boolean mEnablePreviewInEditMode = true;//是否在编辑模式下开启预览功能
    protected boolean mEnableOverScrollBounce = true;//是否启用越界回弹
    protected boolean mEnableAutoLoadmore = true;//是否在列表滚动到底部时自动加载更多
    protected boolean mEnablePureScrollMode = false;//是否开启纯滚动模式
    protected boolean mLoadmoreFinished = false;//数据是否全部加载完成，如果完成就不能在触发加载事件
    //</editor-fold>

    //<editor-fold desc="监听属性">
    protected OnRefreshListener mRefreshListener;
    protected OnLoadmoreListener mLoadmoreListener;
    protected OnMultiPurposeListener mOnMultiPurposeListener;
    //</editor-fold>

    //<editor-fold desc="嵌套滚动">
    protected int[] mParentScrollConsumed = new int[2];
    protected int[] mParentOffsetInWindow = new int[2];
    protected float mTotalUnconsumed;
    protected boolean mNestedScrollInProgress;
    protected NestedScrollingChildHelper mNestedScrollingChildHelper;
    protected NestedScrollingParentHelper mNestedScrollingParentHelper;
    //</editor-fold>

    protected RefreshKernel mKernel;

    //<editor-fold desc="内部视图">
    /**
     * 头部高度
     */
    protected int mHeaderHeight;
    protected int mHeaderTranslationY;
    protected DimensionStatus mHeaderHeightStatus = DimensionStatus.DefaultUnNotify;
    /**
     * 底部高度
     */
    protected int mFooterHeight;
    protected int mFooterTranslationY;
    protected DimensionStatus mFooterHeightStatus = DimensionStatus.DefaultUnNotify;

    /**
     * 扩展高度
     */
    protected int mHeaderExtendHeight;
    /**
     * 扩展高度
     */
    protected int mFooterExtendHeight;
    /**
     * 最大拖动比率(最大高度/Header高度)
     */
    protected float mHeaderMaxDragRate = 2.0f;
    /**
     * 最大拖动比率(最大高度/Footer高度)
     */
    protected float mFooterMaxDragRate = 2.0f;
    /**
     * 下拉头部视图
     */
    protected RefreshHeader mRefreshHeader;
    /**
     * 显示内容视图
     */
    protected RefreshContent mRefreshContent;
    /**
     * 上拉底部视图
     */
    protected RefreshFooter mRefreshFooter;
    //</editor-fold>

    protected static DefaultRefreshFooterCreater mFooterCreater = (context, l) -> new BallPulseFooter(context);
    protected static DefaultRefreshHeaderCreater mHeaderCreater = (context, l) -> new BezierRadarHeader(context);

    protected long mLastLoadingTime = 0;
    protected long mLastRefreshingTime = 0;

    protected Paint mPaint;
    protected int mHeaderBackgroundColor = 0;
    protected int mFooterBackgroundColor = 0;

    //</editor-fold>

    //<editor-fold desc="构造方法 construction methods">
    public SmartRefreshLayout(Context context) {
        super(context);
        this.initView(context, null);
    }

    public SmartRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initView(context, attrs);
    }

    public SmartRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView(context, attrs);
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public SmartRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        setClipToPadding(false);

        mScreenHeightPixels = context.getResources().getDisplayMetrics().heightPixels;
        mReboundInterpolator = new ViscousFluidInterpolator();
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);

        DensityUtil density = new DensityUtil();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SmartRefreshLayout);

        ViewCompat.setNestedScrollingEnabled(this, ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableNestedScrolling, true));
        mDragRate = ta.getFloat(R.styleable.SmartRefreshLayout_srlDragRate, mDragRate);
        mHeaderMaxDragRate = ta.getFloat(R.styleable.SmartRefreshLayout_srlHeaderMaxDragRate, mHeaderMaxDragRate);
        mFooterMaxDragRate = ta.getFloat(R.styleable.SmartRefreshLayout_srlFooterMaxDragRate, mFooterMaxDragRate);
        mEnableRefresh = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableRefresh, mEnableRefresh);
        mReboundDuration = ta.getInt(R.styleable.SmartRefreshLayout_srlReboundDuration, mReboundDuration);
        mEnableLoadmore = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableLoadmore, mEnableLoadmore);
        mHeaderHeight = ta.getDimensionPixelOffset(R.styleable.SmartRefreshLayout_srlHeaderHeight, density.dip2px(100));
        mFooterHeight = ta.getDimensionPixelOffset(R.styleable.SmartRefreshLayout_srlFooterHeight, density.dip2px(60));
        mDisableContentWhenRefresh = ta.getBoolean(R.styleable.SmartRefreshLayout_srlDisableContentWhenRefresh, mDisableContentWhenRefresh);
        mDisableContentWhenLoading = ta.getBoolean(R.styleable.SmartRefreshLayout_srlDisableContentWhenLoading, mDisableContentWhenLoading);
        mEnableHeaderTranslationContent = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableHeaderTranslationContent, mEnableHeaderTranslationContent);
        mEnableFooterTranslationContent = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableFooterTranslationContent, mEnableFooterTranslationContent);
        mEnablePreviewInEditMode = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnablePreviewInEditMode, mEnablePreviewInEditMode);
        mEnableAutoLoadmore = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableAutoLoadmore, mEnableAutoLoadmore);
        mEnableOverScrollBounce = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableAutoLoadmore, mEnableOverScrollBounce);
        mEnablePureScrollMode = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnablePureScrollMode, mEnablePureScrollMode);
        mFixedHeaderViewId = ta.getResourceId(R.styleable.SmartRefreshLayout_srlFixedHeaderViewId, View.NO_ID);
        mFixedFooterViewId = ta.getResourceId(R.styleable.SmartRefreshLayout_srlFixedFooterViewId, View.NO_ID);

        mFooterExtendHeight = (int) Math.max((mFooterHeight * (mHeaderMaxDragRate - 1)), 0);
        mHeaderExtendHeight = (int) Math.max((mHeaderHeight * (mHeaderMaxDragRate - 1)), 0);

        if (ta.hasValue(R.styleable.SmartRefreshLayout_srlHeaderHeight)) {
            mHeaderHeightStatus = DimensionStatus.XmlLayoutUnNotify;
        }
        if (ta.hasValue(R.styleable.SmartRefreshLayout_srlFooterHeight)) {
            mFooterHeightStatus = DimensionStatus.XmlLayoutUnNotify;
        }

        int accentColor = ta.getColor(R.styleable.SmartRefreshLayout_srlAccentColor, 0);
        int primaryColor = ta.getColor(R.styleable.SmartRefreshLayout_srlPrimaryColor, 0);
        if (primaryColor != 0 ) {
            if (accentColor != 0) {
                mPrimaryColors = new int[]{primaryColor, accentColor};
            } else {
                mPrimaryColors = new int[]{primaryColor};
            }
        }

        ta.recycle();

    }
    //</editor-fold>

    //<editor-fold desc="生命周期 life cycle">

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        final int count = getChildCount();
        if (count > 3) {
            throw new RuntimeException("最多只支持3个子View，Most only support three sub view");
        } else if (mEnablePureScrollMode && count > 1) {
            throw new RuntimeException("PureScrollMode模式只支持一个子View，Most only support one sub view in PureScrollMode");
        }

        //定义为确认的子View索引
        boolean[] uncertains = new boolean[count];
        //第一次查找确认的 子View
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            if (mRefreshContent == null && ( view instanceof AbsListView
                    || view instanceof WebView
                    || view instanceof ScrollView
                    || view instanceof ScrollingView
                    || view instanceof NestedScrollingChild
                    || view instanceof NestedScrollingParent
                    || view instanceof ViewPager)) {
                mRefreshContent = new RefreshContentWrapper(view);
            } else if (view instanceof RefreshHeader && mRefreshHeader == null) {
                mRefreshHeader = ((RefreshHeader) view);
            } else if (view instanceof RefreshFooter && mRefreshFooter == null) {
                mRefreshFooter = ((RefreshFooter) view);
            } else {
                uncertains[i] = true;//标记未确认
            }
        }
        //如果有 未确认（uncertains）的子View 通过智能算法计算
        for (int i = 0; i < count; i++) {
            if (uncertains[i]) {
                View view = getChildAt(i);
                if (count == 1 && mRefreshContent == null) {
                    mRefreshContent = new RefreshContentWrapper(view);
                } else if (i == 0 && mRefreshHeader == null) {
                    mRefreshHeader = new RefreshHeaderWrapper(view);
                } else if (count == 2 && mRefreshContent == null) {
                    mRefreshContent = new RefreshContentWrapper(view);
                } else if (i == 2 && mRefreshFooter == null) {
                    mRefreshFooter = new RefreshFooterWrapper(view);
                } else if (mRefreshContent == null) {
                    mRefreshContent = new RefreshContentWrapper(view);
                }
            }
        }

        if (isInEditMode()) {
            if (mPrimaryColors != null) {
                if (mRefreshHeader != null) {
                    mRefreshHeader.setPrimaryColors(mPrimaryColors);
                }
                if (mRefreshFooter != null) {
                    mRefreshFooter.setPrimaryColors(mPrimaryColors);
                }
            }

            //重新排序
            bringChildToFront(mRefreshContent.getView());
            if (mRefreshHeader != null && mRefreshHeader.getSpinnerStyle() != SpinnerStyle.FixedBehind) {
                bringChildToFront(mRefreshHeader.getView());
            }
            if (mRefreshFooter != null && mRefreshFooter.getSpinnerStyle() != SpinnerStyle.FixedBehind) {
                bringChildToFront(mRefreshFooter.getView());
            }

            if (mKernel == null) {
                mKernel = new RefreshKernelImpl();
            }
        }

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isInEditMode()) return;

        if (mKernel == null) {
            mKernel = new RefreshKernelImpl();
        }

        if (mRefreshContent == null) {
            for (int i = 0, len = getChildCount(); i < len; i++) {
                View view = getChildAt(i);
                if ((mRefreshHeader == null || view != mRefreshHeader.getView())&&
                        (mRefreshFooter == null || view != mRefreshFooter.getView())) {
                    mRefreshContent = new RefreshContentWrapper(view);
                }
            }
            if (mRefreshContent == null) {
                mRefreshContent = new RefreshContentWrapper(getContext());
                mRefreshContent.getView().setLayoutParams(new LayoutParams(MATCH_PARENT, MATCH_PARENT));
            }
        }
        if (mFixedHeaderViewId > 0 && mFixedHeaderView == null) {
            mFixedHeaderView = findViewById(mFixedHeaderViewId);
        }
        if (mFixedFooterViewId > 0 && mFixedFooterView == null) {
            mFixedFooterView = findViewById(mFixedFooterViewId);
        }
        mRefreshContent.setupComponent(mKernel, mFixedHeaderView, mFixedFooterView);

        if (mRefreshHeader == null) {
            if (mEnablePureScrollMode) {
                mRefreshHeader = new FalsifyHeader(getContext());
            } else {
                mRefreshHeader = mHeaderCreater.createRefreshHeader(getContext(), this);
            }
            if (!(mRefreshHeader.getView().getLayoutParams() instanceof MarginLayoutParams)) {
                if (mRefreshHeader.getSpinnerStyle() == SpinnerStyle.Scale) {
                    addView(mRefreshHeader.getView(), MATCH_PARENT, MATCH_PARENT);
                } else {
                    addView(mRefreshHeader.getView(), MATCH_PARENT, WRAP_CONTENT);
                }
            }
        }
        if (mRefreshFooter == null) {
            if (mEnablePureScrollMode) {
                mRefreshFooter = new RefreshFooterWrapper(new FalsifyHeader(getContext()));
            } else {
                mRefreshFooter = mFooterCreater.createRefreshFooter(getContext(), this);
            }
            if (!(mRefreshFooter.getView().getLayoutParams() instanceof MarginLayoutParams)) {
                if (mRefreshFooter.getSpinnerStyle() == SpinnerStyle.Scale) {
                    addView(mRefreshFooter.getView(), MATCH_PARENT, MATCH_PARENT);
                } else {
                    addView(mRefreshFooter.getView(), MATCH_PARENT, WRAP_CONTENT);
                }
            }
        }

        //重新排序
        bringChildToFront(mRefreshContent.getView());
        if (mRefreshHeader.getSpinnerStyle() != SpinnerStyle.FixedBehind) {
            bringChildToFront(mRefreshHeader.getView());
        }
        if (mRefreshFooter.getSpinnerStyle() != SpinnerStyle.FixedBehind) {
            bringChildToFront(mRefreshFooter.getView());
        }

        if (mRefreshListener == null) {
            mRefreshListener = refresh -> refresh.finishRefresh(3000);
        }
        if (mLoadmoreListener == null) {
            mLoadmoreListener = refresh -> refresh.finishLoadmore(2000);
        }
        if (mPrimaryColors != null) {
            mRefreshHeader.setPrimaryColors(mPrimaryColors);
            mRefreshFooter.setPrimaryColors(mPrimaryColors);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int minimumHeight = 0;
        final boolean isInEditMode = isInEditMode() && mEnablePreviewInEditMode;

        if (mRefreshHeader != null) {

            final View headerView = mRefreshHeader.getView();
            final LayoutParams lp = (LayoutParams) headerView.getLayoutParams();
            final int widthSpec = getChildMeasureSpec(widthMeasureSpec, lp.leftMargin + lp.rightMargin, lp.width);
            int heightSpec = heightMeasureSpec;

            if (lp.height > 0) {
                if (mHeaderHeightStatus.canReplaceWith(DimensionStatus.XmlExact)) {
                    mHeaderHeightStatus = DimensionStatus.XmlExact;
                    mHeaderHeight = lp.height/* + lp.topMargin*/ + lp.bottomMargin;
                    mHeaderExtendHeight = (int) Math.max((mHeaderHeight * (mHeaderMaxDragRate - 1)), 0);
                    mRefreshHeader.onInitialized(mKernel, mHeaderHeight, mHeaderExtendHeight);
                }
                heightSpec = makeMeasureSpec(lp.height, EXACTLY);
                headerView.measure(widthSpec, heightSpec);
            } else if (lp.height == WRAP_CONTENT) {
                heightSpec = makeMeasureSpec(Math.max(getSize(heightMeasureSpec)/* - lp.topMargin*/ - lp.bottomMargin, 0), AT_MOST);
                headerView.measure(widthSpec, heightSpec);
                int measuredHeight = headerView.getMeasuredHeight();
                if (measuredHeight > 0 && mHeaderHeightStatus.canReplaceWith(DimensionStatus.XmlWrap)) {
                    mHeaderHeightStatus = DimensionStatus.XmlWrap;
                    mHeaderHeight = headerView.getMeasuredHeight()/* + lp.topMargin*/ + lp.bottomMargin;
                    mHeaderExtendHeight = (int) Math.max((mHeaderHeight * (mHeaderMaxDragRate - 1)), 0);
                    mRefreshHeader.onInitialized(mKernel, mHeaderHeight, mHeaderExtendHeight);
                } else if (mHeaderHeight <= 0) {
                    heightSpec = makeMeasureSpec(Math.max(mHeaderHeight/* - lp.topMargin*/ - lp.bottomMargin, 0), EXACTLY);
                    headerView.measure(widthSpec, heightSpec);
                }
            } else if (lp.height == MATCH_PARENT) {
                heightSpec = makeMeasureSpec(Math.max(mHeaderHeight/* - lp.topMargin*/ - lp.bottomMargin, 0), EXACTLY);
                headerView.measure(widthSpec, heightSpec);
            } else {
                headerView.measure(widthSpec, heightSpec);
            }
            if (mRefreshHeader.getSpinnerStyle() == SpinnerStyle.Scale && !isInEditMode) {
                final int height = Math.max(0, mSpinner);
                heightSpec = makeMeasureSpec(Math.max(height/* - lp.topMargin*/ - lp.bottomMargin, 0), EXACTLY);
                headerView.measure(widthSpec, heightSpec);
            }

            if (!mHeaderHeightStatus.notifyed) {
                mHeaderHeightStatus = mHeaderHeightStatus.notifyed();
                mRefreshHeader.onInitialized(mKernel, mHeaderHeight, mHeaderExtendHeight);
            }

            if (isInEditMode) {
                minimumHeight += headerView.getMeasuredHeight();
            }
        }

        if (mRefreshFooter != null) {

            final View footerView = mRefreshFooter.getView();
            final LayoutParams lp = (LayoutParams) footerView.getLayoutParams();
            final int widthSpec = getChildMeasureSpec(widthMeasureSpec, lp.leftMargin + lp.rightMargin, lp.width);
            int heightSpec = heightMeasureSpec;
            if (lp.height > 0) {
                if (mFooterHeightStatus.canReplaceWith(DimensionStatus.XmlExact)) {
                    mFooterHeightStatus = DimensionStatus.XmlExact;
                    mFooterHeight = lp.height + lp.topMargin/* + lp.bottomMargin*/;
                    mFooterExtendHeight = (int) Math.max((mFooterHeight * (mFooterMaxDragRate - 1)), 0);
                    mRefreshFooter.onInitialized(mKernel, mFooterHeight, mFooterExtendHeight);
                }
                heightSpec = makeMeasureSpec(lp.height, EXACTLY);
                footerView.measure(widthSpec, heightSpec);
            } else if (lp.height == WRAP_CONTENT) {
                heightSpec = makeMeasureSpec(Math.max(getSize(heightMeasureSpec) - lp.topMargin/* - lp.bottomMargin*/, 0), AT_MOST);
                footerView.measure(widthSpec, heightSpec);
                int measuredHeight = footerView.getMeasuredHeight();
                if (measuredHeight > 0 && mFooterHeightStatus.canReplaceWith(DimensionStatus.XmlWrap)) {
                    mFooterHeightStatus = DimensionStatus.XmlWrap;
                    mFooterHeight = footerView.getMeasuredHeight() + lp.topMargin/* + lp.bottomMargin*/;
                    mFooterExtendHeight = (int) Math.max((mFooterHeight * (mFooterMaxDragRate - 1)), 0);
                    mRefreshFooter.onInitialized(mKernel, mFooterHeight, mFooterExtendHeight);
                } else if (measuredHeight <= 0){
                    heightSpec = makeMeasureSpec(Math.max(mFooterHeight - lp.topMargin/* - lp.bottomMargin*/, 0), EXACTLY);
                    footerView.measure(widthSpec, heightSpec);
                }
            } else if (lp.height == MATCH_PARENT) {
                heightSpec = makeMeasureSpec(Math.max(mFooterHeight - lp.topMargin/* - lp.bottomMargin*/, 0), EXACTLY);
                footerView.measure(widthSpec, heightSpec);
            } else {
                footerView.measure(widthSpec, heightSpec);
            }

            if (mRefreshFooter.getSpinnerStyle() == SpinnerStyle.Scale && !isInEditMode) {
                final int height = Math.max(0, -mSpinner);
                heightSpec = makeMeasureSpec(Math.max(height - lp.topMargin/* - lp.bottomMargin*/, 0), EXACTLY);
                footerView.measure(widthSpec, heightSpec);
            }

            if (!mFooterHeightStatus.notifyed) {
                mFooterHeightStatus = mFooterHeightStatus.notifyed();
                mRefreshFooter.onInitialized(mKernel, mFooterHeight, mFooterExtendHeight);
            }

            if (isInEditMode) {
                minimumHeight += footerView.getMeasuredHeight();
            }
        }

        if (mRefreshContent != null) {
            final LayoutParams lp = (LayoutParams) mRefreshContent.getLayoutParams();
            final int widthSpec = getChildMeasureSpec(widthMeasureSpec,
                    getPaddingLeft() + getPaddingRight() +
                            lp.leftMargin + lp.rightMargin, lp.width);
            final int heightSpec = getChildMeasureSpec(heightMeasureSpec,
                    getPaddingTop() + getPaddingBottom() +
                            lp.topMargin + lp.bottomMargin +
                            ((isInEditMode && mRefreshHeader != null && (mEnableHeaderTranslationContent||mRefreshHeader.getSpinnerStyle() == SpinnerStyle.FixedBehind)) ? mHeaderHeight : 0) +
                            ((isInEditMode && mRefreshFooter != null && (mEnableFooterTranslationContent||mRefreshFooter.getSpinnerStyle() == SpinnerStyle.FixedBehind)) ? mFooterHeight : 0), lp.height);
            mRefreshContent.measure(widthSpec, heightSpec);
            mRefreshContent.onInitialHeaderAndFooter(mHeaderHeight, mFooterHeight);
            minimumHeight += mRefreshContent.getMeasuredHeight();
        }

        setMeasuredDimension(resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec), resolveSize(minimumHeight, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int paddingLeft = getPaddingLeft();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();
        final boolean isInEditMode = isInEditMode() && mEnablePreviewInEditMode;

        if (mRefreshContent != null) {
            final LayoutParams lp = (LayoutParams) mRefreshContent.getLayoutParams();
            int left = paddingLeft + lp.leftMargin;
            int top = paddingTop + lp.topMargin;
            int right = left + mRefreshContent.getMeasuredWidth();
            int bottom = top + mRefreshContent.getMeasuredHeight();
            if (isInEditMode && mRefreshHeader != null && (mEnableHeaderTranslationContent||mRefreshHeader.getSpinnerStyle() == SpinnerStyle.FixedBehind)) {
                top = top + mHeaderHeight;
                bottom = bottom + mHeaderHeight;
            }
            mRefreshContent.layout(left, top, right, bottom);
        }

        if (mRefreshHeader != null) {
            final View headerView = mRefreshHeader.getView();
            final LayoutParams lp = (LayoutParams) headerView.getLayoutParams();
            int left = lp.leftMargin;
            int top = lp.topMargin ;
            int right = left + headerView.getMeasuredWidth();
            int bottom = top + headerView.getMeasuredHeight();
            if (!isInEditMode) {
                if (mRefreshHeader.getSpinnerStyle() == SpinnerStyle.Translate) {
                    top = top - mHeaderHeight + Math.max(0, mSpinner);
                    bottom = top + headerView.getMeasuredHeight();
                } else if (mRefreshHeader.getSpinnerStyle() == SpinnerStyle.Scale) {
                    bottom = top + Math.max(Math.max(0, mSpinner)/* - lp.topMargin*/ - lp.bottomMargin, 0);
//                } else if (mRefreshHeader.getSpinnerStyle() == SpinnerStyle.FixedBehind
//                        || mRefreshHeader.getSpinnerStyle() == SpinnerStyle.FixedFront) {
//                    bottom = top + Math.max(headerView.getMeasuredHeight(), mSpinner);
                }
            }
            headerView.layout(left, top, right, bottom);
        }

        if (mRefreshFooter != null) {
            final View footerView = mRefreshFooter.getView();
            final LayoutParams lp = (LayoutParams) footerView.getLayoutParams();
            final SpinnerStyle style = mRefreshFooter.getSpinnerStyle();
            int left = lp.leftMargin;
            int top = lp.topMargin + getMeasuredHeight();

            if (isInEditMode
                    || style == SpinnerStyle.FixedFront
                    || style == SpinnerStyle.FixedBehind) {
                top = top - mFooterHeight;
            } else if (style == SpinnerStyle.Scale || style == SpinnerStyle.Translate) {
                top = top - Math.max(Math.max(-mSpinner, 0) - lp.topMargin/* - lp.bottomMargin*/, 0);
            }

            int right = left + footerView.getMeasuredWidth();
            int bottom = top + footerView.getMeasuredHeight();
            footerView.layout(left, top, right, bottom);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mKernel = null;
        mRefreshHeader = null;
        mRefreshFooter = null;
        mRefreshContent = null;
        mFixedHeaderView = null;
        mFixedFooterView = null;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        boolean isInEditMode = mEnablePreviewInEditMode && isInEditMode();
        if (mHeaderBackgroundColor != 0 && (mSpinner > 0 || isInEditMode)) {
            mPaint.setColor(mHeaderBackgroundColor);
            canvas.drawRect(0, 0, getWidth(), (isInEditMode) ? mHeaderHeight : mSpinner, mPaint);
        } else if (mFooterBackgroundColor != 0 && (mSpinner < 0 || isInEditMode)) {
            mPaint.setColor(mFooterBackgroundColor);
            canvas.drawRect(0, 0, getWidth(), getHeight() + (isInEditMode ? (-mFooterHeight) : mSpinner), mPaint);
        }
        super.dispatchDraw(canvas);
    }

    //</editor-fold>

    //<editor-fold desc="滑动判断 judgement of slide">
    MotionEvent mFalsifyEvent = null;

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        final int action = MotionEventCompat.getActionMasked(e);
        if (mRefreshContent != null) {
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mRefreshContent.onActionDown(e);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    mRefreshContent.onActionUpOrCancel();
            }
        }
        if (reboundAnimator != null
                || (mState == RefreshState.Loading && mDisableContentWhenLoading)
                || (mState == RefreshState.Refreshing && mDisableContentWhenRefresh)) {
            return false;
        }
        if (!isEnabled() || mNestedScrollInProgress
                || (!mEnableRefresh && !(mEnableLoadmore && !mLoadmoreFinished))
                || mState == RefreshState.Loading
                || mState == RefreshState.Refreshing) {
            return super.dispatchTouchEvent(e);
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mTouchX = e.getX();
                mTouchY = e.getY();
                super.dispatchTouchEvent(e);
                return true;

            case MotionEvent.ACTION_MOVE:
                final float dx = e.getX() - mTouchX;
                final float dy = e.getY() - mTouchY;
                if(mState == RefreshState.None) {
                    if (Math.abs(dy) >= mTouchSlop && Math.abs(dx) < Math.abs(dy)) {//滑动允许最大角度为45度
                        if (dy > 0 && mEnableRefresh && !mRefreshContent.canScrollUp()) {
                            mInitialMotionY = dy + mTouchY - mTouchSlop;
                            setStatePullDownToRefresh();
                            e.setAction(MotionEvent.ACTION_CANCEL);
                            super.dispatchTouchEvent(e);
                        } else if (dy < 0 && mEnableLoadmore && !mLoadmoreFinished && !mRefreshContent.canScrollDown()) {
                            mInitialMotionY = dy + mTouchY + mTouchSlop;
                            setStatePullUpToLoad();
                            e.setAction(MotionEvent.ACTION_CANCEL);
                            super.dispatchTouchEvent(e);
                        } else {
                            return super.dispatchTouchEvent(e);
                        }
                    } else {
                        return super.dispatchTouchEvent(e);
                    }
                }
                final float spinner = dy + mTouchY - mInitialMotionY;
                if (((mState == RefreshState.PullDownToRefresh || mState == RefreshState.ReleaseToRefresh) && spinner < 0)
                    ||((mState == RefreshState.PullToUpLoad || mState == RefreshState.ReleaseToLoad) && spinner > 0)) {
                    long time = currentTimeMillis();
                    if (mFalsifyEvent == null) {
                        mFalsifyEvent = MotionEvent.obtain(time, time, MotionEvent.ACTION_DOWN, mTouchX + dx, mInitialMotionY, 0);
                        super.dispatchTouchEvent(mFalsifyEvent);
                    }
                    MotionEvent em = MotionEvent.obtain(time, time, MotionEvent.ACTION_MOVE, mTouchX + dx, mInitialMotionY + spinner, 0);
                    super.dispatchTouchEvent(em);
                    if (mSpinner != 0) {
                        moveSpinnerInfinitely(0);
                    }
                    return true;
                }
                if (mState == RefreshState.PullDownToRefresh || mState == RefreshState.ReleaseToRefresh ||
                        mState == RefreshState.PullToUpLoad || mState == RefreshState.ReleaseToLoad) {
                    moveSpinnerInfinitely(spinner);
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                final float y = e.getY();
                if (mFalsifyEvent != null) {
                    mFalsifyEvent = null;
                    long time = currentTimeMillis();
                    MotionEvent ec = MotionEvent.obtain(time, time, MotionEvent.ACTION_CANCEL, mTouchX, y, 0);
                    super.dispatchTouchEvent(ec);
                }
                if (overSpinner()) {
                    return true;
                }
                break;
        }
        return super.dispatchTouchEvent(e);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mState == RefreshState.Refreshing || mState == RefreshState.Loading) {
            if (isNestedScrollingEnabled()) {
                if (mRefreshContent != null && !mRefreshContent.isNestedScrollingChild(ev)) {
                    return true;
                }
            } else {
                if (mState == RefreshState.Refreshing
                        && mRefreshHeader != null && mRefreshHeader.getSpinnerStyle() != SpinnerStyle.FixedFront
                        && mHeaderTranslationY > -mHeaderHeight) {
                    return true;
                }
                if (mState == RefreshState.Loading
                        && mRefreshFooter != null && mRefreshFooter.getSpinnerStyle() != SpinnerStyle.FixedFront
                        && mFooterTranslationY < mFooterHeight) {
                    return true;
                }
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (mState == RefreshState.Refreshing || mState == RefreshState.Loading) {
            final int action = MotionEventCompat.getActionMasked(e);
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mTouchX = e.getX();
                    mTouchY = e.getY();
                    mInitialMotionY = -1;
                    break;
                case MotionEvent.ACTION_MOVE:
                    final float dy = e.getY() - mTouchY;
                    if (mInitialMotionY == -1) {
                        final float dx = e.getX() - mTouchX;
                        if (Math.abs(dy) >= mTouchSlop && Math.abs(dx) < Math.abs(dy)) {//滑动允许最大角度为45度
                            if (dy < 0) {//向上滚动
                                mTouchY = mInitialMotionY = Math.max(dy + mTouchY + mTouchSlop, 1);
                            } else {
                                mTouchY = mInitialMotionY = Math.max(dy + mTouchY - mTouchSlop, 1);
                            }
                            if (mState == RefreshState.Refreshing) {
                                mTouchX = (float) mHeaderTranslationY;
                            } else {
                                mTouchX = (float) mFooterTranslationY;
                            }
                        }
                    } else if (mInitialMotionY > 0) {
                        if (mState == RefreshState.Refreshing) {
                            if (mRefreshHeader != null && mRefreshHeader.getSpinnerStyle() != SpinnerStyle.FixedFront) {
                                mHeaderTranslationY = (int) Math.max(Math.min(0, mTouchX + dy), -mHeaderHeight);
                                mRefreshHeader.getView().setTranslationY(mHeaderTranslationY);
                                if (mEnableHeaderTranslationContent || mRefreshHeader.getSpinnerStyle() == SpinnerStyle.FixedBehind) {
                                    mRefreshContent.moveSpinner(mSpinner + mHeaderTranslationY);
                                }
                                if (mOnMultiPurposeListener != null) {
                                    int spinner = mSpinner + mHeaderTranslationY;
                                    mOnMultiPurposeListener.onHeaderReleasing(mRefreshHeader, 1f * spinner / mHeaderHeight, spinner, mHeaderHeight, mHeaderExtendHeight);
                                }
                                if (mHeaderTranslationY == -mHeaderHeight) {
                                    e.setAction(MotionEvent.ACTION_DOWN);
                                    super.dispatchTouchEvent(e);
                                }
                            }
                        } else {
                            if (mRefreshFooter != null && mRefreshFooter.getSpinnerStyle() != SpinnerStyle.FixedFront) {
                                mFooterTranslationY = (int)Math.min(Math.max(0, mTouchX + dy), mFooterHeight);
                                mRefreshFooter.getView().setTranslationY(mFooterTranslationY);
                                if (mEnableFooterTranslationContent || mRefreshFooter.getSpinnerStyle() == SpinnerStyle.FixedBehind) {
                                    mRefreshContent.moveSpinner(mSpinner + mFooterTranslationY);
                                }
                                if (mOnMultiPurposeListener != null) {
                                    int spinner = mSpinner + mFooterTranslationY;
                                    mOnMultiPurposeListener.onFooterReleasing(mRefreshFooter, 1f * spinner / mFooterHeight, spinner, mFooterHeight, mFooterExtendHeight);
                                }
                                if (mFooterTranslationY == mFooterHeight) {
                                    e.setAction(MotionEvent.ACTION_DOWN);
                                    super.dispatchTouchEvent(e);
                                }
                            }
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    mTouchX = 0;
                    mTouchY = 0;
                    mInitialMotionY = 0;
                    break;
            }
            return true;
        }
        return super.onTouchEvent(e);
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean b) {
        // if this is a List < L or another view that doesn't support nested
        // scrolling, ignore this request so that the vertical scroll event
        // isn't stolen
        View target = mRefreshContent.getScrollableView();
        if ((android.os.Build.VERSION.SDK_INT >= 21 || !(target instanceof AbsListView))
                && (target == null || ViewCompat.isNestedScrollingEnabled(target))) {
                    super.requestDisallowInterceptTouchEvent(b);
                //} else {
            // Nope.
        }
    }

    //</editor-fold>

    //<editor-fold desc="状态更改 state changes">

    protected void notifyStateChanged(RefreshState state) {
        final RefreshState oldState = mState;
        if (oldState != state) {
            mState = state;
            if (mRefreshFooter != null) {
                mRefreshFooter.onStateChanged(this, oldState, state);
            }
            if (mRefreshHeader != null) {
                mRefreshHeader.onStateChanged(this, oldState, state);
            }
            if (mOnMultiPurposeListener != null) {
                mOnMultiPurposeListener.onStateChanged(this, oldState, state);
            }
        }
    }

    protected void setStatePullUpToLoad() {
        notifyStateChanged(RefreshState.PullToUpLoad);
    }

    protected void setStateReleaseToLoad() {
        notifyStateChanged(RefreshState.ReleaseToLoad);
    }

    protected void setStateReleaseToRefresh() {
        notifyStateChanged(RefreshState.ReleaseToRefresh);
    }

    protected void setStatePullDownToRefresh() {
        notifyStateChanged(RefreshState.PullDownToRefresh);
    }

    protected void setStatePullDownCanceled() {
        notifyStateChanged(RefreshState.PullDownCanceled);
        resetStatus();
    }

    protected void setStatePullUpCanceled() {
        notifyStateChanged(RefreshState.PullUpCanceled);
        resetStatus();
    }

    protected void setStateLoding() {
        mLastLoadingTime = currentTimeMillis();
        notifyStateChanged(RefreshState.Loading);
        animSpinner(-mFooterHeight);
        if (mLoadmoreListener != null) {
            mLoadmoreListener.onLoadmore(this);
        }
        if (mRefreshFooter != null) {
            mRefreshFooter.onStartAnimator(this, mFooterHeight, mFooterExtendHeight);
        }
        if (mOnMultiPurposeListener != null) {
            mOnMultiPurposeListener.onLoadmore(this);
            mOnMultiPurposeListener.onFooterStartAnimator(mRefreshFooter, mFooterHeight, mFooterExtendHeight);
        }
    }

    protected void setStateRefresing() {
        mLastRefreshingTime = currentTimeMillis();
        notifyStateChanged(RefreshState.Refreshing);
        animSpinner(mHeaderHeight);
        if (mRefreshListener != null) {
            mRefreshListener.onRefresh(this);
        }
        if (mRefreshHeader != null) {
            mRefreshHeader.onStartAnimator(this, mHeaderHeight, mHeaderExtendHeight);
        }
        if (mOnMultiPurposeListener != null) {
            mOnMultiPurposeListener.onRefresh(this);
            mOnMultiPurposeListener.onHeaderStartAnimator(mRefreshHeader, mHeaderHeight, mHeaderExtendHeight);
        }
    }

    /**
     * 重置状态
     */
    protected void resetStatus() {
        if (mState != RefreshState.None) {
            if (mSpinner == 0) {
                notifyStateChanged(RefreshState.None);
            }
        }
        if (mSpinner != 0) {
            animSpinner(0);
        }
    }
    //</editor-fold>

    //<editor-fold desc="视图位移 displacement">

    //<editor-fold desc="动画监听 Animator Listener">
    protected ValueAnimator reboundAnimator;
    protected AnimatorListener reboundAnimatorEndListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            reboundAnimator = null;
            if ((int)((ValueAnimator)animation).getAnimatedValue() == 0) {
                if (mState != RefreshState.None) {
                    notifyStateChanged(RefreshState.None);
                }
            }
        }
    };

    protected AnimatorUpdateListener reboundUpdateListener = animation -> moveSpinner((int) animation.getAnimatedValue(), true);
    //</editor-fold>

    protected ValueAnimator animSpinner(int endSpinner) {
        return animSpinner(endSpinner, 0);
    }
    protected ValueAnimator animSpinner(int endSpinner, int startDelay) {
        return animSpinner(endSpinner, startDelay, mReboundInterpolator);
    }

    protected ValueAnimator animSpinner(int endSpinner, int startDelay, Interpolator interpolator) {
        if (mSpinner != endSpinner) {
            if (reboundAnimator != null) {
                reboundAnimator.cancel();
            }
            reboundAnimator = ValueAnimator.ofInt(mSpinner, endSpinner);
            reboundAnimator.setDuration(mReboundDuration);
            reboundAnimator.setInterpolator(interpolator);
            reboundAnimator.addUpdateListener(reboundUpdateListener);
            reboundAnimator.addListener(reboundAnimatorEndListener);
            reboundAnimator.setStartDelay(startDelay);
            reboundAnimator.start();
        }
        return reboundAnimator;
    }

    protected ValueAnimator animSpinnerBounce(int bounceSpinner) {
        if (mSpinner == 0 && reboundAnimator == null && mEnableOverScrollBounce) {
            reboundAnimator = ValueAnimator.ofInt(0, bounceSpinner, 0);
            reboundAnimator.setDuration(500);
            reboundAnimator.setInterpolator(new DecelerateInterpolator());
            reboundAnimator.addUpdateListener(reboundUpdateListener);
            reboundAnimator.addListener(reboundAnimatorEndListener);
            reboundAnimator.start();
        }
        return reboundAnimator;
    }

    protected boolean overSpinner() {
        if (mState == RefreshState.PullDownToRefresh
                || (mEnablePureScrollMode && mState == RefreshState.ReleaseToRefresh)) {
            setStatePullDownCanceled();
            return true;
        } else if (mState == RefreshState.PullToUpLoad
                || (mEnablePureScrollMode && mState == RefreshState.ReleaseToLoad)) {
            setStatePullUpCanceled();
            return true;
        } else if (mState == RefreshState.ReleaseToRefresh) {
            setStateRefresing();
            return true;
        } else if (mState == RefreshState.ReleaseToLoad) {
            setStateLoding();
            return true;
        }
        return false;
    }

    protected void moveSpinnerInfinitely(float dy) {
        if (dy >= 0/*mState == RefreshState.PullDownToRefresh || mState == RefreshState.ReleaseToRefresh*/) {
            final double M = mHeaderExtendHeight + mHeaderHeight;
            final double H = Math.max(mScreenHeightPixels / 2, getHeight()) * mDragRate;
            final double x = Math.max(0, dy * mDragRate);
            final double y = Math.min(M*(1-Math.pow(100,-x/H)),x);// 公式 y = M(1-40^(-x/H))
            moveSpinner((int) y, false);
//            return true;
        } else /*if (mState == RefreshState.PullToUpLoad || mState == RefreshState.ReleaseToLoad)*/ {
            final double M = mFooterExtendHeight + mFooterHeight;
            final double H = Math.max(mScreenHeightPixels / 2, getHeight()) * mDragRate;
            final double x = -Math.min(0, dy * mDragRate);
            final double y = -Math.min(M*(1-Math.pow(100,-x/H)),x);// 公式 y = M(1-40^(-x/H))
            moveSpinner((int) y, false);
//            return true;
        }
//        return false;
    }

    /**
     * 移动滚动 Scroll
     * moveSpinner 的取名来自 谷歌官方的 @{@link android.support.v4.widget.SwipeRefreshLayout#moveSpinner(float)}
     */
    protected void moveSpinner(int spinner, boolean isAnimator) {
        if (mSpinner == spinner) {
            return;
        }
        final int oldSpinner = mSpinner;
        this.mSpinner = spinner;
        if (!isAnimator && mState != RefreshState.Refreshing && mState != RefreshState.Loading) {
            if (mSpinner > mHeaderHeight) {
                setStateReleaseToRefresh();
            } else if (-mSpinner > mFooterHeight) {
                setStateReleaseToLoad();
            } else if (mSpinner < 0) {
                setStatePullUpToLoad();
            } else if (mSpinner > 0) {
                setStatePullDownToRefresh();
            }
        }
        if (mRefreshContent != null) {
            if (spinner >= 0) {
                if (mEnableHeaderTranslationContent || mRefreshHeader == null || mRefreshHeader.getSpinnerStyle() == SpinnerStyle.FixedBehind) {
                    mRefreshContent.moveSpinner(spinner);
                    if (mHeaderBackgroundColor != 0) {
                        invalidate();
                    }
                }
            } else {
                if (mEnableFooterTranslationContent || mRefreshFooter == null || mRefreshFooter.getSpinnerStyle() == SpinnerStyle.FixedBehind) {
                    mRefreshContent.moveSpinner(spinner);
                    if (mHeaderBackgroundColor != 0) {
                        invalidate();
                    }
                }
            }
        }
        if ((spinner >= 0 || oldSpinner > 0) && mRefreshHeader != null) {
            spinner = Math.max(spinner, 0);
            if (mEnableRefresh) {
                if (mRefreshHeader.getSpinnerStyle() == SpinnerStyle.Scale
                        || mRefreshHeader.getSpinnerStyle() == SpinnerStyle.Translate) {
                    requestLayout();
//                } else if (mRefreshHeader.getSpinnerStyle() == SpinnerStyle.Translate) {
//                    mRefreshHeader.getView().setTranslationY(spinner);
                }
            }
            if (isAnimator) {
                mRefreshHeader.onReleasing(1f * spinner / mHeaderHeight, spinner, mHeaderHeight, mHeaderExtendHeight);
                if (mOnMultiPurposeListener != null) {
                    mOnMultiPurposeListener.onHeaderReleasing(mRefreshHeader, 1f * spinner / mHeaderHeight, spinner, mHeaderHeight, mHeaderExtendHeight);
                }
            } else {
                mRefreshHeader.onPullingDown(1f * spinner / mHeaderHeight, spinner, mHeaderHeight, mHeaderExtendHeight);
                if (mOnMultiPurposeListener != null) {
                    mOnMultiPurposeListener.onHeaderPulling(mRefreshHeader, 1f * spinner / mHeaderHeight, spinner, mHeaderHeight, mHeaderExtendHeight);
                }
            }
        }
        if ((spinner <= 0 || oldSpinner < 0) && mRefreshFooter != null) {
            spinner = Math.min(spinner, 0);
            if (mEnableLoadmore) {
                if (mRefreshFooter.getSpinnerStyle() == SpinnerStyle.Scale
                        || mRefreshFooter.getSpinnerStyle() == SpinnerStyle.Translate) {
                    requestLayout();
//                } else if (mRefreshFooter.getSpinnerStyle() == SpinnerStyle.Translate) {
//                    mRefreshFooter.getView().setTranslationY(spinner);
                }
            }
            if (isAnimator) {
                mRefreshFooter.onPullReleasing(1f * spinner / mFooterHeight, spinner, mFooterHeight, mFooterExtendHeight);
                if (mOnMultiPurposeListener != null) {
                    mOnMultiPurposeListener.onFooterReleasing(mRefreshFooter, 1f * spinner / mFooterHeight, spinner, mFooterHeight, mFooterExtendHeight);
                }
            } else {
                mRefreshFooter.onPullingUp(1f * spinner / mFooterHeight, spinner, mFooterHeight, mFooterExtendHeight);
                if (mOnMultiPurposeListener != null) {
                    mOnMultiPurposeListener.onFooterPulling(mRefreshFooter, 1f * spinner / mFooterHeight, spinner, mFooterHeight, mFooterExtendHeight);
                }
            }
        }
    }

    //</editor-fold>

    //<editor-fold desc="布局参数 LayoutParams">
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(MATCH_PARENT, MATCH_PARENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    public static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
    //</editor-fold>

    //<editor-fold desc="嵌套滚动 NestedScrolling">

    //<editor-fold desc="NestedScrollingParent">
    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        boolean accepted = isEnabled() && isNestedScrollingEnabled() && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
        accepted = accepted && (mEnableRefresh||(mEnableLoadmore && !mLoadmoreFinished));
        return accepted;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        // Reset the counter of how much leftover scroll needs to be consumed.
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
        // Dispatch up to the nested parent
        startNestedScroll(axes & ViewCompat.SCROLL_AXIS_VERTICAL);
        mTotalUnconsumed = 0;
        mNestedScrollInProgress = true;
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        // If we are in the middle of consuming, a scroll, then we want to move the spinner back up
        // before allowing the list to scroll
        if (mState == RefreshState.Refreshing || mState == RefreshState.Loading) {
            final int[] parentConsumed = mParentScrollConsumed;
            if (dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], parentConsumed, null)) {
                dy -= parentConsumed[1];
            }

            if (mState == RefreshState.Refreshing) {
                if (mRefreshHeader != null && mRefreshHeader.getSpinnerStyle() != SpinnerStyle.FixedFront) {
                    if (dy > 0 && -mHeaderTranslationY < mHeaderHeight) {//向上滚动
                        if (mHeaderHeight + mHeaderTranslationY > dy) {
                            mHeaderTranslationY -= dy;
                            consumed[1] = dy;
                        } else {
                            consumed[1] = dy - (mHeaderHeight + mHeaderTranslationY);
                            mHeaderTranslationY = -mHeaderHeight;
                        }
                    } else if (dy < 0 && mHeaderTranslationY < 0 && !mRefreshContent.canScrollUp()) {
                        if (mHeaderTranslationY < dy) {
                            mHeaderTranslationY -= dy;
                            consumed[1] = dy;
                        } else {
                            consumed[1] = dy - mHeaderTranslationY;
                            mHeaderTranslationY = 0;
                        }
                    }
                    mRefreshHeader.getView().setTranslationY(mHeaderTranslationY);
                    if (mEnableHeaderTranslationContent || mRefreshHeader.getSpinnerStyle() == SpinnerStyle.FixedBehind) {
                        mRefreshContent.moveSpinner(mSpinner + mHeaderTranslationY);
                    }
                    if (mOnMultiPurposeListener != null) {
                        int spinner = mSpinner + mHeaderTranslationY;
                        mOnMultiPurposeListener.onHeaderReleasing(mRefreshHeader, 1f * spinner / mHeaderHeight, spinner, mHeaderHeight, mHeaderExtendHeight);
                    }
                }
            } else {
                if (mRefreshFooter != null && mRefreshFooter.getSpinnerStyle() != SpinnerStyle.FixedFront) {
                    if (dy > 0 && mFooterTranslationY > 0 && !mRefreshContent.canScrollDown()) {//向上滚动
                        if (mFooterTranslationY > dy) {
                            mFooterTranslationY -= dy;
                            consumed[1] = dy;
                        } else {
                            consumed[1] = dy - mFooterTranslationY;
                            mFooterTranslationY = 0;
                        }
                    } else if (dy < 0 && mFooterTranslationY < mFooterHeight) {
                        if (mFooterTranslationY - mFooterHeight < dy) {
                            mFooterTranslationY -= dy;
                            consumed[1] = dy;
                        } else {
                            consumed[1] = dy + mFooterTranslationY;
                            mFooterTranslationY = mFooterHeight;
                        }
                    }
                    mRefreshFooter.getView().setTranslationY(mFooterTranslationY);
                    if (mEnableFooterTranslationContent || mRefreshFooter.getSpinnerStyle() == SpinnerStyle.FixedBehind) {
                        mRefreshContent.moveSpinner(mSpinner + mFooterTranslationY);
                    }
                    if (mOnMultiPurposeListener != null) {
                        int spinner = mSpinner + mFooterTranslationY;
                        mOnMultiPurposeListener.onFooterReleasing(mRefreshFooter, 1f * spinner / mFooterHeight, spinner, mFooterHeight, mFooterExtendHeight);
                    }
                }
            }
        } else {
            if (mEnableRefresh && dy > 0 && mTotalUnconsumed > 0) {
                if (dy > mTotalUnconsumed) {
                    consumed[1] = dy - (int) mTotalUnconsumed;
                    mTotalUnconsumed = 0;
                } else {
                    mTotalUnconsumed -= dy;
                    consumed[1] = dy;
                }
                moveSpinnerInfinitely((int)mTotalUnconsumed);
            } else if (mEnableLoadmore && !mLoadmoreFinished && dy < 0 && mTotalUnconsumed < 0) {
                if (dy < mTotalUnconsumed) {
                    consumed[1] = dy - (int) mTotalUnconsumed;
                    mTotalUnconsumed = 0;
                } else {
                    mTotalUnconsumed -= dy;
                    consumed[1] = dy;
                }
                moveSpinnerInfinitely((int)mTotalUnconsumed);
            }

            // If a client layout is using a custom start position for the circle
            // view, they mean to hide it again before scrolling the child view
            // If we get back to mTotalUnconsumed == 0 and there is more to go, hide
            // the circle so it isn't exposed if its blocking content is moved
//        if (mUsingCustomStart && dy > 0 && mTotalUnconsumed == 0
//                && Math.abs(dy - consumed[1]) > 0) {
//            mCircleView.setVisibility(View.GONE);
//        }

            // Now let our nested parent consume the leftovers
            final int[] parentConsumed = mParentScrollConsumed;
            if (dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], parentConsumed, null)) {
                consumed[0] += parentConsumed[0];
                consumed[1] += parentConsumed[1];
            }
        }

    }

    @Override
    public int getNestedScrollAxes() {
        return mNestedScrollingParentHelper.getNestedScrollAxes();
    }

    @Override
    public void onStopNestedScroll(View target) {
        mNestedScrollingParentHelper.onStopNestedScroll(target);
        mNestedScrollInProgress = false;
        // Finish the spinner for nested scrolling if we ever consumed any
        // unconsumed nested scroll
        if (mTotalUnconsumed != 0) {
            overSpinner();
            mTotalUnconsumed = 0;
        }
        // Dispatch up our nested parent
        stopNestedScroll();
    }

    @Override
    public void onNestedScroll(final View target, final int dxConsumed, final int dyConsumed,
                               final int dxUnconsumed, final int dyUnconsumed) {
        // Dispatch up to the nested parent first
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                mParentOffsetInWindow);

        // This is a bit of a hack. Nested scrolling works from the bottom up, and as we are
        // sometimes between two nested scrolling views, we need a way to be able to know when any
        // nested scrolling parent has stopped handling events. We do that by using the
        // 'offset in window 'functionality to see if we have been moved from the event.
        // This is a decent indication of whether we should take over the event stream or not.
        if (mState != RefreshState.Loading && mState != RefreshState.Refreshing) {
            final int dy = dyUnconsumed + mParentOffsetInWindow[1];
            if (mEnableRefresh && dy < 0 && (mRefreshContent == null || !mRefreshContent.canScrollUp())) {
                if (mState == RefreshState.None) {
                    setStatePullDownToRefresh();
                }
                mTotalUnconsumed += Math.abs(dy);
                moveSpinnerInfinitely(mTotalUnconsumed);
            } else if (mEnableLoadmore && !mLoadmoreFinished && dy > 0 && (mRefreshContent == null || !mRefreshContent.canScrollDown())) {
                if (mState == RefreshState.None) {
                    setStatePullUpToLoad();
                }
                mTotalUnconsumed -= Math.abs(dy);
                moveSpinnerInfinitely(mTotalUnconsumed);
            }
        }
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return reboundAnimator != null
                || mState == RefreshState.PullDownToRefresh || mState == RefreshState.PullToUpLoad
                || mState == RefreshState.ReleaseToRefresh || mState == RefreshState.ReleaseToLoad
                || (mState == RefreshState.Refreshing && mHeaderTranslationY > -mHeaderHeight)
                || (mState == RefreshState.Loading && mFooterTranslationY < mFooterHeight)
                || dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return dispatchNestedFling(velocityX, velocityY, consumed);
    }
    //</editor-fold>

    //<editor-fold desc="NestedScrollingChild">
    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mNestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mNestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mNestedScrollingChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mNestedScrollingChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mNestedScrollingChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
                                        int dyUnconsumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedPreScroll(
                dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }
    //</editor-fold>

    //</editor-fold>

    //<editor-fold desc="开放接口 open interface">
    @Override
    public SmartRefreshLayout setFooterHeight(float heightDp) {
        return setFooterHeight(dp2px(heightDp));
    }
    @Override
    public SmartRefreshLayout setFooterHeight(int heightPx) {
        if (mFooterHeightStatus.canReplaceWith(DimensionStatus.CodeExact)) {
            mFooterHeight = heightPx;
            mFooterExtendHeight = (int) Math.max((heightPx * (mFooterMaxDragRate - 1)), 0);
            if (mRefreshFooter != null) {
                mFooterHeightStatus = DimensionStatus.CodeExact;
                mRefreshFooter.onInitialized(mKernel, mFooterHeight, mFooterExtendHeight);
            } else {
                mFooterHeightStatus = DimensionStatus.CodeExactUnNotify;
            }
        }
        return this;
    }
    @Override
    public SmartRefreshLayout setHeaderHeight(float heightDp) {
        return setHeaderHeight(dp2px(heightDp));
    }
    @Override
    public SmartRefreshLayout setHeaderHeight(int heightPx) {
        if (mHeaderHeightStatus.canReplaceWith(DimensionStatus.CodeExact)) {
            mHeaderHeight = heightPx;
            mHeaderExtendHeight = (int) Math.max((heightPx * (mHeaderMaxDragRate - 1)), 0);
            if (mRefreshHeader != null) {
                mHeaderHeightStatus = DimensionStatus.CodeExact;
                mRefreshHeader.onInitialized(mKernel, mHeaderHeight, mHeaderExtendHeight);
            } else {
                mHeaderHeightStatus = DimensionStatus.CodeExactUnNotify;
            }
        }
        return this;
    }

    /**
     * 设置下拉最大高度和Header高度的比率（将会影响可以下拉的最大高度）
     */
    @Override
    public SmartRefreshLayout setHeaderMaxDragRate(float rate) {
        this.mHeaderMaxDragRate = rate;
        this.mHeaderExtendHeight = (int) Math.max((mHeaderHeight * (mHeaderMaxDragRate - 1)), 0);
        if (mRefreshHeader != null) {
            mRefreshHeader.onInitialized(mKernel, mHeaderHeight, mHeaderExtendHeight);
        }
        return this;
    }

    /**
     * 设置上啦最大高度和Footer高度的比率（将会影响可以上啦的最大高度）
     */
    @Override
    public SmartRefreshLayout setFooterMaxDragRate(float rate) {
        this.mFooterMaxDragRate = rate;
        this.mFooterExtendHeight = (int) Math.max((mFooterHeight * (mFooterMaxDragRate - 1)), 0);
        if (mRefreshFooter != null) {
            mRefreshFooter.onInitialized(mKernel, mFooterHeight, mFooterExtendHeight);
        }
        return this;
    }

    /**
     * 设置回弹显示插值器
     */
    @Override
    public SmartRefreshLayout setReboundInterpolator(Interpolator interpolator) {
        this.mReboundInterpolator = interpolator;
        return this;
    }

    /**
     * 设置回弹动画时长
     */
    @Override
    public SmartRefreshLayout setReboundDuration(int duration) {
        this.mReboundDuration = duration;
        return this;
    }

    /**
     * 设置是否启用上啦加载更多（默认启用）
     */
    @Override
    public SmartRefreshLayout setEnableLoadmore(boolean enable) {
        this.mEnableLoadmore = enable;
        return this;
    }

    /**
     * 是否启用下拉刷新（默认启用）
     */
    @Override
    public SmartRefreshLayout setEnableRefresh(boolean enable) {
        this.mEnableRefresh = enable;
        return this;
    }

    /**
     * 设置是否启用内容视图拖动效果
     */
    @Override
    public SmartRefreshLayout setEnableHeaderTranslationContent(boolean enable) {
        this.mEnableHeaderTranslationContent = enable;
        return this;
    }

    /**
     * 设置是否启用内容视图拖动效果
     */
    @Override
    public SmartRefreshLayout setEnableFooterTranslationContent(boolean enable) {
        this.mEnableFooterTranslationContent = enable;
        return this;
    }

    /**
     * 设置是否开启在刷新时候禁止操作内容视图
     */
    @Override
    public SmartRefreshLayout setDisableContentWhenRefresh(boolean disable) {
        this.mDisableContentWhenRefresh = disable;
        return this;
    }

    /**
     * 设置是否开启在加载时候禁止操作内容视图
     */
    @Override
    public SmartRefreshLayout setDisableContentWhenLoading(boolean disable) {
        this.mDisableContentWhenLoading = disable;
        return this;
    }

    /**
     * 设置是否监听列表在滚动到底部时触发加载事件
     */
    @Override
    public SmartRefreshLayout setEnableAutoLoadmore(boolean enable) {
        this.mEnableAutoLoadmore = enable;
        return this;
    }

    /**
     * 设置是否启用越界回弹
     */
    @Override
    public SmartRefreshLayout setEnableOverScrollBounce(boolean enable) {
        this.mEnableOverScrollBounce = enable;
        return this;
    }

    /**
     * 设置指定的Header
     */
    @Override
    public SmartRefreshLayout setRefreshHeader(RefreshHeader header) {
        if (mRefreshHeader != null) {
            removeView(mRefreshHeader.getView());
        }
        this.mRefreshHeader = header;
        this.mHeaderHeightStatus = mHeaderHeightStatus.unNotify();
        this.addView(mRefreshHeader.getView());
        return this;
    }

    /**
     * 设置指定的Footer
     */
    @Override
    public SmartRefreshLayout setRefreshFooter(RefreshFooter bottom) {
        if (mRefreshFooter != null) {
            removeView(mRefreshFooter.getView());
        }
        this.mRefreshFooter = bottom;
        this.mFooterHeightStatus = mFooterHeightStatus.unNotify();
        this.addView(mRefreshFooter.getView());
        return this;
    }

    /**
     * 获取底部上啦组件的实现
     */
    @Nullable
    @Override
    public RefreshFooter getRefreshFooter() {
        return mRefreshFooter;
    }

    /**
     * 获取顶部下拉组件的实现
     */
    @Nullable
    @Override
    public RefreshHeader getRefreshHeader() {
        return mRefreshHeader;
    }

    /**
     * 获取状态
     */
    @Override
    public RefreshState getState() {
        return mState;
    }

    /**
     * 获取实体布局视图
     */
    @Override
    public SmartRefreshLayout getLayout() {
        return this;
    }

    /**
     * 单独设置刷新监听器
     */
    @Override
    public SmartRefreshLayout setOnRefreshListener(OnRefreshListener listener) {
        this.mRefreshListener = listener;
        return this;
    }

    /**
     * 单独设置加载监听器
     */
    @Override
    public SmartRefreshLayout setOnLoadmoreListener(OnLoadmoreListener listener) {
        this.mLoadmoreListener = listener;
        return this;
    }

    /**
     * 同时设置刷新和加载监听器
     */
    @Override
    public SmartRefreshLayout setOnRefreshLoadmoreListener(OnRefreshLoadmoreListener listener) {
        this.mRefreshListener = listener;
        this.mLoadmoreListener = listener;
        return this;
    }

    /**
     * 设置多功能监听器
     */
    @Override
    public SmartRefreshLayout setOnMultiPurposeListener(OnMultiPurposeListener listener) {
        this.mOnMultiPurposeListener = listener;
        return this;
    }

    /**
     * 设置主题颜色
     */
    @Override
    public SmartRefreshLayout setPrimaryColorsId(@ColorRes int... primaryColorId) {
        int[] colors = new int[primaryColorId.length];
        for (int i = 0; i < primaryColorId.length; i++) {
            colors[i] = ContextCompat.getColor(getContext(), primaryColorId[i]);
        }
        setPrimaryColors(colors);
        return this;
    }
    /**
     * 设置主题颜色
     */
    @Override
    public SmartRefreshLayout setPrimaryColors(int... colors) {
        if (mRefreshHeader != null) {
            mRefreshHeader.setPrimaryColors(colors);
        }
        if (mRefreshFooter != null) {
            mRefreshFooter.setPrimaryColors(colors);
        }
        mPrimaryColors = colors;
        return this;
    }

    /**
     * 设置滚动边界
     */
    @Override
    public RefreshLayout setRefreshScrollBoundary(RefreshScrollBoundary boundary) {
        if (mRefreshContent != null) {
            mRefreshContent.setRefreshScrollBoundary(boundary);
        }
        return this;
    }

    /**
     * 设置数据全部加载完成，将不能再次触发加载功能
     */
    @Override
    public SmartRefreshLayout setLoadmoreFinished(boolean finished) {
        mLoadmoreFinished = finished;
        return this;
    }

    /**
     * 完成刷新
     */
    @Override
    public SmartRefreshLayout finishRefresh(){
        long passTime =  System.currentTimeMillis() - mLastRefreshingTime;
        return finishRefresh(Math.max(0, 1000 - (int)passTime));//保证刷新动画有1000毫秒的时间
    }
    /**
     * 完成加载
     */
    @Override
    public SmartRefreshLayout finishLoadmore(){
        long passTime =  System.currentTimeMillis() - mLastLoadingTime;
        return finishLoadmore(Math.max(0, 1000 - (int)passTime));//保证加载动画有1000毫秒的时间
    }
    /**
     * 完成刷新
     */
    @Override
    public SmartRefreshLayout finishRefresh(int delayed){
        postDelayed(() -> {
            if (mState == RefreshState.Refreshing && mRefreshHeader != null) {
                int startDelay = mRefreshHeader.onFinish(this);
                if (startDelay == Integer.MAX_VALUE) {
                    return;
                }
                if (mHeaderTranslationY != 0) {
                    mRefreshHeader.getView().setTranslationY(0);
                    moveSpinner(mSpinner + mHeaderTranslationY, true);
                    mHeaderTranslationY = 0;
                }
                notifyStateChanged(RefreshState.RefreshFinish);
                if (mOnMultiPurposeListener != null) {
                    mOnMultiPurposeListener.onHeaderFinish(mRefreshHeader);
                }
                if (mSpinner == 0) {
                    resetStatus();
                } else {
                    animSpinner(0, startDelay);
                }
            }
        }, delayed);
        return this;
    }
    /**
     * 完成加载
     */
    @Override
    public SmartRefreshLayout finishLoadmore(int delayed) {
        postDelayed(() -> {
            if (mState == RefreshState.Loading && mRefreshFooter != null) {
                int startDelay = mRefreshFooter.onFinish(this);
                if (startDelay == Integer.MAX_VALUE) {
                    return;
                }
                if (mFooterTranslationY != 0) {
                    mRefreshFooter.getView().setTranslationY(0);
                    moveSpinner(mSpinner + mFooterTranslationY, true);
                    mFooterTranslationY = 0;
                }
                notifyStateChanged(RefreshState.LoadingFinish);
                AnimatorUpdateListener updateListener = mRefreshContent.onLoadingFinish(mFooterHeight, mReboundInterpolator, mReboundDuration);
                if (mOnMultiPurposeListener != null) {
                    mOnMultiPurposeListener.onFooterFinish(mRefreshFooter);
                }
                if (mSpinner == 0) {
                    postDelayed(this::resetStatus, 500);
                } else {
                    ValueAnimator valueAnimator = animSpinner(0, startDelay);
                    if (updateListener != null && valueAnimator != null) {
                        valueAnimator.addUpdateListener(updateListener);
                    }
                }
            }
        }, delayed);
        return this;
    }
    /**
     * 是否正在刷新
     */
    @Override
    public boolean isRefreshing() {
        return mState == RefreshState.Refreshing;
    }
    /**
     * 是否正在加载
     */
    @Override
    public boolean isLoading() {
        return mState == RefreshState.Loading;
    }
    /**
     * 自动刷新
     */
    @Override
    public boolean autoRefresh() {
        return autoRefresh(500);
    }
    /**
     * 自动刷新
     */
    @Override
    public boolean autoRefresh(int delayed) {
        return autoRefresh(delayed, 1f * (mHeaderHeight + mHeaderExtendHeight / 2) / mHeaderHeight);
    }
    /**
     * 自动刷新
     */
    @Override
    public boolean autoRefresh(int delayed, float dragrate) {
        if (mState == RefreshState.None && mEnableRefresh) {
            if (reboundAnimator != null) {
                reboundAnimator.cancel();
            }
            reboundAnimator = new ValueAnimator();
            postDelayed(() -> {
                reboundAnimator = ValueAnimator.ofInt(mSpinner, (int) (mHeaderHeight * dragrate));
                reboundAnimator.setDuration(mReboundDuration);
                reboundAnimator.setInterpolator(new DecelerateInterpolator());
                reboundAnimator.addUpdateListener(animation -> moveSpinner((int) animation.getAnimatedValue(), false));
                reboundAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        setStatePullDownToRefresh();
                    }
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        reboundAnimator = null;
                        if (mState != RefreshState.ReleaseToRefresh) {
                            setStateReleaseToRefresh();
                        }
                        overSpinner();
                    }
                });
                reboundAnimator.start();
            }, delayed);
            return true;
        } else {
            return false;
        }
    }
    /**
     * 自动加载
     */
    @Override
    public boolean autoLoadmore() {
        return autoLoadmore(500);
    }
    /**
     * 自动加载
     */
    @Override
    public boolean autoLoadmore(int delayed) {
        return autoLoadmore(delayed, 1f * (mFooterHeight + mFooterExtendHeight / 2) / mFooterHeight);
    }
    /**
     * 自动加载
     */
    @Override
    public boolean autoLoadmore(int delayed, float dragrate) {
        if (mState == RefreshState.None && mEnableLoadmore && !mLoadmoreFinished) {
            if (reboundAnimator != null) {
                reboundAnimator.cancel();
            }
            reboundAnimator = new ValueAnimator();
            postDelayed(() -> {
                reboundAnimator = ValueAnimator.ofInt(mSpinner, -(int)(mFooterHeight * dragrate));
                reboundAnimator.setDuration(mReboundDuration);
                reboundAnimator.setInterpolator(new DecelerateInterpolator());
                reboundAnimator.addUpdateListener(animation -> moveSpinner((int) animation.getAnimatedValue(), false));
                reboundAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        setStatePullUpToLoad();
                    }
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        reboundAnimator = null;
                        if (mState != RefreshState.ReleaseToLoad) {
                            setStateReleaseToLoad();
                        }
                        overSpinner();
                    }
                });
                reboundAnimator.start();
            }, delayed);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isEnableLoadmore() {
        return mEnableLoadmore;
    }

    @Override
    public boolean isLoadmoreFinished() {
        return mLoadmoreFinished;
    }

    @Override
    public boolean isEnableAutoLoadmore() {
        return mEnableAutoLoadmore;
    }

    @Override
    public boolean isEnableRefresh() {
        return mEnableRefresh;
    }

    @Override
    public boolean isEnableOverScrollBounce() {
        return mEnableOverScrollBounce;
    }

    /**
     * 设置默认Header构建器
     */
    public static void setDefaultRefreshHeaderCreater(@NonNull DefaultRefreshHeaderCreater creater) {
        mHeaderCreater = creater;
    }

    /**
     * 设置默认Footer构建器
     */
    public static void setDefaultRefreshFooterCreater(@NonNull DefaultRefreshFooterCreater creater) {
        mFooterCreater = creater;
    }

    //</editor-fold>

    //<editor-fold desc="核心接口 RefreshKernel">
    protected class RefreshKernelImpl implements RefreshKernel {
        @NonNull
        @Override
        public RefreshLayout getRefreshLayout() {
            return SmartRefreshLayout.this;
        }

        @NonNull
        @Override
        public RefreshContent getRefreshContent() {
            return SmartRefreshLayout.this.mRefreshContent;
        }

        //<editor-fold desc="状态更改 state changes">
        public RefreshKernel setStatePullUpToLoad() {
            SmartRefreshLayout.this.setStatePullUpToLoad();
            return this;
        }
        public RefreshKernel setStateReleaseToLoad() {
            SmartRefreshLayout.this.setStateReleaseToLoad();
            return this;
        }
        public RefreshKernel setStateReleaseToRefresh() {
            SmartRefreshLayout.this.setStateReleaseToRefresh();
            return this;
        }
        public RefreshKernel setStatePullDownToRefresh() {
            SmartRefreshLayout.this.setStatePullDownToRefresh();
            return this;
        }
        public RefreshKernel setStatePullDownCanceled() {
            SmartRefreshLayout.this.setStatePullDownCanceled();
            return this;
        }
        public RefreshKernel setStatePullUpCanceled() {
            SmartRefreshLayout.this.setStatePullUpCanceled();
            return this;
        }
        public RefreshKernel setStateLoding() {
            SmartRefreshLayout.this.setStateLoding();
            return this;
        }
        public RefreshKernel setStateRefresing() {
            SmartRefreshLayout.this.setStateRefresing();
            return this;
        }
        public RefreshKernel resetStatus() {
            SmartRefreshLayout.this.resetStatus();
            return this;
        }
        //</editor-fold>

        //<editor-fold desc="视图位移 Spinner">
        public RefreshKernel overSpinner() {
            SmartRefreshLayout.this.overSpinner();
            return this;
        }
        public RefreshKernel moveSpinnerInfinitely(float dy) {
            SmartRefreshLayout.this.moveSpinnerInfinitely(dy);
            return this;
        }
        public RefreshKernel moveSpinner(int spinner, boolean isAnimator) {
            SmartRefreshLayout.this.moveSpinner(spinner, isAnimator);
            return this;
        }
        public RefreshKernel animSpinner(int endSpinner)  {
            SmartRefreshLayout.this.animSpinner(endSpinner);
            return this;
        }
        @Override
        public RefreshKernel animSpinnerBounce(int bounceSpinner) {
            SmartRefreshLayout.this.animSpinnerBounce(bounceSpinner);
            return this;
        }
        //</editor-fold>

        //<editor-fold desc="绘制背景 Backgound">
        public RefreshKernel requestDrawBackgoundForHeader(int backgroundColor) {
            if (mPaint == null && backgroundColor != 0) {
                mPaint = new Paint();
            }
            mHeaderBackgroundColor = backgroundColor;
            return this;
        }
        public RefreshKernel requestDrawBackgoundForFooter(int backgroundColor) {
            if (mPaint == null && backgroundColor != 0) {
                mPaint = new Paint();
            }
            mFooterBackgroundColor = backgroundColor;
            return this;
        }
        //</editor-fold>
    }
    //</editor-fold>
}
