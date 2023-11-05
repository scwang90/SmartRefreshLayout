package com.scwang.refreshlayout.activity.style;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.refreshlayout.R;
import com.scwang.refreshlayout.adapter.BaseRecyclerAdapter;
import com.scwang.refreshlayout.adapter.SmartViewHolder;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.R.layout.simple_list_item_2;
import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;


public class BezierCircleStyleActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private enum Item {
        ContentNoOffset("内容不偏移", R.string.item_style_content_translation_off),
        ContentOffset("内容跟随偏移", R.string.item_style_content_translation_on),
        ThemeOrange("橙色主题", R.string.item_style_theme_orange_abstract),
        ThemeRed("红色主题", R.string.item_style_theme_red_abstract),
        ThemeGreen("绿色主题", R.string.item_style_theme_green_abstract),
        ThemeBlue("蓝色主题", R.string.item_style_theme_blue_abstract),
        ;
        public final String remark;
        public final int nameId;
        Item(String remark, @StringRes int nameId) {
            this.remark = remark;
            this.nameId = nameId;
        }
    }

    private Toolbar mToolbar;
    private RefreshLayout mRefreshLayout;
    private static boolean isFirstEnter = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_style_circle);

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
            List<Item> items = new ArrayList<>();
            items.addAll(Arrays.asList(Item.values()));
            items.addAll(Arrays.asList(Item.values()));
            recyclerView.setAdapter(new BaseRecyclerAdapter<Item>(items, simple_list_item_2,this) {
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
            case ContentNoOffset:
                mRefreshLayout.setEnableHeaderTranslationContent(false);
                break;
            case ContentOffset:
                mRefreshLayout.setEnableHeaderTranslationContent(true);
                break;
            case ThemeBlue:
                setThemeColor(R.color.colorPrimary, R.color.colorPrimaryDark);
                break;
            case ThemeGreen:
                setThemeColor(android.R.color.holo_green_light, android.R.color.holo_green_dark);
                break;
            case ThemeRed:
                setThemeColor(android.R.color.holo_red_light, android.R.color.holo_red_dark);
                break;
            case ThemeOrange:
                setThemeColor(android.R.color.holo_orange_light, android.R.color.holo_orange_dark);
                break;
        }
        mRefreshLayout.autoRefresh();
    }

    private void setThemeColor(int colorPrimary, int colorPrimaryDark) {
        mToolbar.setBackgroundResource(colorPrimary);
        mRefreshLayout.setPrimaryColorsId(colorPrimary, android.R.color.white);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, colorPrimaryDark));
    }


}
