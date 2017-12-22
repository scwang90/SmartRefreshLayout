package com.scwang.refreshlayout.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout.RefreshKernelImpl;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.impl.RefreshHeaderWrapper;

@SuppressWarnings("unused")
public class TwoLevelHeader extends FrameLayout implements RefreshHeader {

    /**
     * 二级刷新监听器
     * Created by SCWANG on 2017/5/26.
     */

    public interface OnTwoLevelListener {
        /**
         * 二级刷新触发
         * @param refreshLayout 刷新布局
         * @return true 将会展开二楼状态 false 关闭刷新
         */
        boolean onTwoLevel(RefreshLayout refreshLayout);
    }

    //<editor-fold desc="属性字段">
    protected int mSpinner;
    protected float mPercent = 0;
    protected float mMaxRage = 2.0f;
    protected float mFloorRage = 1.9f;
    protected float mRefreshRage = 1f;
    protected boolean mEnableTwoLevel = true;
    protected int mFloorDuration = 1000;
    protected int mHeaderHeight;
    protected Paint mPaint;
    protected RefreshHeader mRefreshHeader;
    protected RefreshKernel mRefreshKernel;
    protected SpinnerStyle mSpinnerStle = SpinnerStyle.FixedBehind;
    protected OnTwoLevelListener mTwoLevelListener;
    //</editor-fold>

    //<editor-fold desc="构造方法">
    public TwoLevelHeader(@NonNull Context context) {
        super(context);
        this.initView(context, null);
    }

    public TwoLevelHeader(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initView(context, attrs);
    }

    public TwoLevelHeader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView(context, attrs);
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public TwoLevelHeader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initView(context, attrs);
    }

    public void initView(Context context, AttributeSet attrs) {
    }
    //</editor-fold>

    //<editor-fold desc="生命周期">
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        for (int i = 0, len = getChildCount(); i < len; i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof RefreshHeader) {
                mRefreshHeader = (RefreshHeader) childAt;
                bringChildToFront(childAt);
                break;
            }
        }
        if (mRefreshHeader == null) {
            mRefreshHeader = new RefreshHeaderWrapper(this);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mSpinnerStle = SpinnerStyle.MatchLayout;
        if (mRefreshHeader == null) {
            mRefreshHeader = new RefreshHeaderWrapper(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mSpinnerStle = SpinnerStyle.FixedBehind;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mRefreshHeader.getView() != this) {
            int mode = MeasureSpec.getMode(heightMeasureSpec);
            if (mode == MeasureSpec.AT_MOST) {
                mRefreshHeader.getView().measure(widthMeasureSpec, heightMeasureSpec);
                int height = mRefreshHeader.getView().getMeasuredHeight();
                setMeasuredDimension(resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec), height);
            } else {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        boolean isInEditMode = isInEditMode();
        if (mPaint != null && (mSpinner > 0 || isInEditMode)) {
            RefreshState state = mRefreshKernel.getRefreshLayout().getState();
            if (state != RefreshState.TwoLevelReleased && state != RefreshState.TwoLevel) {
                canvas.drawRect(0, 0, getWidth(), (isInEditMode) ? mHeaderHeight : mSpinner, mPaint);
                drawChild(canvas, mRefreshHeader.getView(), getDrawingTime());
            }
        }
    }
    //</editor-fold>


    //<editor-fold desc="Header实现">
    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int extendHeight) {
        if (1f * (extendHeight + height) / height < mMaxRage) {
            kernel.getRefreshLayout().setHeaderMaxDragRate(mMaxRage);
            return;
        }
        mHeaderHeight = height;
        mRefreshKernel = kernel;
        mRefreshKernel.requestFloorDuration(mFloorDuration);
        mRefreshHeader.onInitialized(new RefreshKernelImpl((SmartRefreshLayout)kernel.getRefreshLayout()) {
            @Override
            public RefreshKernel requestDrawBackgoundForHeader(int backgroundColor) {
                if (backgroundColor == 0) {
                    mPaint = null;
                } else {
                    if (mPaint == null) {
                        mPaint = new Paint();
                    }
                    mPaint.setColor(backgroundColor);
                }
                return this;
            }
        }, height, extendHeight);
        if (!isInEditMode() && mRefreshHeader.getSpinnerStyle() == SpinnerStyle.Translate) {
            MarginLayoutParams params = (MarginLayoutParams) mRefreshHeader.getView().getLayoutParams();
            params.topMargin -= height;
            mRefreshHeader.getView().setLayoutParams(params);
        }
    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        mRefreshHeader.onStateChanged(refreshLayout, oldState, newState);
        switch (newState) {
            case TwoLevelReleased:
                mRefreshHeader.getView().animate().alpha(0).setDuration(mFloorDuration / 2);
                if (mPaint != null) {
                    invalidate();
                }
                mRefreshKernel.startTwoLevel(mTwoLevelListener == null || mTwoLevelListener.onTwoLevel(refreshLayout));
                break;
            case TwoLevel:
                break;
            case TwoLevelFinish:
                mRefreshHeader.getView().animate().alpha(1).setDuration(mFloorDuration / 2);
                break;
            case PullDownToRefresh:
                if (mRefreshHeader.getView().getAlpha() == 0) {
                    mRefreshHeader.getView().setAlpha(1);
                }
                break;
        }
    }

    @Override
    public void onPullingDown(float percent, int offset, int headerHeight, int extendHeight) {
        moveSpinner(offset);
        mRefreshHeader.onPullingDown(percent, offset, headerHeight, extendHeight);
        if (mPercent < mFloorRage && percent >= mFloorRage && mEnableTwoLevel) {
            mRefreshKernel.setState(RefreshState.ReleaseToTwoLevel);
        } else if (mPercent >= mFloorRage && percent < mRefreshRage) {
            mRefreshKernel.setState(RefreshState.PullDownToRefresh);
        } else if (mPercent >= mFloorRage && percent < mFloorRage) {
            mRefreshKernel.setState(RefreshState.ReleaseToRefresh);
        }
        mPercent = percent;
    }

    @Override
    public void onReleasing(float percent, int offset, int headerHeight, int extendHeight) {
        moveSpinner(offset);
        mRefreshHeader.onReleasing(percent, offset, headerHeight, extendHeight);
    }

    protected void moveSpinner(int spinner) {
        if (mSpinner != spinner) {
            mSpinner = spinner;
            switch (mRefreshHeader.getSpinnerStyle()) {
                case Translate:
                    mRefreshHeader.getView().setTranslationY(spinner);
                    break;
                case Scale:{
                    View view = mRefreshHeader.getView();
                    view.layout(view.getLeft(), view.getTop(), view.getRight(), view.getTop() + Math.max(0, spinner));
                    break;
                }
            }
            if (mPaint != null) {
                invalidate();
            }
        }
    }

    @Override
    public void onRefreshReleased(RefreshLayout layout, int headerHeight, int extendHeight) {
        mRefreshHeader.onRefreshReleased(layout, headerHeight, extendHeight);
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return mSpinnerStle;
    }

    @Override
    public void setPrimaryColors(int... colors) {
        mRefreshHeader.setPrimaryColors(colors);
    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {
        mRefreshHeader.onHorizontalDrag(percentX, offsetX, offsetMax);
    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout layout, int height, int extendHeight) {
        mRefreshHeader.onStartAnimator(layout, height, extendHeight);
    }

    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        return mRefreshHeader.onFinish(layout, success);
    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return mRefreshHeader.isSupportHorizontalDrag();
    }
    //</editor-fold>

    //<editor-fold desc="开放API">
    public void setRefreshHeader(RefreshHeader header) {
        this.mRefreshHeader = header;
    }

    public void setmMaxRage(float rate) {
        this.mMaxRage = rate;
    }

    public void setmFloorRage(float rate) {
        this.mFloorRage = rate;
    }

    public void setmRefreshRage(float rate) {
        this.mRefreshRage = rate;
    }

    public void setEnableTwoLevel(boolean enable) {
        this.mEnableTwoLevel = enable;
    }

    public void setOnTwoLevelListener(OnTwoLevelListener listener) {
        this.mTwoLevelListener = listener;
    }

    public void finishTwoLevel() {
        this.mRefreshKernel.finishTwoLevel();
    }
    //</editor-fold>
}