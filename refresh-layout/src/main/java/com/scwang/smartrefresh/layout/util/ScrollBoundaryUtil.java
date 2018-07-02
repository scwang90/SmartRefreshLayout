package com.scwang.smartrefresh.layout.util;

import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

/**
 * 滚动边界
 * Created by SCWANG on 2017/7/8.
 */

@SuppressWarnings("WeakerAccess")
public class ScrollBoundaryUtil {

    //<editor-fold desc="滚动判断">

    /**
     * 判断内容是否可以刷新
     * @param targetView 内容视图
     * @param touch 按压事件位置
     * @return 是否可以刷新
     */
    public static boolean canRefresh(@NonNull View targetView, PointF touch) {
        if (canScrollUp(targetView) && targetView.getVisibility() == View.VISIBLE) {
            return false;
        }
        //touch == null 时 canRefresh 不会动态递归搜索
        if (targetView instanceof ViewGroup && touch != null) {
            ViewGroup viewGroup = (ViewGroup) targetView;
            final int childCount = viewGroup.getChildCount();
            PointF point = new PointF();
            for (int i = childCount; i > 0; i--) {
                View child = viewGroup.getChildAt(i - 1);
                if (isTransformedTouchPointInView(viewGroup, child, touch.x, touch.y, point)) {
                    touch.offset(point.x, point.y);
                    boolean can = canRefresh(child, touch);
                    touch.offset(-point.x, -point.y);
                    return can;
                }
            }
        }
        return true;
    }

    /**
     * 判断内容视图是否可以加载更多
     * @param targetView 内容视图
     * @param touch 按压事件位置
     * @param contentFull 内容是否填满页面 (未填满时，会通过canScrollUp自动判断)
     * @return 是否可以刷新
     */
    public static boolean canLoadMore(@NonNull View targetView, PointF touch, boolean contentFull) {
        if (canScrollDown(targetView) && targetView.getVisibility() == View.VISIBLE) {
            return false;
        }
        //touch == null 时 canLoadMore 不会动态递归搜索
        if (targetView instanceof ViewGroup && touch != null && !SmartUtil.isScrollableView(targetView)) {
            ViewGroup viewGroup = (ViewGroup) targetView;
            final int childCount = viewGroup.getChildCount();
            PointF point = new PointF();
            for (int i = 0; i < childCount; i++) {
                View child = viewGroup.getChildAt(i);
                if (isTransformedTouchPointInView(viewGroup, child, touch.x, touch.y, point)) {
                    touch.offset(point.x, point.y);
                    boolean can = canLoadMore(child, touch, contentFull);
                    touch.offset(-point.x, -point.y);
                    return can;
                }
            }
        }
        return (contentFull || canScrollUp(targetView));
    }

//    public static boolean canScrollDown(View targetView, MotionEvent event) {
//        if (canScrollDown(targetView) && targetView.getVisibility() == View.VISIBLE) {
//            return true;
//        }
//        //event == null 时 canScrollDown 不会动态递归搜索
//        if (targetView instanceof ViewGroup && event != null) {
//            ViewGroup viewGroup = (ViewGroup) targetView;
//            final int childCount = viewGroup.getChildCount();
//            PointF point = new PointF();
//            for (int i = 0; i < childCount; i++) {
//                View child = viewGroup.getChildAt(i);
//                if (isTransformedTouchPointInView(viewGroup, child, event.getX(), event.getY(), point)) {
//                    event = MotionEvent.obtain(event);
//                    event.offsetLocation(point.x, point.y);
//                    return canScrollDown(child, event);
//                }
//            }
//        }
//        return false;
//    }

    public static boolean canScrollUp(@NonNull View targetView) {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (targetView instanceof AbsListView) {
                final ViewGroup viewGroup = (ViewGroup) targetView;
                final AbsListView absListView = (AbsListView) targetView;
                return viewGroup.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0
                        || viewGroup.getChildAt(0).getTop() < targetView.getPaddingTop());
            } else {
                return targetView.getScrollY() > 0;
            }
        } else {
            return targetView.canScrollVertically(-1);
        }
    }

    public static boolean canScrollDown(@NonNull View targetView) {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (targetView instanceof AbsListView) {
                final ViewGroup viewGroup = (ViewGroup) targetView;
                final AbsListView absListView = (AbsListView) targetView;
                final int childCount = viewGroup.getChildCount();
                return childCount > 0 && (absListView.getLastVisiblePosition() < childCount - 1
                        || viewGroup.getChildAt(childCount - 1).getBottom() > targetView.getPaddingBottom());
            } else {
                return targetView.getScrollY() < 0;
            }
        } else {
            return targetView.canScrollVertically(1);
        }
    }

    //</editor-fold>

    //<editor-fold desc="transform Point">

    public static boolean isTransformedTouchPointInView(@NonNull View group,@NonNull View child, float x, float y,PointF outLocalPoint) {
        if (child.getVisibility() != View.VISIBLE) {
            return false;
        }
        final float[] point = new float[2];
        point[0] = x;
        point[1] = y;
//        transformPointToViewLocal(group, child, point);
        point[0] += group.getScrollX() - child.getLeft();
        point[1] += group.getScrollY() - child.getTop();
//        final boolean isInView = pointInView(child, point[0], point[1], 0);
        final boolean isInView = point[0] >= 0 && point[1] >= 0
                && point[0] < (child.getWidth())
                && point[1] < ((child.getHeight()));
        if (isInView && outLocalPoint != null) {
            outLocalPoint.set(point[0]-x, point[1]-y);
        }
        return isInView;
    }

//    public static boolean pointInView(View view, float localX, float localY, float slop) {
//        final float left = /*Math.max(view.getPaddingLeft(), 0)*/ - slop;
//        final float top = /*Math.max(view.getPaddingTop(), 0)*/ - slop;
//        final float width = view.getWidth()/* - Math.max(view.getPaddingLeft(), 0) - Math.max(view.getPaddingRight(), 0)*/;
//        final float height = view.getHeight()/* - Math.max(view.getPaddingTop(), 0) - Math.max(view.getPaddingBottom(), 0)*/;
//        return localX >= left && localY >= top && localX < ((width) + slop) &&
//                localY < ((height) + slop);
//    }

//    public static void transformPointToViewLocal(ViewGroup group, View child, float[] point) {
//        point[0] += group.getScrollX() - child.getLeft();
//        point[1] += group.getScrollY() - child.getTop();
//    }
    //</editor-fold>

}
