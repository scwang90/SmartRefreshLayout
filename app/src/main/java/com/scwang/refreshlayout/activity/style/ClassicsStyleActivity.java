package com.scwang.refreshlayout.activity.style;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
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

import com.scwang.refreshlayout.R;
import com.scwang.refreshlayout.adapter.BaseRecyclerAdapter;
import com.scwang.refreshlayout.adapter.SmartViewHolder;
import com.scwang.refreshlayout.util.DynamicTimeFormat;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;
import com.scwang.smart.refresh.header.ClassicsHeader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static android.R.layout.simple_list_item_2;
import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;

public class ClassicsStyleActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private enum Item {
        SizeScale("尺寸拉伸", R.string.item_style_spinner_scale),
        PlaceMove("位置平移", R.string.item_style_spinner_translation),
        FixedBehind("背后固定", R.string.item_style_spinner_behind),
        TimeShow("显示时间", R.string.item_style_spinner_update_on),
        TimeHide("隐藏时间", R.string.item_style_spinner_update_off),
//        LoadMore("加载更多", R.string.item_style_load_more),
        ThemeDefault("默认主题", R.string.item_style_theme_default_abstract),
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
    private RefreshLayout mRefreshLayout;
    private ClassicsHeader mClassicsHeader;
    private Drawable mDrawableProgress;
    private static boolean isFirstEnter = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_style_classics);

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setNavigationOnClickListener(v -> finish());

        mRefreshLayout = findViewById(R.id.refreshLayout);

        int delta = new Random().nextInt(7 * 24 * 60 * 60 * 1000);
        mClassicsHeader = (ClassicsHeader)mRefreshLayout.getRefreshHeader();
        assert mClassicsHeader != null;
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

        if (isFirstEnter) {
            isFirstEnter = false;
            //触发自动刷新
            mRefreshLayout.autoRefresh();
        }

//        mRefreshLayout.autoRefresh();//开始刷新数据时无数据
//        mClassicsHeader.setFinishDuration(0);//关闭Header的完成延时
//        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
//            @Override
//            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
//                //刷新启动一秒后 关闭刷新
//                refreshLayout.getLayout().postDelayed(()->{
//                    refreshLayout.finishRefresh();
//                }, 1000);
//                //启动刷新两秒后 添加数据
//                refreshLayout.getLayout().postDelayed(()->{
//                    List<Item> items = new ArrayList<>();
//                    items.addAll(Arrays.asList(Item.values()));
//                    items.addAll(Arrays.asList(Item.values()));
//                    mAdpater.refresh(items);
//                }, 2000);
//            }
//        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (Item.values()[position % Item.values().length]) {
            case FixedBehind:
                mClassicsHeader.setSpinnerStyle(SpinnerStyle.FixedBehind);
                mRefreshLayout.setPrimaryColors(0xff444444, 0xffffffff);
                mDrawableProgress.setTint(0xffffffff);
                /*
                 * 由于是后面才设置，需要手动更改视图的位置
                 * 如果在 onCreate 或者 xml 中设置好[SpinnerStyle] 就不用手动调整位置了
                 */
                mRefreshLayout.getLayout().bringChildToFront(mRecyclerView);
                break;
            case SizeScale:
                mClassicsHeader.setSpinnerStyle(SpinnerStyle.values[1]);
                break;
            case PlaceMove:
                mClassicsHeader.setSpinnerStyle(SpinnerStyle.Translate);
                break;
            case TimeShow:
                mClassicsHeader.setEnableLastTime(true);
                break;
            case TimeHide:
                mClassicsHeader.setEnableLastTime(false);
                break;
            case ThemeDefault:
                setThemeColor(R.color.colorPrimary, R.color.colorPrimaryDark);
                mRefreshLayout.getLayout().setBackgroundResource(android.R.color.transparent);
                mRefreshLayout.setPrimaryColors(0, 0xff666666);
                mDrawableProgress.setTint(0xff666666);
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
//            case LoadMore:
//                mRefreshLayout.autoLoadMore();
//                return;
        }
        mRefreshLayout.autoRefresh();
    }

    private void setThemeColor(int colorPrimary, int colorPrimaryDark) {
        mToolbar.setBackgroundResource(colorPrimary);
        mRefreshLayout.setPrimaryColorsId(colorPrimary, android.R.color.white);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, colorPrimaryDark));
        mDrawableProgress.setTint(0xffffffff);
    }


}
