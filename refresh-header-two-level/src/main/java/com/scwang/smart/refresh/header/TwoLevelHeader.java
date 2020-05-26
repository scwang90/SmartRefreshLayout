package com.scwang.smart.refresh.header;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent2;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.scwang.smart.refresh.header.listener.OnTwoLevelListener;
import com.scwang.smart.refresh.header.two.level.R;
import com.scwang.smart.refresh.layout.simple.SimpleComponent;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshComponent;
import com.scwang.smart.refresh.layout.api.RefreshKernel;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;
import com.scwang.smart.refresh.layout.listener.OnStateChangedListener;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * 二级刷新
 * Created by scwang on 2017/5/26.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class TwoLevelHeader extends SimpleComponent implements RefreshHeader, NestedScrollingParent2 {

    //<editor-fold desc="属性字段">
    protected int mSpinner;
    protected float mPercent = 0;
    protected float mMaxRate = 2.5f;
    protected float mFloorRate = 1.9f;
    protected float mRefreshRate = 1f;
    protected float mBottomPullUpToCloseRate = 1/6f;
    protected boolean mEnableRefresh = true;
    protected boolean mEnableTwoLevel = true;
    protected boolean mEnablePullToCloseTwoLevel = true;
    protected int mFloorDuration = 1000;
    protected int mHeaderHeight;

    protected RefreshComponent mRefreshHeader;
    protected RefreshKernel mRefreshKernel;
    protected OnTwoLevelListener mTwoLevelListener;
    //</editor-fold>

    //<editor-fold desc="构造方法">
    public TwoLevelHeader(Context context) {
        this(context, null);
    }

    public TwoLevelHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);

        mSpinnerStyle = SpinnerStyle.FixedBehind;

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TwoLevelHeader);

        mMaxRate = ta.getFloat(R.styleable.TwoLevelHeader_srlMaxRate, mMaxRate);
        mFloorRate = ta.getFloat(R.styleable.TwoLevelHeader_srlFloorRate, mFloorRate);
        mRefreshRate = ta.getFloat(R.styleable.TwoLevelHeader_srlRefreshRate, mRefreshRate);
        mFloorDuration = ta.getInt(R.styleable.TwoLevelHeader_srlFloorDuration, mFloorDuration);
        mEnableRefresh = ta.getBoolean(R.styleable.TwoLevelHeader_srlEnableRefresh, mEnableRefresh);
        mEnableTwoLevel = ta.getBoolean(R.styleable.TwoLevelHeader_srlEnableTwoLevel, mEnableTwoLevel);
        mBottomPullUpToCloseRate = ta.getFloat(R.styleable.TwoLevelHeader_srlBottomPullUpToCloseRate, mBottomPullUpToCloseRate);
        mEnablePullToCloseTwoLevel = ta.getBoolean(R.styleable.TwoLevelHeader_srlEnablePullToCloseTwoLevel, mEnablePullToCloseTwoLevel);

        ta.recycle();
    }
    //</editor-fold>

    //<editor-fold desc="生命周期">
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        final ViewGroup thisGroup = this;
        for (int i = 0, len = thisGroup.getChildCount(); i < len; i++) {
            View childAt = thisGroup.getChildAt(i);
            if (childAt instanceof RefreshHeader) {
                mRefreshHeader = (RefreshHeader) childAt;
                mWrappedInternal = (RefreshComponent) childAt;
                thisGroup.bringChildToFront(childAt);
                break;
            }
        }
        if (mRefreshHeader == null) {
            final ViewGroup thisView = this;
            setRefreshHeader(new ClassicsHeader(thisView.getContext()));
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mSpinnerStyle = SpinnerStyle.MatchLayout;
        if (mRefreshHeader == null) {
            final ViewGroup thisView = this;
            setRefreshHeader(new ClassicsHeader(thisView.getContext()));
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mSpinnerStyle = SpinnerStyle.FixedBehind;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final RefreshComponent refreshHeader = mRefreshHeader;
        if (refreshHeader != null) {
            int mode = MeasureSpec.getMode(heightMeasureSpec);
            if (mode == MeasureSpec.AT_MOST) {
                refreshHeader.getView().measure(widthMeasureSpec, heightMeasureSpec);
                int height = refreshHeader.getView().getMeasuredHeight();
                super.setMeasuredDimension(View.resolveSize(super.getSuggestedMinimumWidth(), widthMeasureSpec), height);
            } else {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    public boolean equals(Object obj) {
        final Object header = mRefreshHeader;
        return (header != null && header.equals(obj)) || super.equals(obj);
    }

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {
        final View thisView = this;
        final RefreshComponent refreshHeader = mRefreshHeader;
        if (refreshHeader == null) {
            return;
        }
        if (1f * (maxDragHeight + height) / height != mMaxRate && mHeaderHeight == 0) {
            mHeaderHeight = height;
            mRefreshHeader = null;
            kernel.getRefreshLayout().setHeaderMaxDragRate(mMaxRate);
            mRefreshHeader = refreshHeader;
        }
        if (mRefreshKernel == null //第一次初始化
                && refreshHeader.getSpinnerStyle() == SpinnerStyle.Translate
                && !thisView.isInEditMode()) {
            MarginLayoutParams params = (MarginLayoutParams) refreshHeader.getView().getLayoutParams();
            params.topMargin -= height;
            refreshHeader.getView().setLayoutParams(params);
        }

        mHeaderHeight = height;
        mRefreshKernel = kernel;
        kernel.requestFloorDuration(mFloorDuration);
        kernel.requestFloorBottomPullUpToCloseRate(mBottomPullUpToCloseRate);
        kernel.requestNeedTouchEventFor(this, !mEnablePullToCloseTwoLevel);
        refreshHeader.onInitialized(kernel, height, maxDragHeight);

    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        final RefreshComponent refreshHeader = mRefreshHeader;
        if (refreshHeader != null) {
            final OnStateChangedListener listener = mRefreshHeader;
            if (newState == RefreshState.ReleaseToRefresh && !mEnableRefresh) {
                newState = RefreshState.PullDownToRefresh;
            }
            listener.onStateChanged(refreshLayout, oldState, newState);
            switch (newState) {
                case TwoLevelReleased:
                    if (refreshHeader.getView() != this) {
                        refreshHeader.getView().animate().alpha(0).setDuration(mFloorDuration / 2);
                    }
                    final RefreshKernel refreshKernel = mRefreshKernel;
                    if (refreshKernel != null) {
                        final OnTwoLevelListener twoLevelListener = mTwoLevelListener;
                        refreshKernel.startTwoLevel(twoLevelListener == null || twoLevelListener.onTwoLevel(refreshLayout));
                    }
                    break;
                case TwoLevel:
                    break;
                case TwoLevelFinish:
                    if (refreshHeader.getView() != this) {
                        refreshHeader.getView().animate().alpha(1).setDuration(mFloorDuration / 2);
                    }
                    break;
                case PullDownToRefresh:
                    if (refreshHeader.getView().getAlpha() == 0 && refreshHeader.getView() != this) {
                        refreshHeader.getView().setAlpha(1);
                    }
                    break;
            }
        }
    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
        moveSpinner(offset);
        final RefreshComponent refreshHeader = mRefreshHeader;
        final RefreshKernel refreshKernel = mRefreshKernel;
        if (refreshHeader != null) {
            refreshHeader.onMoving(isDragging, percent, offset, height, maxDragHeight);
        }
        if (isDragging) {
            if (mPercent < mFloorRate && percent >= mFloorRate && mEnableTwoLevel) {
                refreshKernel.setState(RefreshState.ReleaseToTwoLevel);
            } else if (mPercent >= mFloorRate && percent < mRefreshRate) {
                refreshKernel.setState(RefreshState.PullDownToRefresh);
            } else if (mPercent >= mFloorRate && percent < mFloorRate && mEnableRefresh) {
                refreshKernel.setState(RefreshState.ReleaseToRefresh);
            } else if (!mEnableRefresh && refreshKernel.getRefreshLayout().getState() != RefreshState.ReleaseToTwoLevel) {
                refreshKernel.setState(RefreshState.PullDownToRefresh);
            }
            mPercent = percent;
        }
    }

    protected void moveSpinner(int spinner) {
        final RefreshComponent refreshHeader = mRefreshHeader;
        if (mSpinner != spinner && refreshHeader != null) {
            mSpinner = spinner;
            SpinnerStyle style = refreshHeader.getSpinnerStyle();
            if (style == SpinnerStyle.Translate) {
                refreshHeader.getView().setTranslationY(spinner);
            } else if (style.scale) {
                View view = refreshHeader.getView();
                view.layout(view.getLeft(), view.getTop(), view.getRight(), view.getTop() + Math.max(0, spinner));
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="NestedScrollingParent2">
    private int mNestedScrollAxes = 0;
    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int nestedScrollAxes) {
        return true;
    }
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int nestedScrollAxes) {
        mNestedScrollAxes = nestedScrollAxes;
    }
    public void onStopNestedScroll(@NonNull View target) {}
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {}
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed){}
    public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed){return false;}
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {return false;}
    public int getNestedScrollAxes() {return mNestedScrollAxes;}

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int nestedScrollAxes, int i1) {
        return true;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int nestedScrollAxes, int i1) {
        mNestedScrollAxes = nestedScrollAxes;
    }

    @Override
    public void onStopNestedScroll(@NonNull View view, int i) {

    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int i4) {

    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int i2) {

    }
    //</editor-fold>

    //<editor-fold desc="开放接口 - API">

    /**
     * 设置指定的 Header
     * @param header RefreshHeader
     * @return TwoLevelHeader
     */
    public TwoLevelHeader setRefreshHeader(RefreshHeader header) {
        return setRefreshHeader(header, 0, 0);
    }

    /**
     * 设置指定的 Header
     * @param header RefreshHeader
     * @param width 指定宽度
     * @param height 指定高度
     * @return TwoLevelHeader
     */
    public TwoLevelHeader setRefreshHeader(RefreshHeader header, int width, int height) {
        final ViewGroup thisGroup = this;
        if (header != null) {
            /*
             * 2020-3-16 修复 header 中自带 LayoutParams 丢失问题
             */
            width = width == 0 ? MATCH_PARENT : width;
            height = height == 0 ? WRAP_CONTENT : height;
            LayoutParams lp = new LayoutParams(width, height);
            Object olp = header.getView().getLayoutParams();
            if (olp instanceof LayoutParams) {
                lp = ((LayoutParams) olp);
            }

            RefreshComponent refreshHeader = mRefreshHeader;
            if (refreshHeader != null) {
                thisGroup.removeView(refreshHeader.getView());
            }
            refreshHeader = header;
            if (refreshHeader.getSpinnerStyle() == SpinnerStyle.FixedBehind) {
                thisGroup.addView(refreshHeader.getView(), 0, lp);
            } else {
                thisGroup.addView(refreshHeader.getView(), thisGroup.getChildCount(), lp);
            }
            this.mRefreshHeader = header;
            this.mWrappedInternal = header;
        }
        return this;
    }

    /**
     * 设置下拉 Header 的最大高度比值
     * @param rate MaxDragHeight/HeaderHeight
     * @return TwoLevelHeader
     */
    public TwoLevelHeader setMaxRate(float rate) {
        if (this.mMaxRate != rate) {
            this.mMaxRate = rate;
            final RefreshKernel refreshKernel = mRefreshKernel;
            if (refreshKernel != null) {
                this.mHeaderHeight = 0;
                refreshKernel.getRefreshLayout().setHeaderMaxDragRate(mMaxRate);
            }
        }
        return this;
    }

    /**
     * 是否禁止在二极状态是上滑关闭状态回到初态
     * @param enabled 是否启用
     * @return TwoLevelHeader
     */
    public TwoLevelHeader setEnablePullToCloseTwoLevel(boolean enabled) {
        final RefreshKernel refreshKernel = mRefreshKernel;
        this.mEnablePullToCloseTwoLevel = enabled;
        if (refreshKernel != null) {
            refreshKernel.requestNeedTouchEventFor(this, !enabled);
        }
        return this;
    }

    /**
     * 设置是否开启
     * @param enabled 刷新功能
     * @return TwoLevelHeader
     */
    public TwoLevelHeader setEnableRefresh(boolean enabled) {
        this.mEnableRefresh = enabled;
        return this;
    }

    /**
     * 设置触发二楼的白百分比
     * @param rate 比率 要求大于 RefreshRate
     * @return TwoLevelHeader
     */
    public TwoLevelHeader setFloorRate(float rate) {
        this.mFloorRate = rate;
        return this;
    }

    /**
     * 设置触发刷新的百分比
     * @param rate 比率 要求小于 FloorRate
     * @return TwoLevelHeader
     */
    public TwoLevelHeader setRefreshRate(float rate) {
        this.mRefreshRate = rate;
        return this;
    }

    /**
     * 设置是否开启二级刷新
     * @param enabled 是否开启
     * @return TwoLevelHeader
     */
    public TwoLevelHeader setEnableTwoLevel(boolean enabled) {
        this.mEnableTwoLevel = enabled;
        return this;
    }

    /**
     * 设置二楼展开动画持续的时间
     * @param duration 毫秒
     * @return TwoLevelHeader
     */
    public TwoLevelHeader setFloorDuration(int duration) {
        this.mFloorDuration = duration;
        return this;
    }

    /**
     * 设置二路底部上划关闭所占高度比率
     * @param rate 底部占高度比率
     */
    public TwoLevelHeader setBottomPullUpToCloseRate(float rate) {
        this.mBottomPullUpToCloseRate = rate;
        return this;
    }

    /**
     * 设置二级刷新监听器
     * @param listener 监听器
     * @return TwoLevelHeader
     */
    public TwoLevelHeader setOnTwoLevelListener(OnTwoLevelListener listener) {
        this.mTwoLevelListener = listener;
        return this;
    }

    /**
     * 结束二级刷新
     * @return TwoLevelHeader
     */
    public TwoLevelHeader finishTwoLevel() {
        final RefreshKernel refreshKernel = mRefreshKernel;
        if (refreshKernel != null) {
            refreshKernel.finishTwoLevel();
        }
        return this;
    }

    /**
     * Open the second floor voluntarily
     * 主动打开二楼
     * @param widthOnTwoLevelListener 是否触发 OnTwoLevelListener 监听器
     * @return TwoLevelHeader
     */
    public TwoLevelHeader openTwoLevel(boolean widthOnTwoLevelListener) {
        final RefreshKernel refreshKernel = mRefreshKernel;
        if (refreshKernel != null) {
            final OnTwoLevelListener twoLevelListener = mTwoLevelListener;
            refreshKernel.startTwoLevel(!widthOnTwoLevelListener || twoLevelListener == null || twoLevelListener.onTwoLevel(refreshKernel.getRefreshLayout()));
        }
        return this;
    }
    //</editor-fold>
}