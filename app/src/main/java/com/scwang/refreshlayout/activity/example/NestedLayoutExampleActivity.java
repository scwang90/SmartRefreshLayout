package com.scwang.refreshlayout.activity.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.scwang.refreshlayout.R;
import com.scwang.refreshlayout.adapter.BaseRecyclerAdapter;
import com.scwang.refreshlayout.adapter.SmartViewHolder;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class NestedLayoutExampleActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static String[] provinces = new String[]{
            "北京","天津","上海","重庆",
            "黑龙江","吉林","辽宁","河北","河南","山东","江苏","山西","陕西","甘肃","四川","青海","湖南","湖北","江西","安徽","浙江","福建","广东","广西","贵州","云南","海南",
            "内蒙古","新疆维吾尔族自治区","宁夏回族自治区","西藏","宁夏回族自治区",
            "香港","澳门"
    };

    private static boolean isFirstEnter = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_region);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        View view = findViewById(R.id.recyclerView);
        if (view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
            recyclerView.setAdapter(new BaseRecyclerAdapter<String>(Arrays.asList(provinces),android.R.layout.simple_list_item_1) {
                @Override
                protected void onBindViewHolder(SmartViewHolder holder, String model, int position) {
                    holder.text(android.R.id.text1, model);
                }
            }.setOnItemClickListener(this));
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss SSS", Locale.CHINA);
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    Log.e("recyclerView", dateFormat.format(new Date()) + " - onScrollStateChanged - " + newState);
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                }
            });
        }
        /*
         * 以下代码仅仅为了演示效果而已，不是必须的
         * 关键代码在 activity_example_assign_xml 中
         */
        final RefreshLayout refreshLayout = findViewById(R.id.refreshLayout);
        if (isFirstEnter && refreshLayout != null) {
            isFirstEnter = false;
            //触发上拉加载
            refreshLayout.autoRefresh();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String province = provinces[position];
        view = findViewById(R.id.region);
        if (view instanceof TextView) {
            ((TextView) view).setText(province);
        }
    }
}
