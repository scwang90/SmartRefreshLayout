package com.scwang.refreshlayout.activity.using;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.scwang.refreshlayout.R;
import com.scwang.refreshlayout.adapter.BaseRecyclerAdapter;
import com.scwang.refreshlayout.adapter.SmartViewHolder;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.Arrays;

public class NestLayoutUsingActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

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
        setContentView(R.layout.activity_using_region);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        }
        /**
         * 以下代码仅仅为了演示效果而已，不是必须的
         * 关键代码在 activity_using_assign_xml 中
         */
        final RefreshLayout refreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        if (isFirstEnter && refreshLayout != null) {
            isFirstEnter = false;
            //触发上啦加载
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
