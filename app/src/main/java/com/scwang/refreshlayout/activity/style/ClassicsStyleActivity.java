package com.scwang.refreshlayout.activity.style;

import static android.R.layout.simple_list_item_2;
import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.scwang.refreshlayout.R;
import com.scwang.refreshlayout.adapter.BaseRecyclerAdapter;
import com.scwang.refreshlayout.adapter.SmartViewHolder;
import com.scwang.refreshlayout.util.DynamicTimeFormat;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class ClassicsStyleActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private BaseRecyclerAdapter<Item> mAdpater;

    private enum Item {
        尺寸拉伸(R.string.item_style_spinner_scale),
        位置平移(R.string.item_style_spinner_translation),
        背后固定(R.string.item_style_spinner_behind),
        显示时间(R.string.item_style_spinner_update_on),
        隐藏时间(R.string.item_style_spinner_update_off),
//        加载更多(R.string.item_style_load_more),
        默认主题(R.string.item_style_theme_default_abstract),
        橙色主题(R.string.item_style_theme_orange_abstract),
        红色主题(R.string.item_style_theme_red_abstract),
        绿色主题(R.string.item_style_theme_green_abstract),
        蓝色主题(R.string.item_style_theme_blue_abstract),
        ;
        public int nameId;
        Item(@StringRes int nameId) {
            this.nameId = nameId;
        }
    }

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private RefreshLayout mRefreshLayout;
    private ClassicsHeader mClassicsHeader;
    private Drawable mDrawableProgress;
    private static boolean isFirstEnter = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_style_classics);

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mRefreshLayout = findViewById(R.id.refreshLayout);

        int delta = new Random().nextInt(7 * 24 * 60 * 60 * 1000);
        mClassicsHeader = (ClassicsHeader)mRefreshLayout.getRefreshHeader();
        mClassicsHeader.setLastUpdateTime(new Date(System.currentTimeMillis()-delta));
        mClassicsHeader.setTimeFormat(new SimpleDateFormat("更新于 MM-dd HH:mm", Locale.CHINA));
        mClassicsHeader.setTimeFormat(new DynamicTimeFormat("更新于 %s"));

//        mDrawableProgress = mClassicsHeader.getProgressView().getDrawable();
        mDrawableProgress = ((ImageView)mClassicsHeader.findViewById(ClassicsHeader.ID_IMAGE_PROGRESS)).getDrawable();
        if (mDrawableProgress instanceof LayerDrawable) {
            mDrawableProgress = ((LayerDrawable) mDrawableProgress).getDrawable(0);
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
            recyclerView.setAdapter(mAdpater = new BaseRecyclerAdapter<Item>(items, simple_list_item_2,this) {
                @Override
                protected void onBindViewHolder(SmartViewHolder holder, Item model, int position) {
                    holder.text(android.R.id.text1, model.name());
                    holder.text(android.R.id.text2, model.nameId);
                    holder.textColorId(android.R.id.text2, R.color.colorTextAssistant);
                }
            });
            mRecyclerView = recyclerView;
        }

        if (isFirstEnter) {
            isFirstEnter = false;
            //触发自动刷新
            mRefreshLayout.autoRefresh();
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (Item.values()[position % Item.values().length]) {
            case 背后固定:
                mClassicsHeader.setSpinnerStyle(SpinnerStyle.FixedBehind);
                mRefreshLayout.setPrimaryColors(0xff444444, 0xffffffff);
                if (Build.VERSION.SDK_INT >= 21) {
                    mDrawableProgress.setTint(0xffffffff);
                } else if (mDrawableProgress instanceof VectorDrawableCompat) {
                    mDrawableProgress.setTint(0xffffffff);
                }
                /*
                 * 由于是后面才设置，需要手动更改视图的位置
                 * 如果在 onCreate 或者 xml 中设置好[SpinnerStyle] 就不用手动调整位置了
                 */
                mRefreshLayout.getLayout().bringChildToFront(mRecyclerView);
                break;
            case 尺寸拉伸:
                mClassicsHeader.setSpinnerStyle(SpinnerStyle.values[1]);
                break;
            case 位置平移:
                mClassicsHeader.setSpinnerStyle(SpinnerStyle.Translate);
                break;
            case 显示时间:
                mClassicsHeader.setEnableLastTime(true);
                break;
            case 隐藏时间:
                mClassicsHeader.setEnableLastTime(false);
                break;
            case 默认主题:
                setThemeColor(R.color.colorPrimary, R.color.colorPrimaryDark);
                mRefreshLayout.getLayout().setBackgroundResource(android.R.color.transparent);
                mRefreshLayout.setPrimaryColors(0, 0xff666666);
                if (Build.VERSION.SDK_INT >= 21) {
                    mDrawableProgress.setTint(0xff666666);
                } else if (mDrawableProgress instanceof VectorDrawableCompat) {
                    mDrawableProgress.setTint(0xff666666);
                }
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
//            case 加载更多:
//                mRefreshLayout.autoLoadMore();
//                return;
        }
        mRefreshLayout.autoRefresh();
    }

    private void setThemeColor(int colorPrimary, int colorPrimaryDark) {
        mToolbar.setBackgroundResource(colorPrimary);
        mRefreshLayout.setPrimaryColorsId(colorPrimary, android.R.color.white);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, colorPrimaryDark));
            mDrawableProgress.setTint(0xffffffff);
        } else if (mDrawableProgress instanceof VectorDrawableCompat) {
            mDrawableProgress.setTint(0xffffffff);
        }
    }


}
