package com.scwang.smart.refresh.layout.constant;

/**
 * 顶部和底部的组件在拖动时候的变换方式
 * Created by SCWANG on 2017/5/26.
 */

public class SpinnerStyle {

    public static final SpinnerStyle Translate = new SpinnerStyle(0, false);
    public static final SpinnerStyle Scale = new SpinnerStyle(1, false);
    public static final SpinnerStyle FixedBehind = new SpinnerStyle(2, false);
    public static final SpinnerStyle FixedFront = new SpinnerStyle(3, true);
    public static final SpinnerStyle MatchLayout = new SpinnerStyle(4, true);

    public static final SpinnerStyle[] values = new SpinnerStyle[]{
            Translate, //平行移动        特点: HeaderView高度不会改变，
            Scale, //拉伸形变            特点：在下拉和上弹（HeaderView高度改变）时候，会自动触发OnDraw事件
            FixedBehind, //固定在背后    特点：HeaderView高度不会改变，
            FixedFront, //固定在前面     特点：HeaderView高度不会改变，
            MatchLayout//填满布局        特点：HeaderView高度不会改变，尺寸充满 RefreshLayout
    };

    public final int ordinal;
    public final boolean front;

    protected SpinnerStyle(int ordinal, boolean front) {
        this.ordinal = ordinal;
        this.front = front;
    }
}
