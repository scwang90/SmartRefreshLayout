package com.scwang.refreshlayout.fragment.index;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.scwang.refreshlayout.activity.using.BasicUsingActivity;
import com.scwang.refreshlayout.activity.using.CustomUsingActivity;
import com.scwang.refreshlayout.activity.using.ListenerUsingActivity;
import com.scwang.refreshlayout.activity.using.NestedLayoutUsingActivity;
import com.scwang.refreshlayout.activity.using.SnapHelperUsingActivity;
import com.scwang.refreshlayout.adapter.BaseRecyclerAdapter;
import com.scwang.refreshlayout.adapter.SmartViewHolder;
import com.scwang.refreshlayout.fragment.using.EmptyLayoutUsingFragment;
import com.scwang.refreshlayout.fragment.using.NestedScrollUsingFragment;
import com.scwang.refreshlayout.fragment.using.PureScrollUsingFragment;
import com.scwang.refreshlayout.fragment.using.SpecifyStyleUsingFragment;
import com.scwang.refreshlayout.fragment.using.ViewPagerUsingFragment;
import com.scwang.refreshlayout.util.StatusBarUtil;

import java.util.Arrays;

import static android.R.layout.simple_list_item_2;
import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

/**
 * 使用示例
 * A simple {@link Fragment} subclass.
 */
public class RefreshUsingFragment extends Fragment implements AdapterView.OnItemClickListener {

    private enum Item {
        Basic("基本的使用", BasicUsingActivity.class),
        SpecifyStyle("使用指定的Header和Footer", SpecifyStyleUsingFragment.class),
        EmptyLayout("整合空页面", EmptyLayoutUsingFragment.class),
        NestedLayout("嵌套Layout作为内容", NestedLayoutUsingActivity.class),
        NestedScroll("嵌套滚动使用", NestedScrollUsingFragment.class),
        PureScroll("纯滚动模式", PureScrollUsingFragment.class),
        Listener("多功能监听器", ListenerUsingActivity.class),
        Custom("自定义Header", CustomUsingActivity.class),
        SnapHelper("结合 SnapHelper 使用", SnapHelperUsingActivity.class),
        ViewPager("ViewPager 多页面共用一个 RefreshLayout", ViewPagerUsingFragment.class),;
        public String name;
        public Class<?> clazz;

        Item(String name, Class<?> clazz) {
            this.name = name;
            this.clazz = clazz;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_refresh_using, container, false);
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
