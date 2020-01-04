package com.scwang.smartrefresh.header;

import android.content.Context;
import android.util.AttributeSet;

import com.scwang.smartrefresh.layout.api.RefreshHeader;

import java.util.Random;

/**
 * Created by scwang on 2018/3/09.
 * from https://github.com/Hitomis/FunGameRefresh
 */
public class FunGameBattleCityHeader extends com.scwang.smart.refresh.header.FunGameBattleCityHeader implements RefreshHeader {

    //<editor-fold desc="初始方法">
    public FunGameBattleCityHeader(Context context) {
        this(context, null);
    }

    public FunGameBattleCityHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        random = new Random();
    }
    //</editor-fold>

}
