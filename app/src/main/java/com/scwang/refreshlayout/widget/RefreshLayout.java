package com.scwang.refreshlayout.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Px;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.OverScroller;

import com.scwang.smartrefresh.layout.api.RefreshContent;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
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

public class RefreshLayout extends ViewGroup implements GestureDetector.OnGestureListener {

    private OverScroller mScroller;
    private RefreshHeader mRefreshHeader;
    private RefreshFooter mRefreshFooter;
    private RefreshContent mRefreshContent;
    private GestureDetectorCompat mGesture;
    private int mTouchSlop;
    /**
     * 头部高度
     */
    protected int mHeaderHeight;
    /**
     * 底部高度
     */
    protected int mFooterHeight;


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
        mScroller = new OverScroller(context);
        mGesture = new GestureDetectorCompat(context, this);
        mGesture.setIsLongpressEnabled(false);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
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
            mRefreshContent.layout(0, 0, getMeasuredWidth(), getMeasuredHeight());
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

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        switch (MotionEventCompat.getActionMasked(ev)) {
//            case MotionEvent.ACTION_DOWN:
//                mIsBeingDragged = false;
//                mInitialDownY = ev.getY();
//                mGesture.onTouchEvent(ev);
//                break;
//
//        }
//        return mIsBeingDragged;//super.dispatchTouchEvent(ev);
//    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mGesture.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        return false;//super.onInterceptTouchEvent(ev);
//    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        return mGesture.onTouchEvent(event);
//    }
    //</editor-fold>

    //<editor-fold desc="滚动计算">
    private int mlastScrollY;
    boolean isFling = false;
    int scrollY;
    int currllY;
    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int currY = mScroller.getCurrY();
            int dy = currY - mlastScrollY;
//            if (isFling) {
//                scrollTo(0, getScrollY() + dy);
//            } else {
//                if (dy < 0 && mRefreshContent.canRefresh()) {
//                    isFling = true;
//                    scrollY = getScrollY();
//                    currllY = mScroller.getCurrY();
//                } else if (dy > 0 && mRefreshContent.canLoadmore()) {
//                    isFling = true;
//                    scrollY = getScrollY();
//                    currllY = mScroller.getCurrY();
//                }
//            }
//            mlastScrollY = currY;
            postInvalidate();
        } else {
            isFling = false;
        }
    }

    @Override
    public void scrollTo(@Px int x, @Px int y) {
        super.scrollTo(x, y);
    }
    //</editor-fold>

    //<editor-fold desc="OnGestureListener">
    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//        scrollBy(0, (int)distanceY);
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        isFling = false;
        mScroller.fling(0, getScrollY(), 0, -(int)velocityY, 0, 0, -mHeaderHeight, mFooterHeight);
        mlastScrollY = mScroller.getCurrY();
        postInvalidate();
        return true;
    }
    //</editor-fold>
}
