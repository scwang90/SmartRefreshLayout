package com.scwang.refreshlayout.fragment;


import android.content.Intent;
import android.os.Bundle;
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
import com.scwang.refreshlayout.activity.using.AssignCodeUsingActivity;
import com.scwang.refreshlayout.activity.using.AssignDefaultUsingActivity;
import com.scwang.refreshlayout.activity.using.AssignXmlUsingActivity;
import com.scwang.refreshlayout.activity.using.BasicUsingActivity;
import com.scwang.refreshlayout.activity.using.CustomUsingActivity;
import com.scwang.refreshlayout.activity.using.ListenerUsingActivity;
import com.scwang.refreshlayout.activity.using.NestLayoutUsingActivity;
import com.scwang.refreshlayout.activity.using.SnapHelperUsingActivity;
import com.scwang.refreshlayout.adapter.BaseRecyclerAdapter;
import com.scwang.refreshlayout.adapter.SmartViewHolder;
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
        DefaultCreater("设置全局默认的Header和Footer", AssignDefaultUsingActivity.class),
        XmlDefine("在XML中定义Header和Footer", AssignXmlUsingActivity.class),
        CodeDefine("在代码中指定Header和Footer", AssignCodeUsingActivity.class),
        Listener("多功能监听器", ListenerUsingActivity.class),
        NestLayout("嵌套Layout作为内容", NestLayoutUsingActivity.class),
        Custom("自定义Header", CustomUsingActivity.class),
        SnapHelper("结合 SnapHelper 使用", SnapHelperUsingActivity.class),
        ;
        public String name;
        public Class<?> clazz;
        Item(String name, Class<?> clazz) {
            this.name = name;
            this.clazz = clazz;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_refresh_using, container, false);
    }

    @Override
    public void onViewCreated(View root, @Nullable Bundle savedInstanceState) {
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
        startActivity(new Intent(getContext(), item.clazz));
    }
}
