package com.scwang.refreshlayout.activity.example;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.scwang.refreshlayout.R;
import com.scwang.refreshlayout.adapter.BaseRecyclerAdapter;
import com.scwang.refreshlayout.adapter.SmartViewHolder;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static android.R.layout.simple_list_item_2;

public class I18nExampleActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private enum Item {
        i1(R.string.item_example_i18n_system),
        i2(R.string.item_example_i18n_chinese),
        i3(R.string.item_example_i18n_english),
        ;
        public int nameId;
        Item(@StringRes int nameId) {
            this.nameId = nameId;
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_i18n);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        RefreshLayout refreshLayout = findViewById(R.id.refreshLayout);
        if (refreshLayout != null) {
            refreshLayout.autoRefresh();//第一次进入触发自动刷新，演示效果
            refreshLayout.setRefreshHeader(new ClassicsHeader(this));
            refreshLayout.setOnRefreshListener((OnRefreshListener) layout -> layout.finishRefresh(1000));
        }

        View view = findViewById(R.id.listView);
        if (view instanceof ListView) {
            ListView listView = ((ListView) view);
            List<Item> items = new ArrayList<>();
            items.addAll(Arrays.asList(Item.values()));
            items.addAll(Arrays.asList(Item.values()));
            listView.setAdapter(new BaseRecyclerAdapter<Item>(items, simple_list_item_2, this) {
                @Override
                protected void onBindViewHolder(SmartViewHolder holder, Item model, int position) {
                    holder.text(android.R.id.text1, model.nameId);
                    holder.text(android.R.id.text2, getString(R.string.item_example_i18n_click, getString(model.nameId)));
                    holder.textColorId(android.R.id.text2, R.color.colorTextAssistant);
                }
            });
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (Item.values()[position % Item.values().length].nameId) {
            case R.string.item_example_i18n_system:
                changeAppLanguage(Locale.getDefault());
                break;
            case R.string.item_example_i18n_chinese:
                changeAppLanguage(Locale.CHINESE);
                break;
            case R.string.item_example_i18n_english:
                changeAppLanguage(Locale.ENGLISH);
                break;
        }
    }

    private void changeAppLanguage(Locale locale) {
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            conf.locale = locale;
        } else {
            conf.setLocale(locale);
        }
        res.updateConfiguration(conf, dm);
        startActivity(new Intent(this, getClass()));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
