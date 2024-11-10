package com.scwang.refresh.layout.activity.style;

import android.content.res.ColorStateList;
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

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.scwang.refresh.layout.R;
import com.scwang.refresh.layout.adapter.BaseRecyclerAdapter;
import com.scwang.refresh.layout.adapter.SmartViewHolder;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.R.layout.simple_list_item_2;
import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;

public class TaurusStyleActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private enum Item {
        Collapse("折叠", R.string.item_style_app_bar_collapse),
        Expend("展开", R.string.item_style_app_bar_expand),
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
    private RecyclerView mRecyclerView;
    private AppBarLayout mAppBarLayout;
    private RefreshLayout mRefreshLayout;
    private FloatingActionButton mActionButton;
    private CollapsingToolbarLayout mToolbarLayout;
    private static boolean isFirstEnter = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_style_taurus);

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setNavigationOnClickListener(v -> finish());

        mRefreshLayout = findViewById(R.id.refreshLayout);
        if (isFirstEnter) {
            isFirstEnter = false;
            mRefreshLayout.autoRefresh();//第一次进入触发自动刷新，演示效果
        }

        mAppBarLayout = findViewById(R.id.appbar);
        mToolbarLayout = findViewById(R.id.toolbarLayout);

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
            mRecyclerView = recyclerView;
        }

        /*
         * 监听 AppBarLayout 的关闭和开启 给 ActionButton 设置关闭隐藏动画
         */
        mActionButton = findViewById(R.id.fab);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean misAppbarExpand = true;
            final View fab = findViewById(R.id.fab);
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int scrollRange = appBarLayout.getTotalScrollRange();
                float fraction = 1f * (scrollRange + verticalOffset) / scrollRange;
                if (fraction < 0.1 && misAppbarExpand) {
                    misAppbarExpand = false;
                    fab.animate().scaleX(0).scaleY(0);
                }
                if (fraction > 0.8 && !misAppbarExpand) {
                    misAppbarExpand = true;
                    fab.animate().scaleX(1).scaleY(1);
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (Item.values()[position % Item.values().length]) {
            case Collapse:
                mAppBarLayout.setExpanded(false, true);
                mAppBarLayout.setEnabled(false);
                mRecyclerView.setNestedScrollingEnabled(false);
                break;
            case Expend:
                mAppBarLayout.setEnabled(true);
                mAppBarLayout.setExpanded(true, true);
                mRecyclerView.setNestedScrollingEnabled(true);
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
        mAppBarLayout.setBackgroundResource(colorPrimary);
        mToolbarLayout.setContentScrimResource(colorPrimary);
        mRefreshLayout.setPrimaryColorsId(colorPrimary, android.R.color.white);
        mActionButton.setBackgroundColor(ContextCompat.getColor(this, colorPrimaryDark));
        mActionButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, colorPrimaryDark)));
        getWindow().setStatusBarColor(ContextCompat.getColor(this, colorPrimaryDark));
    }

}
