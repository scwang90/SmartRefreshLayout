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
import com.scwang.refreshlayout.activity.using.BasicExampleActivity;
import com.scwang.refreshlayout.activity.using.CustomExampleActivity;
import com.scwang.refreshlayout.activity.using.ListenerExampleActivity;
import com.scwang.refreshlayout.activity.using.NestedLayoutExampleActivity;
import com.scwang.refreshlayout.activity.using.SnapHelperExampleActivity;
import com.scwang.refreshlayout.adapter.BaseRecyclerAdapter;
import com.scwang.refreshlayout.adapter.SmartViewHolder;
import com.scwang.refreshlayout.fragment.using.EmptyLayoutExampleFragment;
import com.scwang.refreshlayout.fragment.using.NestedScrollExampleFragment;
import com.scwang.refreshlayout.fragment.using.PureScrollExampleFragment;
import com.scwang.refreshlayout.fragment.using.SpecifyStyleExampleFragment;
import com.scwang.refreshlayout.fragment.using.ThreeLevelExampleFragment;
import com.scwang.refreshlayout.fragment.using.ViewPagerExampleFragment;
import com.scwang.refreshlayout.util.StatusBarUtil;

import java.util.Arrays;

import static android.R.layout.simple_list_item_2;
import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

/**
 * 使用示例
 * A simple {@link Fragment} subclass.
 */
public class RefreshExampleFragment extends Fragment implements AdapterView.OnItemClickListener {

    private enum Item {
        Basic(R.string.index_example_basic, BasicExampleActivity.class),
        SpecifyStyle(R.string.index_example_style, SpecifyStyleExampleFragment.class),
        EmptyLayout(R.string.index_example_empty, EmptyLayoutExampleFragment.class),
        NestedLayout(R.string.index_example_layout, NestedLayoutExampleActivity.class),
        NestedScroll(R.string.index_example_nested, NestedScrollExampleFragment.class),
        PureScroll(R.string.index_example_scroll, PureScrollExampleFragment.class),
        Listener(R.string.index_example_listener, ListenerExampleActivity.class),
        Custom(R.string.index_example_custom, CustomExampleActivity.class),
        SnapHelper(R.string.index_example_snap_helper, SnapHelperExampleActivity.class),
        ViewPager(R.string.index_example_pager, ViewPagerExampleFragment.class),
        ThreeLevel(R.string.index_example_level, ThreeLevelExampleFragment.class),;
        public int nameId;
        public Class<?> clazz;

        Item(@StringRes int nameId, Class<?> clazz) {
            this.nameId = nameId;
            this.clazz = clazz;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_refresh_example, container, false);
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
                    holder.text(android.R.id.text2, getString(model.nameId));
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
