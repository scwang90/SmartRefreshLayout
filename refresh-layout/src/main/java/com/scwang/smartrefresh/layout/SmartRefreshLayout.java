package com.scwang.smartrefresh.layout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.Scroller;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshContent;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshInternal;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.api.ScrollBoundaryDecider;
import com.scwang.smartrefresh.layout.constant.DimensionStatus;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.header.BezierRadarHeader;
import com.scwang.smartrefresh.layout.impl.RefreshContentWrapper;
import com.scwang.smartrefresh.layout.impl.RefreshFooterWrapper;
import com.scwang.smartrefresh.layout.impl.RefreshHeaderWrapper;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnMultiPurposeListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.scwang.smartrefresh.layout.util.DelayedRunnable;
import com.scwang.smartrefresh.layout.util.DensityUtil;
import com.scwang.smartrefresh.layout.util.DesignUtil;
import com.scwang.smartrefresh.layout.util.ViscousFluidInterpolator;

import java.util.ArrayList;
import java.util.List;

import static android.view.MotionEvent.obtain;
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
 * Intelligent RefreshLayout
 * Created by SCWANG on 2017/5/26.
 */
@SuppressLint("RestrictedApi")
@SuppressWarnings({"unused", "WeakerAccess"})
public class SmartRefreshLayout extends ViewGroup implements RefreshLayout, NestedScrollingParent/*, NestedScrollingChild*/ {

    //<editor-fold desc="属性变量 property and variable">

    //<editor-fold desc="滑动属性">
    protected int mTouchSlop;
    protected int mSpinner;//当前的 Spinner
    protected int mLastSpinner;//最后的，的Spinner
    protected int mTouchSpinner;//触摸时候，的Spinner
    protected int mFloorDuration = 250;//二楼展开时长
    protected int mReboundDuration = 250;//回弹动画时长
    protected int mScreenHeightPixels;//屏幕高度
    protected float mTouchX;
    protected float mTouchY;
    protected float mLastTouchX;//用于实现Header的左右拖动效果
    protected float mLastTouchY;//用于实现多点触摸
    protected float mDragRate = .5f;
    protected char mDragDirection = 'n';//拖动的方向 none-n horizontal-h vertical-v
    protected boolean mIsBeingDragged;//是否正在拖动
    protected boolean mSuperDispatchTouchEvent;//父类是否处理触摸事件
    protected int mFixedHeaderViewId;//固定在头部的视图Id
    protected int mFixedFooterViewId;//固定在底部的视图Id

    protected int mMinimumVelocity;
    protected int mMaximumVelocity;
    protected Scroller mScroller;
    protected VelocityTracker mVelocityTracker;
    protected Interpolator mReboundInterpolator;

    //</editor-fold>

    //<editor-fold desc="功能属性">
    protected int[] mPrimaryColors;
    protected boolean mEnableRefresh = true;
    protected boolean mEnableLoadMore = false;
    protected boolean mEnableClipHeaderWhenFixedBehind = true;//当 Header FixedBehind 时候是否剪裁遮挡 Header
    protected boolean mEnableClipFooterWhenFixedBehind = true;//当 Footer FixedBehind 时候是否剪裁遮挡 Footer
    protected boolean mEnableHeaderTranslationContent = true;//是否启用内容视图拖动效果
    protected boolean mEnableFooterTranslationContent = true;//是否启用内容视图拖动效果
    protected boolean mEnableFooterFollowWhenLoadFinished = false;//是否在全部加载结束之后Footer跟随内容 1.0.4-6
    protected boolean mEnablePreviewInEditMode = true;//是否在编辑模式下开启预览功能
    protected boolean mEnableOverScrollBounce = true;//是否启用越界回弹
    protected boolean mEnableOverScrollDrag = true;//是否启用越界拖动（仿苹果效果）1.0.4-6
    protected boolean mEnableAutoLoadMore = true;//是否在列表滚动到底部时自动加载更多
    protected boolean mEnablePureScrollMode = false;//是否开启纯滚动模式
    protected boolean mEnableScrollContentWhenLoaded = true;//是否在加载更多完成之后滚动内容显示新数据
    protected boolean mEnableScrollContentWhenRefreshed = true;//是否在刷新完成之后滚动内容显示新数据
    protected boolean mEnableLoadMoreWhenContentNotFull = true;//在内容不满一页的时候，是否可以上拉加载更多
    protected boolean mDisableContentWhenRefresh = false;//是否开启在刷新时候禁止操作内容视图
    protected boolean mDisableContentWhenLoading = false;//是否开启在刷新时候禁止操作内容视图
    protected boolean mFooterNoMoreData = false;//数据是否全部加载完成，如果完成就不能在触发加载事件

    protected boolean mManualLoadMore = false;//是否手动设置过LoadMore，用于智能开启
    protected boolean mManualNestedScrolling = false;//是否手动设置过 NestedScrolling，用于智能开启
    protected boolean mManualHeaderTranslationContent = false;//是否手动设置过内容视图拖动效果
    //</editor-fold>

    //<editor-fold desc="监听属性">
    protected OnRefreshListener mRefreshListener;
    protected OnLoadMoreListener mLoadMoreListener;
    protected OnMultiPurposeListener mOnMultiPurposeListener;
    protected ScrollBoundaryDecider mScrollBoundaryDecider;
    //</editor-fold>

    //<editor-fold desc="嵌套滚动">
    protected int mTotalUnconsumed;
    protected boolean mNestedInProgress;
    protected int[] mParentOffsetInWindow = new int[2];
    protected NestedScrollingChildHelper mNestedChild = new NestedScrollingChildHelper(this);
    protected NestedScrollingParentHelper mNestedParent = new NestedScrollingParentHelper(this);
    //</editor-fold>

    //<editor-fold desc="内部视图">

    protected int mHeaderHeight;        //头部高度 和 头部高度状态
    protected DimensionStatus mHeaderHeightStatus = DimensionStatus.DefaultUnNotify;
    protected int mFooterHeight;        //底部高度 和 底部高度状态
    protected DimensionStatus mFooterHeightStatus = DimensionStatus.DefaultUnNotify;

    protected int mHeaderInsetStart;    // Header 起始位置便宜
    protected int mFooterInsetStart;    // Footer 起始位置便宜
    protected int mHeaderExtendHeight;  //扩展高度
    protected int mFooterExtendHeight;  //扩展高度

    protected float mHeaderMaxDragRate = 2.5f;  //最大拖动比率(最大高度/Header高度)
    protected float mFooterMaxDragRate = 2.5f;  //最大拖动比率(最大高度/Footer高度)
    protected float mHeaderTriggerRate = 1.0f;  //触发刷新距离 与 HeaderHeight 的比率
    protected float mFooterTriggerRate = 1.0f;  //触发加载距离 与 FooterHeight 的比率

    protected RefreshHeader mRefreshHeader;     //下拉头部视图
    protected RefreshFooter mRefreshFooter;     //上拉底部视图
    protected RefreshContent mRefreshContent;   //显示内容视图
    //</editor-fold>

    protected Paint mPaint;
    protected Handler mHandler;
    protected RefreshKernel mKernel;
    protected List<DelayedRunnable> mListDelayedRunnable;

    protected RefreshState mState = RefreshState.None;          //主状态
    protected RefreshState mViceState = RefreshState.None;      //副状态（主状态刷新时候的滚动状态）

    protected long mLastOpenTime = 0;                           //上一次 刷新或者加载 时间

    protected int mHeaderBackgroundColor = 0;                   //为Header绘制纯色背景
    protected int mFooterBackgroundColor = 0;

    protected boolean mHeaderNeedTouchEventWhenRefreshing;      //为游戏Header提供独立事件
    protected boolean mFooterNeedTouchEventWhenLoading;

    protected boolean mFooterLocked = false;//Footer 正在loading 的时候是否锁住 列表不能向上滚动

    protected static boolean sManualFooterCreator = false;
    protected static DefaultRefreshFooterCreator sFooterCreator = new DefaultRefreshFooterCreator() {
        @NonNull
        @Override
        public RefreshFooter createRefreshFooter(@NonNull Context context, @NonNull RefreshLayout layout) {
            return new BallPulseFooter(context);
        }
    };
    protected static DefaultRefreshHeaderCreator sHeaderCreator = new DefaultRefreshHeaderCreator() {
        @NonNull
        @Override
        public RefreshHeader createRefreshHeader(@NonNull Context context, @NonNull RefreshLayout layout) {
            return new BezierRadarHeader(context);
        }
    };

    //</editor-fold>

    //<editor-fold desc="构造方法 construction methods">
    public SmartRefreshLayout(Context context) {
        this(context, null);
    }

    public SmartRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmartRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setClipToPadding(false);

        DensityUtil density = new DensityUtil();
        ViewConfiguration configuration = ViewConfiguration.get(context);

        mScroller = new Scroller(context);
        mKernel = new RefreshKernelImpl();
        mVelocityTracker = VelocityTracker.obtain();
        mScreenHeightPixels = context.getResources().getDisplayMetrics().heightPixels;
        mReboundInterpolator = new ViscousFluidInterpolator();
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SmartRefreshLayout);

        setNestedScrollingEnabled(ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableNestedScrolling, false));
        mDragRate = ta.getFloat(R.styleable.SmartRefreshLayout_srlDragRate, mDragRate);
        mHeaderMaxDragRate = ta.getFloat(R.styleable.SmartRefreshLayout_srlHeaderMaxDragRate, mHeaderMaxDragRate);
        mFooterMaxDragRate = ta.getFloat(R.styleable.SmartRefreshLayout_srlFooterMaxDragRate, mFooterMaxDragRate);
        mHeaderTriggerRate = ta.getFloat(R.styleable.SmartRefreshLayout_srlHeaderTriggerRate, mHeaderTriggerRate);
        mFooterTriggerRate = ta.getFloat(R.styleable.SmartRefreshLayout_srlFooterTriggerRate, mFooterTriggerRate);
        mEnableRefresh = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableRefresh, mEnableRefresh);
        mReboundDuration = ta.getInt(R.styleable.SmartRefreshLayout_srlReboundDuration, mReboundDuration);
        mEnableLoadMore = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableLoadMore, mEnableLoadMore);
        mHeaderHeight = ta.getDimensionPixelOffset(R.styleable.SmartRefreshLayout_srlHeaderHeight, density.dip2px(100));
        mFooterHeight = ta.getDimensionPixelOffset(R.styleable.SmartRefreshLayout_srlFooterHeight, density.dip2px(60));
        mHeaderInsetStart = ta.getDimensionPixelOffset(R.styleable.SmartRefreshLayout_srlHeaderInsetStart, 0);
        mFooterInsetStart = ta.getDimensionPixelOffset(R.styleable.SmartRefreshLayout_srlFooterInsetStart, 0);
        mDisableContentWhenRefresh = ta.getBoolean(R.styleable.SmartRefreshLayout_srlDisableContentWhenRefresh, mDisableContentWhenRefresh);
        mDisableContentWhenLoading = ta.getBoolean(R.styleable.SmartRefreshLayout_srlDisableContentWhenLoading, mDisableContentWhenLoading);
        mEnableHeaderTranslationContent = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableHeaderTranslationContent, mEnableHeaderTranslationContent);
        mEnableFooterTranslationContent = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableFooterTranslationContent, mEnableFooterTranslationContent);
        mEnablePreviewInEditMode = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnablePreviewInEditMode, mEnablePreviewInEditMode);
        mEnableAutoLoadMore = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableAutoLoadMore, mEnableAutoLoadMore);
        mEnableOverScrollBounce = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableOverScrollBounce, mEnableOverScrollBounce);
        mEnablePureScrollMode = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnablePureScrollMode, mEnablePureScrollMode);
        mEnableScrollContentWhenLoaded = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableScrollContentWhenLoaded, mEnableScrollContentWhenLoaded);
        mEnableScrollContentWhenRefreshed = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableScrollContentWhenRefreshed, mEnableScrollContentWhenRefreshed);
        mEnableLoadMoreWhenContentNotFull = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableLoadMoreWhenContentNotFull, mEnableLoadMoreWhenContentNotFull);
        mEnableFooterFollowWhenLoadFinished = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableFooterFollowWhenLoadFinished, mEnableFooterFollowWhenLoadFinished);
        mEnableClipHeaderWhenFixedBehind = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableClipHeaderWhenFixedBehind, mEnableClipHeaderWhenFixedBehind);
        mEnableClipFooterWhenFixedBehind = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableClipFooterWhenFixedBehind, mEnableClipFooterWhenFixedBehind);
        mEnableOverScrollDrag = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableOverScrollDrag, mEnableOverScrollDrag);
        mFixedHeaderViewId = ta.getResourceId(R.styleable.SmartRefreshLayout_srlFixedHeaderViewId, View.NO_ID);
        mFixedFooterViewId = ta.getResourceId(R.styleable.SmartRefreshLayout_srlFixedFooterViewId, View.NO_ID);

        mManualLoadMore = ta.hasValue(R.styleable.SmartRefreshLayout_srlEnableLoadMore);
        mManualNestedScrolling = ta.hasValue(R.styleable.SmartRefreshLayout_srlEnableNestedScrolling);
        mManualHeaderTranslationContent = ta.hasValue(R.styleable.SmartRefreshLayout_srlEnableHeaderTranslationContent);
        mHeaderHeightStatus = ta.hasValue(R.styleable.SmartRefreshLayout_srlHeaderHeight) ? DimensionStatus.XmlLayoutUnNotify : mHeaderHeightStatus;
        mFooterHeightStatus = ta.hasValue(R.styleable.SmartRefreshLayout_srlFooterHeight) ? DimensionStatus.XmlLayoutUnNotify : mFooterHeightStatus;

        mHeaderExtendHeight = (int) Math.max((mHeaderHeight * (mHeaderMaxDragRate - 1)), 0);
        mFooterExtendHeight = (int) Math.max((mFooterHeight * (mFooterMaxDragRate - 1)), 0);

        int accentColor = ta.getColor(R.styleable.SmartRefreshLayout_srlAccentColor, 0);
        int primaryColor = ta.getColor(R.styleable.SmartRefreshLayout_srlPrimaryColor, 0);
        if (primaryColor != 0) {
            if (accentColor != 0) {
                mPrimaryColors = new int[]{primaryColor, accentColor};
            } else {
                mPrimaryColors = new int[]{primaryColor};
            }
        } else if (accentColor != 0) {
            mPrimaryColors = new int[]{0, accentColor};
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
        }

        int indexContent = -1;
        int[] indexArray = {1,0,2};

        for (int index : indexArray) {
            if (index < count) {
                View view = getChildAt(index);
                if (!(view instanceof RefreshInternal)) {
                    indexContent = index;
                }
                if (RefreshContentWrapper.isScrollableView(view)) {
                    indexContent = index;
                    break;
                }
            }
        }

        int indexHeader = -1;
        int indexFooter = -1;
        if (indexContent >= 0) {
            mRefreshContent = new RefreshContentWrapper(getChildAt(indexContent));
            if (indexContent == 1) {
                indexHeader = 0;
                if (count == 3) {
                    indexFooter = 2;
                }
            } else if (count == 2) {
                indexFooter = 1;
            }
        }

        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            if (i == indexHeader || (i != indexFooter && indexHeader == -1 && mRefreshHeader == null && view instanceof RefreshHeader)) {
                mRefreshHeader = (view instanceof RefreshHeader)? (RefreshHeader) view : new RefreshHeaderWrapper(view);
            } else if (i == indexFooter || (indexFooter == -1 && view instanceof RefreshFooter)) {
                mRefreshFooter = (view instanceof RefreshFooter)? (RefreshFooter) view : new RefreshFooterWrapper(view);
            } else if (mRefreshContent == null) {
                mRefreshContent = new RefreshContentWrapper(view);
            }
        }

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (!isInEditMode()) {

            if (mHandler == null) {
                mHandler = new Handler();
            }

            if (mListDelayedRunnable != null) {
                for (DelayedRunnable runnable : mListDelayedRunnable) {
                    mHandler.postDelayed(runnable, runnable.delayMillis);
                }
                mListDelayedRunnable.clear();
                mListDelayedRunnable = null;
            }

            if (mRefreshHeader == null) {
                setRefreshHeader(sHeaderCreator.createRefreshHeader(getContext(), this));
            }
            if (mRefreshFooter == null) {
                setRefreshFooter(sFooterCreator.createRefreshFooter(getContext(), this));
            } else {
                mEnableLoadMore = mEnableLoadMore || !mManualLoadMore;
            }

            if (mRefreshContent == null) {
                int padding = DensityUtil.dp2px(20);
                TextView errorView = new TextView(getContext());
                errorView.setTextColor(0xffff6600);
                errorView.setGravity(Gravity.CENTER);
                errorView.setTextSize(20);
                errorView.setPadding(padding, padding, padding, padding);
                errorView.setText(R.string.srl_content_empty);
                addView(errorView, MATCH_PARENT, MATCH_PARENT);
                mRefreshContent = new RefreshContentWrapper(errorView);
            }

            View fixedHeaderView = mFixedHeaderViewId > 0 ? findViewById(mFixedHeaderViewId) : null;
            View fixedFooterView = mFixedFooterViewId > 0 ? findViewById(mFixedFooterViewId) : null;

            mRefreshContent.setScrollBoundaryDecider(mScrollBoundaryDecider);
            mRefreshContent.setEnableLoadMoreWhenContentNotFull(mEnableLoadMoreWhenContentNotFull);
            mRefreshContent.setUpComponent(mKernel, fixedHeaderView, fixedFooterView);

            if (mSpinner != 0) {
                notifyStateChanged(RefreshState.None);
                mRefreshContent.moveSpinner(mSpinner = 0);
            }

            if (!mManualNestedScrolling && !isNestedScrollingEnabled()) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        for (ViewParent parent = getParent() ; parent != null ; parent = parent.getParent()) {
                            if (parent instanceof NestedScrollingParent) {
                                View target = SmartRefreshLayout.this;
                                //noinspection RedundantCast
                                if (((NestedScrollingParent)parent).onStartNestedScroll(target,target,ViewCompat.SCROLL_AXIS_VERTICAL)) {
                                    setNestedScrollingEnabled(true);
                                    mManualNestedScrolling = false;
                                    break;
                                }
                            }
                        }
                    }
                });
            }
        }

        if (mPrimaryColors != null) {
            if (mRefreshHeader != null) {
                mRefreshHeader.setPrimaryColors(mPrimaryColors);
            }
            if (mRefreshFooter != null) {
                mRefreshFooter.setPrimaryColors(mPrimaryColors);
            }
        }

        //重新排序
        if (mRefreshContent != null) {
            bringChildToFront(mRefreshContent.getView());
        }
        if (mRefreshHeader != null && mRefreshHeader.getSpinnerStyle() != SpinnerStyle.FixedBehind) {
            bringChildToFront(mRefreshHeader.getView());
        }
        if (mRefreshFooter != null && mRefreshFooter.getSpinnerStyle() != SpinnerStyle.FixedBehind) {
            bringChildToFront(mRefreshFooter.getView());
        }

    }

    @Override
    protected void onMeasure(final int widthMeasureSpec,final int heightMeasureSpec) {
        int minimumHeight = 0;
        final boolean isInEditMode = isInEditMode() && mEnablePreviewInEditMode;

        for (int i = 0, len = getChildCount(); i < len; i++) {
            View child = getChildAt(i);

            if (mRefreshHeader != null && mRefreshHeader.getView() == child) {
                final View headerView = mRefreshHeader.getView();
                final LayoutParams lp = (LayoutParams) headerView.getLayoutParams();
                final int widthSpec = getChildMeasureSpec(widthMeasureSpec, lp.leftMargin + lp.rightMargin, lp.width);
                int heightSpec = heightMeasureSpec;

                if (mHeaderHeightStatus.gteReplaceWith(DimensionStatus.XmlLayoutUnNotify)) {
                    heightSpec = makeMeasureSpec(Math.max(mHeaderHeight - lp.bottomMargin - lp.topMargin, 0), EXACTLY);
                    headerView.measure(widthSpec, heightSpec);
                } else if (mRefreshHeader.getSpinnerStyle() == SpinnerStyle.MatchLayout) {
                    int headerHeight = 0;
                    if (!mHeaderHeightStatus.notified) {
                        measureChild(headerView, widthSpec, makeMeasureSpec(Math.max(getSize(heightSpec) - lp.bottomMargin - lp.topMargin, 0), AT_MOST));
                        headerHeight = headerView.getMeasuredHeight();
                    }
                    headerView.measure(widthSpec, makeMeasureSpec(Math.max(getSize(heightSpec) - lp.bottomMargin - lp.topMargin, 0), EXACTLY));
                    if (headerHeight > 0 && headerHeight != headerView.getMeasuredHeight()) {
                        mHeaderHeight = headerHeight + lp.bottomMargin + lp.topMargin;
                    }
                } else if (lp.height > 0) {
                    if (mHeaderHeightStatus.canReplaceWith(DimensionStatus.XmlExactUnNotify)) {
                        mHeaderHeight = lp.height + lp.bottomMargin + lp.topMargin;
                        mHeaderHeightStatus = DimensionStatus.XmlExactUnNotify;
                    }
                    heightSpec = makeMeasureSpec(lp.height, EXACTLY);
                    headerView.measure(widthSpec, heightSpec);
                } else if (lp.height == WRAP_CONTENT) {
                    heightSpec = makeMeasureSpec(Math.max(getSize(heightMeasureSpec) - lp.bottomMargin - lp.topMargin, 0), AT_MOST);
                    headerView.measure(widthSpec, heightSpec);
                    int measuredHeight = headerView.getMeasuredHeight();
                    if (measuredHeight > 0 && mHeaderHeightStatus.canReplaceWith(DimensionStatus.XmlWrapUnNotify)) {
                        mHeaderHeightStatus = DimensionStatus.XmlWrapUnNotify;
                        mHeaderHeight = headerView.getMeasuredHeight() + lp.bottomMargin + lp.topMargin;
                    } else if (measuredHeight <= 0) {
                        heightSpec = makeMeasureSpec(Math.max(mHeaderHeight - lp.bottomMargin - lp.topMargin, 0), EXACTLY);
                        headerView.measure(widthSpec, heightSpec);
                    }
                } else if (lp.height == MATCH_PARENT) {
                    heightSpec = makeMeasureSpec(Math.max(mHeaderHeight - lp.bottomMargin - lp.topMargin, 0), EXACTLY);
                    headerView.measure(widthSpec, heightSpec);
                } else {
                    headerView.measure(widthSpec, heightSpec);
                }
                if (mRefreshHeader.getSpinnerStyle() == SpinnerStyle.Scale && !isInEditMode) {
                    final int height = Math.max(0, isEnableRefresh() ? mSpinner : 0);
                    heightSpec = makeMeasureSpec(Math.max(height - lp.bottomMargin - lp.topMargin, 0), EXACTLY);
                    headerView.measure(widthSpec, heightSpec);
                }

                if (!mHeaderHeightStatus.notified) {
                    mHeaderHeightStatus = mHeaderHeightStatus.notified();
                    mHeaderExtendHeight = (int) Math.max((mHeaderHeight * (mHeaderMaxDragRate - 1)), 0);
                    mRefreshHeader.onInitialized(mKernel, mHeaderHeight, mHeaderExtendHeight);
                }

                if (isInEditMode && isEnableRefresh()) {
                    minimumHeight += headerView.getMeasuredHeight();
                }
            }

            if (mRefreshFooter != null && mRefreshFooter.getView() == child) {
                final View footerView = mRefreshFooter.getView();
                final LayoutParams lp = (LayoutParams) footerView.getLayoutParams();
                final int widthSpec = getChildMeasureSpec(widthMeasureSpec, lp.leftMargin + lp.rightMargin, lp.width);
                int heightSpec = heightMeasureSpec;
                if (mFooterHeightStatus.gteReplaceWith(DimensionStatus.XmlLayoutUnNotify)) {
                    heightSpec = makeMeasureSpec(Math.max(mFooterHeight - lp.topMargin - lp.bottomMargin, 0), EXACTLY);
                    footerView.measure(widthSpec, heightSpec);
                } else if (mRefreshFooter.getSpinnerStyle() == SpinnerStyle.MatchLayout) {
                    int footerHeight = 0;
                    if (!mFooterHeightStatus.notified) {
                        measureChild(footerView, widthSpec, makeMeasureSpec(getSize(heightSpec) - lp.topMargin - lp.bottomMargin, AT_MOST));
                        footerHeight = footerView.getMeasuredHeight();
                    }
                    footerView.measure(widthSpec, makeMeasureSpec(getSize(heightSpec) - lp.topMargin - lp.bottomMargin, EXACTLY));
                    if (footerHeight > 0 && footerHeight != footerView.getMeasuredHeight()) {
                        mHeaderHeight = footerHeight + lp.topMargin + lp.bottomMargin;
                    }
                } else if (lp.height > 0) {
                    if (mFooterHeightStatus.canReplaceWith(DimensionStatus.XmlExactUnNotify)) {
                        mFooterHeight = lp.height + lp.topMargin + lp.bottomMargin;
                        mFooterHeightStatus = DimensionStatus.XmlExactUnNotify;
                    }
                    heightSpec = makeMeasureSpec(lp.height, EXACTLY);
                    footerView.measure(widthSpec, heightSpec);
                } else if (lp.height == WRAP_CONTENT) {
                    heightSpec = makeMeasureSpec(Math.max(getSize(heightMeasureSpec) - lp.topMargin - lp.bottomMargin, 0), AT_MOST);
                    footerView.measure(widthSpec, heightSpec);
                    int measuredHeight = footerView.getMeasuredHeight();
                    if (measuredHeight > 0 && mFooterHeightStatus.canReplaceWith(DimensionStatus.XmlWrapUnNotify)) {
                        mFooterHeightStatus = DimensionStatus.XmlWrapUnNotify;
                        mFooterHeight = footerView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
                    } else if (measuredHeight <= 0) {
                        heightSpec = makeMeasureSpec(Math.max(mFooterHeight - lp.topMargin - lp.bottomMargin, 0), EXACTLY);
                        footerView.measure(widthSpec, heightSpec);
                    }
                } else if (lp.height == MATCH_PARENT) {
                    heightSpec = makeMeasureSpec(Math.max(mFooterHeight - lp.topMargin - lp.bottomMargin, 0), EXACTLY);
                    footerView.measure(widthSpec, heightSpec);
                } else {
                    footerView.measure(widthSpec, heightSpec);
                }

                if (mRefreshFooter.getSpinnerStyle() == SpinnerStyle.Scale && !isInEditMode) {
                    final int height = Math.max(0, mEnableLoadMore ? -mSpinner : 0);
                    heightSpec = makeMeasureSpec(Math.max(height - lp.topMargin - lp.bottomMargin, 0), EXACTLY);
                    footerView.measure(widthSpec, heightSpec);
                }

                if (!mFooterHeightStatus.notified) {
                    mFooterHeightStatus = mFooterHeightStatus.notified();
                    mFooterExtendHeight = (int) Math.max((mFooterHeight * (mFooterMaxDragRate - 1)), 0);
                    mRefreshFooter.onInitialized(mKernel, mFooterHeight, mFooterExtendHeight);
                }

                if (isInEditMode && isEnableLoadMore()) {
                    minimumHeight += footerView.getMeasuredHeight();
                }
            }

            if (mRefreshContent != null && mRefreshContent.getView() == child) {
                final View contentView = mRefreshContent.getView();
                final LayoutParams lp = (LayoutParams) contentView.getLayoutParams();
                final int widthSpec = getChildMeasureSpec(widthMeasureSpec,
                        getPaddingLeft() + getPaddingRight() +
                                lp.leftMargin + lp.rightMargin, lp.width);
                final int heightSpec = getChildMeasureSpec(heightMeasureSpec,
                        getPaddingTop() + getPaddingBottom() +
                                lp.topMargin + lp.bottomMargin +
                                ((isInEditMode && isEnableRefresh() && mRefreshHeader != null && (mEnableHeaderTranslationContent || mRefreshHeader.getSpinnerStyle() == SpinnerStyle.FixedBehind)) ? mHeaderHeight : 0) +
                                ((isInEditMode && isEnableLoadMore() && mRefreshFooter != null && (mEnableFooterTranslationContent || mRefreshFooter.getSpinnerStyle() == SpinnerStyle.FixedBehind)) ? mFooterHeight : 0), lp.height);
                contentView.measure(widthSpec, heightSpec);
                mRefreshContent.onInitialHeaderAndFooter(mHeaderHeight, mFooterHeight);
                minimumHeight += contentView.getMeasuredHeight();
            }
        }

        setMeasuredDimension(resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec), resolveSize(minimumHeight, heightMeasureSpec));

        mLastTouchX = getMeasuredWidth() / 2;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int paddingLeft = getPaddingLeft();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();


        for (int i = 0, len = getChildCount(); i < len; i++) {
            View child = getChildAt(i);

            if (mRefreshContent != null && mRefreshContent.getView() == child) {
                boolean isPreviewMode = isInEditMode() && mEnablePreviewInEditMode && isEnableRefresh() && mRefreshHeader != null;
                final View contentView = mRefreshContent.getView();
                final LayoutParams lp = (LayoutParams) contentView.getLayoutParams();
                int left = paddingLeft + lp.leftMargin;
                int top = paddingTop + lp.topMargin;
                int right = left + contentView.getMeasuredWidth();
                int bottom = top + contentView.getMeasuredHeight();
                if (isPreviewMode && (mEnableHeaderTranslationContent || mRefreshHeader.getSpinnerStyle() == SpinnerStyle.FixedBehind)) {
                    top = top + mHeaderHeight;
                    bottom = bottom + mHeaderHeight;
                }

                contentView.layout(left, top, right, bottom);
            }
            if (mRefreshHeader != null && mRefreshHeader.getView() == child) {
                boolean isPreviewMode = isInEditMode() && mEnablePreviewInEditMode && isEnableRefresh();
                final View headerView = mRefreshHeader.getView();
                final LayoutParams lp = (LayoutParams) headerView.getLayoutParams();
                int left = lp.leftMargin;
                int top = lp.topMargin + mHeaderInsetStart;
                int right = left + headerView.getMeasuredWidth();
                int bottom = top + headerView.getMeasuredHeight();
                if (!isPreviewMode) {
                    if (mRefreshHeader.getSpinnerStyle() == SpinnerStyle.Translate) {
                        top = top - mHeaderHeight;
                        bottom = bottom - mHeaderHeight;
                        /*
                         * SpinnerStyle.Scale  headerView.getMeasuredHeight() 已经重复处理
                         **/
//                    } else if (mRefreshHeader.getSpinnerStyle() == SpinnerStyle.Scale && mSpinner > 0) {
//                        bottom = top + Math.max(Math.max(0, isEnableRefresh() ? mSpinner : 0) - lp.bottomMargin - lp.topMargin, 0);
                    }
                }
                headerView.layout(left, top, right, bottom);
            }
            if (mRefreshFooter != null && mRefreshFooter.getView() == child) {
                final boolean isPreviewMode = isInEditMode() && mEnablePreviewInEditMode && isEnableLoadMore();
                final View footerView = mRefreshFooter.getView();
                final LayoutParams lp = (LayoutParams) footerView.getLayoutParams();
                final SpinnerStyle style = mRefreshFooter.getSpinnerStyle();
                int left = lp.leftMargin;
                int top = lp.topMargin + getMeasuredHeight() - mFooterInsetStart;

                if (isPreviewMode
                        || style == SpinnerStyle.FixedFront
                        || style == SpinnerStyle.FixedBehind) {
                    top = top - mFooterHeight;
                } else if (style == SpinnerStyle.Scale && mSpinner < 0) {
                    top = top - Math.max(isEnableLoadMore() ? -mSpinner : 0, 0);
                }

                int right = left + footerView.getMeasuredWidth();
                int bottom = top + footerView.getMeasuredHeight();
                footerView.layout(left, top, right, bottom);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        moveSpinner(0, false);
        notifyStateChanged(RefreshState.None);
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        mManualLoadMore = true;
        mManualNestedScrolling = true;
        animationRunnable = null;
        if (reboundAnimator != null) {
            reboundAnimator.removeAllListeners();
            reboundAnimator.removeAllUpdateListeners();
            reboundAnimator.cancel();
            reboundAnimator = null;
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        View contentView = mRefreshContent != null ? mRefreshContent.getView() : null;
        if (mRefreshHeader != null && mRefreshHeader.getView() == child) {
            if (!isEnableRefresh() || (!mEnablePreviewInEditMode && isInEditMode())) {
                return true;
            }
            if (contentView != null) {
                int bottom = Math.max(contentView.getTop() + contentView.getPaddingTop() + mSpinner, child.getTop());
                if (mHeaderBackgroundColor != 0 && mPaint != null) {
                    mPaint.setColor(mHeaderBackgroundColor);
                    if (mRefreshHeader.getSpinnerStyle() == SpinnerStyle.Scale) {
                        bottom = child.getBottom();
                    } else if (mRefreshHeader.getSpinnerStyle() == SpinnerStyle.Translate) {
                        bottom = child.getBottom() + mSpinner;
                    }
                    canvas.drawRect(child.getLeft(), child.getTop(), child.getRight(), bottom, mPaint);
                }
                if (mEnableClipHeaderWhenFixedBehind && mRefreshHeader.getSpinnerStyle() == SpinnerStyle.FixedBehind) {
                    canvas.save();
                    canvas.clipRect(child.getLeft(), child.getTop(), child.getRight(), bottom);
                    boolean ret = super.drawChild(canvas, child, drawingTime);
                    canvas.restore();
                    return ret;
                }
            }
        }
        if (mRefreshFooter != null && mRefreshFooter.getView() == child) {
            if (!isEnableLoadMore() || (!mEnablePreviewInEditMode && isInEditMode())) {
                return true;
            }
            if (contentView != null) {
                int top = Math.min(contentView.getBottom() - contentView.getPaddingBottom() + mSpinner, child.getBottom());
                if (mFooterBackgroundColor != 0 && mPaint != null) {
                    mPaint.setColor(mFooterBackgroundColor);
                    if (mRefreshFooter.getSpinnerStyle() == SpinnerStyle.Scale) {
                        top = child.getTop();
                    } else if (mRefreshFooter.getSpinnerStyle() == SpinnerStyle.Translate) {
                        top = child.getTop() + mSpinner;
                    }
                    canvas.drawRect(child.getLeft(), top, child.getRight(), child.getBottom(), mPaint);
                }
                if (mEnableClipFooterWhenFixedBehind && mRefreshFooter.getSpinnerStyle() == SpinnerStyle.FixedBehind) {
                    canvas.save();
                    canvas.clipRect(child.getLeft(), top, child.getRight(), child.getBottom());
                    boolean ret = super.drawChild(canvas, child, drawingTime);
                    canvas.restore();
                    return ret;
                }
            }

        }
        return super.drawChild(canvas, child, drawingTime);
    }


    //<editor-fold desc="惯性计算">
    protected boolean mVerticalPermit = false;                  //竖直通信证（用于特殊事件的权限判定）
    @Override
    public void computeScroll() {
        int lastCurY = mScroller.getCurrY();
        if (mScroller.computeScrollOffset()) {
            int finalY = mScroller.getFinalY();
            if ((finalY < 0 && (mEnableOverScrollDrag || isEnableRefresh()) && mRefreshContent.canRefresh())
                    || (finalY > 0 && (mEnableOverScrollDrag || isEnableLoadMore()) && mRefreshContent.canLoadMore())) {
                if(mVerticalPermit) {
                    float velocity;
                    if (Build.VERSION.SDK_INT >= 14) {
                        velocity = finalY > 0 ? -mScroller.getCurrVelocity() : mScroller.getCurrVelocity();
                    } else {
                        velocity = 1f * (mScroller.getCurrY() - finalY) / Math.max((mScroller.getDuration() - mScroller.timePassed()), 1);
                    }
                    animSpinnerBounce(velocity);
                }
                mScroller.forceFinished(true);
            } else {
                mVerticalPermit = true;//打开竖直通行证
                invalidate();
            }
        }
    }
    //</editor-fold>

    //</editor-fold>

    //<editor-fold desc="滑动判断 judgement of slide">
    protected MotionEvent mFalsifyEvent = null;

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {

        //<editor-fold desc="多点触摸计算代码">
        //---------------------------------------------------------------------------
        //多点触摸计算代码
        //---------------------------------------------------------------------------
        final int action = e.getActionMasked();
        final boolean pointerUp = action == MotionEvent.ACTION_POINTER_UP;
        final int skipIndex = pointerUp ? e.getActionIndex() : -1;

        // Determine focal point
        float sumX = 0, sumY = 0;
        final int count = e.getPointerCount();
        for (int i = 0; i < count; i++) {
            if (skipIndex == i) continue;
            sumX += e.getX(i);
            sumY += e.getY(i);
        }
        final int div = pointerUp ? count - 1 : count;
        final float touchX = sumX / div;
        final float touchY = sumY / div;
        if ((action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_POINTER_DOWN)
                && mIsBeingDragged) {
            mTouchY += touchY - mLastTouchY;
        }
        mLastTouchX = touchX;
        mLastTouchY = touchY;
        //---------------------------------------------------------------------------
        //</editor-fold>

        if (mNestedInProgress) {//嵌套滚动时，补充竖直方向不滚动，但是水平方向滚动，需要通知 onHorizontalDrag
            int totalUnconsumed = this.mTotalUnconsumed;
            boolean ret = super.dispatchTouchEvent(e);
            //noinspection ConstantConditions
            if (action == MotionEvent.ACTION_MOVE) {
                if (totalUnconsumed == mTotalUnconsumed) {
                    final int offsetX = (int) mLastTouchX;
                    final int offsetMax = getWidth();
                    final float percentX = mLastTouchX / (offsetMax == 0 ? 1 : offsetMax);
                    if (isEnableRefresh() && mSpinner > 0 && mRefreshHeader != null && mRefreshHeader.isSupportHorizontalDrag()) {
                        mRefreshHeader.onHorizontalDrag(percentX, offsetX, offsetMax);
                    } else if (isEnableLoadMore() && mSpinner < 0 && mRefreshFooter != null && mRefreshFooter.isSupportHorizontalDrag()) {
                        mRefreshFooter.onHorizontalDrag(percentX, offsetX, offsetMax);
                    }
                }
            }
            return ret;
        } else if (!isEnabled()
                || (!isEnableRefresh() && !isEnableLoadMore() && !mEnableOverScrollDrag)
                || (mHeaderNeedTouchEventWhenRefreshing && ((mState.opening || mState.finishing) && mState.isHeader()))
                || (mFooterNeedTouchEventWhenLoading && ((mState.opening || mState.finishing) && mState.isFooter()))) {
            return super.dispatchTouchEvent(e);
        }

        if (interceptByAnimator(action) || mState.finishing
                || (mState == RefreshState.Loading && mDisableContentWhenLoading)
                || (mState == RefreshState.Refreshing && mDisableContentWhenRefresh)) {
            return false;
        }

        if (mRefreshContent != null) {
            //为 RefreshContent 传递当前触摸事件的坐标，用于智能判断对应坐标位置View的滚动边界和相关信息
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mVelocityTracker.clear();
                    mVelocityTracker.addMovement(e);
                    mRefreshContent.onActionDown(e);
                    mScroller.forceFinished(true);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (!mNestedInProgress) {
                        mVelocityTracker.addMovement(e);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (!mNestedInProgress) {
                        mVelocityTracker.addMovement(e);
                        mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                    }
                case MotionEvent.ACTION_CANCEL:
                    mRefreshContent.onActionUpOrCancel();
            }
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mTouchX = touchX;
                mTouchY = touchY;
                mLastSpinner = 0;
                mTouchSpinner = mSpinner;
                mIsBeingDragged = false;
                mSuperDispatchTouchEvent = super.dispatchTouchEvent(e);
                if (mState == RefreshState.TwoLevel && mTouchY < 5 * getMeasuredHeight() / 6) {
                    mDragDirection = 'h';//二级刷新标记水平滚动来禁止拖动
                    return mSuperDispatchTouchEvent;
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                float dx = touchX - mTouchX;
                float dy = touchY - mTouchY;
                if (!mIsBeingDragged && mDragDirection != 'h' && mRefreshContent != null) {//没有拖动之前，检测  canRefresh canLoadMore 来开启拖动
                    if (mDragDirection == 'v' || (Math.abs(dy) >= mTouchSlop && Math.abs(dx) < Math.abs(dy))) {//滑动允许最大角度为45度
                        mDragDirection = 'v';
                        if (dy > 0 && (mSpinner < 0 || ((mEnableOverScrollDrag || isEnableRefresh()) && mRefreshContent.canRefresh()))) {
                            mIsBeingDragged = true;
                            mTouchY = touchY - mTouchSlop;//调整 mTouchSlop 偏差
                        } else if (dy < 0 && (mSpinner > 0 || ((mEnableOverScrollDrag || isEnableLoadMore()) && ((mState==RefreshState.Loading&&mFooterLocked)||mRefreshContent.canLoadMore())))) {
                            mIsBeingDragged = true;
                            mTouchY = touchY + mTouchSlop;//调整 mTouchSlop 偏差
                        }
                        if (mIsBeingDragged) {
                            dy = touchY - mTouchY;//调整 mTouchSlop 偏差 重新计算 dy
                            if (mSuperDispatchTouchEvent) {//如果父类拦截了事件，发送一个取消事件通知
                                e.setAction(MotionEvent.ACTION_CANCEL);
                                super.dispatchTouchEvent(e);
                            }
                            if (mSpinner > 0 || (mSpinner == 0 && dy > 0)) {
                                mKernel.setState(RefreshState.PullDownToRefresh);
                            } else {
                                mKernel.setState(RefreshState.PullUpToLoad);
                            }
                            getParent().requestDisallowInterceptTouchEvent(true);//通知父控件不要拦截事件
                        }
                    } else if (Math.abs(dx) >= mTouchSlop && Math.abs(dx) > Math.abs(dy) && mDragDirection != 'v') {
                        mDragDirection = 'h';//标记为水平拖动，将无法再次触发 下拉刷新 上拉加载
                    }
                }
                if (mIsBeingDragged) {
                    int spinner = (int) dy + mTouchSpinner;
                    if ((mViceState.isHeader() && (spinner < 0 || mLastSpinner < 0)) || (mViceState.isFooter() && (spinner > 0 || mLastSpinner > 0))) {
                        mLastSpinner = spinner;
                        long time = e.getEventTime();
                        if (mFalsifyEvent == null) {
                            mFalsifyEvent = obtain(time, time, MotionEvent.ACTION_DOWN, mTouchX + dx, mTouchY, 0);
                            super.dispatchTouchEvent(mFalsifyEvent);
                        }
                        MotionEvent em = obtain(time, time, MotionEvent.ACTION_MOVE, mTouchX + dx, mTouchY + spinner, 0);
                        super.dispatchTouchEvent(em);
                        if (mFooterLocked && dy > mTouchSlop && mSpinner < 0) {
                            mFooterLocked = false;//内容向下滚动时 解锁Footer 的锁定
                        }
                        if (spinner > 0 && ((mEnableOverScrollDrag || isEnableRefresh()) && mRefreshContent.canRefresh())) {
                            mTouchY = mLastTouchY = touchY;
                            mTouchSpinner = spinner = 0;
                            mKernel.setState(RefreshState.PullDownToRefresh);
                        } else if (spinner < 0 && ((mEnableOverScrollDrag || isEnableLoadMore()) && mRefreshContent.canLoadMore())) {
                            mTouchY = mLastTouchY = touchY;
                            mTouchSpinner = spinner = 0;
                            mKernel.setState(RefreshState.PullUpToLoad);
                        }
                        if ((mViceState.isHeader() && spinner < 0) || (mViceState.isFooter() && spinner > 0)) {
                            if (mSpinner != 0) {
                                moveSpinnerInfinitely(0);
                            }
                            return true;
                        } else if (mFalsifyEvent != null) {
                            mFalsifyEvent = null;
                            em.setAction(MotionEvent.ACTION_CANCEL);
                            super.dispatchTouchEvent(em);
                        }
                        em.recycle();
                    }
                    moveSpinnerInfinitely(spinner);
                    return true;
                } else if (mFooterLocked && dy > mTouchSlop && mSpinner < 0) {
                    mFooterLocked = false;//内容向下滚动时 解锁Footer 的锁定
                }
                break;
            case MotionEvent.ACTION_UP:
                startFlingIfNeed(null);
            case MotionEvent.ACTION_CANCEL:
                mDragDirection = 'n';//关闭拖动方向
                if (mFalsifyEvent != null) {
                    mFalsifyEvent.recycle();
                    mFalsifyEvent = null;
                    long time = e.getEventTime();
                    MotionEvent ec = obtain(time, time, action, mTouchX, touchY, 0);
                    super.dispatchTouchEvent(ec);
                    ec.recycle();
                }
                overSpinner();
                if (mIsBeingDragged) {
                    mIsBeingDragged = false;//关闭拖动状态
                    return true;
                }
                break;
        }
        return super.dispatchTouchEvent(e);
    }

    protected boolean startFlingIfNeed(Float flingVelocity) {
        final float velocity = flingVelocity == null ? mVelocityTracker.getYVelocity() : flingVelocity;
        if (Math.abs(velocity) > mMinimumVelocity) {
            if ((velocity < 0 && ((mEnableOverScrollBounce && (mEnableOverScrollDrag || isEnableLoadMore())) || (mState == RefreshState.Loading && mSpinner >= 0) || (mEnableAutoLoadMore&&isEnableLoadMore())))
                    || (velocity > 0 && ((mEnableOverScrollBounce && (mEnableOverScrollDrag || isEnableRefresh())) || (mState == RefreshState.Refreshing && mSpinner <= 0)))) {
                mVerticalPermit = false;//关闭竖直通行证
                mScroller.fling(0, 0, 0, (int) -velocity, 0, 0, -Integer.MAX_VALUE, Integer.MAX_VALUE);
                mScroller.computeScrollOffset();
                invalidate();
            }
            if (velocity * mSpinner < 0 && mState != RefreshState.TwoLevel && mState != mViceState) {
                /*
                 * 解决刷新时，惯性丢失问题
                 * 速度方向匹配并且不能是二楼打开状态
                 * 副操作状态:loading refreshing noMoreData
                 */
                animationRunnable = new FlingRunnable(velocity).start();
                return true;
            }
        }
        return false;
    }

    /*
     * 在动画执行时，触摸屏幕，打断动画，转为拖动状态
     */
    protected boolean interceptByAnimator(int action) {
        if (action == MotionEvent.ACTION_DOWN) {
            animationRunnable = null;
            if (reboundAnimator != null) {
                if (mState.finishing) {
                    return true;
                }
                if (mState == RefreshState.PullDownCanceled) {
                    mKernel.setState(RefreshState.PullDownToRefresh);
                } else if (mState == RefreshState.PullUpCanceled) {
                    mKernel.setState(RefreshState.PullUpToLoad);
                }
                reboundAnimator.cancel();
                reboundAnimator = null;
            }
        }
        return reboundAnimator != null;
    }

    /*
     * 这段代码来自谷歌官方的 SwipeRefreshLayout
     * 应用场景已经在英文注释中解释清楚
     * 大部分第三方下拉刷新库都保留了这段代码，本库也不例外
     */
    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        // if this is a List < L or another view that doesn't support nested
        // scrolling, ignore this request so that the vertical scroll event
        // isn't stolen
        View target = mRefreshContent.getScrollableView();
        if ((android.os.Build.VERSION.SDK_INT >= 21 || !(target instanceof AbsListView))
                && (target == null || ViewCompat.isNestedScrollingEnabled(target))) {
            super.requestDisallowInterceptTouchEvent(disallowIntercept);
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
            mViceState = state;
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

    protected void setStateDirectLoading() {
        if (mState != RefreshState.Loading) {
            mLastOpenTime = currentTimeMillis();
//            if (mState != RefreshState.LoadReleased) {
//                if (mState != RefreshState.ReleaseToLoad) {
//                    if (mState != RefreshState.PullUpToLoad) {
//                        mKernel.setState(RefreshState.PullUpToLoad);
//                    }
//                    mKernel.setState(RefreshState.ReleaseToLoad);
//                }
//                notifyStateChanged(RefreshState.LoadReleased);
//                if (mRefreshFooter != null) {
//                    mRefreshFooter.onReleased(this, mFooterHeight, mFooterExtendHeight);
//                }
//            }
            mFooterLocked = true;
            notifyStateChanged(RefreshState.Loading);
            if (mLoadMoreListener != null) {
                mLoadMoreListener.onLoadMore(this);
            } else if (mOnMultiPurposeListener == null) {
                finishLoadMore(2000);
            }
            if (mRefreshFooter != null) {
                mRefreshFooter.onStartAnimator(this, mFooterHeight, mFooterExtendHeight);
            }
            if (mOnMultiPurposeListener != null) {
                mOnMultiPurposeListener.onLoadMore(this);
                mOnMultiPurposeListener.onFooterStartAnimator(mRefreshFooter, mFooterHeight, mFooterExtendHeight);
            }
        }
    }

    protected void setStateLoading() {
        AnimatorListenerAdapter listener = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setStateDirectLoading();
            }
        };
        notifyStateChanged(RefreshState.LoadReleased);
        ValueAnimator animator = animSpinner(-mFooterHeight);
        if (animator != null) {
            animator.addListener(listener);
        }
        if (mRefreshFooter != null) {
            //onReleased 的执行顺序定在 animSpinner 之后 onAnimationEnd 之前
            // 这样 onReleased 内部 可以做出 对 前面 animSpinner 的覆盖 操作
            mRefreshFooter.onReleased(this, mFooterHeight, mFooterExtendHeight);
        }
        if (mOnMultiPurposeListener != null) {
            //同 mRefreshFooter.onReleased 一致
            mOnMultiPurposeListener.onFooterReleased(mRefreshFooter, mFooterHeight, mFooterExtendHeight);
        }
        if (animator == null) {
            //onAnimationEnd 会改变状态为 loading 必须在 onReleased 之后调用
            listener.onAnimationEnd(null);
        }
    }

    protected void setStateRefreshing() {
        AnimatorListenerAdapter listener = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLastOpenTime = currentTimeMillis();
                notifyStateChanged(RefreshState.Refreshing);
                if (mRefreshListener != null) {
                    mRefreshListener.onRefresh(SmartRefreshLayout.this);
                } else if (mOnMultiPurposeListener == null) {
                    finishRefresh(3000);
                }
                if (mRefreshHeader != null) {
                    mRefreshHeader.onStartAnimator(SmartRefreshLayout.this, mHeaderHeight, mHeaderExtendHeight);
                }
                if (mOnMultiPurposeListener != null) {
                    mOnMultiPurposeListener.onRefresh(SmartRefreshLayout.this);
                    mOnMultiPurposeListener.onHeaderStartAnimator(mRefreshHeader, mHeaderHeight, mHeaderExtendHeight);
                }
            }
        };
        notifyStateChanged(RefreshState.RefreshReleased);
        ValueAnimator animator = animSpinner(mHeaderHeight);
        if (animator != null) {
            animator.addListener(listener);
        }
        if (mRefreshHeader != null) {
            //onReleased 的执行顺序定在 animSpinner 之后 onAnimationEnd 之前
            // 这样 onRefreshReleased内部 可以做出 对 前面 animSpinner 的覆盖 操作
            mRefreshHeader.onReleased(this, mHeaderHeight, mHeaderExtendHeight);
        }
        if (mOnMultiPurposeListener != null) {
            //同 mRefreshHeader.onReleased 一致
            mOnMultiPurposeListener.onHeaderReleased(mRefreshHeader, mHeaderHeight, mHeaderExtendHeight);
        }
        if (animator == null) {
            //onAnimationEnd 会改变状态为 Refreshing 必须在 onReleased 之后调用
            listener.onAnimationEnd(null);
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

    protected void setViceState(RefreshState state) {
        if (mState.dragging && mState.isHeader() != state.isHeader()) {
            notifyStateChanged(RefreshState.None);
        }
        if (mViceState != state) {
            mViceState = state;
        }
    }

    //</editor-fold>

    //<editor-fold desc="视图位移 displacement">

    //<editor-fold desc="动画监听 Animator Listener">
    protected Runnable animationRunnable;
    protected ValueAnimator reboundAnimator;
    protected class FlingRunnable implements Runnable {
        int mOffset;
        int mFrame = 0;
        int mFrameDelay = 10;
        float mVelocity;
        float mDamping = 0.95f;
        long mLastTime = AnimationUtils.currentAnimationTimeMillis();

        FlingRunnable(float velocity) {
            mVelocity = velocity;
            mOffset = mSpinner;
        }

        public Runnable start() {
            if (mState.finishing) {
                return null;
            }
            if (mSpinner != 0 && (!(mState.opening || (mFooterNoMoreData && mEnableFooterFollowWhenLoadFinished && isEnableLoadMore()))
                    || ((mState == RefreshState.Loading || (mFooterNoMoreData && mEnableFooterFollowWhenLoadFinished && isEnableLoadMore())) && mSpinner < -mFooterHeight)
                    || (mState == RefreshState.Refreshing && mSpinner > mHeaderHeight))) {
                int frame = 0;
                int offset = mSpinner;
                int spinner = mSpinner;
                float velocity = mVelocity;
                while (spinner * offset > 0) {
                    velocity *= Math.pow(mDamping, ++frame);
                    float velocityFrame = (velocity * (1f * mFrameDelay / 1000));
                    if (Math.abs(velocityFrame) < 1) {
                        if (!mState.opening
                                || (mState == RefreshState.Refreshing&&offset > mHeaderHeight)
                                || (mState != RefreshState.Refreshing&&offset < -mFooterHeight)) {
                            return null;
                        }
                        break;
                    }
                    offset += velocityFrame;
                }
            }
            postDelayed(this, mFrameDelay);
            return this;
        }

        @Override
        public void run() {
            if (animationRunnable == this && !mState.finishing) {
                mVelocity *= Math.pow(mDamping, ++mFrame);
                long now = AnimationUtils.currentAnimationTimeMillis();
                long span = now - mLastTime;
                float velocity = (mVelocity * (1f * span / 1000));
                if (Math.abs(velocity) > 1) {
                    mLastTime = now;
                    mOffset += velocity;
                    if (mSpinner * mOffset > 0) {
                        moveSpinner(mOffset, false);
                        postDelayed(this, mFrameDelay);
                    } else {
                        animationRunnable = null;
                        moveSpinner(0, false);
                        mRefreshContent.fling((int) -mVelocity);
                        if (mFooterLocked && velocity > 0) {
                            mFooterLocked = false;
                        }
                    }
                } else {
                    animationRunnable = null;
                }
            }
        }
    }
    protected class BounceRunnable implements Runnable {
        int mFrame = 0;
        int mFrameDelay = 10;
        int mSmoothDistance;
        long mLastTime;
        float mOffset = 0;
        float mVelocity;
        BounceRunnable(float velocity, int smoothDistance){
            mVelocity = velocity;
            mSmoothDistance = smoothDistance;
            mLastTime = AnimationUtils.currentAnimationTimeMillis();
            postDelayed(this, mFrameDelay);
        }
        @Override
        public void run() {
            if (animationRunnable == this && !mState.finishing) {
                if (Math.abs(mSpinner) >= Math.abs(mSmoothDistance)) {
                    if (mSmoothDistance != 0) {
                        mVelocity *= Math.pow(0.45f, ++mFrame);//刷新、加载时回弹滚动数度衰减
                    } else {
                        mVelocity *= Math.pow(0.85f, ++mFrame);//回弹滚动数度衰减
                    }
                } else {
                    mVelocity *= Math.pow(0.95f, ++mFrame);//平滑滚动数度衰减
                }
                long now = AnimationUtils.currentAnimationTimeMillis();
                float t = 1f * (now - mLastTime) / 1000;
                float velocity = mVelocity * t;
                if (Math.abs(velocity) >= 1) {
                    mLastTime = now;
                    mOffset += velocity;
                    moveSpinnerInfinitely(mOffset);
                    postDelayed(this, mFrameDelay);
                } else {
                    animationRunnable = null;
                    if (Math.abs(mSpinner) >= Math.abs(mSmoothDistance)) {
                        int duration = 10 * Math.min(Math.max((int) DensityUtil.px2dp(Math.abs(mSpinner-mSmoothDistance)), 30), 100);
                        animSpinner(mSmoothDistance, 0, mReboundInterpolator, duration);
                    }
                }
            }
        }
    }
    //</editor-fold>

    protected ValueAnimator animSpinner(int endSpinner) {
        return animSpinner(endSpinner, 0, mReboundInterpolator, mReboundDuration);
    }

    /*
     * 执行回弹动画
     */
    protected ValueAnimator animSpinner(int endSpinner, int startDelay, Interpolator interpolator, int duration) {
        if (mSpinner != endSpinner) {
            if (reboundAnimator != null) {
                reboundAnimator.cancel();
            }
            animationRunnable = null;
            reboundAnimator = ValueAnimator.ofInt(mSpinner, endSpinner);
            reboundAnimator.setDuration(duration);
            reboundAnimator.setInterpolator(interpolator);
            reboundAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationEnd(animation);
                }
                @Override
                public void onAnimationEnd(Animator animation) {
                    reboundAnimator = null;
                    if (mSpinner == 0) {
                        if (mState != RefreshState.None && !mState.opening) {
                            notifyStateChanged(RefreshState.None);
                        }
                    } else if (mState != mViceState) {
                        setViceState(mState);
                    }
                }
            });
            reboundAnimator.addUpdateListener(new AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    moveSpinner((int) animation.getAnimatedValue(), true);
                }
            });
            reboundAnimator.setStartDelay(startDelay);
            reboundAnimator.start();
            return reboundAnimator;
        }
        return null;
    }

    /*
     * 越界回弹动画
     */
    protected void animSpinnerBounce(final float velocity) {
        if (reboundAnimator == null) {
            if (velocity > 0 && (mState == RefreshState.Refreshing || mState == RefreshState.TwoLevel)) {
                animationRunnable = new BounceRunnable(velocity, mHeaderHeight);
            } else if (velocity < 0 && (mState == RefreshState.Loading
                    || (mEnableFooterFollowWhenLoadFinished && mFooterNoMoreData && isEnableLoadMore())
                    || (mEnableAutoLoadMore && !mFooterNoMoreData && isEnableLoadMore() && mState != RefreshState.Refreshing))) {
                animationRunnable = new BounceRunnable(velocity, -mFooterHeight);
            } else if (mSpinner == 0 && mEnableOverScrollBounce) {
                animationRunnable = new BounceRunnable(velocity, 0);
            }
        }
    }

    /*
     * 手势拖动结束
     * 开始执行回弹动画
     */
    protected void overSpinner() {
        if (mState == RefreshState.TwoLevel) {
            if (mVelocityTracker.getYVelocity() > -1000 && mSpinner > getMeasuredHeight() / 2) {
                ValueAnimator animator = animSpinner(getMeasuredHeight());
                if (animator != null) {
                    animator.setDuration(mFloorDuration);
                }
            } else if (mIsBeingDragged) {
                mKernel.finishTwoLevel();
            }
        } else if (mState == RefreshState.Loading
                || (mEnableFooterFollowWhenLoadFinished && mFooterNoMoreData && mSpinner < 0 && isEnableLoadMore())) {
            if (mSpinner < -mFooterHeight) {
                animSpinner(-mFooterHeight);
            } else if (mSpinner > 0) {
                animSpinner(0);
            }
        } else if (mState == RefreshState.Refreshing) {
            if (mSpinner > mHeaderHeight) {
                animSpinner(mHeaderHeight);
            } else if (mSpinner < 0) {
                animSpinner(0);
            }
        } else if (mState == RefreshState.PullDownToRefresh) {
            mKernel.setState(RefreshState.PullDownCanceled);
        } else if (mState == RefreshState.PullUpToLoad) {
            mKernel.setState(RefreshState.PullUpCanceled);
        } else if (mState == RefreshState.ReleaseToRefresh) {
            setStateRefreshing();
        } else if (mState == RefreshState.ReleaseToLoad) {
            setStateLoading();
        } else if (mState == RefreshState.ReleaseToTwoLevel) {
            mKernel.setState(RefreshState.TwoLevelReleased);
        } else if (mSpinner != 0) {
            animSpinner(0);
        }
    }

    protected void moveSpinnerInfinitely(float spinner) {
        if (mState == RefreshState.TwoLevel && spinner > 0) {
            moveSpinner(Math.min((int) spinner, getMeasuredHeight()), false);
        } else if (mState == RefreshState.Refreshing && spinner >= 0) {
            if (spinner < mHeaderHeight) {
                moveSpinner((int) spinner, false);
            } else {
                final double M = mHeaderExtendHeight;
                final double H = Math.max(mScreenHeightPixels * 4 / 3, getHeight()) - mHeaderHeight;
                final double x = Math.max(0, (spinner - mHeaderHeight) * mDragRate);
                final double y = Math.min(M * (1 - Math.pow(100, -x / (H == 0 ? 1 : H))), x);// 公式 y = M(1-100^(-x/H))
                moveSpinner((int) y + mHeaderHeight, false);
            }
        } else if (spinner < 0 && (mState == RefreshState.Loading
                || (mEnableFooterFollowWhenLoadFinished && mFooterNoMoreData && isEnableLoadMore())
                || (mEnableAutoLoadMore && !mFooterNoMoreData && isEnableLoadMore()))) {
            if (spinner > -mFooterHeight) {
                moveSpinner((int) spinner, false);
            } else {
                final double M = mFooterExtendHeight;
                final double H = Math.max(mScreenHeightPixels * 4 / 3, getHeight()) - mFooterHeight;
                final double x = -Math.min(0, (spinner + mFooterHeight) * mDragRate);
                final double y = -Math.min(M * (1 - Math.pow(100, -x / (H == 0 ? 1 : H))), x);// 公式 y = M(1-100^(-x/H))
                moveSpinner((int) y - mFooterHeight, false);
            }
        } else if (spinner >= 0) {
            final double M = mHeaderExtendHeight + mHeaderHeight;
            final double H = Math.max(mScreenHeightPixels / 2, getHeight());
            final double x = Math.max(0, spinner * mDragRate);
            final double y = Math.min(M * (1 - Math.pow(100, -x / (H == 0 ? 1 : H))), x);// 公式 y = M(1-100^(-x/H))
            moveSpinner((int) y, false);
        } else {
            final double M = mFooterExtendHeight + mFooterHeight;
            final double H = Math.max(mScreenHeightPixels / 2, getHeight());
            final double x = -Math.min(0, spinner * mDragRate);
            final double y = -Math.min(M * (1 - Math.pow(100, -x / (H == 0 ? 1 : H))), x);// 公式 y = M(1-100^(-x/H))
            moveSpinner((int) y, false);
        }
        if (mEnableAutoLoadMore && !mFooterNoMoreData && isEnableLoadMore() && spinner < 0
                && mState != RefreshState.Refreshing
                && mState != RefreshState.Loading
                && mState != RefreshState.LoadFinish) {
            setStateDirectLoading();
            if (mDisableContentWhenLoading) {
                animationRunnable = null;
                animSpinner(-mFooterHeight);
            }
        }
    }

    /*
     * 移动滚动 Scroll
     * moveSpinner 的取名来自 谷歌官方的 {@link android.support.v4.widget.SwipeRefreshLayout#moveSpinner(float)}
     */
    protected void moveSpinner(final int spinner, boolean isAnimator) {
        if (mSpinner == spinner
                && (mRefreshHeader == null || !mRefreshHeader.isSupportHorizontalDrag())
                && (mRefreshFooter == null || !mRefreshFooter.isSupportHorizontalDrag())) {
            return;
        }
        final int oldSpinner = mSpinner;
        this.mSpinner = spinner;
        if (!isAnimator && mViceState.dragging) {
            if (mSpinner > mHeaderHeight * mHeaderTriggerRate) {
                if (mState != RefreshState.ReleaseToTwoLevel) {
                    mKernel.setState(RefreshState.ReleaseToRefresh);
                }
            } else if (-mSpinner > mFooterHeight * mFooterTriggerRate && !mFooterNoMoreData) {
                mKernel.setState(RefreshState.ReleaseToLoad);
            } else if (mSpinner < 0 && !mFooterNoMoreData) {
                mKernel.setState(RefreshState.PullUpToLoad);
            } else if (mSpinner > 0) {
                mKernel.setState(RefreshState.PullDownToRefresh);
            }
        }
        if (mRefreshContent != null) {
            Integer tSpinner = null;
            if (spinner >= 0 && mRefreshHeader != null) {
                if (mEnableHeaderTranslationContent || mRefreshHeader.getSpinnerStyle() == SpinnerStyle.FixedBehind) {
                    tSpinner = spinner;
                } else if (oldSpinner < 0) {
                    tSpinner = 0;
                }
            }
            if (spinner <= 0 && mRefreshFooter != null) {
                if (mEnableFooterTranslationContent || mRefreshFooter.getSpinnerStyle() == SpinnerStyle.FixedBehind) {
                    tSpinner = spinner;
                } else if (oldSpinner > 0) {
                    tSpinner = 0;
                }
            }
            if (tSpinner != null) {
                mRefreshContent.moveSpinner(tSpinner);
                boolean header = mEnableClipHeaderWhenFixedBehind && mRefreshHeader.getSpinnerStyle() == SpinnerStyle.FixedBehind;
                header = header || mHeaderBackgroundColor != 0;
                boolean footer = mEnableClipFooterWhenFixedBehind && mRefreshFooter.getSpinnerStyle() == SpinnerStyle.FixedBehind;
                footer = footer || mFooterBackgroundColor != 0;
                if ((header && (tSpinner >= 0 || oldSpinner > 0)) || (footer && (tSpinner <= 0 || oldSpinner < 0))) {
                    invalidate();
                }
            }
        }
        if ((spinner >= 0 || oldSpinner > 0) && mRefreshHeader != null) {

            final int offset = Math.max(spinner, 0);
            final int headerHeight = mHeaderHeight;
            final int extendHeight = mHeaderExtendHeight;
            final float percent = 1f * offset / (mHeaderHeight == 0 ? 1 : mHeaderHeight);

            if (isEnableRefresh() || (mState == RefreshState.RefreshFinish && isAnimator)) {
                if (oldSpinner != mSpinner) {
                    if (mRefreshHeader.getSpinnerStyle() == SpinnerStyle.Translate) {
                        mRefreshHeader.getView().setTranslationY(mSpinner);
                    } else if (mRefreshHeader.getSpinnerStyle() == SpinnerStyle.Scale){
                        mRefreshHeader.getView().requestLayout();
                    }
                    if (isAnimator) {
                        mRefreshHeader.onReleasing(percent, offset, headerHeight, extendHeight);
                    }
                }
                if (!isAnimator) {
                    if (mRefreshHeader.isSupportHorizontalDrag()) {
                        final int offsetX = (int) mLastTouchX;
                        final int offsetMax = getWidth();
                        final float percentX = mLastTouchX / (offsetMax == 0 ? 1 : offsetMax);
                        mRefreshHeader.onHorizontalDrag(percentX, offsetX, offsetMax);
                        mRefreshHeader.onPulling(percent, offset, headerHeight, extendHeight);
                    } else if (oldSpinner != mSpinner) {
                        mRefreshHeader.onPulling(percent, offset, headerHeight, extendHeight);
                    }
                }
            }

            if (oldSpinner != mSpinner && mOnMultiPurposeListener != null) {
                if (isAnimator) {
                    mOnMultiPurposeListener.onHeaderReleasing(mRefreshHeader, percent, offset, headerHeight, extendHeight);
                } else {
                    mOnMultiPurposeListener.onHeaderPulling(mRefreshHeader, percent, offset, headerHeight, extendHeight);
                }
            }

        }
        if ((spinner <= 0 || oldSpinner < 0) && mRefreshFooter != null) {

            final int offset = -Math.min(spinner, 0);
            final int footerHeight = mFooterHeight;
            final int extendHeight = mFooterExtendHeight;
            final float percent = offset * 1f / (mFooterHeight == 0 ? 1 : mFooterHeight);

            if (isEnableLoadMore() || (mState == RefreshState.LoadFinish && isAnimator)) {
                if (oldSpinner != mSpinner) {
                    if (mRefreshFooter.getSpinnerStyle() == SpinnerStyle.Translate) {
                        mRefreshFooter.getView().setTranslationY(mSpinner);
                    } else if (mRefreshFooter.getSpinnerStyle() == SpinnerStyle.Scale){
                        mRefreshFooter.getView().requestLayout();
                    }
                    if (isAnimator) {
                        mRefreshFooter.onReleasing(percent, offset, footerHeight, extendHeight);
                    }
                }

                if (!isAnimator) {
                    if (mRefreshFooter.isSupportHorizontalDrag()) {
                        final int offsetX = (int) mLastTouchX;
                        final int offsetMax = getWidth();
                        final float percentX = mLastTouchX / (offsetMax == 0 ? 1 : offsetMax);
                        mRefreshFooter.onHorizontalDrag(percentX, offsetX, offsetMax);
                        mRefreshFooter.onPulling(percent, offset, footerHeight, extendHeight);
                    } else if (oldSpinner != mSpinner) {
                        mRefreshFooter.onPulling(percent, offset, footerHeight, extendHeight);
                    }
                }
            }

            if (oldSpinner != mSpinner && mOnMultiPurposeListener != null) {
                if (isAnimator) {
                    mOnMultiPurposeListener.onFooterReleasing(mRefreshFooter, percent, offset, footerHeight, extendHeight);
                } else {
                    mOnMultiPurposeListener.onFooterPulling(mRefreshFooter, percent, offset, footerHeight, extendHeight);
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

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SmartRefreshLayout_Layout);
            backgroundColor = ta.getColor(R.styleable.SmartRefreshLayout_Layout_layout_srlBackgroundColor, backgroundColor);
            if (ta.hasValue(R.styleable.SmartRefreshLayout_Layout_layout_srlSpinnerStyle)) {
                spinnerStyle = SpinnerStyle.values()[ta.getInt(R.styleable.SmartRefreshLayout_Layout_layout_srlSpinnerStyle, SpinnerStyle.Translate.ordinal())];
            }
            ta.recycle();
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

        public int backgroundColor = 0;
        public SpinnerStyle spinnerStyle = null;
    }
    //</editor-fold>

    //<editor-fold desc="嵌套滚动 NestedScrolling">

    //<editor-fold desc="NestedScrollingParent">

    @Override
    public int getNestedScrollAxes() {
        return mNestedParent.getNestedScrollAxes();
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int nestedScrollAxes) {
        boolean accepted = isEnabled() && isNestedScrollingEnabled() && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
        accepted = accepted && (mEnableOverScrollDrag || isEnableRefresh() || isEnableLoadMore());
        return accepted;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes) {
        // Reset the counter of how much leftover scroll needs to be consumed.
        mNestedParent.onNestedScrollAccepted(child, target, axes);
        // Dispatch up to the nested parent
        mNestedChild.startNestedScroll(axes & ViewCompat.SCROLL_AXIS_VERTICAL);
        mTotalUnconsumed = mSpinner;//0;
        mNestedInProgress = true;
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed) {
        // If we are in the middle of consuming, a scroll, then we want to move the spinner back up
        // before allowing the list to scroll
        int consumedY = 0;

        if (dy * mTotalUnconsumed > 0) {
            if (Math.abs(dy) > Math.abs(mTotalUnconsumed)) {
                consumedY = mTotalUnconsumed;
                mTotalUnconsumed = 0;
            } else {
                consumedY = dy;
                mTotalUnconsumed -= dy;
            }
            moveSpinnerInfinitely(mTotalUnconsumed);
            if (mViceState.opening || mViceState == RefreshState.None) {
                if (mSpinner > 0) {
                    mKernel.setState(RefreshState.PullDownToRefresh);
                } else {
                    mKernel.setState(RefreshState.PullUpToLoad);
                }
            }
        } else if (dy > 0 && mFooterLocked) {
            consumedY = dy;
            mTotalUnconsumed -= dy;
            moveSpinnerInfinitely(mTotalUnconsumed);
        }

        // Now let our nested parent consume the leftovers
        mNestedChild.dispatchNestedPreScroll(dx, dy - consumedY, consumed, null);
        consumed[1] += consumedY;

    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        // Dispatch up to the nested parent first
        mNestedChild.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, mParentOffsetInWindow);

        // This is a bit of a hack. Nested scrolling works from the bottom up, and as we are
        // sometimes between two nested scrolling views, we need a way to be able to know when any
        // nested scrolling parent has stopped handling events. We do that by using the
        // 'offset in window 'functionality to see if we have been moved from the event.
        // This is a decent indication of whether we should take over the event stream or not.
        final int dy = dyUnconsumed + mParentOffsetInWindow[1];
        if (dy != 0 && (mEnableOverScrollDrag || (dy < 0 && isEnableRefresh()) || (dy > 0 && isEnableLoadMore()))) {
            if (mViceState == RefreshState.None) {
                mKernel.setState(dy > 0 ? RefreshState.PullUpToLoad : RefreshState.PullDownToRefresh);
            }
            moveSpinnerInfinitely(mTotalUnconsumed -= dy);
        }

    }

    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        return mFooterLocked && velocityY > 0 || startFlingIfNeed(-velocityY) || mNestedChild.dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed) {
        return mNestedChild.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public void onStopNestedScroll(@NonNull View target) {
        mNestedParent.onStopNestedScroll(target);
        mNestedInProgress = false;
        // Finish the spinner for nested scrolling if we ever consumed any
        // unconsumed nested scroll
        mTotalUnconsumed = 0;
        overSpinner();
        // Dispatch up our nested parent
        mNestedChild.stopNestedScroll();
    }
    //</editor-fold>

    //<editor-fold desc="NestedScrollingChild">
    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mManualNestedScrolling = true;
        mNestedChild.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mNestedChild.isNestedScrollingEnabled();
    }

//    @Override
//    @Deprecated
//    public boolean startNestedScroll(int axes) {
//        return mNestedChild.startNestedScroll(axes);
//    }
//
//    @Override
//    @Deprecated
//    public void stopNestedScroll() {
//        mNestedChild.stopNestedScroll();
//    }
//
//    @Override
//    @Deprecated
//    public boolean hasNestedScrollingParent() {
//        return mNestedChild.hasNestedScrollingParent();
//    }
//
//    @Override
//    @Deprecated
//    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
//                                        int dyUnconsumed, int[] offsetInWindow) {
//        return mNestedChild.dispatchNestedScroll(dxConsumed, dyConsumed,
//                dxUnconsumed, dyUnconsumed, offsetInWindow);
//    }
//
//    @Override
//    @Deprecated
//    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
//        return mNestedChild.dispatchNestedPreScroll(
//                dx, dy, consumed, offsetInWindow);
//    }
//
//    @Override
//    @Deprecated
//    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
//        return mNestedChild.dispatchNestedFling(velocityX, velocityY, consumed);
//    }
//
//    @Override
//    @Deprecated
//    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
//        return mNestedChild.dispatchNestedPreFling(velocityX, velocityY);
//    }
    //</editor-fold>

    //</editor-fold>

    //<editor-fold desc="开放接口 open interface">
    @Override
    public SmartRefreshLayout setFooterHeight(float heightDp) {
        return setFooterHeightPx(dp2px(heightDp));
    }

    @Override
    public SmartRefreshLayout setFooterHeightPx(int heightPx) {
        if (mFooterHeightStatus.canReplaceWith(DimensionStatus.CodeExact)) {
            mFooterHeight = heightPx;
            mFooterExtendHeight = (int) Math.max((heightPx * (mFooterMaxDragRate - 1)), 0);
            mFooterHeightStatus = DimensionStatus.CodeExactUnNotify;
            if (mRefreshFooter != null) {
                mRefreshFooter.getView().requestLayout();
            }
        }
        return this;
    }

    @Override
    public SmartRefreshLayout setHeaderHeight(float heightDp) {
        return setHeaderHeightPx(dp2px(heightDp));
    }

    @Override
    public SmartRefreshLayout setHeaderHeightPx(int heightPx) {
        if (mHeaderHeightStatus.canReplaceWith(DimensionStatus.CodeExact)) {
            mHeaderHeight = heightPx;
            mHeaderExtendHeight = (int) Math.max((heightPx * (mHeaderMaxDragRate - 1)), 0);
            mHeaderHeightStatus = DimensionStatus.CodeExactUnNotify;
            if (mRefreshHeader != null) {
                mRefreshHeader.getView().requestLayout();
            }
        }
        return this;
    }

    @Override
    public SmartRefreshLayout setHeaderInsetStart(float insetDp) {
        return setHeaderInsetStartPx(dp2px(insetDp));
    }

    @Override
    public SmartRefreshLayout setHeaderInsetStartPx(int insetPx) {
        mHeaderInsetStart = insetPx;
        return this;
    }

    @Override
    public SmartRefreshLayout setFooterInsetStart(float insetDp) {
        return setFooterInsetStartPx(dp2px(insetDp));
    }

    @Override
    public SmartRefreshLayout setFooterInsetStartPx(int insetPx) {
        mFooterInsetStart = insetPx;
        return this;
    }

    /**
     * @param rate 显示拖动高度/真实拖动高度 比率
     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout setDragRate(float rate) {
        this.mDragRate = rate;
        return this;
    }

    /**
     * 设置下拉最大高度和Header高度的比率（将会影响可以下拉的最大高度）
     * @param rate 下拉最大高度和Header高度的比率
     */
    @Override
    public SmartRefreshLayout setHeaderMaxDragRate(float rate) {
        this.mHeaderMaxDragRate = rate;
        this.mHeaderExtendHeight = (int) Math.max((mHeaderHeight * (mHeaderMaxDragRate - 1)), 0);
        if (mRefreshHeader != null && mHandler != null) {
            mRefreshHeader.onInitialized(mKernel, mHeaderHeight, mHeaderExtendHeight);
        } else {
            mHeaderHeightStatus = mHeaderHeightStatus.unNotify();
        }
        return this;
    }

    /**
     * 设置上拉最大高度和Footer高度的比率（将会影响可以上拉的最大高度）
     * @param rate 上拉最大高度和Footer高度的比率
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout setFooterMaxDragRate(float rate) {
        this.mFooterMaxDragRate = rate;
        this.mFooterExtendHeight = (int) Math.max((mFooterHeight * (mFooterMaxDragRate - 1)), 0);
        if (mRefreshFooter != null && mHandler != null) {
            mRefreshFooter.onInitialized(mKernel, mFooterHeight, mFooterExtendHeight);
        } else {
            mFooterHeightStatus = mFooterHeightStatus.unNotify();
        }
        return this;
    }

    /**
     * 设置 触发刷新距离 与 HeaderHeight 的比率
     * @param rate 触发刷新距离 与 HeaderHeight 的比率
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout setHeaderTriggerRate(float rate) {
        this.mHeaderTriggerRate = rate;
        return this;
    }

    /**
     * 设置 触发加载距离 与 FooterHeight 的比率
     * @param rate 触发加载距离 与 FooterHeight 的比率
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout setFooterTriggerRate(float rate) {
        this.mFooterTriggerRate = rate;
        return this;
    }

    /**
     * 设置回弹显示插值器
     * @param interpolator 动画插值器
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout setReboundInterpolator(@NonNull Interpolator interpolator) {
        this.mReboundInterpolator = interpolator;
        return this;
    }

    /**
     * 设置回弹动画时长
     * @param duration 时长
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout setReboundDuration(int duration) {
        this.mReboundDuration = duration;
        return this;
    }

    /**
     * 设置是否启用上拉加载更多（默认启用）
     * @param enabled 是否启用
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout setEnableLoadMore(boolean enabled) {
        this.mManualLoadMore = true;
        this.mEnableLoadMore = enabled;
        return this;
    }

    /**
     * 是否启用下拉刷新（默认启用）
     * @param enabled 是否启用
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout setEnableRefresh(boolean enabled) {
        this.mEnableRefresh = enabled;
        return this;
    }

    /**
     * 设置是否启用内容视图拖动效果
     * @param enabled 是否启用
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout setEnableHeaderTranslationContent(boolean enabled) {
        this.mEnableHeaderTranslationContent = enabled;
        this.mManualHeaderTranslationContent = true;
        return this;
    }

    /**
     * 设置是否启用内容视图拖动效果
     * @param enabled 是否启用
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout setEnableFooterTranslationContent(boolean enabled) {
        this.mEnableFooterTranslationContent = enabled;
        return this;
    }

    /**
     * 设置是否监听列表在滚动到底部时触发加载事件
     * @param enabled 是否启用
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout setEnableAutoLoadMore(boolean enabled) {
        this.mEnableAutoLoadMore = enabled;
        return this;
    }

    /**
     * 设置是否启用越界回弹
     * @param enabled 是否启用
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout setEnableOverScrollBounce(boolean enabled) {
        this.mEnableOverScrollBounce = enabled;
        return this;
    }

    /**
     * 设置是否开启纯滚动模式
     * @param enabled 是否启用
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout setEnablePureScrollMode(boolean enabled) {
        this.mEnablePureScrollMode = enabled;
        return this;
    }

    /**
     * 设置是否在加载更多完成之后滚动内容显示新数据
     * @param enabled 是否启用
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout setEnableScrollContentWhenLoaded(boolean enabled) {
        this.mEnableScrollContentWhenLoaded = enabled;
        return this;
    }

    /**
     * 是否在刷新完成之后滚动内容显示新数据
     * @param enabled 是否启用
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout setEnableScrollContentWhenRefreshed(boolean enabled) {
        this.mEnableScrollContentWhenRefreshed = enabled;
        return this;
    }

    /**
     * 设置在内容不满一页的时候，是否可以上拉加载更多
     * @param enabled 是否启用
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout setEnableLoadMoreWhenContentNotFull(boolean enabled) {
        this.mEnableLoadMoreWhenContentNotFull = enabled;
        if (mRefreshContent != null) {
            mRefreshContent.setEnableLoadMoreWhenContentNotFull(enabled);
        }
        return this;
    }

    /**
     * 设置是否启用越界拖动（仿苹果效果）
     * @param enabled 是否启用
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout setEnableOverScrollDrag(boolean enabled) {
        this.mEnableOverScrollDrag = enabled;
        return this;
    }

    /**
     * 设置是否在全部加载结束之后Footer跟随内容
     * @param enabled 是否启用
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout setEnableFooterFollowWhenLoadFinished(boolean enabled) {
        this.mEnableFooterFollowWhenLoadFinished = enabled;
        return this;
    }

    /**
     * 设置是否 当 Header FixedBehind 时候是否剪裁遮挡 Header
     * @param enabled 是否启用
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout setEnableClipHeaderWhenFixedBehind(boolean enabled) {
        this.mEnableClipHeaderWhenFixedBehind = enabled;
        return this;
    }

    /**
     * 设置是否 当 Footer FixedBehind 时候是否剪裁遮挡 Footer
     * @param enabled 是否启用
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout setEnableClipFooterWhenFixedBehind(boolean enabled) {
        this.mEnableClipFooterWhenFixedBehind = enabled;
        return this;
    }

    /**
     * 设置是会否启用嵌套滚动功能（默认关闭+智能开启）
     * @param enabled 是否启用
     * @return SmartRefreshLayout
     */
    @Override
    public RefreshLayout setEnableNestedScroll(boolean enabled) {
        setNestedScrollingEnabled(enabled);
        return this;
    }

    /**
     * 设置是否开启在刷新时候禁止操作内容视图
     * @param disable 是否禁止
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout setDisableContentWhenRefresh(boolean disable) {
        this.mDisableContentWhenRefresh = disable;
        return this;
    }

    /**
     * 设置是否开启在加载时候禁止操作内容视图
     * @param disable 是否禁止
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout setDisableContentWhenLoading(boolean disable) {
        this.mDisableContentWhenLoading = disable;
        return this;
    }

    /**
     * 设置指定的 Header
     * @param header 刷新头
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout setRefreshHeader(@NonNull RefreshHeader header) {
        return setRefreshHeader(header, MATCH_PARENT, WRAP_CONTENT);
    }

    /**
     * 设置指定的 Header
     * @param header 刷新头
     * @param width 宽度 可以使用 MATCH_PARENT, WRAP_CONTENT
     * @param height 高度 可以使用 MATCH_PARENT, WRAP_CONTENT
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout setRefreshHeader(@NonNull RefreshHeader header, int width, int height) {
        if (mRefreshHeader != null) {
            removeView(mRefreshHeader.getView());
        }
        this.mRefreshHeader = header;
        this.mHeaderBackgroundColor = 0;
        this.mHeaderNeedTouchEventWhenRefreshing = false;
        this.mHeaderHeightStatus = mHeaderHeightStatus.unNotify();
        if (header.getSpinnerStyle() == SpinnerStyle.FixedBehind) {
            this.addView(mRefreshHeader.getView(), 0, new LayoutParams(width, height));
        } else {
            this.addView(mRefreshHeader.getView(), width, height);
        }
        return this;
    }

    /**
     * 设置指定的 Footer
     * @param footer 刷新尾巴
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout setRefreshFooter(@NonNull RefreshFooter footer) {
        return setRefreshFooter(footer, MATCH_PARENT, WRAP_CONTENT);
    }

    /**
     * 设置指定的 Footer
     * @param footer 刷新尾巴
     * @param width 宽度 可以使用 MATCH_PARENT, WRAP_CONTENT
     * @param height 高度 可以使用 MATCH_PARENT, WRAP_CONTENT
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout setRefreshFooter(@NonNull RefreshFooter footer, int width, int height) {
        if (mRefreshFooter != null) {
            removeView(mRefreshFooter.getView());
        }
        this.mRefreshFooter = footer;
        this.mFooterBackgroundColor = 0;
        this.mFooterNeedTouchEventWhenLoading = false;
        this.mFooterHeightStatus = mFooterHeightStatus.unNotify();
        this.mEnableLoadMore = !mManualLoadMore || mEnableLoadMore;
        if (mRefreshFooter.getSpinnerStyle() == SpinnerStyle.FixedBehind) {
            this.addView(mRefreshFooter.getView(), 0, new LayoutParams(width, height));
        } else {
            this.addView(mRefreshFooter.getView(), width, height);
        }
        return this;
    }

    /**
     * 设置指定的Content
     * @param content 内容视图
     * @return SmartRefreshLayout
     */
    @Override
    public RefreshLayout setRefreshContent(@NonNull View content) {
        return setRefreshContent(content, MATCH_PARENT, MATCH_PARENT);
    }

    /**
     * 设置指定的 Content
     * @param content 内容视图
     * @param width 宽度 可以使用 MATCH_PARENT, WRAP_CONTENT
     * @param height 高度 可以使用 MATCH_PARENT, WRAP_CONTENT
     * @return SmartRefreshLayout
     */
    @Override
    public RefreshLayout setRefreshContent(@NonNull View content, int width, int height) {
        if (mRefreshContent != null) {
            removeView(mRefreshContent.getView());
        }
        addView(content, 0, new LayoutParams(width, height));
        if (mRefreshHeader != null && mRefreshHeader.getSpinnerStyle() == SpinnerStyle.FixedBehind) {
            bringChildToFront(content);
            if (mRefreshFooter != null && mRefreshFooter.getSpinnerStyle() != SpinnerStyle.FixedBehind) {
                bringChildToFront(mRefreshFooter.getView());
            }
        } else if (mRefreshFooter != null && mRefreshFooter.getSpinnerStyle() == SpinnerStyle.FixedBehind) {
            bringChildToFront(content);
            if (mRefreshHeader != null && mRefreshHeader.getSpinnerStyle() == SpinnerStyle.FixedBehind) {
                bringChildToFront(mRefreshHeader.getView());
            }
        }
        mRefreshContent = new RefreshContentWrapper(content);
        if (mHandler != null) {
            View fixedHeaderView = mFixedHeaderViewId > 0 ? findViewById(mFixedHeaderViewId) : null;
            View fixedFooterView = mFixedFooterViewId > 0 ? findViewById(mFixedFooterViewId) : null;

            mRefreshContent.setScrollBoundaryDecider(mScrollBoundaryDecider);
            mRefreshContent.setEnableLoadMoreWhenContentNotFull(mEnableLoadMoreWhenContentNotFull);
            mRefreshContent.setUpComponent(mKernel, fixedHeaderView, fixedFooterView);
        }
        return this;
    }

    /**
     * 获取底部上拉组件的实现
     * @return RefreshFooter
     */
    @Nullable
    @Override
    public RefreshFooter getRefreshFooter() {
        return mRefreshFooter;
    }

    /**
     * 获取顶部下拉组件的实现
     * @return RefreshHeader
     */
    @Nullable
    @Override
    public RefreshHeader getRefreshHeader() {
        return mRefreshHeader;
    }

    /**
     * 获取状态
     * @return RefreshState
     */
    @Override
    public RefreshState getState() {
        return mState;
    }

    /**
     * 获取实体布局视图
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout getLayout() {
        return this;
    }

    /**
     * 单独设置刷新监听器
     * @param listener 刷新监听器
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout setOnRefreshListener(OnRefreshListener listener) {
        this.mRefreshListener = listener;
        return this;
    }

    /**
     * 同时设置刷新和加载监听器
     * @param listener 加载监听器
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.mLoadMoreListener = listener;
        this.mEnableLoadMore = mEnableLoadMore || (!mManualLoadMore && listener != null);
        return this;
    }

    /**
     * 单独设置加载监听器
     * @param listener 刷新加载监听器
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout setOnRefreshLoadMoreListener(OnRefreshLoadMoreListener listener) {
        this.mRefreshListener = listener;
        this.mLoadMoreListener = listener;
        this.mEnableLoadMore = mEnableLoadMore || (!mManualLoadMore && listener != null);
        return this;
    }

    /**
     * 设置多功能监听器
     * @param listener 建议使用 {@link com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener}
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout setOnMultiPurposeListener(OnMultiPurposeListener listener) {
        this.mOnMultiPurposeListener = listener;
        return this;
    }

    /**
     * 设置主题颜色
     * @param primaryColors 主题颜色
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout setPrimaryColors(@ColorInt int... primaryColors) {
        if (mRefreshHeader != null) {
            mRefreshHeader.setPrimaryColors(primaryColors);
        }
        if (mRefreshFooter != null) {
            mRefreshFooter.setPrimaryColors(primaryColors);
        }
        mPrimaryColors = primaryColors;
        return this;
    }

    /**
     * 设置主题颜色
     * @param primaryColorId 主题颜色ID
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout setPrimaryColorsId(@ColorRes int... primaryColorId) {
        int[] colors = new int[primaryColorId.length];
        for (int i = 0; i < primaryColorId.length; i++) {
            colors[i] = DesignUtil.getColor(getContext(), primaryColorId[i]);
        }
        setPrimaryColors(colors);
        return this;
    }

    /**
     * 设置滚动边界
     * @param boundary 建议使用 {@link com.scwang.smartrefresh.layout.impl.ScrollBoundaryDeciderAdapter}
     * @return SmartRefreshLayout
     */
    @Override
    public RefreshLayout setScrollBoundaryDecider(ScrollBoundaryDecider boundary) {
        mScrollBoundaryDecider = boundary;
        if (mRefreshContent != null) {
            mRefreshContent.setScrollBoundaryDecider(boundary);
        }
        return this;
    }

    /**
     * 恢复没有更多数据的原始状态
     * @param noMoreData 是否有更多数据
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout setNoMoreData(boolean noMoreData) {
        mFooterNoMoreData = noMoreData;
        if (mRefreshFooter != null && !mRefreshFooter.setNoMoreData(noMoreData)) {
            System.out.println("Footer:" + mRefreshFooter + " Prompt completion is not supported.(不支持提示完成)");
        }
        return this;
    }

    /**
     * 完成刷新
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout finishRefresh() {
        long passTime = System.currentTimeMillis() - mLastOpenTime;
        return finishRefresh(Math.max(0, 1000 - (int) passTime));//保证刷新动画有1000毫秒的时间
    }

    /**
     * 完成加载
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout finishLoadMore() {
        long passTime = System.currentTimeMillis() - mLastOpenTime;
        return finishLoadMore(Math.max(0, 1000 - (int) passTime));//保证加载动画有1000毫秒的时间
    }

    /**
     * 完成刷新
     * @param delayed 开始延时
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout finishRefresh(int delayed) {
        return finishRefresh(delayed, true);
    }

    /**
     * 完成刷新
     * @param success 数据是否成功刷新 （会影响到上次更新时间的改变）
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout finishRefresh(boolean success) {
        long passTime = System.currentTimeMillis() - mLastOpenTime;
        return finishRefresh(success ? Math.max(0, 1000 - (int) passTime) : 0, success);//保证加载动画有1000毫秒的时间
    }

    /**
     * 完成刷新
     * @param delayed 开始延时
     * @param success 数据是否成功刷新 （会影响到上次更新时间的改变）
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout finishRefresh(int delayed, final boolean success) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mState == RefreshState.Refreshing && mRefreshHeader != null && mRefreshContent != null) {
                    notifyStateChanged(RefreshState.RefreshFinish);
                    int startDelay = mRefreshHeader.onFinish(SmartRefreshLayout.this, success);
                    if (mOnMultiPurposeListener != null) {
                        mOnMultiPurposeListener.onHeaderFinish(mRefreshHeader, success);
                    }
                    if (startDelay < Integer.MAX_VALUE) {
                        if (mIsBeingDragged) {
                            mTouchSpinner = 0;
                            mTouchY = mLastTouchY;
                            mIsBeingDragged = false;
                            long time = System.currentTimeMillis();
                            SmartRefreshLayout.super.dispatchTouchEvent(obtain(time, time, MotionEvent.ACTION_DOWN, mLastTouchX, mTouchY + mSpinner - mTouchSlop*2, 0));
                            SmartRefreshLayout.super.dispatchTouchEvent(obtain(time, time, MotionEvent.ACTION_MOVE, mLastTouchX, mTouchY + mSpinner, 0));
                        }
                        if (mSpinner > 0) {
                            AnimatorUpdateListener updateListener = null;
                            ValueAnimator valueAnimator = animSpinner(0, startDelay, mReboundInterpolator, mReboundDuration);
                            if (mEnableScrollContentWhenRefreshed) {
                                updateListener = mRefreshContent.scrollContentWhenFinished(mSpinner);
                            }
                            if (valueAnimator != null && updateListener != null) {
                                valueAnimator.addUpdateListener(updateListener);
                            }
                        } else if (mSpinner < 0) {
                            animSpinner(0, startDelay, mReboundInterpolator, mReboundDuration);
                        } else {
                            moveSpinner(0, true);
                            resetStatus();
                        }
                    }
                }
            }
        }, delayed <= 0 ? 1 : delayed);
        return this;
    }

    /**
     * 完成加载
     * @param delayed 开始延时
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout finishLoadMore(int delayed) {
        return finishLoadMore(delayed, true, false);
    }

    /**
     * 完成加载
     * @param success 数据是否成功
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout finishLoadMore(boolean success) {
        long passTime = System.currentTimeMillis() - mLastOpenTime;
        return finishLoadMore(success ? Math.max(0, 1000 - (int) passTime) : 0, success, false);
    }

    /**
     * 完成加载
     * @param delayed 开始延时
     * @param success 数据是否成功
     * @param noMoreData 是否有更多数据
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout finishLoadMore(int delayed, final boolean success, final boolean noMoreData) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mState == RefreshState.Loading && mRefreshFooter != null && mRefreshContent != null) {
                    notifyStateChanged(RefreshState.LoadFinish);
                    final int startDelay = mRefreshFooter.onFinish(SmartRefreshLayout.this, success);
                    if (mOnMultiPurposeListener != null) {
                        mOnMultiPurposeListener.onFooterFinish(mRefreshFooter, success);
                    }
                    if (startDelay < Integer.MAX_VALUE) {
                        //计算布局将要移动的偏移量
                        final boolean needHoldFooter = noMoreData && mEnableFooterFollowWhenLoadFinished && mSpinner < 0 && mRefreshContent.canLoadMore();
                        final int offset = mSpinner - (needHoldFooter ? Math.max(mSpinner,-mFooterHeight) : 0);
                        //如果正在拖动的话，偏移初始点击事件
                        if (mIsBeingDragged) {
                            mTouchSpinner = mSpinner - offset;
                            mTouchY = mLastTouchY;
                            mIsBeingDragged = false;
                            final long time = System.currentTimeMillis();
                            SmartRefreshLayout.super.dispatchTouchEvent(obtain(time, time, MotionEvent.ACTION_DOWN, mLastTouchX, mTouchY + offset + mTouchSlop * 2, 0));
                            SmartRefreshLayout.super.dispatchTouchEvent(obtain(time, time, MotionEvent.ACTION_MOVE, mLastTouchX, mTouchY + offset, 0));
                        }
                        //准备：偏移并结束状态
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AnimatorUpdateListener updateListener = null;
                                if (mEnableScrollContentWhenLoaded && offset < 0) {
                                    updateListener = mRefreshContent.scrollContentWhenFinished(mSpinner);
                                }
                                if (updateListener != null) {
                                    updateListener.onAnimationUpdate(ValueAnimator.ofInt(0, 0));
                                }
                                ValueAnimator animator = null;
                                AnimatorListenerAdapter listenerAdapter = new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationCancel(Animator animation) {
                                        super.onAnimationEnd(animation);
                                    }
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        mFooterLocked = false;
                                        if (noMoreData) {
                                            setNoMoreData(true);
                                        }
                                        if (mState == RefreshState.LoadFinish) {
                                            notifyStateChanged(RefreshState.None);
                                        }
                                    }
                                };
                                if (mSpinner > 0) {
                                    animator = animSpinner(0);
                                } else if (updateListener != null || mSpinner == 0) {
                                    if (reboundAnimator != null) {
                                        reboundAnimator.cancel();
                                        reboundAnimator = null;
                                    }
                                    moveSpinner(0, true);
                                    resetStatus();
                                } else {
                                    if (noMoreData && mEnableFooterFollowWhenLoadFinished) {
                                        if (mSpinner >= -mFooterHeight) {
                                            notifyStateChanged(RefreshState.None);
                                        } else {
                                            animator = animSpinner(-mFooterHeight);
                                        }
                                    } else {
                                        animator = animSpinner(0);
                                    }
                                }
                                if (animator != null) {
                                    animator.addListener(listenerAdapter);
                                } else {
                                    listenerAdapter.onAnimationEnd(null);
                                }
                            }
                        }, mSpinner < 0 ? startDelay : 0);
                    }
                } else {
                    if (noMoreData) {
                        setNoMoreData(true);
                    }
                }
            }
        }, delayed <= 0 ? 1 : delayed);
        return this;
    }

    /**
     * 完成加载并标记没有更多数据
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout finishLoadMoreWithNoMoreData() {
        long passTime = System.currentTimeMillis() - mLastOpenTime;
        return finishLoadMore(Math.max(0, 1000 - (int) passTime), true, true);
    }

    /**
     * 是否正在刷新
     * @return 是否正在刷新
     */
    @Override
    public boolean isRefreshing() {
        return mState == RefreshState.Refreshing;
    }

    /**
     * 是否正在加载
     * @return 是否正在加载
     */
    @Override
    public boolean isLoading() {
        return mState == RefreshState.Loading;
    }

    /**
     * 自动刷新
     * @return 是否成功
     */
    @Override
    public boolean autoRefresh() {
        return autoRefresh(mHandler == null ? 400 : 0);
    }

    /**
     * 自动刷新
     * @param delayed 开始延时
     * @return 是否成功
     */
    @Override
    public boolean autoRefresh(int delayed) {
        return autoRefresh(delayed, mReboundDuration, 1f * (mHeaderHeight + mHeaderExtendHeight / 2) / (mHeaderHeight == 0 ? 1 : mHeaderHeight));
    }

    /**
     * 自动刷新
     * @param delayed 开始延时
     * @param duration 拖拽动画持续时间
     * @param dragRate 拉拽的高度比率（要求 ≥ 1 ）
     * @return 是否成功
     */
    @Override
    public boolean autoRefresh(int delayed, final int duration, final float dragRate) {
        if (mState == RefreshState.None && isEnableRefresh()) {
            if (reboundAnimator != null) {
                reboundAnimator.cancel();
            }
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    reboundAnimator = ValueAnimator.ofInt(mSpinner, (int) (mHeaderHeight * dragRate));
                    reboundAnimator.setDuration(duration);
                    reboundAnimator.setInterpolator(new DecelerateInterpolator());
                    reboundAnimator.addUpdateListener(new AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            moveSpinner((int) animation.getAnimatedValue(), false);
                        }
                    });
                    reboundAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            mLastTouchX = getMeasuredWidth() / 2;
                            mKernel.setState(RefreshState.PullDownToRefresh);
                        }
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            reboundAnimator = null;
                            if (mState != RefreshState.ReleaseToRefresh) {
                                mKernel.setState(RefreshState.ReleaseToRefresh);
                            }
                            overSpinner();
                        }
                    });
                    reboundAnimator.start();
                }
            };
            if (delayed > 0) {
                reboundAnimator = new ValueAnimator();
                postDelayed(runnable, delayed);
            } else {
                runnable.run();
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * 自动加载
     * @return 是否成功
     */
    @Override
    public boolean autoLoadMore() {
        return autoLoadMore(0);
    }

    /**
     * 自动加载
     * @param delayed 开始延时
     * @return 是否成功
     */
    @Override
    public boolean autoLoadMore(int delayed) {
        return autoLoadMore(delayed, mReboundDuration, 1f * (mFooterHeight + mFooterExtendHeight / 2) / (mFooterHeight == 0 ? 1 : mFooterHeight));
    }

    /**
     * 自动加载
     * @param delayed 开始延时
     * @param duration 拖拽动画持续时间
     * @param dragRate 拉拽的高度比率（要求 ≥ 1 ）
     * @return 是否成功
     */
    @Override
    public boolean autoLoadMore(int delayed, final int duration, final float dragRate) {
        if (mState == RefreshState.None && (isEnableLoadMore() && !mFooterNoMoreData)) {
            if (reboundAnimator != null) {
                reboundAnimator.cancel();
            }
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    reboundAnimator = ValueAnimator.ofInt(mSpinner, -(int) (mFooterHeight * dragRate));
                    reboundAnimator.setDuration(duration);
                    reboundAnimator.setInterpolator(new DecelerateInterpolator());
                    reboundAnimator.addUpdateListener(new AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            moveSpinner((int) animation.getAnimatedValue(), false);
                        }
                    });
                    reboundAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            mLastTouchX = getMeasuredWidth() / 2;
                            mKernel.setState(RefreshState.PullUpToLoad);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            reboundAnimator = null;
                            if (mState != RefreshState.ReleaseToLoad) {
                                mKernel.setState(RefreshState.ReleaseToLoad);
                            }
                            if (mEnableAutoLoadMore) {
                                mEnableAutoLoadMore = false;
                                overSpinner();
                                mEnableAutoLoadMore = true;
                            } else {
                                overSpinner();
                            }
                        }
                    });
                    reboundAnimator.start();
                }
            };
            if (delayed > 0) {
                reboundAnimator = new ValueAnimator();
                postDelayed(runnable, delayed);
            } else {
                runnable.run();
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isEnableRefresh() {
        return mEnableRefresh && !mEnablePureScrollMode;
    }

    @Override
    public boolean isEnableLoadMore() {
        return mEnableLoadMore && !mEnablePureScrollMode;
    }

    /**
     * 设置默认 Header 构建器
     * @param creator Header构建器
     */
    public static void setDefaultRefreshHeaderCreator(@NonNull DefaultRefreshHeaderCreator creator) {
        sHeaderCreator = creator;
    }

    /**
     * 设置默认 Footer 构建器
     * @param creator Footer构建器
     */
    public static void setDefaultRefreshFooterCreator(@NonNull DefaultRefreshFooterCreator creator) {
        sFooterCreator = creator;
        sManualFooterCreator = true;
    }

    //<editor-fold desc="丢弃的API">

    /**
     * 恢复没有更多数据的原始状态
     * @deprecated 使用 {@link RefreshLayout#setNoMoreData(boolean)} 代替
     * @return SmartRefreshLayout
     */
    @Override
    @Deprecated
    public SmartRefreshLayout resetNoMoreData() {
        return setNoMoreData(false);
    }

    //</editor-fold>

    //</editor-fold>

    //<editor-fold desc="核心接口 RefreshKernel">
    public class RefreshKernelImpl implements RefreshKernel {

        @NonNull
        @Override
        public RefreshLayout getRefreshLayout() {
            return SmartRefreshLayout.this;
        }

        @NonNull
        @Override
        public RefreshContent getRefreshContent() {
            return mRefreshContent;
        }

        @Override
        public RefreshKernel setState(@NonNull RefreshState state) {
            switch (state) {
                case None:
                    resetStatus();
                    break;
                case PullDownToRefresh:
                    if (!mState.opening && isEnableRefresh()) {
                        notifyStateChanged(RefreshState.PullDownToRefresh);
                    } else {
                        setViceState(RefreshState.PullDownToRefresh);
                    }
                    break;
                case PullUpToLoad:
                    if (isEnableLoadMore() && !mState.opening && !mState.finishing && !(mFooterNoMoreData && mEnableFooterFollowWhenLoadFinished)) {
                        notifyStateChanged(RefreshState.PullUpToLoad);
                    } else {
                        setViceState(RefreshState.PullUpToLoad);
                    }
                    break;
                case PullDownCanceled:
                    if (!mState.opening && isEnableRefresh()) {
                        notifyStateChanged(RefreshState.PullDownCanceled);
                        resetStatus();
                    } else {
                        setViceState(RefreshState.PullDownCanceled);
                    }
                    break;
                case PullUpCanceled:
                    if (isEnableLoadMore() && !mState.opening && !(mFooterNoMoreData && mEnableFooterFollowWhenLoadFinished)) {
                        notifyStateChanged(RefreshState.PullUpCanceled);
                        resetStatus();
                    } else {
                        setViceState(RefreshState.PullUpCanceled);
                    }
                    break;
                case ReleaseToRefresh:
                    if (!mState.opening && isEnableRefresh()) {
                        notifyStateChanged(RefreshState.ReleaseToRefresh);
                    } else {
                        setViceState(RefreshState.ReleaseToRefresh);
                    }
                    break;
                case ReleaseToLoad:
                    if (isEnableLoadMore() && !mState.opening && !mState.finishing && !(mFooterNoMoreData && mEnableFooterFollowWhenLoadFinished)) {
                        notifyStateChanged(RefreshState.ReleaseToLoad);
                    } else {
                        setViceState(RefreshState.ReleaseToLoad);
                    }
                    break;
                case ReleaseToTwoLevel: {
                    if (!mState.opening && isEnableRefresh()) {
                        notifyStateChanged(RefreshState.ReleaseToTwoLevel);
                    } else {
                        setViceState(RefreshState.ReleaseToTwoLevel);
                    }
                    break;
                }
                case RefreshReleased: {
                    if (!mState.opening && isEnableRefresh()) {
                        notifyStateChanged(RefreshState.RefreshReleased);
                    } else {
                        setViceState(RefreshState.RefreshReleased);
                    }
                    break;
                }
                case LoadReleased: {
                    if (!mState.opening && isEnableLoadMore()) {
                        notifyStateChanged(RefreshState.LoadReleased);
                    } else {
                        setViceState(RefreshState.LoadReleased);
                    }
                    break;
                }
                case Refreshing:
                    setStateRefreshing();
                    break;
                case Loading:
                    setStateLoading();
                    break;
                case RefreshFinish: {
                    if (mState == RefreshState.Refreshing) {
                        notifyStateChanged(RefreshState.RefreshFinish);
                    }
                    break;
                }
                case LoadFinish:{
                    if (mState == RefreshState.Loading) {
                        notifyStateChanged(RefreshState.LoadFinish);
                    }
                    break;
                }
                case TwoLevelReleased:
                    notifyStateChanged(RefreshState.TwoLevelReleased);
                    break;
                case TwoLevelFinish:
                    notifyStateChanged(RefreshState.TwoLevelFinish);
                    break;
                case TwoLevel:
                    notifyStateChanged(RefreshState.TwoLevel);
                    break;
            }
            return null;
        }

        @Override
        public RefreshKernel startTwoLevel(boolean open) {
            if (open) {
                AnimatorListenerAdapter listener = new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mKernel.setState(RefreshState.TwoLevel);
                    }
                };
                ValueAnimator animator = SmartRefreshLayout.this.animSpinner(getMeasuredHeight());
                if (animator != null && animator == reboundAnimator) {
                    animator.setDuration(mFloorDuration);
                    animator.addListener(listener);
                } else {
                    listener.onAnimationEnd(null);
                }
            } else {
                if (animSpinner(0) == null) {
                    notifyStateChanged(RefreshState.None);
                }
            }
            return this;
        }

        @Override
        public RefreshKernel finishTwoLevel() {
            if (mState == RefreshState.TwoLevel) {
                mKernel.setState(RefreshState.TwoLevelFinish);
                if (mSpinner == 0) {
                    moveSpinner(0, true);
                    notifyStateChanged(RefreshState.None);
                } else {
                    SmartRefreshLayout.this.animSpinner(0).setDuration(mFloorDuration);
                }
            }
            return this;
        }
        //<editor-fold desc="状态更改 state changes">

        //</editor-fold>

        //<editor-fold desc="视图位移 Spinner">

        public RefreshKernel moveSpinner(int spinner, boolean isAnimator) {
            SmartRefreshLayout.this.moveSpinner(spinner, isAnimator);
            return this;
        }

        public RefreshKernel animSpinner(int endSpinner) {
            SmartRefreshLayout.this.animSpinner(endSpinner);
            return this;
        }

        //</editor-fold>

        //<editor-fold desc="请求事件">
        @Override
        public RefreshKernel requestDrawBackgroundForHeader(int backgroundColor) {
            if (mPaint == null && backgroundColor != 0) {
                mPaint = new Paint();
            }
            mHeaderBackgroundColor = backgroundColor;
            return this;
        }

        @Override
        public RefreshKernel requestDrawBackgroundForFooter(int backgroundColor) {
            if (mPaint == null && backgroundColor != 0) {
                mPaint = new Paint();
            }
            mFooterBackgroundColor = backgroundColor;
            return this;
        }
        @Override
        public RefreshKernel requestNeedTouchEventWhenRefreshing(boolean request) {
            mHeaderNeedTouchEventWhenRefreshing = request;
            return this;
        }
        @Override
        public RefreshKernel requestNeedTouchEventWhenLoading(boolean request) {
            mFooterNeedTouchEventWhenLoading = request;
            return this;
        }
        @Override
        public RefreshKernel requestDefaultHeaderTranslationContent(boolean translation) {
            if (!mManualHeaderTranslationContent) {
                mManualHeaderTranslationContent = true;
                mEnableHeaderTranslationContent = translation;
            }
            return this;
        }
        @Override
        public RefreshKernel requestRemeasureHeightForHeader() {
            if (mHeaderHeightStatus.notified) {
                mHeaderHeightStatus = mHeaderHeightStatus.unNotify();
            }
            return this;
        }
        @Override
        public RefreshKernel requestRemeasureHeightForFooter() {
            if (mFooterHeightStatus.notified) {
                mFooterHeightStatus = mFooterHeightStatus.unNotify();
            }
            return this;
        }
        @Override
        public RefreshKernel requestFloorDuration(int duration) {
            mFloorDuration = duration;
            return this;
        }
        //</editor-fold>
    }
    //</editor-fold>

    //<editor-fold desc="内存泄漏 postDelayed优化">

    @Override
    public boolean post(@NonNull Runnable action) {
        if (mHandler == null) {
            mListDelayedRunnable = mListDelayedRunnable == null ? new ArrayList<DelayedRunnable>() : mListDelayedRunnable;
            mListDelayedRunnable.add(new DelayedRunnable(action));
            return false;
        }
        return mHandler.post(new DelayedRunnable(action));
    }

    @Override
    public boolean postDelayed(@NonNull Runnable action, long delayMillis) {
        if (delayMillis == 0) {
            new DelayedRunnable(action).run();
            return true;
        }
        if (mHandler == null) {
            mListDelayedRunnable = mListDelayedRunnable == null ? new ArrayList<DelayedRunnable>() : mListDelayedRunnable;
            mListDelayedRunnable.add(new DelayedRunnable(action, delayMillis));
            return false;
        }
        return mHandler.postDelayed(new DelayedRunnable(action), delayMillis);
    }

    //</editor-fold>
}
