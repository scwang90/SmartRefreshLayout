package com.scwang.smartrefresh.layout.header;

import android.content.Context;
import android.util.AttributeSet;

import com.scwang.smartrefresh.layout.api.RefreshHeader;

/**
 * 虚假的 Header
 * 用于 正真的 Header 在 RefreshLayout 外部时，
 * 使用本虚假的 FalsifyHeader 填充在 RefreshLayout 内部
 * 具体使用方法 参考 纸飞机（FlyRefreshHeader）
 * Created by scwang on 2017/6/14.
 */
public class FalsifyHeader extends com.scwang.smart.refresh.header.FalsifyHeader implements RefreshHeader {

    //<editor-fold desc="FalsifyHeader">
    public FalsifyHeader(Context context) {
        this(context, null);
    }

    public FalsifyHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    //</editor-fold>
}
