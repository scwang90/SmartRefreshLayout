package com.scwang.refreshlayout.fragment.index;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.scwang.refreshlayout.R;
import com.scwang.refreshlayout.activity.FragmentActivity;
import com.scwang.refreshlayout.activity.practice.BannerPracticeActivity;
import com.scwang.refreshlayout.activity.practice.FeedlistPracticeActivity;
import com.scwang.refreshlayout.activity.practice.ProfilePracticeActivity;
import com.scwang.refreshlayout.activity.practice.QQBrowserPracticeActivity;
import com.scwang.refreshlayout.activity.practice.RepastPracticeActivity;
import com.scwang.refreshlayout.activity.practice.WebViewPracticeActivity;
import com.scwang.refreshlayout.activity.practice.WeiboPracticeActivity;
import com.scwang.refreshlayout.adapter.BaseRecyclerAdapter;
import com.scwang.refreshlayout.adapter.SmartViewHolder;
import com.scwang.refreshlayout.fragment.practice.SecondFloorPracticeFragment;
import com.scwang.refreshlayout.util.StatusBarUtil;

import java.util.Arrays;

import static android.R.layout.simple_list_item_2;
import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

/**
 * 实战演示
 * A simple {@link Fragment} subclass.
 */
public class RefreshPractiveFragment extends Fragment implements AdapterView.OnItemClickListener {

    private enum Item {
        Repast(R.string.index_practice_repast, RepastPracticeActivity.class),
        Profile(R.string.index_practice_profile, ProfilePracticeActivity.class),
        WebView(R.string.index_practice_web_view, WebViewPracticeActivity.class),
        FeedList(R.string.index_practice_feed_list, FeedlistPracticeActivity.class),
        Weibo(R.string.index_practice_weibo, WeiboPracticeActivity.class),
        Banner(R.string.index_practice_banner, BannerPracticeActivity.class),
        QQBrowser(R.string.index_practice_qq_browser, QQBrowserPracticeActivity.class),
//        TwoLevel("二级刷新", SecondFloorPracticeFragment.class),
        SecondFloor(R.string.index_practice_second_floor, SecondFloorPracticeFragment.class),
        ;
        @StringRes
        public int name;
        public Class<?> clazz;
        Item(@StringRes int name, Class<?> clazz) {
            this.name = name;
            this.clazz = clazz;
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_refresh_practive, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);
        StatusBarUtil.setPaddingSmart(getContext(), root.findViewById(R.id.toolbar));

        View view = root.findViewById(R.id.recyclerView);
        if (view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), VERTICAL));
            recyclerView.setAdapter(new BaseRecyclerAdapter<Item>(Arrays.asList(Item.values()), simple_list_item_2,this) {
                @Override
                protected void onBindViewHolder(SmartViewHolder holder, Item model, int position) {
                    holder.text(android.R.id.text1, model.name());
                    holder.text(android.R.id.text2, model.name);
                    holder.textColorId(android.R.id.text2, R.color.colorTextAssistant);
                }
            });
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Item item = Item.values()[position];
        if (Activity.class.isAssignableFrom(item.clazz)) {
            startActivity(new Intent(getContext(), item.clazz));
        } else if (Fragment.class.isAssignableFrom(item.clazz)) {
            FragmentActivity.start(this, item.clazz);
        }
    }
}
