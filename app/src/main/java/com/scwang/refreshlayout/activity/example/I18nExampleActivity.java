package com.scwang.refreshlayout.activity.example;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.scwang.refreshlayout.R;
import com.scwang.refreshlayout.adapter.BaseRecyclerAdapter;
import com.scwang.refreshlayout.adapter.SmartViewHolder;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static android.R.layout.simple_list_item_2;

public class I18nExampleActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private enum Item {
        LanguageSystem(R.string.item_example_i18n_system),
        LanguageChinese(R.string.item_example_i18n_chinese),
        LanguageEnglish(R.string.item_example_i18n_english),
        ;
        public final int nameId;
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
        switch (Item.values()[position % Item.values().length]) {
            case LanguageSystem:
                changeAppLanguage(Locale.getDefault());
                break;
            case LanguageChinese:
                changeAppLanguage(Locale.CHINESE);
                break;
            case LanguageEnglish:
                changeAppLanguage(Locale.ENGLISH);
                break;
        }
    }

    private void changeAppLanguage(Locale locale) {
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(locale);
        res.updateConfiguration(conf, dm);
        startActivity(new Intent(this, getClass()));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
