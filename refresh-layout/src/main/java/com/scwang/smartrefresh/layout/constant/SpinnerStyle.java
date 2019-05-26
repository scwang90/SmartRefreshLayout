package com.scwang.smartrefresh.layout.constant;

/**
 * 顶部和底部的组件在拖动时候的变换方式
 * Created by SCWANG on 2017/5/26.
 */

public class SpinnerStyle {

    public static final SpinnerStyle Translate = new SpinnerStyle(0);
    public static final SpinnerStyle Scale = new SpinnerStyle(1);
    public static final SpinnerStyle FixedBehind = new SpinnerStyle(2);
    public static final SpinnerStyle FixedFront = new SpinnerStyle(3);
    public static final SpinnerStyle MatchLayout = new SpinnerStyle(4);

    public static final SpinnerStyle[] values = new SpinnerStyle[]{
            Translate, //平行移动        特点: HeaderView高度不会改变，
            Scale, //拉伸形变            特点：在下拉和上弹（HeaderView高度改变）时候，会自动触发OnDraw事件
            FixedBehind, //固定在背后    特点：HeaderView高度不会改变，
            FixedFront, //固定在前面     特点：HeaderView高度不会改变，
            MatchLayout//填满布局        特点：HeaderView高度不会改变，尺寸充满 RefreshLayout
    };

    public final int ordinal;

    private SpinnerStyle(int ordinal) {
        this.ordinal = ordinal;
    }
}
