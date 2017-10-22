package com.scwang.refreshlayout.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.scwang.smartrefresh.layout.api.RefreshContent;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.impl.RefreshContentWrapper;
import com.scwang.smartrefresh.layout.impl.RefreshFooterWrapper;
import com.scwang.smartrefresh.layout.impl.RefreshHeaderWrapper;

import static android.view.View.MeasureSpec.AT_MOST;
import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.getSize;
import static android.view.View.MeasureSpec.makeMeasureSpec;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * ScrollView
 * Created by SCWANG on 2017/7/10.
 */

public class RefreshLayout extends ViewGroup {

    private RefreshHeader mRefreshHeader;
    private RefreshFooter mRefreshFooter;
    private RefreshContent mRefreshContent;
    /**
     * 头部高度
     */
    protected int mHeaderHeight;
    /**
     * 底部高度
     */
    protected int mFooterHeight;
    private int mSpinner;
    private RefreshState mState;
    private int mFooterExtendHeight;
    private int mHeaderExtendHeight;
    private int mScreenHeightPixels;
    private float mDragRate = 0.5f;
    protected float mTouchX;
    protected float mTouchY;
    private int mTouchSpinner;
    private boolean mIsBeingDragged;
    private int mTouchSlop;
    private boolean mEnableRefresh = true;
    private boolean mEnableLoadmore = true;


    //<editor-fold desc="构造方法">
    public RefreshLayout(Context context) {
        super(context);
        initView(context);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public RefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RefreshLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        ViewConfiguration configuration = ViewConfiguration.get(context);

        mScreenHeightPixels = context.getResources().getDisplayMetrics().heightPixels;
        mTouchSlop = configuration.getScaledTouchSlop();
    }
    //</editor-fold>

    //<editor-fold desc="测量和布局">
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        final int childCount = getChildCount();
        if (childCount == 1) {
            mRefreshContent = new RefreshContentWrapper(getChildAt(0));
        } else if (childCount > 1) {
            mRefreshHeader = new RefreshHeaderWrapper(getChildAt(0));
            mRefreshContent = new RefreshContentWrapper(getChildAt(1));
            if (childCount > 2) {
                mRefreshFooter = new RefreshFooterWrapper(getChildAt(2));
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mRefreshContent != null) {
            mRefreshContent.measure(widthMeasureSpec, heightMeasureSpec);
        }
        if (mRefreshHeader != null) {
            final View headerView = mRefreshHeader.getView();
            final LayoutParams lp = (LayoutParams) headerView.getLayoutParams();
            final int widthSpec = getChildMeasureSpec(widthMeasureSpec, lp.leftMargin + lp.rightMargin, lp.width);
            int heightSpec = heightMeasureSpec;
            if (lp.height > 0) {
                heightSpec = makeMeasureSpec(lp.height, EXACTLY);
                headerView.measure(widthSpec, heightSpec);
            } else if (lp.height == WRAP_CONTENT) {
                heightSpec = makeMeasureSpec(Math.max(getSize(heightMeasureSpec)/* - lp.topMargin*/ - lp.bottomMargin, 0), AT_MOST);
                headerView.measure(widthSpec, heightSpec);
            } else {
                headerView.measure(widthSpec, heightSpec);
            }
            mHeaderHeight = headerView.getMeasuredHeight();
        }
        if (mRefreshFooter != null) {
            final View footerView = mRefreshFooter.getView();
            final LayoutParams lp = (LayoutParams) footerView.getLayoutParams();
            final int widthSpec = getChildMeasureSpec(widthMeasureSpec, lp.leftMargin + lp.rightMargin, lp.width);
            int heightSpec = heightMeasureSpec;
            if (lp.height > 0) {
                heightSpec = makeMeasureSpec(lp.height, EXACTLY);
                footerView.measure(widthSpec, heightSpec);
            } else if (lp.height == WRAP_CONTENT) {
                heightSpec = makeMeasureSpec(Math.max(getSize(heightMeasureSpec)/* - lp.topMargin*/ - lp.bottomMargin, 0), AT_MOST);
                footerView.measure(widthSpec, heightSpec);
            } else {
                footerView.measure(widthSpec, heightSpec);
            }
            mFooterHeight = footerView.getMeasuredHeight();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mRefreshContent != null) {
            mRefreshContent.layout(0, 0, getMeasuredWidth(), getMeasuredHeight(), false);
        }
        if (mRefreshHeader != null) {
            final View headerView = mRefreshHeader.getView();
            int right = headerView.getMeasuredWidth();
            int top = -headerView.getMeasuredHeight();
            headerView.layout(0, top, right, 0);
        }
        if (mRefreshFooter != null) {
            final View headerView = mRefreshFooter.getView();
            int right = headerView.getMeasuredWidth();
            int top = getMeasuredHeight();
            int bottom = top + headerView.getMeasuredHeight();
            headerView.layout(0, top, right, bottom);
        }
    }
    //</editor-fold>

    //<editor-fold desc="布局参数">
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

    //<editor-fold desc="触摸事件">
    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        int actionMasked = e.getActionMasked();
        float touchX = e.getX();
        float touchY = e.getY();
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                mTouchX = touchX;
                mTouchY = touchY;
                mTouchSpinner = mSpinner;
                mIsBeingDragged = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = touchX - mTouchX;
                float dy = touchY - mTouchY;
                if (!mIsBeingDragged && Math.abs(dy) >= mTouchSlop && Math.abs(dx) < Math.abs(dy)) {//滑动允许最大角度为45度
                    if (dy > 0 && (mSpinner < 0 || (mEnableRefresh && mRefreshContent.canRefresh()))) {
                        dy = touchY - mTouchY;
                        mIsBeingDragged = true;
                        mTouchY = touchY - mTouchSlop;
                        e.setAction(MotionEvent.ACTION_CANCEL);
                        superDispatchTouchEvent(e);
                    } else if (dy < 0 && (mSpinner > 0 || (mEnableLoadmore && mRefreshContent.canLoadmore()))) {
                        dy = touchY - mTouchY;
                        mIsBeingDragged = true;
                        mTouchY = touchY + mTouchSlop;
                        e.setAction(MotionEvent.ACTION_CANCEL);
                        superDispatchTouchEvent(e);
                    }
                }
                if (mIsBeingDragged) {
//                    final float spinner = dy + mTouchSpinner;
//                    if ((mRefreshContent != null)
//                            && (getViceState().isHeader() && (spinner < 0 || mLastSpinner < 0))
//                            || (getViceState().isFooter() && (spinner > 0 || mLastSpinner > 0))) {
//                        long time = e.getEventTime();
//                        if (mFalsifyEvent == null) {
//                            mFalsifyEvent = MotionEvent.obtain(time, time, MotionEvent.ACTION_DOWN, mTouchX + dx, mTouchY, 0);
//                            superDispatchTouchEvent(mFalsifyEvent);
//                        }
//                        MotionEvent em = MotionEvent.obtain(time, time, MotionEvent.ACTION_MOVE, mTouchX + dx, mTouchY + spinner, 0);
//                        superDispatchTouchEvent(em);
//                        if ((getViceState().isHeader() && spinner < 0) || (getViceState().isFooter() && spinner > 0)) {
//                            mLastSpinner = (int) spinner;
//                            if (mSpinner != 0) {
//                                moveSpinnerInfinitely(0);
//                            }
//                            return true;
//                        }
//                        mLastSpinner = (int) spinner;
//                        mFalsifyEvent = null;
//                        MotionEvent ec = MotionEvent.obtain(time, time, MotionEvent.ACTION_CANCEL, mTouchX, mTouchY + spinner, 0);
//                        superDispatchTouchEvent(ec);
//                    }
//                    if (getViceState().isDraging()) {
//                        moveSpinnerInfinitely(spinner);
//                        return true;
//                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;

        }
        return superDispatchTouchEvent(e);
    }

    private boolean superDispatchTouchEvent(MotionEvent e) {
        return super.dispatchTouchEvent(e);
    }

    //</editor-fold>


    protected void moveSpinnerInfinitely(float dy) {
        if (mState == RefreshState.Refreshing && dy >= 0) {
            if (dy < mHeaderHeight) {
                moveSpinner((int) dy, false);
            } else {
                final double M = mHeaderExtendHeight;
                final double H = Math.max(mScreenHeightPixels * 4 / 3, getHeight()) - mHeaderHeight;
                final double x = Math.max(0, (dy - mHeaderHeight) * mDragRate);
                final double y = Math.min(M * (1 - Math.pow(100, -x / H)), x);// 公式 y = M(1-40^(-x/H))
                moveSpinner((int) y + mHeaderHeight, false);
            }
        } else if (mState == RefreshState.Loading && dy < 0) {
            if (dy > -mFooterHeight) {
                moveSpinner((int) dy, false);
            } else {
                final double M = mFooterExtendHeight;
                final double H = Math.max(mScreenHeightPixels * 4 / 3, getHeight()) - mFooterHeight;
                final double x = -Math.min(0, (dy + mHeaderHeight) * mDragRate);
                final double y = -Math.min(M * (1 - Math.pow(100, -x / H)), x);// 公式 y = M(1-40^(-x/H))
                moveSpinner((int) y - mFooterHeight, false);
            }
        } else if (dy >= 0) {
            final double M = mHeaderExtendHeight + mHeaderHeight;
            final double H = Math.max(mScreenHeightPixels / 2, getHeight());
            final double x = Math.max(0, dy * mDragRate);
            final double y = Math.min(M * (1 - Math.pow(100, -x / H)), x);// 公式 y = M(1-40^(-x/H))
            moveSpinner((int) y, false);
        } else {
            final double M = mFooterExtendHeight + mFooterHeight;
            final double H = Math.max(mScreenHeightPixels / 2, getHeight());
            final double x = -Math.min(0, dy * mDragRate);
            final double y = -Math.min(M * (1 - Math.pow(100, -x / H)), x);// 公式 y = M(1-40^(-x/H))
            moveSpinner((int) y, false);
        }
    }

    /**
     * 移动滚动 Scroll
     * moveSpinner 的取名来自 谷歌官方的 @{@link android.support.v4.widget.SwipeRefreshLayout#moveSpinner(float)}
     */
    protected void moveSpinner(int spinner, boolean isAnimator) {
        if (mSpinner == spinner
                && (mRefreshHeader == null || !mRefreshHeader.isSupportHorizontalDrag())
                && (mRefreshFooter == null || !mRefreshFooter.isSupportHorizontalDrag())) {
            return;
        }
        final int oldSpinner = mSpinner;
        this.mSpinner = spinner;
        if (mRefreshContent != null) {
            if (spinner > 0) {
                if (mRefreshHeader == null || mRefreshHeader.getSpinnerStyle() == SpinnerStyle.FixedBehind) {
                    mRefreshContent.moveSpinner(spinner);
                }
            } else {
                if (mRefreshFooter == null || mRefreshFooter.getSpinnerStyle() == SpinnerStyle.FixedBehind) {
                    mRefreshContent.moveSpinner(spinner);
                }
            }
        }
        if ((spinner > 0 || oldSpinner > 0) && mRefreshHeader != null) {
            spinner = Math.max(spinner, 0);
            if ((mState == RefreshState.RefreshFinish && isAnimator)) {
                if (oldSpinner != mSpinner
                        && (mRefreshHeader.getSpinnerStyle() == SpinnerStyle.Scale
                        || mRefreshHeader.getSpinnerStyle() == SpinnerStyle.Translate)) {
                    mRefreshHeader.getView().requestLayout();
                }
            }

            final int offset = spinner;
            final int headerHeight = mHeaderHeight;
            final int extendHeight = mHeaderExtendHeight;
            final float percent = 1f * spinner / mHeaderHeight;
            if (isAnimator) {
                mRefreshHeader.onReleasing(percent, offset, headerHeight, extendHeight);
            } else {
                mRefreshHeader.onPullingDown(percent, offset, headerHeight, extendHeight);
            }
        }
        if ((spinner < 0 || oldSpinner < 0) && mRefreshFooter != null) {
            spinner = Math.min(spinner, 0);
            if ((mState == RefreshState.LoadFinish && isAnimator)) {
                if (oldSpinner != mSpinner
                        && (mRefreshFooter.getSpinnerStyle() == SpinnerStyle.Scale
                        || mRefreshFooter.getSpinnerStyle() == SpinnerStyle.Translate)) {
                    mRefreshFooter.getView().requestLayout();
                }
            }

            final int offset = -spinner;
            final int footerHeight = mFooterHeight;
            final int extendHeight = mFooterExtendHeight;
            final float percent = -spinner * 1f / mFooterHeight;
            if (isAnimator) {
                mRefreshFooter.onPullReleasing(percent, offset, footerHeight, extendHeight);
            } else {
                mRefreshFooter.onPullingUp(percent, offset, footerHeight, extendHeight);
            }
        }
    }
}
