package com.scwang.smartrefresh.layout.util;

import android.content.Context;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.ScrollingView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ScrollView;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * SmartUtil
 * Created by SCWANG on 2018/3/5.
 */

public class SmartUtil {

    public static int getColor(@NonNull Context context, @ColorRes int colorId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(colorId);
        }
        //noinspection deprecation
        return context.getResources().getColor(colorId);
    }

    public static int measureViewHeight(View view) {
        ViewGroup.LayoutParams p = view.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(MATCH_PARENT,WRAP_CONTENT);
        }
        int childHeightSpec;
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        if (p.height > 0) {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(p.height, View.MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        }
        view.measure(childWidthSpec, childHeightSpec);
        return view.getMeasuredHeight();
    }

    public static void scrollListBy(@NonNull AbsListView listView, int y) {
        if (Build.VERSION.SDK_INT >= 19) {
            // Call the framework version directly
            listView.scrollListBy(y);
        } else if (listView instanceof ListView) {
            // provide backport on earlier versions
            final int firstPosition = listView.getFirstVisiblePosition();
            if (firstPosition == ListView.INVALID_POSITION) {
                return;
            }

            //noinspection UnnecessaryLocalVariable
            final ViewGroup listGroup = listView;
            final View firstView = listGroup.getChildAt(0);
            if (firstView == null) {
                return;
            }

            final int newTop = firstView.getTop() - y;
            ((ListView) listView).setSelectionFromTop(firstPosition, newTop);
        } else {
            listView.smoothScrollBy(y, 0);
        }
    }

    public static boolean isScrollableView(View view) {
        return view instanceof AbsListView
                || view instanceof ScrollView
                || view instanceof ScrollingView
                || view instanceof WebView
                || view instanceof NestedScrollingChild;
    }

    public static boolean isContentView(View view) {
        return isScrollableView(view)
                || view instanceof ViewPager
                || view instanceof NestedScrollingParent;
    }

    public static void fling(View scrollableView, int velocity) {
        if (scrollableView instanceof ScrollView) {
            ((ScrollView) scrollableView).fling(velocity);
        } else if (scrollableView instanceof AbsListView) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((AbsListView) scrollableView).fling(velocity);
            }
        } else if (scrollableView instanceof WebView) {
            ((WebView) scrollableView).flingScroll(0, velocity);
        } else if (scrollableView instanceof NestedScrollView) {
            ((NestedScrollView) scrollableView).fling(velocity);
        } else if (scrollableView instanceof RecyclerView) {
            ((RecyclerView) scrollableView).fling(0, velocity);
        }
    }
}
