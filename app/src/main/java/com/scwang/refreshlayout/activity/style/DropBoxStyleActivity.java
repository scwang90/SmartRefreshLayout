package com.scwang.refreshlayout.activity.style;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;

import com.scwang.refreshlayout.R;
import com.scwang.refreshlayout.adapter.BaseRecyclerAdapter;
import com.scwang.refreshlayout.adapter.SmartViewHolder;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

import java.util.Arrays;

import static android.R.layout.simple_list_item_2;
import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class DropBoxStyleActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private enum Item {
        默认主题(R.string.item_style_theme_default_abstract),
        橙色主题(R.string.item_style_theme_orange_abstract),
        红色主题(R.string.item_style_theme_red_abstract),
        绿色主题(R.string.item_style_theme_green_abstract),
        蓝色主题(R.string.item_style_theme_blue_abstract),


        默认1主题(R.string.item_style_theme_default_abstract),
        橙色1主题(R.string.item_style_theme_orange_abstract),
        红色1主题(R.string.item_style_theme_red_abstract),
        绿色1主题(R.string.item_style_theme_green_abstract),
        蓝色1主题(R.string.item_style_theme_blue_abstract),


        默认2主题(R.string.item_style_theme_default_abstract),
        橙色2主题(R.string.item_style_theme_orange_abstract),
        红色2主题(R.string.item_style_theme_red_abstract),
        绿色2主题(R.string.item_style_theme_green_abstract),
        蓝色2主题(R.string.item_style_theme_blue_abstract),
        ;
        public int nameId;
        Item(@StringRes int nameId) {
            this.nameId = nameId;
        }
    }

    private Toolbar mToolbar;
    private RefreshLayout mRefreshLayout;
    private static boolean isFirstEnter = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_style_dropbox);

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setNavigationOnClickListener(v -> finish());

        mRefreshLayout = findViewById(R.id.refreshLayout);
        if (isFirstEnter) {
            isFirstEnter = false;
            mRefreshLayout.autoRefresh();//第一次进入触发自动刷新，演示效果
        }

        View view = findViewById(R.id.recyclerView);
        if (view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.addItemDecoration(new DividerItemDecoration(this, VERTICAL));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(new BaseRecyclerAdapter<Item>(Arrays.asList(Item.values()), simple_list_item_2,this) {
                @Override
                protected void onBindViewHolder(SmartViewHolder holder, Item model, int position) {
                    holder.text(android.R.id.text1, model.name());
                    holder.text(android.R.id.text2, model.nameId);
                    holder.textColorId(android.R.id.text2, R.color.colorTextAssistant);
                }
            });
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (Item.values()[position % Item.values().length]) {
            case 默认主题:
                setThemeColor(R.color.colorPrimary, R.color.colorPrimaryDark);
                mRefreshLayout.setPrimaryColors(0xff283645, 0xff6ea9ff);
                break;
            case 蓝色主题:
                setThemeColor(R.color.colorPrimary, R.color.colorPrimaryDark);
                break;
            case 绿色主题:
                setThemeColor(android.R.color.holo_green_light, android.R.color.holo_green_dark);
                break;
            case 红色主题:
                setThemeColor(android.R.color.holo_red_light, android.R.color.holo_red_dark);
                break;
            case 橙色主题:
                setThemeColor(android.R.color.holo_orange_light, android.R.color.holo_orange_dark);
                break;
        }
        mRefreshLayout.autoRefresh();
    }

    private void setThemeColor(int colorPrimary, int colorPrimaryDark) {
        mToolbar.setBackgroundResource(colorPrimary);
        mRefreshLayout.setPrimaryColorsId(colorPrimary, android.R.color.white);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, colorPrimaryDark));
        }
    }


}
