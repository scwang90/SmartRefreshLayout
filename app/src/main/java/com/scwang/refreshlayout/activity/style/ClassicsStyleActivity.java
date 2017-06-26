package com.scwang.refreshlayout.activity.style;

import android.os.Build;
import android.os.Bundle;
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
import com.scwang.refreshlayout.util.DynamicTimeFormat;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import static android.R.layout.simple_list_item_2;
import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class ClassicsStyleActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private enum Item {
        尺寸拉伸("下拉的时候Header的高度跟随变大"),
        位置平移("下拉的时候Header的位置向下偏移"),
        背后固定("下拉的时候Header固定在背后"),
        默认主题("更改为默认主题颜色"),
        橙色主题("更改为橙色主题颜色"),
        红色主题("更改为红色主题颜色"),
        绿色主题("更改为绿色主题颜色"),
        蓝色主题("更改为蓝色主题颜色"),
        加载更多("上啦加载更多"),
        ;
        public String name;
        Item(String name) {
            this.name = name;
        }
    }

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private RefreshLayout mRefreshLayout;
    private ClassicsHeader mClassicsHeader;
    private static boolean isFirstEnter = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_style_classics);

        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mRefreshLayout = (RefreshLayout)findViewById(R.id.smart);

        int deta = new Random().nextInt(7 * 24 * 60 * 60 * 1000);
        mClassicsHeader = (ClassicsHeader)mRefreshLayout.getRefreshHeader();
        mClassicsHeader.setLastUpdateTime(new Date(System.currentTimeMillis()-deta));
        mClassicsHeader.setTimeFormat(new SimpleDateFormat("更新于 MM-dd HH:mm", Locale.CHINA));
        mClassicsHeader.setTimeFormat(new DynamicTimeFormat("更新于 %s"));


        View view = findViewById(R.id.recycler);
        if (view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.addItemDecoration(new DividerItemDecoration(this, VERTICAL));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(new BaseRecyclerAdapter<Item>(Arrays.asList(Item.values()), simple_list_item_2,this) {
                @Override
                protected void onBindViewHolder(SmartViewHolder holder, Item model, int position) {
                    holder.text(android.R.id.text1, model.name());
                    holder.text(android.R.id.text2, model.name);
                    holder.textColorId(android.R.id.text2, R.color.colorTextAssistant);
                }
            });
            mRecyclerView = recyclerView;
        }

        if (isFirstEnter) {
            isFirstEnter = false;
            //触发上啦加载
            mRefreshLayout.autoLoadmore();
            //通过多功能监听接口实现 在第一次加载完成之后 自动刷新
            mRefreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener(){
                @Override
                public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
                    if (oldState == RefreshState.LoadingFinish && newState == RefreshState.None) {
                        mRefreshLayout.autoRefresh();
                        mRefreshLayout.setOnMultiPurposeListener(null);//保准只有第一次关联
                    }
                }
            });
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (Item.values()[position]) {
            case 背后固定:
                mClassicsHeader.setSpinnerStyle(SpinnerStyle.FixedBehind);
                mRefreshLayout.setPrimaryColors(0xff444444, 0xffffffff);
                /**
                 * 由于是后面才设置，需要手动更改视图的位置
                 * 如果在 onCreate 或者 xml 中设置好[SpinnerStyle] 就不用手动调整位置了
                 */
                mRefreshLayout.getLayout().bringChildToFront(mRecyclerView);
                break;
            case 尺寸拉伸:
                mClassicsHeader.setSpinnerStyle(SpinnerStyle.Scale);
                break;
            case 位置平移:
                mClassicsHeader.setSpinnerStyle(SpinnerStyle.Translate);
                break;
            case 默认主题:
                setThemeColor(R.color.colorPrimary, R.color.colorPrimaryDark);
                mRefreshLayout.getLayout().setBackgroundResource(android.R.color.transparent);
                mRefreshLayout.setPrimaryColorsId(android.R.color.transparent, android.R.color.tertiary_text_dark);
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
            case 加载更多:
                mRefreshLayout.autoLoadmore();
                return;
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
